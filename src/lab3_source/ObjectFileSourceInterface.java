package lab3_source;

import java.io.File;
import java.io.IOException;

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
	
	/**
	 * Import the object source code into an ArrayList<Sting>. This will be a member variable. This is where code can be before it is tokenized as an option. Each line of input will be associated with
	 * an index in the ArrayList (ie line 1 will be at index [0] of the array). The method will also clear out the previous source code just to be sure that when source code is imported
	 * it is on a fresh data structure.
	 * @throws IOException 
	 */
	void importSourceCode (File objectFileName) throws IOException;
	
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
	 * gets the appropriate first Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getFirstTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate first LabelReference from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the LabelReference to return
	 */
	String getFirstLabelReferenceFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate first Action from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Action to return
	 */
	String getFirstActionFromTextAtLine(int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Second Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getSecondTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Second LabelReference from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the LabelReference to return
	 */
	String getSecondLabelReferenceFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate first Second from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Action to return
	 */
	String getSecondActionFromTextAtLine(int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Third Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getThirdTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Third LabelReference from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the LabelReference to return
	 */
	String getThirdLabelReferenceFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate first Third from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Action to return
	 */
	String getThirdActionFromTextAtLine(int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Fourth Type from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Type to return
	 */
	String getFourthTypeFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate Fourth LabelReference from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the LabelReference to return
	 */
	String getFourthLabelReferenceFromTextAtLine (int lineNumberOfObjectFile);
	
	/**
	 * gets the appropriate first Fourth from the line
	 * 
	 * @param lineNumberOfObjectFile
	 * line to get the data from
	 * @return 
	 * the Action to return
	 */
	String getFourthActionFromTextAtLine(int lineNumberOfObjectFile);
	
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
