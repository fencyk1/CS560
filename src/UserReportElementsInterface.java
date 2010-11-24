/**
 * Contains the data for the UserReportTwo to output, and stores it
 * in a way that is easy to understand.
 * 
 * @author Jeff Wolfe
 *
 */
public interface UserReportElementsInterface {
//Hex location, data word, ARE, line number, source code
	
	/**
	 * Sets the hex location of the user report line.
	 * 
	 * @param hexLocation The hex location element.
	 */
	void setHexLocation(String hexLocation);
	
	/**
	 * Sets the data word of the user report line.
	 * 
	 * @param dataWord The data word element.
	 */
	void setDataWord(String dataWord);
	
	/**
	 * Sets the type of the user report line.
	 * 
	 * @param type The type element.
	 */
	void setType(String type);
	
	/**
	 * Sets the source code line number of the user report line.
	 * 
	 * @param lineNumber The line number the element appears at in the source code.
	 */
	void setSourceLineNumber(String lineNumber);
	
	/**
	 * Sets the source code element of the user report line.
	 * 
	 * @param sourceCode The source code element.
	 */
	void setSourceCode(String sourceCode);
	
	/**
	 * Gets the hex location element of the user report line.
	 * 
	 * @return The hex location element of the user report line
	 */
	String getHexLocation();
	
	/**
	 * Gets the data word element of the user report line.
	 * 
	 * @return The data word element.
	 */
	String getDataWord();
	
	/**
	 * Gets the type element of the user report line.
	 * 
	 * @return The type element.
	 */
	String getType();
	
	/**
	 * Gets the line number of the source code in the user report line.
	 * 
	 * @return The source code line number.
	 */
	String getSourceLineNumber();
	
	/**
	 * Gets the source code element of the current spot in the user report line.
	 * 
	 * @return The source code line.
	 */
	String getSourceCode();
}
