package lab3_source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	 * @throws IO Exception 
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Starting linking loading process.");
		
		//user has to add all object files into the objectFiles directory
		//only object files can be in the directory
		//user needs to list them in the args array in the order in which they would like them to be done
		
	    //Create the load interface
	    Loader loadFile = new Loader();
	    
	    //Create the GEST
	    GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
	    
	    //error boolean
	    Boolean errorsExist = false;
	    
		int i = 0;
		//while there are still object files in the folder do the following. ensure that we do not get any invisible files
		while (i < args.length && args[i].toString().endsWith(".txt"))
		{
			//create a tokenized and parsed object file
			ObjectFileSource objectFile = new ObjectFileSource(new File (args[i]));
		
			//create the checking component
			ObjectFileChecker checkingComponent = new ObjectFileChecker();
			
			//this is where we will call a method to check the object file syntax
			errorsExist = checkingComponent.checkEverything(objectFile);			
			
			//if errors then dont bother with load file
			if (!errorsExist)
			{
				//Pass the object file to the symbol table to create symbols in it
				globalSymbolTable.createSymbolTable(objectFile);
				
				//pass 2, adjust added all records to the load file that have symbols 
				loadFile.addObjectToLoadFile();
			}
			
			i++;
		}
		
		//if errors then dont bother with load file
		if (!errorsExist)
		{
			
			//correct the external symbols in the load object
			loadFile.correctSymbolAddresses();
			
			//print out the load file
			loadFile.output();
		}
		else
		{
			System.out.println("Errors in object files");
		}
		
		//output userReport TODO: double check if you make a user report for every object file or only one
		UserReport userReport.outputUserReport();
		
		
		System.out.println("Ending linking loading process.");
	}

}
