import java.util.*;

abstract public class Command {
	
	
	
	public boolean isSingleReplicated(String variableNumber) {
		String num = variableNumber.substring(1);
		int val = Integer.parseInt(num);
		if(val % 2 != 0) return true;
		return false;
	}
	public abstract void executeCommand(TransactionManager transactionManager, int currentTime);
}

class BeginCommand extends Command{
	private String transactionName;
	
	public BeginCommand(String transactionName) {
		this.transactionName = transactionName;
	}
	
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		Transaction newTransaction = new Transaction(this.transactionName, TransactionType.READ_AND_WRITE);
		transactionManager.transactionDetails.put(transactionName, newTransaction);
	}
	
}

class BeginROCommand extends Command{
	
	private String transactionName;
	
	public BeginROCommand(String transactionName) {
		this.transactionName = transactionName;
	}
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		Transaction newTransaction = new Transaction(this.transactionName, TransactionType.READ_ONLY);
		transactionManager.transactionDetails.put(transactionName, newTransaction);
		
		for(int variableIndex = 1; variableIndex <= 20; ++variableIndex) {
			String variableName = "x" + variableIndex;
			
			for(int siteIndex = 1; siteIndex <= 10; ++siteIndex) {
				DataManager dataManager = transactionManager.sites[siteIndex];
				if(dataManager.dataManagerStatus == DataManagerStatus.FAILED) continue;
				if(!dataManager.commitedTable.containsKey(variableName)) continue;
				if(isSingleReplicated(variableName)) {
					newTransaction.variableValues.put(variableName, dataManager.commitedTable.get(variableName));
					break;
				}
				int lastRecoveredTime = transactionManager.lastUpTime.get(siteIndex);
				if(lastRecoveredTime < dataManager.commitedTable.get(variableName)) {
					newTransaction.variableValues.put(variableName, dataManager.commitedTable.get(variableName));
					break;
				}
			}
			
		}
	}
	
}

class ReadCommand extends Command{
	public String transactionName;
	public String variableName;
	
	public ReadCommand(String transactionName, String variableName) {
		this.transactionName = transactionName;
		this.variableName = variableName;
	}
	
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		Transaction transaction = transactionManager.transactionDetails.get(this.transactionName);
		if(transaction.transactionStatus == TransactionStatus.ABORTED) return;
		
		if(transaction.transactionType == TransactionType.READ_ONLY) {
			if(transaction.variableValues.containsKey(this.variableName)) {
				Solution.logger.log(this.variableName + ": " + transaction.variableValues.get(this.variableName));
			}else {
				if(isSingleReplicated(this.variableName)) {
					transaction.transactionStatus = TransactionStatus.WAITING;
					transactionManager.waitListedCommands.add(this);
					Solution.logger.log("Transaction Waiting Due to Site Down : " + transaction.transactionName);
				}else {
					transaction.transactionStatus = TransactionStatus.ABORTED;
					Solution.logger.log("Transaction Aborted : " + transaction.transactionName);
					transactionManager.abortTransaction(transaction);
					
				}
			}
			return;
		}
		
		boolean isAdded = false;
		for(int siteIndex = 1; siteIndex <= 10; siteIndex++) {
			DataManager dataManager = transactionManager.sites[siteIndex];
			if(dataManager.dataManagerStatus == DataManagerStatus.FAILED) continue;
			if(!dataManager.commitedTable.containsKey(variableName)) continue;
			if(!isSingleReplicated(variableName)) {
				int lastCommitedTime = dataManager.commitedTime.get(variableName);
				int lastUpTime = transactionManager.lastUpTime.get(siteIndex);
				if(lastUpTime > lastCommitedTime) continue;
			}
			boolean getLock = dataManager.acquireReadLock(transaction, this.variableName);
			if(!getLock) {
				transaction.transactionStatus = TransactionStatus.WAITING;
				Solution.logger.log("Transaction Waiting on Read Lock : " + transaction.transactionName);
			}else {
				Solution.logger.log(this.variableName  + ": " + dataManager.commitedTable.get(this.variableName));
			}
			isAdded = true;
			break;	//Read From one site only
		}
		if(!isAdded) {
				transaction.transactionStatus = TransactionStatus.WAITING;
				transactionManager.waitListedCommands.add(this);
				Solution.logger.log("Transaction Waiting Due to Site Down : " + transaction.transactionName);
		}
	}
	
}

class WriteCommand extends Command{
	public String transactionName;
	public String variableName;
	public int variableValue;
	
	public WriteCommand(String transactionName, String variableName, String variableValue) {
		this.transactionName = transactionName;
		this.variableName = variableName;
		this.variableValue = Integer.parseInt(variableValue);
	}
	
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		// TODO Auto-generated method stub
		
		Transaction transaction = transactionManager.transactionDetails.get(this.transactionName);
		if(transaction.transactionStatus == TransactionStatus.ABORTED) return;
		if(transaction.transactionStatus == TransactionStatus.WAITING) return;
		
		ArrayList<DataManager> validDataManagers = new ArrayList<>();
		for(int siteIndex = 1; siteIndex <= 10; siteIndex++) {
			DataManager dataManager = transactionManager.sites[siteIndex];
			if(dataManager.dataManagerStatus == DataManagerStatus.FAILED) continue;
			if(!dataManager.commitedTable.containsKey(this.variableName)) continue;
			validDataManagers.add(dataManager);
		}
		
		if(validDataManagers.size() == 0) {
			transactionManager.waitListedCommands.add(this);
			transaction.transactionStatus = TransactionStatus.WAITING;
			Solution.logger.log("No Site Avialable for writing for Transaction : " + transaction.transactionName);
			return;
		}
		
		boolean canGetLock = true;
		for(int i = 0; i < validDataManagers.size(); i++) {
			if(!validDataManagers.get(i).checkWriteLock(transaction, variableName)) {
				canGetLock = false;
				break;
			}
		}
		for(int i = 0; i < validDataManagers.size(); i++) {
			if(canGetLock) {
				validDataManagers.get(i).acquireWriteLock(transaction, variableName, this.variableValue);
				transaction.transactionStatus = TransactionStatus.LIVE;
			}else {
				validDataManagers.get(i).waitListForWriteLock(transaction, variableName, this.variableValue);
				transaction.transactionStatus = TransactionStatus.WAITING;
			}
		}
		if(!canGetLock) {
			Solution.logger.log("Lock Conflict for Transaction : " + transaction.transactionName);
		}
		
	}
	
}

class DumpCommand extends Command{
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		// TODO Auto-generated method stub
		for(int i = 1; i < transactionManager.sites.length; i++) {
			Solution.logger.log("site "+i+" - " + transactionManager.sites[i]);
		}
	}
}

class EndCommand extends Command{
	private String transactionName;
	
	public EndCommand(String transactionName) {
		this.transactionName = transactionName;
	}
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		Transaction transaction = transactionManager.transactionDetails.get(this.transactionName);
		if(transaction.transactionType == TransactionType.READ_ONLY) {
			if(transaction.transactionStatus != TransactionStatus.ABORTED && transaction.transactionStatus != TransactionStatus.WAITING) {
				transaction.transactionStatus = TransactionStatus.COMMITED;
				Solution.logger.log("Read Only Transaction Ends (Commited) : " + transaction.transactionName);
			}else {
				Solution.logger.log("Read Only Transaction Ends (Aborted) : " + transaction.transactionName);
			}
			return;
		}
		
		if(transaction.transactionStatus != TransactionStatus.ABORTED) {
			HashMap<Integer, Integer> siteUpTime = transactionManager.lastUpTime;
			HashMap<Integer, Integer> transactionAcquiredTime = transaction.timeHoldUp;
			
			for(int serverID : transactionAcquiredTime.keySet()) {
				if(siteUpTime.get(serverID) > transactionAcquiredTime.get(serverID)) {
					//Abort Transaction : 
					transaction.transactionStatus = TransactionStatus.ABORTED;
					break;
				}
			}
			if(transaction.transactionStatus != TransactionStatus.ABORTED) {
				transactionManager.commitTransaction(transaction);
				transaction.transactionStatus = TransactionStatus.COMMITED;
			}
		}
		
		if(transaction.transactionStatus == TransactionStatus.COMMITED) {
			Solution.logger.log("Transaction Committed : " + transaction.transactionName);
		}else {
			Solution.logger.log("Transaction Aborted : " + transaction.transactionName);
		}
		transactionManager.abortTransaction(transaction);
		transactionManager.enqueueNextWaitingTransaction();
	}
}

class FailCommand extends Command{
	private int siteNumber;
	
	public FailCommand(String siteNum) {
		this.siteNumber = Integer.parseInt(siteNum);
	}
	
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		DataManager failedDataManager = transactionManager.sites[this.siteNumber];
		failedDataManager.dataManagerStatus = DataManagerStatus.FAILED;
		HashSet<String> transactionNames = new HashSet<>();
		
		for(String variableName : failedDataManager.currentLocks.keySet()) {
			for(Lock lock : failedDataManager.currentLocks.getOrDefault(variableName, new ArrayList<>())){
				transactionNames.add(lock.transaction.transactionName);
			}
		}
		
		for(String variableName : failedDataManager.waitingLocks.keySet()) {
			for(Lock lock : failedDataManager.waitingLocks.getOrDefault(variableName, new ArrayList<>())){
				transactionNames.add(lock.transaction.transactionName);
			}
		}
		
		for(String transactionName : transactionNames) {
			Transaction transaction = transactionManager.transactionDetails.get(transactionName);
			if(transaction.transactionStatus == TransactionStatus.COMMITED) continue;
			if(transaction.transactionStatus == TransactionStatus.ABORTED) continue;
			transactionManager.abortTransaction(transaction);
			Solution.logger.log("Aborting Transaction : " + transaction.transactionName);
			transaction.transactionStatus = TransactionStatus.ABORTED;
		}
		
		failedDataManager.currentLocks = new HashMap<>();
		failedDataManager.waitingLocks = new HashMap<>();
		transactionManager.enqueueNextWaitingTransaction();
	}
}

class RecoverCommand extends Command{
	private int siteNumber;
	
	public RecoverCommand(String siteNum) {
		this.siteNumber = Integer.parseInt(siteNum);
	}
	
	@Override
	public void executeCommand(TransactionManager transactionManager, int currentTime) {
		DataManager recoveredSite = transactionManager.sites[siteNumber];
		recoveredSite.dataManagerStatus = DataManagerStatus.WORKING;
		transactionManager.lastUpTime.put(siteNumber, currentTime);
		
		Stack<Integer> deleteIndexes = new Stack<>();
		for(int i = 0; i < transactionManager.waitListedCommands.size(); i++) {
			Command myCommand = transactionManager.waitListedCommands.get(i);
			if(myCommand instanceof ReadCommand) {
				ReadCommand tmpCommand = (ReadCommand)myCommand;
				if(transactionManager.transactionDetails.get(tmpCommand.transactionName).transactionStatus == TransactionStatus.ABORTED)continue;
				if(!recoveredSite.commitedTable.containsKey(tmpCommand.variableName))continue;
				transactionManager.transactionDetails.get(tmpCommand.transactionName).transactionStatus = TransactionStatus.LIVE;
				tmpCommand.executeCommand(transactionManager, currentTime);
				deleteIndexes.push(i);
			}else if(myCommand instanceof WriteCommand) {
				WriteCommand tmpCommand = (WriteCommand)myCommand;
				if(transactionManager.transactionDetails.get(tmpCommand.transactionName).transactionStatus == TransactionStatus.ABORTED)continue;
				if(!recoveredSite.commitedTable.containsKey(tmpCommand.variableName))continue;
				transactionManager.transactionDetails.get(tmpCommand.transactionName).transactionStatus = TransactionStatus.LIVE;
				tmpCommand.executeCommand(transactionManager, currentTime);
				deleteIndexes.push(i);
			}
		}
		
		while(!deleteIndexes.isEmpty()) {
			int index = deleteIndexes.pop();
			transactionManager.waitListedCommands.remove(index);
		}
		
	}
}
