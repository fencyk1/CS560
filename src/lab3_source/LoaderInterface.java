package lab3_source;

import java.io.IOException;

/**
 * Makes the loader file, references the symbol table to find the address offsets
 * outputs the loader file
 * 
 * @author damicoac
 *
 */

public interface LoaderInterface {

	/**
	 * add object file to the load file
	 */
	void addObjectToLoadFile(ObjectFileSource objectFile);
	
	/**
	 * adjust the memory addresses from the global symbol table
	 */
	void correctSymbolAddresses(GlobalSymbolTable globalSymbolTable);
	
	/**
	 * output the load file
	 * @throws IOException 
	 */
	void output() throws IOException;
	
	/**
	 * make and add header to load file
	 */
	void addHeaderToLoadFile(ObjectFileSource objectFile);
}
