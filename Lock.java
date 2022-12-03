/*
 * 
 * Author : Devesh Devendra[dd3053]
 * Creation Date : 22nd November 2022
 * Last Modification Date:	3rd December 2022
 * 
 * Description : 
 * 
 * 
 */

enum LockType{
	READ_LOCK,
	WRITE_LOCK
}

enum LockStatus{
	LOCK_ACQUIRED,
	LOCK_WAITING
}

public class Lock {
	public int lockID;
	public LockType lockType;
	public LockStatus lockStatus;
	private static int lockCount;
	
	public Transaction transaction;
	public String variableName;
	public int variableValue;
	public DataManager dataManager;
	
	public Lock(Transaction transaction, String variableName, int variableValue, DataManager dataManager, LockType lockType, LockStatus lockStatus) {
		lockCount = 0;
		this.lockID = lockCount;
		lockCount++;
		this.lockType = lockType;
		this.transaction = transaction;
		this.variableName = variableName;
		this.variableValue = variableValue;
		this.dataManager = dataManager;
		this.lockType = lockType;
		this.lockStatus = lockStatus;
	}
	
}
