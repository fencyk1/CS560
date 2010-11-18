package lab3_source;

import java.util.ArrayList;

public class GlobalSymbolTable implements GlobalSymbolTableInterface {
	private ArrayList<GlobalSymbol> gest;
	private boolean firstObjectRead;
	private String currentEndOfProgram;
	
	
	public GlobalSymbolTable () {
		this.gest = new ArrayList<GlobalSymbol>();
		this.firstObjectRead = false;
		this.currentEndOfProgram = new String();
	}
	
	@Override
	public void createSymbolTable(ObjectFileSource objectFile) {
		
		//START: Create program name records
		
		//If this is the first object file we are reading, set the new program
		//load point with the first (main) program name
		if (!this.firstObjectRead)
		{
			//Create the fields we will be using for the symbol information.
			String symbolName = objectFile.getProgramNameFromHeader();
			String loadAddress = objectFile.getProgramLoadAddressInHexfromHeader();
			String computedAddress = objectFile.getProgramLoadAddressInHexfromHeader();
			String relocatedAddress = objectFile.getProgramLoadAddressInHexfromHeader();
			String length = objectFile.getProgramLengthInHexFromHeader();
			String relocationAdjustment = "0";
			String executionStart = objectFile.getExecutionStartAddressFromHeader();
			
			//Add the symbol to the symbol table.
			this.gest.add(new GlobalSymbol(symbolName, loadAddress, computedAddress, 
					relocatedAddress, length, relocationAdjustment, executionStart));
			
			//Set the flag to note we've already read the first object.
			this.firstObjectRead = true;
			
			//Set the current end of the program
			this.currentEndOfProgram = Integer.toHexString(Integer.parseInt(loadAddress, 16) + Integer.parseInt(length, 16));
		}
		//Otherwise, if this is not the first object we are reading, set the program
		//name's fields accordingly
		else
		{
			//Set the fields we will be using for the symbol information.
			String symbolName = objectFile.getProgramNameFromHeader();
			String loadAddress = objectFile.getProgramLoadAddressInHexfromHeader();
			String computedAddress = objectFile.getProgramLoadAddressInHexfromHeader();
			
			//Since this is the second or later object file we are dealing with, set the relocated address
			//relative to the start and length of the other one(s).
			String relocatedAddress = Integer.toHexString(Integer.parseInt(this.currentEndOfProgram, 16) 
					+ Integer.parseInt(objectFile.getProgramLoadAddressInHexfromHeader(), 16));
			String length = objectFile.getProgramLengthInHexFromHeader();
			
			//Set the relocation adjustment equal to the current end of the program.
			String relocationAdjustment = this.currentEndOfProgram;
			String executionStart = objectFile.getExecutionStartAddressFromHeader();
			
			//Reset the current end of the program to the new end of the program.
			this.currentEndOfProgram = Integer.toHexString(Integer.parseInt(relocatedAddress, 16) + Integer.parseInt(length, 16));
			
			//Add the symbol to the symbol table.
			this.gest.add(new GlobalSymbol(symbolName, loadAddress, computedAddress, 
					relocatedAddress, length, relocationAdjustment, executionStart));
		}

		//END: Create program name records
		
		//START: Deal with linking records.
		
		//Create a counter variable
		int counter = 0;
		
		while (counter < Integer.parseInt(objectFile.getNumberOfLinkingRecoredsFromHeader(), 16))
		{
			if (objectFile.getTypeFromLinkingAtLine(counter).equalsIgnoreCase("ent"))
			{
				//Get the symbol name at the current line in the linking record.
				String symbolName = objectFile.getEntryNameFromLinkingAtLine(counter);
				//Initialize the load address to being empty.
				String loadAddress = new String();
				//Get the assembler computed address for the symbol.
				String computedAddress = objectFile.getAddressInHexFromLinkingAtLine(counter);
				//Set the rest to new empty strings because they're unnecessary for non-program names
				String relocatedAddress = new String();
				String length = new String();
				String relocationAdjustment = new String();
				String executionStart = new String();
				
				//Add the symbol to the symbol table.
				this.gest.add(new GlobalSymbol(symbolName, loadAddress, computedAddress, 
						relocatedAddress, length, relocationAdjustment, executionStart));
			}

			//Increment the counter
			counter++;
		}
		
		//END: Deal with linking records.
				
	}

	@Override
	public void addNewGlobalSymbol(GlobalSymbol newSymbol) {
		//Add the given symbol to the symbol table.
		this.gest.add(newSymbol);

	}

	@Override
	public void createAndAddNewGlobalSymbol(String symbolName,
			String loadAddress, String computedAddress,
			String relocatedAddress, String length,
			String relocationAdjustment, String executionStart) {
		
		//Add a new symbol to the symbol table with the given parameters.
		this.gest.add(new GlobalSymbol(symbolName, loadAddress, computedAddress, 
				relocatedAddress, length, relocationAdjustment, executionStart));

	}

	@Override
	public GlobalSymbol getSymbolGivenName(String symbolName) {

		//Create a new symbol to return
		GlobalSymbol returnValue = new GlobalSymbol();
		//Create a counter for iteration
		int counter = 0;
		
		//Check each spot in the GEST array
		while (counter < this.gest.size())
		{
			//If the symbol at a given spot is named the same, set the return value
			//and break.
			if (this.gest.get(counter).getSymbolName().equals(symbolName))
			{
				returnValue = this.gest.get(counter);
				break;
			}
			//Increment the counter
			counter++;
		}
		
		return returnValue;
	}

	@Override
	public GlobalSymbol getSymbolGivenAddress(String assemblerAddress) {
		//Create a new symbol to return
		GlobalSymbol returnValue = new GlobalSymbol();
		//Create a counter for iteration
		int counter = 0;
		
		//Check each spot in the GEST array
		while (counter < this.gest.size())
		{
			//If the symbol at a given spot is named the same, set the return value
			//and break.
			if (this.gest.get(counter).getAssemblerComputedAddress().equals(assemblerAddress))
			{
				returnValue = this.gest.get(counter);
				break;
			}
			//Increment the counter
			counter++;
		}
		
		return returnValue;
	}

}
