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
import java.io.*;


public class Solution{
	public static Logger logger;
	
	/**
	 * 
	 * @param args : The Command Line Arguments
	 */
	public static void main(String[] args) {
		//Start with input from the user :
		logger = new Logger();
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.initialiseDataBase();
		Scanner scanner = new Scanner(System.in);
		int currentTime = 0;
		System.out.println("COunt of Args : " + args.length);
		if(args.length >= 1) {
			//Setup the Input File
			System.out.println("Input File : " + args[0]);
			File file = new File(args[0]);
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Input File Not Found .. Exiting System");
				System.exit(0);
			}
		}
		
		if(args.length >= 2) {
			System.out.println("Output File : " + args[1]);
			logger = new Logger(args[1]);
		}
		int testCase = 1;
		Solution.logger.log("===========================================");
		Solution.logger.log("TestCase : " + testCase);
		Solution.logger.log("===========================================");
		while(true) {
			String inputCommand = scanner.nextLine();
			if(inputCommand.equals("Exit")) break;
			if(inputCommand.length() == 0)continue;
			if(inputCommand.equals("nextTC()")) {
				testCase++;
				Solution.logger.log("===========================================");
				Solution.logger.log("TestCase : " + testCase);
				Solution.logger.log("===========================================");
				transactionManager = new TransactionManager();
				transactionManager.initialiseDataBase();
				currentTime = 0;
				continue;
			}
			if(Solution.logger.isFileMode) {
				Solution.logger.log(inputCommand);
			}
			currentTime++;
			transactionManager.executeCommand(inputCommand, currentTime);
		}
		
	}
	
}
