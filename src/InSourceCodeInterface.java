import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Aaron D'Amico
 *
 * Import the source code into an ArrayList<String>. Store this as a member variable.
 *
 */
public interface InSourceCodeInterface 
{
	
	/**
	 * 
	 * @param File sourceCodeFileName is the name of the file to be imported
	 *
	 * Import the source code into an ArrayList<Sting>. This will be a member variable. This is where code can be before it is tokenized as an option. Each line of input will be associated with
	 * an index inteh ArrayList (ie line 1 will be at index [0] of the array).
	 * @throws IOException 
	 */
	void importSourceCode (File sourceCodeFileName) throws IOException;
	
	/**
	 * Return the imported source code as an ArrayList<String> ie the member variable
	 * @return the sourceCode as an array with each line at an index of the array. ie line 1 will be at index 0 of array
	 */
	ArrayList<String> getSourceCodeArray ();

}
