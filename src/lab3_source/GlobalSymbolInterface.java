package lab3_source;

/**
 * GlobalSymbolInterface is an interface for the class that will store all of the
 * information regarding symbols, and those objects will then be stored and accessible
 * in the GlobalSymbolTable.
 * There will be a smart constructor so that the GlobalSymbolTable can add a new symbol
 * with just one method.
 * 
 * @author Jeff Wolfe
 *
 */
public interface GlobalSymbolInterface {

	/**
	 * Sets the symbol's name, i.e. what it is referred to as in the program.
	 * 
	 * @param symbolName The symbol's name.
	 */
	void setSymbolName(String symbolName);
	
	/**
	 * Sets the program's initial load address. This is only used for the program name.
	 * 
	 * @param loadAddress The program's initial load address.
	 */
	void setInitialLoadAddress(String loadAddress);
	
	/**
	 * Sets the address that the assembler computes in the individual program.
	 * 
	 * @param computedAddress The address the assembler computed local to the program.
	 */
	void setAssemblerComputedAddress(String computedAddress);
	
	/**
	 * Sets the loader's re-computed relocated address. This is only used for the
	 * program name, and SDR Share.
	 * 
	 * @param relocatedAddress The re-computed relocated address.
	 */
	void setLoaderRelocatedAddress(String relocatedAddress);
	
	/**
	 * Sets the length of the symbol.
	 * 
	 * @param length The length of the symbol.
	 */
	void setLength(String length);
	
	/**
	 * Sets the relocation adjustment value. This is only used for the program name.
	 * 
	 * @param relocationAdjustment The relocation adjustment value for the program name.
	 */
	void setRelocationAdjustment(String relocationAdjustment);
	
	/**
	 * Sets the execution start address. This is only used for the program name.
	 * 
	 * @param executionStart The execution start address.
	 */
	void setExecutionStartAddress(String executionStart);
	
	/**
	 * Returns the symbol's name, i.e. what it is referred to as in the program.
	 * 
	 * @return The symbol's name.
	 */
	String getSymbolName();
	
	/**
	 * Gets the address that the assembler computes in the individual program.
	 * 
	 * @return The initial load address.
	 */
	String getInitialLoadAddress();
	
	/**
	 * Gets the address that the assembler computes in the individual program.
	 * 
	 * @return The assembler computed address.
	 */
	String getAssemblerComputedAddress();
	
	/**
	 * Gets the loader's re-computed relocated address. This is only used for the
	 * program name, and SDR Share.
	 * 
	 * @return The recomputed relocated address.
	 */
	String getLoaderRelocatedAddress();
	
	/**
	 * Gets the length of the symbol. This is used only for the program name.
	 * 
	 * @return The symbol's length.
	 */
	String getLength();
	
	/**
	 * Gets the relocation adjustment value. This is only used for the program name.
	 * 
	 * @return The relocation adjustment value.
	 */
	String getRelocationAdjustment();
	
	/**
	 * Gets the execution start address. This is only used for the program name.
	 * 
	 * @return The execution start address.
	 */
	String getExecutionStartAddress();
	
}
