package lab3_source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ObjectFileSource implements ObjectFileSourceInterface {

	//member variable
	/*
	 * this is the data structure that will have all header and end records modeled over it
	 */
	private ArrayList<String[]> headerEndRecords = new ArrayList<String[]>();

	/*
	 * this is the data structure that will have all text records modeled over it
	 */
	private ArrayList<String[]> textRecords = new ArrayList<String[]>();
	
	/*
	 * this is the data structure that will have all linking records modeled over it
	 */
	private ArrayList<String[]> linkingRecords = new ArrayList<String[]>();
	
	//constructors
	public ObjectFileSource (File objectFileName) throws IOException 
	{
		this.importObjectFile(objectFileName);
	}
	
	/*
	 * import the object file. tokenize on '|' and the header record to the member variable. iterate over the text 
	 * records and add them to the member variable. do the same for the linking records and then do it for the end record
	 * 
	 */
	private void importObjectFile (File objectFileName) throws IOException 
	{
		System.out.println("Importing object file : " + objectFileName);
		
		//clear data structure
		headerEndRecords.clear();
		textRecords.clear();
		linkingRecords.clear();
		
		//get input from file, set up initial variables 
		BufferedReader input = new BufferedReader(new FileReader(objectFileName));
		String newLine = new String();
		int numberOfLinkingRecords  = 0;
		int numberOfTextRecords = 0;
		
		//get the header record TODO
		
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		//do this for text files
		int i = 0;
		while (i < numberOfLinkingRecords)
		{
			
			//TODO
			
			numberOfLinkingRecords++;
		}
		
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		//do this for linking files
		i = 0;
		while (i < numberOfTextRecords)
		{
		
			
			//TODO
			numberOfTextRecords++;
		}
		
		//get end record TODO
		
		
		
		
		
		//close the input
		input.close();
		
		
	}
	
	
	@Override
	public String getEntryNameFromLinkingAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAddressInHexFromLinkingAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeFromLinkingAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAddressInHexFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDebugCodeFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataWordFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAdjustmentsFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabelReferenceFromTextAtLine(int lineNumberOfObjectFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTotalRecordsFromEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleNameFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgramLengthInHexFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgramLoadAddressInHexfromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTimeFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNumberOfLinkingRecoredsFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNumberOfTextRecordsFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExecutionStartAddressFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgramNameFromHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
