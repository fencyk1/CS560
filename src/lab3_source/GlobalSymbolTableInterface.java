package lab3_source;

/**
 * GlobalSymbolTableInterface is an interface for the class that will hold all
 * of the symbols across all programs, with all necessary information describing
 * them.
 * External symbols will not appear in the table until they are defined from another
 * program as ents.
 * 
 * @author Jeff Wolfe
 *
 */
public interface GlobalSymbolTableInterface {

	/**
	 * Adds a new symbol to the table, given the fully qualified symbol.
	 * 
	 * @param newSymbol The already defined symbol to be added to the table.
	 */
	void addNewGlobalSymbol(GlobalSymbol newSymbol);
	
	/**
	 * Creates a new symbol and adds it to the table in one fell swoop.
	 * 
	 * @param symbolName The name of the symbol.
	 * @param loadAddress The program's initial load address.
	 * @param computedAddress The symbol's assembler computed address.
	 * @param relocatedAddress The loader's re-computed relocated address.
	 * @param length The length of the program.
	 * @param relocationAdjustment The relocation adjustment value.
	 * @param executionStart The execution's start address.
	 */
	void createAndAddNewGlobalSymbol(String symbolName, String loadAddress,
			String computedAddress, String relocatedAddress, String length,
			String relocationAdjustment, String executionStart);
	
	/**
	 * Searches the table for a symbol with the correct name, and returns it.
	 * 
	 * @param symbolName The name of the symbol to be returned.
	 * 
	 * @return The symbol with the same name as symbolName.
	 */
	GlobalSymbol getSymbolGivenName(String symbolName);
	
	/**
	 * Searches the table for a symbol with the correct address, and returns it.
	 * 
	 * @param assemblerAddress The address of the symbol to be returned.
	 * 
	 * @return The symbol with the same address as assemblerAddress.
	 */
	GlobalSymbol getSymbolGivenAddress(String assemblerAddress);
	
}
