/*
 * 
 * Author : Anand Kumar [ak8288]
 * Creation Date : 22nd November 2022
 * Last Modification Date:	3rd December 2022
 * 
 * Description : 
 * 
 * 
 */

import java.util.*;

public class TransactionManager {
	
	private static int currentTime;
	private CommandManager commandManager;
	public DataManager[] sites;
	public HashMap<String, Transaction> transactionDetails;
	public HashMap<Integer, Integer> lastUpTime;
	public ArrayList<Command> waitListedCommands;
	
	public TransactionManager() {
		this.commandManager = new CommandManager();
		this.transactionDetails = new HashMap<>();
		lastUpTime = new HashMap<>();
		waitListedCommands = new ArrayList<>();
		currentTime = 0;
	}
	
	/**
	 * Initialises the Data Sites.
	 */
	public void initialiseDataBase() {
		//Send a SeedValue and all the Sites will manage the creation of DataBases : 
		sites = new DataManager[11];
		for(int i = 1; i <= 10; ++i) {
			sites[i] = new DataManager(i);
			lastUpTime.put(i,  0);
		}
	}
	
	/**
	 * 
	 * @return true if there are any Active/Live/Waiting Transactions
	 */
	private boolean hasActiveTransaction() {
		int count = 0;
		for(String transactionName : this.transactionDetails.keySet()) {
			Transaction transaction = this.transactionDetails.get(transactionName);
			if(transaction.transactionStatus == TransactionStatus.COMMITED) continue;
			if(transaction.transactionStatus == TransactionStatus.ABORTED) continue;
			count++;
		}
		return count!=0;
	}
	
	/**
	 * 
	 * @return @true if there is a Deadlock. @false Otherwise.
	 */
	private boolean hasDeadLock() {
		if(!hasActiveTransaction()) return false;
		ArrayList<Transaction> listOfActiveTransaction = getReadyTransactions();
		if(listOfActiveTransaction.size() == 0) return true;
		return false;
	}
	
	/**
	 * 
	 * @return the Youngest Transaction to be killed to get rid of DeadLock
	 */
	private Transaction getYoungestTransaction() {
		Transaction transaction = null;
		int timeOfStart = -1;
		for(String transactionName : this.transactionDetails.keySet()) {
			Transaction tmp = this.transactionDetails.get(transactionName);
			if(tmp.creationTime > timeOfStart) {
				timeOfStart = tmp.creationTime;
				transaction = tmp;
			}
		}
		return transaction;
	}
	
	/**
	 * 
	 * @param inputCommand : Takes the input Command from CLI/File and Executes it accordingly.
	 * @param currentTime : The currentTime
	 */
	public void executeCommand(String inputCommand, int currentTime) {
		currentTime++;
		//Check here for DeadLock Detection and Abort the youngest Process : 
		while(hasDeadLock()) {
			Transaction transaction = getYoungestTransaction();
			abortTransaction(transaction);
			transaction.transactionStatus = TransactionStatus.ABORTED;
			Solution.logger.log("Aborting Transaction : " + transaction.transactionName);
		}
		Command newCommand = commandManager.getCommand(inputCommand);
		newCommand.executeCommand(this, currentTime);
	}
	
	/**
	 * 
	 * @param transaction : Removes all the Locks on all Sites related to a Given Transaction
	 */
	public void abortTransaction(Transaction transaction) {
		for(Lock lock : transaction.lockList) {
			lock.dataManager.removeLock(transaction, lock.variableName);
		}
	}
	
	/**
	 * 
	 * @param transaction
	 * Commits all the Values to be written by the Transaction
	 */
	public void commitTransaction(Transaction transaction) {
		// Basically, replicate the Variables on which I have got the Locks
		// To be in this state, I should have all the Locks :
		for(Lock lock : transaction.lockList) {
			if(lock.lockType == LockType.WRITE_LOCK) {
				// Write the value of the variable on the Server : 
				// It should be the responsibility of the DataManager to replicate the value:
				lock.dataManager.commitValue(lock.variableName, lock.variableValue, currentTime);
			}
		}
		
	}
	
	/**
	 * 
	 * @return the @list of Transactions that can be excuted Next and they are not waiting on any other Transaction.
	 */
	private ArrayList<Transaction> getReadyTransactions(){
		ArrayList<Transaction> res = new ArrayList<>();
		HashMap<String, HashSet<String>> adjList = new HashMap<>();
		
		//Fetch every Transaction that is not Aborted/Completed :
		for(String transactionName : this.transactionDetails.keySet()) {
			Transaction transaction = this.transactionDetails.get(transactionName);
			if(transaction.transactionStatus == TransactionStatus.ABORTED || transaction.transactionStatus == TransactionStatus.COMMITED) continue;
			adjList.put(transactionName, new HashSet<>());
		}
		
		// Now, Iterate through all the Sites to Check the Condition for 
		for(int i = 1; i < this.sites.length; i++) {
			DataManager currentSite = sites[i];
			// THe currentOnes do not wait for ANy :
			// The Waiting List is required
			for(String variableName : currentSite.waitingLocks.keySet()) {
				ArrayList<Lock> currentLock = currentSite.currentLocks.getOrDefault(variableName, new ArrayList<>());
				HashSet<String> waitingTransaction = new HashSet<>();
				//Beware of Duplicates
				for(Lock lock : currentLock) {
					waitingTransaction.add(lock.transaction.transactionName);
				}
				ArrayList<Lock> waitingLock = currentSite.waitingLocks.getOrDefault(variableName, new ArrayList<>());
				for(int j = 0; j < waitingLock.size(); j++) {
					//Go through all the transaction
					String currentTransactionName = waitingLock.get(j).transaction.transactionName;
					for(String str : waitingTransaction) {
						adjList.get(currentTransactionName).add(str);
					}
					waitingTransaction.add(currentTransactionName);
				}
			}
		}
		
		//Remove Duplicate ENtries/ Self-Loops
		for(String tName :adjList.keySet()) {
			HashSet<String> tmpSet = adjList.get(tName);
			if(tmpSet.contains(tName)) {
				tmpSet.remove(tName);
			}
		}
		
		
		//Check which have Empty Entries : 
		
		for(String transactionName : adjList.keySet()) {
			HashSet<String> tmpSet = adjList.get(transactionName);
			if(tmpSet.isEmpty()) {
				res.add(this.transactionDetails.get(transactionName));
			}
		}
		
		return res;
	}
	
	/**
	 * Checks if a Waiting Transaction[Due to Site Down] can be made to run.
	 */
	public void enqueueNextWaitingTransaction() {
		//There can be multiple Transactions that can be started :
		ArrayList<Transaction> readyTransaction = getReadyTransactions();
		for(int i = 0; i < readyTransaction.size(); i++) {
			Transaction currTransaction = readyTransaction.get(i);
			//Iterate over the Lock List and see which of them are not acquired :
			for(Lock lock : currTransaction.lockList) {
				if(lock.lockStatus == LockStatus.LOCK_WAITING) {
					lock.dataManager.acquireWaitingLock(lock);
					if(lock.lockType == LockType.READ_LOCK) {
						Solution.logger.log(lock.variableName + ": "+lock.dataManager.commitedTable.get(lock.variableName));
					}
				}
			}
		}
	}
	
	
}
