package lab3_source;

import java.io.File;
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
	private ArrayList<String[]> linkingREcords = new ArrayList<String[]>();
	
	//constructors
	public ObjectFileSource (File objectFileName)
	{
		this.importObjectFile(objectFileName);
	}
	
	/*
	 * import the object file. tokenize on '|' and the header record to the member variable. iterate over the text 
	 * records and add them to the member variable. do the same for the linking records and then do it for the end record
	 * 
	 */
	private void importObjectFile (File objectFileName)
	{
		
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
