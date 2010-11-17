package lab3_source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ObjectFileSource implements ObjectFileSourceInterface {

	//member variable
	/*
	 * this is the data structure that will have all header and end records modeled over it
	 */
	private	String[] header = new String [8];
	
	/*
	 * this is the data structure for the end record
	 */
	private	String[] endRecord = new String [1];
	
	/*
	 * this is the data structure that will have all text records modeled over it
	 */
	private ArrayList<String[]> textRecords = new ArrayList<String[]>();
	
	/*
	 * this is the data structure that will have all linking records modeled over it
	 */
	private ArrayList<String[]> linkingRecords = new ArrayList<String[]>();
	
	//number of text and linking records
	private int numberOfLinkingRecords  = 0;
	private int numberOfTextRecords = 0;
	
	//constructors
	public ObjectFileSource (File objectFileName) throws IOException 
	{
		this.importObjectFile(objectFileName);
	}
	
	/*
	 * import the object file. tokenize on '|' and the header record to the member variable. iterate over the text 
	 * records and add them to the member variable. do the same for the linking records and then do it for the end record
	 * assumes that the text records come before the linking records
	 * 
	 */
	private void importObjectFile (File objectFileName) throws IOException 
	{
		System.out.println("Importing object file : " + objectFileName);
		
		//clear data structure, not really needed and im not sure that "= null" will work
		header = null;
		textRecords.clear();
		linkingRecords.clear();
		endRecord = null;
		
		//get input from file, set up initial variables 
		BufferedReader input = new BufferedReader(new FileReader(objectFileName));
		String newLine = new String();
		
/////////////////////////////////////////////////////////////////////////
//get the header record
/////////////////////////////////////////////////////////////////////////

		//get the header file from the object file
		newLine = input.readLine();
			
		//tokenize it
		StringTokenizer headerTokens = new StringTokenizer (newLine, "|");
		
		//skip the 'H"
		headerTokens.nextToken();
		
		//get the module name
		header[0] = headerTokens.nextToken();
		
		//get program length
		header[1] = headerTokens.nextToken();
		
		//get assembler assigned program load address in hex
		header[2] = headerTokens.nextToken();
		
		//get date
		header[3] = headerTokens.nextToken();
		
		//get time
		header[4] = headerTokens.nextToken();
		
		//get number of linking records
		header[5] = headerTokens.nextToken();
		
		//set number of linking records
		numberOfLinkingRecords = Integer.parseInt(header[5]);
		
		//get number of text records
		header[6] = headerTokens.nextToken();
		
		//set number of text records
		numberOfTextRecords = Integer.parseInt(header[6]);
		
		//get execution start address
		header[7] = headerTokens.nextToken();
		
		//skip the 'SAL"
		headerTokens.nextToken();
		
		//skip the 'Version"
		headerTokens.nextToken();
		
		//skip the 'Revision"
		headerTokens.nextToken();
		
		//get program name
		header[8] = headerTokens.nextToken();
		
/////////////////////////////////////////////////////////////////////////
//get the text record		
/////////////////////////////////////////////////////////////////////////

		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		//do this for text files
		int i = 0;
		while (i < numberOfTextRecords)
		{
			//get the header file from the object file
			newLine = input.readLine();
			
			//tokenize it
			StringTokenizer textTokens = new StringTokenizer (newLine, "|");
						
			//make an array to add those tokens to
			String[] text = new String [textTokens.countTokens() -1];
			
			//skip "T"
			textTokens.nextToken();
			
			//get address in hex
			text[0] = textTokens.nextToken();
			
			//get debug code
			text[1] = textTokens.nextToken();
			
			//get data word in hex
			text[2] = textTokens.nextToken();
			
			//get number of adjustments
			text[3] = textTokens.nextToken();
			
			//get type
			text[4] = textTokens.nextToken();
			
			//if there are tokens left because of a label, if greater than 1 then the next token must be a label reference
			if (textTokens.countTokens() > 1)
			{
				//get label reference if it exists
				text[5] = textTokens.nextToken();
							
			}
			
			//TODO how many of the action, label, or type tokens can we have in object file??????? Currently do not get this data
			//for now 
			//get type,action, or label
			text[6] = textTokens.nextToken();
			
			
			//add the array to the list of text Records
			textRecords.add(text);
			
			//get next text record
			numberOfTextRecords++;
		}
		
/////////////////////////////////////////////////////////////////////////
//get the linking record
/////////////////////////////////////////////////////////////////////////

		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		//do this for linking files
		i = 0;
		while (i < numberOfLinkingRecords)
		{
		
			//get the header file from the object file
			newLine = input.readLine();
			
			//tokenize it
			StringTokenizer linkingTokens = new StringTokenizer (newLine, "|");
			
			//make an array to add those tokens to
			String[] linking = new String [3];
			
			//skip "L"
			linkingTokens.nextToken();
			
			//get entry name
			linking[0] = linkingTokens.nextToken();
			
			//get address in hex
			linking[1] = linkingTokens.nextToken();
			
			//get the type
			linking[2] = linkingTokens.nextToken();
			
			//add the array to the list of text Records
			linkingRecords.add(linking);
			
			//get next linking record
			numberOfLinkingRecords++;
		}

/////////////////////////////////////////////////////////////////////////
//get the end record
/////////////////////////////////////////////////////////////////////////		
		
		//get end record 
		//get the end record from the object file
		newLine = input.readLine();
		
		//tokenize it
		StringTokenizer endTokens = new StringTokenizer (newLine, "|");
		
		//skip the 'E'
		endTokens.nextToken();
		
		//get the total number of records
		endRecord [0] = endTokens.nextToken();
		
		
		//close the input
		input.close();
		
		
	}
	
/////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getEntryNameFromLinkingAtLine(int lineNumberOfObjectFile) 
	{
		
		//subtract all the text records, minus 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - numberOfTextRecords - 2;

		return linkingRecords.get(lineNumberOfObjectFile)[0];
	}

	@Override
	public String getAddressInHexFromLinkingAtLine(int lineNumberOfObjectFile) {
		
		//subtract all the text records, minus 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - numberOfTextRecords - 2;

		return linkingRecords.get(lineNumberOfObjectFile)[1];		
	}

	@Override
	public String getTypeFromLinkingAtLine(int lineNumberOfObjectFile) 
	{

		//subtract all the text records, minus 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - numberOfTextRecords - 2;

		return linkingRecords.get(lineNumberOfObjectFile)[2];
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String getAddressInHexFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[0];
	}

	@Override
	public String getDebugCodeFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[1];
	}

	@Override
	public String getDataWordFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[2];
	}

	@Override
	public String getAdjustmentsFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[3];
	}

	@Override
	public String getTypeFromTextAtLine(int lineNumberOfObjectFile) {

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[4];
	}

	@Override
	public String getLabelReferenceFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[5];
	}
	
	@Override
	public String getActionTypeLabelFromTextAtLine(int lineNumberOfObjectFile) 
	{

		//subtract 1 for header record, minus 1 for index
		lineNumberOfObjectFile = lineNumberOfObjectFile - 2;

		return textRecords.get(lineNumberOfObjectFile)[6];
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getTotalRecordsFromEnd() 
	{

		return endRecord[0];
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String getModuleNameFromHeader() 
	{
		return header[0];
	}

	@Override
	public String getProgramLengthInHexFromHeader() 
	{
		return header[1];
	}

	@Override
	public String getProgramLoadAddressInHexfromHeader() 
	{
		return header[2];
	}

	@Override
	public String getDateFromHeader() 
	{
		return header[3];
	}

	@Override
	public String getTimeFromHeader() 
	{
		return header[4];
	}

	@Override
	public String getNumberOfLinkingRecoredsFromHeader() 
	{
		return header[5];
	}

	@Override
	public String getNumberOfTextRecordsFromHeader() 
	{
		return header[6];
	}

	@Override
	public String getExecutionStartAddressFromHeader() 
	{
		return header[7];
	}

	@Override
	public String getProgramNameFromHeader() 
	{
		return header[8];
	}

}
