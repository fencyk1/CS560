package source;

/**
 * 
 * @author damicoac
 *
 * Imports the object file source. The data structures is an ArrayList of Arrays. Object Files are tokenized on the '|' 
 * character. Each one of those tokens goes into the array. This then has numerous methods to get data at different indexes.
 * While this is simple code it is easier to read
 */
public interface ObjectFileSourceInterface 
{
	//********************************************************
	////////////////////////////linking record methods////////////////////////
	
	/**
	 * gets the appropriate data from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the data to return
	 */
	String getEntryNameFromLinkingAtLine (int lineNumberOfObjectFile);
	
	String getAddressInHexFromLinkingAtLine (int lineNumberOfObjectFile);
	
	String getTypeFromLinkingAtLine (int lineNumberOfObjectFile);
	
	//********************************************************
	////////////////////////////text record methods////////////////////////
	
	String getAddressInHexFromTextAtLine (int lineNumberOfObjectFile);
	
	String getDebugCodeFromTextAtLine (int lineNumberOfObjectFile);
	
	String getDataWordFromTextAtLine (int lineNumberOfObjectFile);
	
	String getAdjustmentsFromTextAtLine (int lineNumberOfObjectFile);
	
	String getTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	String getLabelReferenceTextAtLine (int lineNumberOfObjectFile);
	
	//********************************************************
	//////////////////////end record methods//////////////////////////
	
	String getTotalrecordsFromEnd (int lineNumberOfObjectFile);
	
	//********************************************************
	//////////////////////header methods//////////////////////////
	
	String getModuleNameFromHeader();
	
	String getProgramLengthInHexFromHeader();
	
	String getProgramLoadAddressInHexfromHeader();
	
	String getDateFromHeader();
	
	String getTimeFromHeader();
	
	String getNumberOfLinkingRecoredsFromHeader();
	
	String getNumberOfTextRecordsFromHeader();
	
	String getExecutionStartAddressFromHeader();
	
	String getProgramNameFromHeader();
	
}
