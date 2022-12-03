import java.util.*;
import java.io.*;

/*
 * 
 * Q: What is Waiting Queue,
 * When to pop out an element ?
 * 
 * Scenario 1 : A Normal Transaction Starts
 * -------------------------------------------
 * 1. Begin(T1) : Begin the Transaction
 * 2.1 Read Request Comes :
 * 		- I need to acquire a Read Lock :
 * 			- Iterate over the Sites :
 * 				-If not down and Variable is available :
 * 				- Try to Acquire a Read Lock
 * 				- If yes : Lock Acquired
 * 				- Otherwise : remain in the queue & update the Variable Status as WAITING
 * 			- If Unable to get any Site, Then add to Wait Queue
 * 
 * 2.2 Write Request Comes :
 * 		- Need to acquire locks over all Site where the Variables are present 
 * 		and Site is up. ->Keep the time at which Server is up
 * 		- Check if all the locks can be acquired
 * 		- If locks are acquired :
 * 			- Alright
 * 		- If not, Maintain the position in Queue
 
 * 
 * 3. end(Transaction)
 * 		- Check if all Sites are up on which the control was held
 * 		- Check the time at which they were held
 * 		- If invalid, then Abort []
 * 		- Commit The Values on all Sites
 *		
 * 4. Abort(Transaction)
 * 
 * 
 * 
 * 5.Commit a Transaction
 * 
 * Scenario 2 : A Read Only Transaction Starts
 * --------------------------------------------
 * 
 * 
 * 
 */

//Solution.logger.log
public class Solution{
	public static Logger logger;
	
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
			String inputCommand = scanner.next();
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
/*
class Flower {
	private String type;
	private String color;
	private int count;
	
	public Flower(String type, String color) {
		this.type = type;
		this.color = color;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getColor() {
		return this.color;
	}
	
}
public class Solution{
public static void main(String[] args) {
		
		double total = 0;
		
		Flower[] flowers = {new Flower("Rose", "red"), new Flower("Rose", "white"), 
								new Flower("Carnation", "pink"), new Flower("Daisy", "white"),
								new Flower("Daffodil", "yellow"), new Flower("Lily", "pink"),
								new Flower("Orchid", "purple"), new Flower("Tulip", "red")};
		double[][] inventory = {{5, 5.0}, {12, 6.2}, {3, 2.3}, {20, 1.2}, {13, 2.8}, {4, 5.4}, {2, 12.5}, {7, 4.5}};
		
		while(true) {
		System.out.println("Please pick an operation (1) View All (2) Pick flowers (3) Place order (4) Exit");
		
		Scanner input = new Scanner(System.in);
		int operation = input.nextInt();
		
		switch(operation) {
		case 1:
			System.out.println("Displaying all available fresh flowers today.");
			for (int i=0; i<flowers.length; i++) {
				System.out.println(flowers[i].getType() + ", " + flowers[i].getColor() + ": " 
									+ (int) inventory[i][0] + " available, with $" +  inventory[i][1] + " each.");
			}
			break;
		case 2:
			System.out.println("Please select which flower you would like to add in your bouquet: ");
			String flower = input.next();
			System.out.println("Please select which color: ");
			String color = input.next();
			double price = 0; int available = 0;
			try {
				int index = 0;
				int found = 0;
			for (int i=0; i<flowers.length; i++) {
				if (flower.equals(flowers[i].getType()) & color.equals(flowers[i].getColor())) {
					available = (int) inventory[i][0];
					price = inventory[i][1];
					index = i;
					found = 1;
				}	
			}
			if (found == 0) {
				throw new Exception("Flower not available");
			}
			
			System.out.println("Please select how many flowers you will be adding: ");
			int amount = input.nextInt();
			if (amount <= available ) {
				System.out.println("Added " + amount + " to your bouquet.");
				inventory[index][0] = inventory[index][0] - amount;
			}
			else {
				throw new Exception("Amount not available.");
			}
			total = total + amount*price;
			
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 3:
			System.out.println("Total price is " + total + ". Please proceed with entering your address.");
			Scanner input2 = new Scanner(System.in);
			String address = input2.nextLine();
			System.out.println("Order will be delivered tomorrow to " + address + ".");
			break;
		case 4:
			System.out.println("Exiting.");
			break;
		}
		
	}
	}
}*/
