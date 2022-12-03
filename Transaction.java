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
// Contains the information of a Transaction : 

enum TransactionStatus{
	ABORTED,
	LIVE,
	COMMITED,
	WAITING
};

enum TransactionType{
	READ_ONLY,
	READ_AND_WRITE
};

public class Transaction {
	
	public int creationTime;
	public String transactionName;
	private static int transactionCount;
	public TransactionStatus transactionStatus;
	public TransactionType transactionType;
	public HashMap<String, Integer> variableValues;
	public HashMap<Integer, Integer> timeHoldUp;
	public ArrayList<Lock> lockList;
	
	public Transaction(String transactionName, TransactionType transactionType) {
		this.transactionName = transactionName;
		this.creationTime = transactionCount;
		transactionCount++;	
		this.transactionType = transactionType;
		transactionStatus = TransactionStatus.LIVE;
		this.variableValues = new HashMap<>();
		this.timeHoldUp = new HashMap<>();
		this.lockList = new ArrayList<>();
	}
	
}




