/*
 * 
 * Author : Devesh Devendra [dd3053]
 * Creation Date : 22nd November 2022
 * Last Modification Date:	3rd December 2022
 * 
 * Description : 
 * This class Deals with the Data on a given Site
 * 
 * 
 */

import java.util.*;

enum DataManagerStatus{
	FAILED,
	WORKING
};

public class DataManager {
	
	public HashMap<String, Integer> commitedTable;
	public HashMap<String, Integer> commitedTime;
	public HashMap<String, ArrayList<Lock>> waitingLocks;
	public HashMap<String, ArrayList<Lock>> currentLocks;
	DataManagerStatus dataManagerStatus;
	public int seedVal;
	
	/**
	 * 
	 * @param seedVal
	 * Initialises a Data Site in accordance with the SeedVal
	 */
	public DataManager(int seedVal) {
		commitedTable = new HashMap<>();
		commitedTime = new HashMap<>();
		waitingLocks = new HashMap<>();
		currentLocks = new HashMap<>();
		this.seedVal = seedVal;
		
		for(int i = 1; i <= 20; ++i) {
			if(i % 2 == 0) {
				String variableName = "x" + i;
				commitedTable.put(variableName, 10*i);
				commitedTime.put(variableName, 0);
			}else if(seedVal == 1 + (i % 10)) {
				String variableName = "x" + i;
				commitedTable.put(variableName, 10*i);
				commitedTime.put(variableName, 0);
			}
		}
		dataManagerStatus = DataManagerStatus.WORKING;
	}
	
	private boolean alreadyHasWriteLock(ArrayList<Lock> listOfLocks, Transaction transaction) {
		//Checks if the given Transaction Already has a Lock
		
		for(Lock lock : listOfLocks) {
			if(lock.transaction == transaction) return true;
		}
		
		return false;
	}
	/**
	 * 
	 * @param transaction
	 * @param variableName
	 * @return true if the transaction is able to acquire a read lock on the variable Name. False if the transaction is unable to acquire a ReadLock
	 * If Read Lock is not acquired, the trasnaction is wait Listed.
	 */
	public boolean acquireReadLock(Transaction transaction, String variableName) {
		if(currentLocks.getOrDefault(variableName, new ArrayList<>()).size() > 0) {
			ArrayList<Lock> listOfLocks = currentLocks.get(variableName);
			//If a Given Transaction already has Write Lock :
			if(alreadyHasWriteLock(listOfLocks, transaction)) {
				Lock newLock = new Lock(transaction, variableName, -1, this, LockType.READ_LOCK, LockStatus.LOCK_ACQUIRED);
				listOfLocks.add(newLock);
				transaction.lockList.add(newLock);
				return true;
			}
			//Else Check if it already has a read lock :
			boolean allLocksReadLocks = true;
			for(Lock itrLock : listOfLocks) {
				if(itrLock.lockType != LockType.READ_LOCK) {
					allLocksReadLocks = false;
				}
				if(itrLock.transaction.transactionName.equals(transaction.transactionName)) {
					//Grant the Request : 
					Lock newLock = new Lock(transaction, variableName, -1, this, LockType.READ_LOCK, LockStatus.LOCK_ACQUIRED);
					listOfLocks.add(newLock);
					transaction.lockList.add(newLock);
					return true;
				}
			}
			//Else Check if all the holds are Read Locks and there is nothing in the listOfLocks 
			if(allLocksReadLocks) {
				ArrayList<Lock> waitingLockList = waitingLocks.getOrDefault(variableName, new ArrayList<>());
				if(waitingLockList.size() == 0) {
					//Grant the Request : 
					Lock newLock = new Lock(transaction, variableName, -1, this, LockType.READ_LOCK, LockStatus.LOCK_ACQUIRED);
					listOfLocks.add(newLock);
					transaction.lockList.add(newLock);
					return true;
				}
			}
		}else if(waitingLocks.getOrDefault(variableName, new ArrayList<>()).size() == 0) {
			//Grant the Request : 
			Lock newLock = new Lock(transaction, variableName, -1, this, LockType.READ_LOCK, LockStatus.LOCK_ACQUIRED);
			ArrayList<Lock> itrLock = currentLocks.getOrDefault(variableName, new ArrayList<>());
			itrLock.add(newLock);
			currentLocks.put(variableName, itrLock);
			transaction.lockList.add(newLock);
			return true;
		}
		//Get in the Waiting : 
		Lock newLock = new Lock(transaction, variableName, -1, this, LockType.READ_LOCK, LockStatus.LOCK_WAITING);
		ArrayList<Lock> tmpLock = waitingLocks.getOrDefault(variableName, new ArrayList<>());
		tmpLock.add(newLock);
		waitingLocks.put(variableName, tmpLock);
		transaction.lockList.add(newLock);
		transaction.transactionStatus = TransactionStatus.WAITING;
		return false;
	}
	/**
	 * 
	 * @param transaction
	 * @param variableName
	 * @return true if a transaction can get a Write Lock. false if a trasnaction cannot get a Write Lock.
	 */
	public boolean checkWriteLock(Transaction transaction, String variableName) {
		ArrayList<Lock> currentLockList = currentLocks.getOrDefault(variableName, new ArrayList<>());
		ArrayList<Lock> currentWaitList = waitingLocks.getOrDefault(variableName, new ArrayList<>());
		for(Lock lock : currentLockList) {
			if(lock.transaction.transactionName.equals(transaction.transactionName)) {
				if(lock.lockType == LockType.WRITE_LOCK) {
					return true;
				}
			}
		}
		if(currentWaitList.size() == 0 && currentLockList.size() == 0) {
			return true;
		}
		//Already Have Read Lock : 
		if(currentWaitList.size() == 0) {
			for(Lock lock : currentLockList) {
				if(lock.transaction.transactionName.equals(transaction.transactionName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param transaction : The transaction that wants to acquire Lock.
	 * @param variableName : The name of the variable on which Write Lock is requested.
	 * @param variableValue : The Value that the Transaction wants to write to the Variable.
	 * @return true signifying that a transaction acquires a Write Log for a Variable Name.
	 */
	public boolean acquireWriteLock(Transaction transaction, String variableName, int variableValue) {
		Lock newLock = new Lock(transaction, variableName, variableValue, this, LockType.WRITE_LOCK, LockStatus.LOCK_ACQUIRED);
		ArrayList<Lock> currentLockList = currentLocks.getOrDefault(variableName, new ArrayList<>());
		currentLockList.add(newLock);
		currentLocks.put(variableName, currentLockList);
		transaction.lockList.add(newLock);
		return true;
	}

	/**
	 * 
	 * @param transaction : The transaction that wants to acquire Lock.
	 * @param variableName : The name of the variable on which Write Lock is requested.
	 * @param variableValue : The Value that the Transaction wants to write to the Variable.
	 * @return true signifying that a transaction is in the Waitlist for the variable.
	 */
	public boolean waitListForWriteLock(Transaction transaction, String variableName, int variableValue) {
		Lock newLock = new Lock(transaction, variableName, variableValue, this, LockType.WRITE_LOCK, LockStatus.LOCK_WAITING);
		ArrayList<Lock> waitingLockList = waitingLocks.getOrDefault(variableName, new ArrayList<>());
		waitingLockList.add(newLock);
		waitingLocks.put(variableName, waitingLockList);
		transaction.lockList.add(newLock);
		transaction.transactionStatus = TransactionStatus.WAITING;
		return true;
	}
	
	/**
	 * Converts the Site to a corresponding String value
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i <= 20 ; i++) {
			String variableName = "x" + i;
			if(commitedTable.containsKey(variableName)) {
				sb.append(variableName+": ");
				sb.append(commitedTable.get(variableName));
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param transaction : The transaction whose locks will the removed.
	 * @param variableName : The name of the Variable for which the Transactions would be removed.
	 * @return true signifying that all the locks[including the WaitListed ones] corresponding to the transaction and a variableName will be removed.
	 */
	public boolean removeLock(Transaction transaction, String variableName) {
		//Iterate through all the Locks and find which locks correspond to the given Site
		// Remove them
		
		ArrayList<Lock> tmpLockList = this.currentLocks.getOrDefault(variableName, new ArrayList<>());
		Stack<Integer> stack = new Stack<>();
		for(int i = 0; i < tmpLockList.size(); i++) {
			Lock lock  =tmpLockList.get(i);
			if(lock.transaction == transaction) {
				stack.push(i);
			}
		}
		while(!stack.isEmpty()) {
			int index = stack.pop();
			tmpLockList.remove(index);
		}
		this.currentLocks.put(variableName, tmpLockList);
		tmpLockList = this.waitingLocks.getOrDefault(variableName, new ArrayList<>());
		stack = new Stack<>();
		for(int i = 0; i < tmpLockList.size(); i++) {
			Lock lock  =tmpLockList.get(i);
			if(lock.transaction == transaction) {
				stack.push(i);
			}
		}
		while(!stack.isEmpty()) {
			int index = stack.pop();
			tmpLockList.remove(index);
		}
		this.waitingLocks.put(variableName, tmpLockList);
		return true;
	}
	/**
	 * 
	 * @param variableName : The Variable whose value will be commited
	 * @param variableValue : The value that will be written.
	 * @param commitTime : The time at which Transaction is being committed.
	 * @return @true signifying that the value has been committed.
	 */
	public boolean commitValue(String variableName, int variableValue, int commitTime) {
		commitedTable.put(variableName, variableValue);
		commitedTime.put(variableName, commitTime);
		Solution.logger.log("Writing Value : " + variableName + "." + this.seedVal);
		return true;
	}
	
	/**
	 * 
	 * @param lock : The lock that will acquire the Variable Now.
	 * This lock was in Waiting Queue but now would be able to fetch the value.
	 */
	public void acquireWaitingLock(Lock lock) {
		// TODO Auto-generated method stub
		// 1. Remove from Waiting List
		// 2. Add to Current List
		// 3. Update the Status of Lock
		String variableName = lock.variableName;
		ArrayList<Lock> waitingLockList = waitingLocks.get(variableName);
		for(int i = 0; i < waitingLockList.size(); i++) {
			if(waitingLockList.get(i).lockID == lock.lockID) {
				waitingLockList.remove(i);
				break;
			}
		}
		ArrayList<Lock> currentLockList = this.currentLocks.getOrDefault(variableName,  new ArrayList<>());
		currentLockList.add(lock);
		this.currentLocks.put(variableName, currentLockList);
		lock.lockStatus = LockStatus.LOCK_ACQUIRED;
	}
	
}
