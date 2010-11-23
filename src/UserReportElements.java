
public class UserReportElements implements UserReportElementsInterface {

	private String hexLocation;
	private String dataWord;
	private String type;
	private String lineNumber;
	private String sourceCode;
	
	public UserReportElements() {
		this.hexLocation = "none";
		this.dataWord = "--------";
		this.type = "-";
		this.lineNumber = "none";
		this.sourceCode = "none";
	}
	
	public UserReportElements(String hexLocation, String dataWord, String type,
			String lineNumber, String sourceCode) {
		
		this.hexLocation = hexLocation;
		this.dataWord = dataWord;
		this.type = type;
		this.lineNumber = lineNumber;
		this.sourceCode = sourceCode;
	}
	
	@Override
	public void setHexLocation(String hexLocation) {
		this.hexLocation = hexLocation;

	}

	@Override
	public void setDataWord(String dataWord) {
		this.dataWord = dataWord;

	}

	@Override
	public void setType(String type) {
		this.type = type;

	}

	@Override
	public void setSourceLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;

	}

	@Override
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;

	}

	@Override
	public String getHexLocation() {
		return this.hexLocation;
	}

	@Override
	public String getDataWord() {
		return this.dataWord;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public String getSourceLineNumber() {
		return this.lineNumber;
	}

	@Override
	public String getSourceCode() {
		return this.sourceCode;
	}

}
