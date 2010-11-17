package lab3_source;

import ErrorTable;

import java.io.File;

/**
 * 
 * @author damicoac
 *
 * Basically SP3 is 2 passes
 * Pass 1
 * -syntax checking of the object file? 
 * -grab all the header and linking records and build a linker Global Symbol table
 * -using the H records add up all the lengths and verify it will fit into memory
 * -compute the linker assigned location for each module
 * Pass 2
 * -Adjust all relative addresses
 * -look up external references in the symbol table and add their address to the right hand side of the word(last 4 hex digits)
 * -produce a user report
 * -produce a load file (think .exe in windows)
 * 
 */
public class LoaderLinkingMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Starting linking loading process.");
		
		//get all the files in a directory 
		//only object files can be in the directory
		
		
		//while there are still object files in the folder do the following
		while ()
		{
			//create a tokenized and parsed object file
			ObjectFileSource errorIn = new ErrorTable(new File ("error.tbl"));
		
			
			//make the load file
			
			//make the global symbol table
		}
		
		//correct the external symbols in the load object
		
		
		//print out the load file
		
		
		
		System.out.println("Ending linking loading process.");
	}

}
