import java.io.*;

/**
 * 
 * @author Aaron D'Amico
 * 
 * Error table is a flat text file. The name of this file is stored on the root directory of the code (As it is with all
 * tables or files that need to be imported). This class has the methods to import this file into a data structure. 
 */
public interface ErrorTableInterface 
{

	/**
	 * import the error table text file into a data structure.
	 * @throws IOException 
	 * @param tableFileName is the name of the file to be imported
	 */
	void importTable (File tableFileName) throws IOException;
	
	/**
	 * 
	 * @param errorCode
	 * @return True or False if the error code exists
	 * 
	 * When given an error code it returns a boolean if the error code exists in the data structure. Return is true if error code 
	 * exists. False otherwise.
	 */
	Boolean hasErrorType (String errorCode);
	
	/**
	 * 
	 * @param errorCode
	 * @return the string that represents the error message associate with the error code
	 * 
	 * When given an error code this returns its associated error message. Return String "no error message for that code" if that error code doesnt exist
	 */
	String getErrorMessage (String errorCode);
	
//	/**
//	 * 
//	 * @param errorCode
//	 * @return the string that represents the error type associate with the error code
//	 * 
//	 * Get the Error type based on an error code. This returns a string. It returns the string "no error type for that code" if the error code doesnt exist in the object.
//	 */
//	String getErrorType (String errorCode);
	
	/**
	 * 
	 * @param theErrorMessage
	 * @return the string that represents the error code associate with the error type
	 * 
	 * Get the Error code based on an error type. This returns a string. It returns the string "no error code for that type" if the error type doesnt exist in the object.
	 * It will also return the error code if given the error type
	 */
	String getErrorCode (String theErrorMessage);
	
	
}
