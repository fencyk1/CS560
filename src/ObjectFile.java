import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;




public class ObjectFile implements ObjectFileInterface {

	// member variables
	boolean inDebugMode;
	String headerLine;
	String endLine;
	String prgmName;
	ArrayList<String> textLines;
	ArrayList<String> linkerLines;
	
	//constructor
	public ObjectFile ()
	{	
		this.inDebugMode = false;
		this.headerLine = new String();
		this.endLine = new String();
		this.textLines = new ArrayList<String>();
		this.linkerLines = new ArrayList<String>();
		this.prgmName = new String();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ObjectFileInterface#outputObjectFile(IntermediateFile, SymbolTable, int)
	 * create the object file
	 */
	@Override
	public void outputObjectFile(File objectFileName,
			SymbolTable symbolTable,   IntermediateFile intermediateFile) throws IOException 
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
		
		//Set the program name
		this.prgmName = symbolTable.getSymbolGivenUsage("Program Name").getLabel();
		symbolTable.resetSymbolSearch();
		
		//output header file 
		printHeaderRecord(objectFileName, symbolTable,   intermediateFile);
		
		int i = 0;
		//output the text record
		while (intermediateFile.binCode.size() > i)
		{
			printTextRecord(objectFileName, symbolTable,  
					intermediateFile, intermediateFile.binCode.get(i), i+1);
			i++;
		}
		
		Symbol entOrStart = symbolTable.getSymbolGivenUsage("Program Name");
		
		//output the linking record
		while (!entOrStart.getValue().equalsIgnoreCase("N/A"))
		{
			printLinkingRecord(objectFileName, symbolTable,   intermediateFile, entOrStart);
			entOrStart = symbolTable.getSymbolGivenUsage("ent");
			
		}
		
		//Reset the symbol searching variable
		symbolTable.resetSymbolSearch();
		
		//Remove all the ents in the symbolTable
		symbolTable.removeEnts();
		
		//output header file 
		printHeaderRecord(objectFileName, symbolTable,   intermediateFile);
		
		//output the end record
		printEndRecord(objectFileName, symbolTable,   intermediateFile);
		
		outputTheObjectFile(objectFileName);
		

	}
	
	
	
	//get program name form symbol table and other important data and then output it to the file, convert the bin data to hex
	private void printHeaderRecord(File objectFileName,
			SymbolTable symbolTable,   IntermediateFile intermediateFile) throws IOException
	{
		Converter converter = new Converter();
		
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
		
		//Pass it to the string that will be printed
		String programLengthHex = programLengthString;
		
		//Create a string to store the program load address.
		String programLoadAddress = new String();
		
		//If there's an exec.start, get that value
		if (symbolTable.symbolIsDefined("exec.start"))
		{
			programLoadAddress = symbolTable.getSymbolGivenUsage("exec.start").getValue();
			//After getting the required symbol, reset the search counter
			symbolTable.resetSymbolSearch();
		}
		//Otherwise go with the default .start value.
		else
		{
			programLoadAddress = symbolTable.getSymbolGivenUsage("Program Name").getValue();
			//After getting the required symbol, reset the search counter
			symbolTable.resetSymbolSearch();
		}
		
		//Get the number of linking records from the size of the linker array
		String numLinkingRecords = converter.decimalToHex(Integer.toString(this.linkerLines.size()));
		
		//Extend it to 4 hex digits
		while (numLinkingRecords.length() < 4)
		{
			numLinkingRecords = "0" + numLinkingRecords;
		}
		//Get the number of text records from teh size of the text array
		String numTextRecords = converter.decimalToHex(Integer.toString(this.textLines.size()));
		
		//Extend it to 4 hex digits
		while (numLinkingRecords.length() < 4)
		{
			numTextRecords = "0" + numTextRecords;
		}
		
		//Set the execution address equal to the program address, we aren't linking yet.
		String execStartAddress = programLoadAddress;
		
		//Set the version and revision numbers
		String version = "Version # 2.01";
		String revision = "Revision # 1";
		
		//Get the program name and store it in a symbol.
		Symbol programNameInfo = symbolTable.getSymbolGivenUsage("Program Name");
		
		//Get the label from the symbol, it's the programName
		String programName = programNameInfo.getLabel();
		
		//Set up the date and time for printing.		
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd\t|\tHH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
        
        //Save this to a string with the header info in it
		this.headerLine = "H\t|\t" + programName + "\t|\t" + programLengthHex + "\t|\t" + programLoadAddress + "\t|\t" + 
					format + "\t|\t" + numLinkingRecords + "\t|\t" + numTextRecords + "\t|\t" + execStartAddress + "\t|\t"
					+ "SAL\t|\t" + version + "\t|\t" + revision + "\t|\t" + programName;
	}

	//get data for a linking record and print it to the file, convert the bin data to hex
	private void printLinkingRecord(File objectFileName,
			SymbolTable symbolTable,   IntermediateFile intermediateFile, Symbol entOrStart) throws IOException
	{
		//Create a new string for the name of the entry
		String entryName = entOrStart.getLabel();
		
		//Create a new string for the entry address in hex
		String entryAddress = entOrStart.getLocation();
		
		//Create a new string for the type of the entry
		String entryType = entOrStart.getUsage();
		
		this.linkerLines.add("L\t|t" + entryName + "\t|\t" + entryAddress + "\t|\t" + entryType + "\t|\t" + this.prgmName);
	}
	
	//get the data for a text record and output to the file, convert the bin data to hex
	private void printTextRecord(File objectFileName,
			SymbolTable symbolTable,  
			IntermediateFile intermediateFile, String binary, int lineInIntermediate) throws IOException
	{
		//First check if we're just flipping the debug flag
		if(binary.equalsIgnoreCase("debug1"))
		{
			this.inDebugMode = true;
		}
		else if (binary.equalsIgnoreCase("debug0"))
		{
			this.inDebugMode = false;
		}
		//Otherwise, turn it into a normal text record
		else
		{
			//Create a string to hold the label reference if not a type A
			String labelRef = new String();
			
			//Create a string with the type of the instruction in it
			String typeAndAction = "type";
			
			//Create a number of adjustments variable to be altered if it is an expression
			int numOfAdjustments = 1;
			
			//Create a new string to get the start of the program.
			String programLoadAddress = symbolTable.getSymbolGivenUsage("Program Name").getValue();
	
			//After getting the required symbol, reset the search counter
			symbolTable.resetSymbolSearch();
			
			Converter converter = new Converter();
			
			//Get the program's starting address for the purpose of adding it to the current counter in the intermediate file.
			int prgmStart = Integer.parseInt(converter.binaryToDecimal(converter.hexToBinary(programLoadAddress)));
			
			//Get the hex address of where the operation exists in memory
			String hexAddress = Integer.toHexString(lineInIntermediate+prgmStart);
			
			//Extend it
			while (hexAddress.length() < 4)
			{
				hexAddress = "0" + hexAddress;
			}
			
			//Set the debug code
			String debugCode = new String();
			if (this.inDebugMode)
			{
				debugCode = "Y";
			}
			else
			{
				debugCode = "N";
			}
			
			//Create a counter for iteration through the binary string
			int binaryCounter = 0;
			
			//Create a boolean to say whether or not the binary string is completely in binary
			boolean allBinary = true;
			
			//Check to see if the data is completely in binary
			while (binaryCounter < binary.length())
			{
				//Check if the current character isn't a 1 or 0
				if(!(binary.charAt(binaryCounter) == '1' || binary.charAt(binaryCounter) == '0'))
				{
					//Set the flag to say that the binary string isn't completely binary.
					allBinary = false;
				}
				//increment the counter
				binaryCounter++;
			}
			
			String dataWord = new String();
			
			//If the string is completely binary then encode it as a data word
			if (allBinary)
			{
				dataWord = converter.binaryToHex(binary);
				
				//Extend the dataWord
				while (dataWord.length() < 8)
				{
					dataWord = "0" + dataWord;
				}
			}
			//Otherwise, turn it into completely binary
			else
			{
				//Get the label associated inside of the binary.
				String label = binary.substring(binary.indexOf('[')+1, binary.indexOf(']'));
				
				//Create a new string to hold the value of the label
				String labelValue = new String();
				
				//If the label is an expression, evaluate it
				if (label.contains("+") || label.contains("-"))
				{
					//TODO: expressions, remember to repeat type/labelref as many times as we have adjustments
					//Increment number of adjustments as necessary.
					
				}
				//Otherwise, just get the value associated with the label
				else
				{
					labelValue = symbolTable.GetValue(label);
				}
				
				//If the symbol is a label, use it's location, as it referrs to an address
				if (symbolTable.GetUsage(label).equalsIgnoreCase("label"))
				{
					labelValue = symbolTable.GetLocation(label);
					//Convert it to binary
					labelValue = converter.hexToBinary(labelValue);
				}
				//Otherwise, check if it's an int.data
				else if (symbolTable.GetUsage(label).equalsIgnoreCase("int.data"))
				{	
					labelValue = symbolTable.GetValue(label);
					//If it is, convert the decimal integer to binary
					labelValue = converter.decimalToBinary(labelValue);
				}
				//Otherwise, check if it's hex.data
				else if (symbolTable.GetUsage(label).equalsIgnoreCase("hex.data"))
				{
					labelValue = symbolTable.GetValue(label);
					//If it is, convert the hex into binary
					labelValue = converter.hexToBinary(labelValue);
				}
				//Otherwise, check if it's a string
				else if (symbolTable.GetUsage(label).equalsIgnoreCase("str.data"))
				{
					labelValue = symbolTable.GetValue(label);
					//If it is, convert the ascii into binary
					labelValue = converter.asciiToBinary(labelValue.substring(1, labelValue.length()));
					
					//Extend the ascii with spaces instead of 0's before the stuff
					while (labelValue.length() < 32)
					{
						labelValue = labelValue + "00100000";
					}
				}
				//Otherwise, check if it's a bin.data
				else if (symbolTable.GetUsage(label).equalsIgnoreCase("bin.data"))
				{
					labelValue = symbolTable.GetValue(label);
					//If it is, it's fine as it is it's already in binary.
				}
				
				
				//Extend labelValue
				while ((labelValue.length() + binary.substring(0, binary.indexOf('[')).length()) < 32)
				{
					labelValue = "0" + labelValue;
				}
				
				//Create the dataWord
				dataWord = binary.substring(0, binary.indexOf('[')) + labelValue;
				dataWord = converter.binaryToHex(dataWord);
				
				//Extend the dataWord
				while (dataWord.length() < 8)
				{
					dataWord = "0" + dataWord;
				}
			}
			
			//If the type is R or E, store the text field using a label reference
			if (typeAndAction.charAt(0) != 'A')
			{
				this.textLines.add("T" + "\t|\t" + hexAddress + "\t|\t" + debugCode + "\t|\t" + dataWord + "\t|\t"
						+ numOfAdjustments + "\t|\t" + typeAndAction + "\t|\t" + labelRef + "\t|\t" + this.prgmName);
			}
			//Otherwise, don't store the text field using a label reference
			else
			{
				this.textLines.add("T" + "\t|\t" + hexAddress + "\t|\t" + debugCode + "\t|\t" + dataWord + "\t|\t"
						+ numOfAdjustments + "\t|\t" + typeAndAction + "\t|\t" + this.prgmName);
			}
		}
	}
	
	//get the data for a end record and output to the file, convert the bin data to hex
	private void printEndRecord(File objectFileName,
			SymbolTable symbolTable,   IntermediateFile intermediateFile) throws IOException
	{
		//Create a new integer to hold the total number of records in the file, 1 for header 1 for end, then the rest
		int totalRecords = 2 + linkerLines.size() + textLines.size();
		
		this.endLine = "E\t|\t" + totalRecords + "\t|\t" + this.prgmName;
	}
	
	//ouputs the object file
	private void outputTheObjectFile(File objectFileName) throws IOException
	{
		
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(objectFileName)));         
        
        /*
        out.println(//public variable for the header);
		while(array size of text record is greater than 0)
			out.println(//public variable for the text);
		while(array size of linker record is greater than 0)
			out.println(//public variable for the linker);
		*/
		
		out.println(this.headerLine);

		int i = 0;
		
		while(this.textLines.size() > i)
		{
			out.println(textLines.get(i));
			i++;
		}
		
		i = 0;
		
		while(this.linkerLines.size() > i)
		{
			out.println(linkerLines.get(i));
			i++;
		}
		
		out.println(this.endLine);
        
		out.close();
        
	}
	
	
}
