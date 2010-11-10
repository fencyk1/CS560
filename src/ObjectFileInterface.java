import java.io.File;
import java.io.IOException;


/**
 * @author Aaron D'Amico, Jeff Wolfe, Austin Lohr
 * 
 * Collects the data for object file from the parser object
 * its main goal is to output the object file with this data taken into consideration
 * there will he statments in here that and pretty print statements 
 * ie System.out.println " Header || header stuff" etc
 *
 */
public interface ObjectFileInterface {

	/**
	 * 
	 * @param objectFileName is the output file of all the data in hex in the appropriate formats
	 * @param symbolTable is the symbols found in pass one
	 * @param intermediateFile is the intermediate file object that holds all the intermediate file info in it
	 * @param errorsFound is a list of all of the errors encountered in the source code
	 * @param sourceCode is an array of the incoming source code.
	 * 
	 * This outputs the ArrayList<String> member variable to a file with the name as given in the parameter. Each index of the Array becomes its own line of output in the file. 
	 * @throws IOException 
	 * 
	 */
	void outputObjectFile (File objectFileName, SymbolTable symbolTable, IntermediateFile intermediateFile, ErrorOut errorsFound, InSourceCode sourceCode) throws IOException;
}
