package lab3_source;

import java.util.ArrayList;

public class GlobalSymbolTable implements GlobalSymbolTableInterface {
	private ArrayList<GlobalSymbol> gest;
	
	
	public GlobalSymbolTable () {
		this.gest = new ArrayList<GlobalSymbol>();
	}
	
	@Override
	public void createSymbolTable(ObjectFileSource objectFile) {
		// TODO Auto-generated method stub

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
