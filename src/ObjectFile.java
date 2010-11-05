import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;




public class ObjectFile implements ObjectFileInterface {

	// member variables
	boolean inDebugMode = false;
	
	
	//constructor
	public void ObjectFile (){
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ObjectFileInterface#outputObjectFile(IntermediateFile, SymbolTable, int)
	 * create the object file
	 */
	@Override
	public void outputObjectFile(File objectFileName,
			SymbolTable symbolTable, int locationCounter, IntermediateFile intermediateFile) 
	{
		System.out.println(">>>>>>>>>>>>> 		Outputting the object file.");
		
		/*
		 * reference the intermediate file and symbol table
		 *
		 * parse one line at a time
		 * output the tables in order as you parse them
		 * 
		 * if there is a '[' in the line then there is a symbol in it
		 * get the index of the'[' and the ']' to get the substring params of the symbol name
		 * then look up the symbol name in the symbol table to get its value 
		 * remove the symbol and replace with the data
		 * 
		 * check every line for the string "debug" if you find debug then set the member variable flag ie (inDebugMode)
		 * this is based on the debug value given ie 1 is true and 0 is false
		 * 
		 */
		
		//output header file 
		printHeaderRecord(objectFileName,symbolTable, locationCounter, intermediateFile);
		
		int i = 0;
		//output the lining or text record based in the input of the line
		while (intermediateFile.binCode.size() > i)
		{
			
			//determine which record to make
			if
			printLinkingRecord(objectFileName,symbolTable, locationCounter, intermediateFile);
		
			printTextRecord(objectFileName,symbolTable, locationCounter, intermediateFile);
		
		}
		
		//output the end record
		printEndRecord(objectFileName,symbolTable, locationCounter, intermediateFile);
		
		
		

	}
	
	
	
	//get program name form symbol table and other important data and then output it to the file, convert the bin data to hex
	private void printHeaderRecord(File objectFileName,
			SymbolTable symbolTable, int locationCounter, IntermediateFile intermediateFile) throws IOException
	{
		Converter converter = new Converter();
		
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(objectFileName)));
		
		//Get the program length
		int prgmLength = intermediateFile.binCode.size();
		
		//Convert it into a string
		String programLengthString = Integer.toString(prgmLength);
		
		//Convert it into hex
		programLengthString = converter.decimalToHex(programLengthString);
		
		//Extend it
		while(programLengthString.length() < 4)
		{
			programLengthString = "0" + programLengthString;
		}
		
		String programLengthHex = programLengthString;
		
		Symbol programStart
		
		String programLoadAddress = new String();
		
		String numLinkingRecords = new String();
		String numTextRecords = new String();
		String execStartAddress = new String();
		String version = "Version # 2.0";
		String revision = "Revision # 0";
		
		//Add functionality for this.
		Symbol programNameInfo = symbolTable.getSymbolGivenUsage("Program Name");
		
		//Get the label from the symbol, it's the programName
		String programName = programNameInfo.getLabel();
		
		//convert binary to hex
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd\t|\tHH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
		
	    
	    //lookup program name and hex data
		
        
        
        
        
        
		out.println("H\t|\t" + programName + "\t|\t" + programLengthHex + "\t|\t" + programLoadAddress + "\t|\t" + 
					format + "\t|\t" + numLinkingRecords + "\t|\t" + numTextRecords + "\t|\t" + execStartAddress + "\t|\t"
					+ "SAL\t|\t" + version + "\t|\t" + revision + "\t|\t" + programName);
	}

	//get data for a linking record and print it to the file, convert the bin data to hex
	private void printLinkingRecord(File objectFileName,
			SymbolTable symbolTable, int locationCounter, IntermediateFile intermediateFile) throws IOException
	{
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(objectFileName)));
	}
	
	//get the data for a text record and output to the file, convert the bin data to hex
	private void printTextRecord(File objectFileName,
			SymbolTable symbolTable, int locationCounter, IntermediateFile intermediateFile) throws IOException
	{
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(objectFileName)));
	}
	
	//get the data for a end record and output to the file, convert the bin data to hex
	private void printEndRecord(File objectFileName,
			SymbolTable symbolTable, int locationCounter, IntermediateFile intermediateFile) throws IOException
	{
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(objectFileName)));
	}
	
	
	
	
}
