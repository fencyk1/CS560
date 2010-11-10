import java.io.File;
import java.io.IOException;


/**
 * 
 * @author Aaron D'Amico
 * 
 * Directives table is a flat text file. The name of this file is stored on the root directory of the code (As it is with all
 * tables or files that need to be imported). This class has the methods to import this file into a data structure.
 *
 */
public interface DirectiveTableInterface 
{

	/**
	 * 
	 * import the directive text file into a data structure.
	 * @throws IOException 
	 * @param tableFileName is the name of the file to be imported
	 */
	void importTable (File tableFileName) throws IOException;
	
	/**
	 * 
	 * When given an directive it returns a boolean if the error code exists in the data structure. Return is true if directive 
	 * exists. False otherwise.
	 * @param directiveName is the name of the directive to check to see if it exists
	 */
	Boolean hasDirective (String directiveName);
	
	/**
	 * 
	 * @param directiveName is the name of the directive to see if the directive impacts memory
	 * @return
	 * 
	 * Returns true if the directive name impacts memory. ie it is one word or longer. otherwise it returns false.
	 */
	Boolean impactsMemory (String directiveName);
	
	
}
