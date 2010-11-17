package lab3_source;

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
	 * gets the appropriate EntryName from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the EntryName to return
	 */
	String getEntryNameFromLinkingAtLine (int lineNumberOfObjectFile);

	/**
	 * gets the appropriate AddressInHex from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the AddressInHex to return
	 */
	String getAddressInHexFromLinkingAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getTypeFromLinkingAtLine (int lineNumberOfObjectFile);
	
	//********************************************************
	////////////////////////////text record methods////////////////////////
	
	/**
	 * gets the appropriate AddressInHex from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the AddressInHex to return
	 */
	String getAddressInHexFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate DebugCode from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the DebugCode to return
	 */
	String getDebugCodeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate DataWord from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the DataWord to return
	 */
	String getDataWordFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Adjustments from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Adjustments to return
	 */
	String getAdjustmentsFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate LabelReference from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the LabelReference to return
	 */
	String getLabelReferenceFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate ActionTypeLabel from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the ActionTypeLabel to return
	 */
	String getActionTypeLabelFromTextAtLine(int lineNumberOfObjectFile);
	
	//********************************************************
	//////////////////////end record methods//////////////////////////
	
	/**
	 * gets the appropriate TotalRecords from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the TotalRecords to return
	 */
	String getTotalRecordsFromEnd ();
	
	//********************************************************
	//////////////////////header methods//////////////////////////
	
	/**
	 * gets the appropriate ModuleName from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the ModuleName to return
	 */
	String getModuleNameFromHeader();
	
	/**
	 * gets the appropriate ProgramLengthInHex from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the ProgramLengthInHex to return
	 */
	String getProgramLengthInHexFromHeader();
	
	/**
	 * gets the appropriate ProgramLoadAddressInHex from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the ProgramLoadAddressInHex to return
	 */
	String getProgramLoadAddressInHexfromHeader();
	
	/**
	 * gets the appropriate Date from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the Date to return
	 */
	String getDateFromHeader();
	
	/**
	 * gets the appropriate Time from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the Time to return
	 */
	String getTimeFromHeader();
	
	/**
	 * gets the appropriate NumberOfLinkingRecoreds from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the NumberOfLinkingRecoreds to return
	 */
	String getNumberOfLinkingRecoredsFromHeader();
	
	/**
	 * gets the appropriate NumberOfTextRecords from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the NumberOfTextRecords to return
	 */
	String getNumberOfTextRecordsFromHeader();
	
	/**
	 * gets the appropriate ExecutionStartAddress from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the ExecutionStartAddress to return
	 */
	String getExecutionStartAddressFromHeader();
	
	/**
	 * gets the appropriate ProgramName from the line
	 * 
	 * line to get the data from
	 * @return 
	 * the ProgramName to return
	 */
	String getProgramNameFromHeader();
	
}
