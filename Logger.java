/*
 * 
 * Author : Devesh Devendra [dd3053]
 * Creation Date : 22nd November 2022
 * Last Modification Date:	3rd December 2022
 * 
 * Description : 
 * Provides the Flexibility to either Log the details on the Standard Output[Console] or a File[as per the provided Command Line Argument]. 
 * 
 */
import java.util.*;
import java.io.*;

public class Logger {
	public boolean isFileMode;
	private PrintStream out;
	
	public Logger(String fileName) {
		try {
			out = new PrintStream(new FileOutputStream(fileName));
			isFileMode = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Output File is Invalid. Continue in Standard Output Mode");
			isFileMode = false;
		}
	}
	
	public Logger() {
		isFileMode = false;
	}
	/**
	 * 
	 * @param str : The logs that needs to be printed or written to a File.
	 */
	public void log(String str) {
		if(isFileMode) {
			out.println(str);
		}else {
			System.out.println(str);
		}
	}
	
}
