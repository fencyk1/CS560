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
		// TODO Auto-generated method stub

		System.out.println("Starting linking loading process.");
		
		//get all the files in the objectFiles directory 
		//user has to add all object files into the objectFiles directory
		//only object files can be in the directory
		File folder = new File("objectFiles/");
	    File[] listOfFiles = folder.listFiles();
		
	    //Create the load interface
	    Loader loadFile = new Loader();
	    
	    //Create the GEST
	    GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
	    
		int i = 0;
		//while there are still object files in the folder do the following. ensure that we do not get any invisible files
		while (i < listOfFiles.length && listOfFiles[i].toString().endsWith(".txt"))
		{
			//create a tokenized and parsed object file
			ObjectFileSource objectFile = new ObjectFileSource(listOfFiles[i]);
		
			//create the checking component
			ObjectFileChecker checkingComponent = new ObjectFileChecker();
			
			//TODO: this is where we will call a method to check the object file syntax
			
			
			//Pass the object file to the symbol table to create symbols in it
			globalSymbolTable.createSymbolTable(objectFile);
			
			//adjuct the load file
			loadFile.createInitialLoadFile();
			
			i++;
		}
		
		//correct the external symbols in the load object
		loadFile.correctSymbolAddresses();
		
		//print out the load file
		loadFile.output();
		
		
		System.out.println("Ending linking loading process.");
	}

}
