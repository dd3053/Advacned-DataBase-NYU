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
	
	public void log(String str) {
		if(isFileMode) {
			out.println(str);
		}else {
			System.out.println(str);
		}
	}
	
}
