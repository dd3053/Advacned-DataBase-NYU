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
	public static int selectMode;
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
		selectMode = 0;
		System.out.println("Enter the Number for the corresponding Mode : ");
		
		System.out.println("(1) Interactive Mode");
		System.out.println("(2) Input From File And Output to Command Line");
		System.out.println("(3) Input From File And Output to File");
		selectMode = scanner.nextInt();
		
		if(selectMode == 1) {
			// Interactive Mode
			// Nothing is Required
		}else if(selectMode == 2) {
			
			System.out.println("Input File : ");
			String inputFileName = scanner.next();
			File file = new File(inputFileName);
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Input File Not Found .. Exiting System");
				System.exit(0);
			}
			
		}else if(selectMode == 3) {
			
			System.out.println("Input File : ");
			String inputFileName = scanner.next();
			System.out.println("Output File : ");
			String outputFileName = scanner.next();
			File file = new File(inputFileName);
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Input File Not Found .. Exiting System");
				System.exit(0);
			}
			logger = new Logger(outputFileName);
			
		}else {
			System.out.println("Invalid Mode Provided");
			System.exit(0);
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
			if(selectMode != 1) {
				Solution.logger.log(inputCommand);
			}
			currentTime++;
			transactionManager.executeCommand(inputCommand, currentTime);
		}
		
	}
	
}
