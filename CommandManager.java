import java.io.*;
import java.util.Arrays;

//	Takes an input String and convert it into a 
// 	proper Command Object


public class CommandManager {
	//Takes an input String and converts it into a Command
	
	public Command getCommand(String inputString) {
		inputString = inputString.trim();
		int firstParenthesisIndex = inputString.indexOf('(');
		String commandName = inputString.substring(0, firstParenthesisIndex);
		if(commandName.equals("begin")) {
			String transactionName = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			return new BeginCommand(transactionName.trim());
		}else if(commandName.equals("beginRO")) {
			String transactionName = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			return new BeginROCommand(transactionName.trim());
		}else if(commandName.equals("R")) {
			String argumentList = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			String[] splitString = argumentList.split(",");
			return new ReadCommand(splitString[0].trim(), splitString[1].trim());
		}else if(commandName.equals("W")) {
			String argumentList = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			String[] splitString = argumentList.split(",");
			return new WriteCommand(splitString[0].trim(), splitString[1].trim(), splitString[2].trim());
		}else if(commandName.equals("dump")) {
			return new DumpCommand();
		}else if(commandName.equals("end")) {
			String transactionName = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			return new EndCommand(transactionName.trim());
		}else if(commandName.equals("fail")) {
			String stateName = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			return new FailCommand(stateName.trim());
		}else if(commandName.equals("recover")) {
			String stateName = inputString.substring(firstParenthesisIndex + 1, inputString.length() - 1);
			return new RecoverCommand(stateName.trim());
		}
		
		return null;
	}
}
