import java.util.ArrayList;


public class ParserV2 implements ParserV2Interface{
	private DirectiveTableInterface directives;
	private ErrorTable errorsPossible;
	private InstructTableInterface commands;
	private SymbolTable symbols;
	private Boolean endOfProgram;
	private String programName;
	private ArrayList<String> undefinedVariables;
	private int lc;
	private InfoHolder outputData;
	private static final String NOPBINARY = "00001000000000000000000000000000";
	private Converter converter;
	
	
	/**
	 * The constructor takes in the master error list, the directives list, and
	 * the instructions list. It stores these in private variables. It then
	 * creates a symbol table(type SymbolTable).
	 * 
	 * @param errorOut is the list of all errors found in the source code
	 * @param errorT is the master list of all possible errors
	 * @param directiveT is the master list of all the directives
	 * @param instructT is the master list of all the instructions
	 */
	public ParserV2(ErrorTable errorT, DirectiveTable directiveT, InstructTable instructT) {
		commands = instructT;
		directives = directiveT;
		errorsPossible = errorT;

		// set end of program flag to false
		endOfProgram = false;

		// initialize undefinedVariables
		undefinedVariables = new ArrayList<String>();

		// make symbol table here
		symbols = new SymbolTable();

		// make InfoHolder to store binary data
		outputData = new InfoHolder();

		// create converter object
		converter = new Converter();

	}

	@Override
	public Boolean parse(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getUndefinedVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SymbolTable getSymbols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfoHolder getBinaryData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseStartDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		// check the number of correct items in the syntax
		if (line.size() == 3)
		{
			
			//if syntax is correct, set program name and lc start point
			programName = line.get(1);
			
			//check if int is valid
			boolean intFlag = true;
			
			//check if each char is a digit
			for ( int inc = 0; inc < line.get(2).length(); inc ++ )
			{
				
				// if NOT between 48 and 57 intFlag = false
				if (  !(line.get(2).charAt(inc) >= 48 && line.get(2).charAt(inc) <= 57))
				{
						intFlag = false;
				}
			}
			
			//if valid int, set start point
			if (intFlag)
			{
				int startPoint = Integer.parseInt(line.get(2));
				lc = startPoint;
			}
			
			//else set error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("invalidInteger");
				
				//add the errorData object to errorsFound
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
		}
		
		//if the syntax is incorrect, create ErrorData object
		else
		{	
			ErrorData error = new ErrorData();
			String code;
			
			//get the correct error code depending on the violation of syntax
			if (line.size() < 3)
			{
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			//add the errorData object to errorsFound
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
	}

	@Override
	public void parseEndDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//if the syntax has the correct amount of tokens
		if (line.size() == 2)
		{
			
			//compare the program name to the one entered in the .start directive
			//if the name doesn't match, get error code
			if ( programName.compareTo(line.get(1)) != 0)
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("invalidProgramName");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			
		}
		
		//if the syntax is incorrect, generate errorData object and add to errorsFound
		else
		{
			ErrorData error = new ErrorData();
			String code;
			if (line.size() < 2)
			{	
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		//flag that the .end was found
		endOfProgram = true;
		
	}

	@Override
	public void parseDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if (line.size() > 1)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("tooManyParameters");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseTextDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if (line.size() > 1)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("tooManyParameters");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseIntDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check if syntax is correct
		if (line.size() == 2)
		{
			
			// check if int value is in accepted range
			Integer value = Integer.parseInt(line.get(1));
			if ((value < 231) && (value > -231))
			{
				String code = converter.decimalToBinary(value.toString());
				outputData.AddLine(lc, code);
				
				//increase the lc by 1 word
				lc++;
			}
			
			//if the int is invalid create error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("invalidOperand");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
				
				//on second thought, I don't think we need this here
				//put nop in data
				//outputData.AddLine(lc, NOPBINARY);
			}
		}
		
		//if syntax is
		//if the syntax is incorrect, generate errorData object and add to errorsFound
		else
		{
			ErrorData error = new ErrorData();
			String code;
			if (line.size() == 1)
			{	
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseStrDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO check for word count
		
		if (line.size() == 2)
		{
			String value = line.get(1);
			if (value.startsWith("'") && value.endsWith("'"))
			{
				
				//remove quotes
				value = value.substring(1, value.length() - 1);
				
				
				//convert to binary
				String valueBin;
				int inc = 0;
				
				//loop while inc is less than length
				while (inc < value.length())
				{
					
					//if the substring of four characters would go past the length
					//take the substring from the inc to the end
					if ( (inc + 3) >= value.length())
					{
						
						//convert to binary
						valueBin = value.substring(inc, value.length() - 1);
						int wordCount = value.length() % 4;
						
						//fill in the last character spots to complete the word
						for ( ; wordCount < 4; wordCount++)
						{
							valueBin = valueBin.concat("00000000");
						}
						
						//add binary to data and update lc
						outputData.AddLine(lc, valueBin);
						lc++;
					}
					
					//else, take a substring of four, convert and add to data
					//also update the lc
					else
					{
						valueBin = value.substring(inc, inc + 3);
						valueBin = converter.asciiToBinary(valueBin);
						outputData.AddLine(lc, valueBin);
						lc++;	
					}
					
					inc = inc + 4;
					
				}
				
				
			}
			
			// if it is missing quotes, add error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("missingQuotes");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
		}
		
		//if the syntax is incorrect, generate errorData object and add to errorsFound
		else
		{
			ErrorData error = new ErrorData();
			String code;
			if (line.size() < 2)
			{	
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseHexDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check number of parameters
		if (line.size() == 2)
		{
			String value = line.get(1);
			
			//check for single quotes
			if (value.startsWith("'") && value.endsWith("'"))
			{
				
				//remove quotes and make lower case
				value = value.substring(1, value.length() - 1);
				value = value.toLowerCase();
				
				Boolean hexFlag = true;
				
				//check if hex via loop looking for characters
				int inc = 0;
				while (inc < value.length() )
				{
					
					// if NOT between 48 and 57
					// then check if between 97 and 102
					//if not, hexFlag = false
					if (  !(value.charAt(inc) >= 48 && value.charAt(inc) <= 57))
					{
						if (  !(value.charAt(inc) >= 97 && value.charAt(inc) <= 102))
						{
							hexFlag = false;
						}
						
					}
					
					inc++;
				}
				
				
				
				//if it is hex, check value
				if ( hexFlag){
					
					//convert to int and then check range
					int decValue = Integer.parseInt(converter.binaryToDecimal(converter.hexToBinary(value)));
					
					if (decValue <= 2147483647 && decValue > -2147483647 )
					{
						
						//if it is valid, add binary to data
						outputData.AddLine(lc, converter.hexToBinary(value));
						
						//increase the lc by 1 word
						lc++;
					}
					
					// if the value is out of range, add error
					else
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("invalidOperand");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,Integer.parseInt(code), message);
						errorsFound.add(error);
					}
				}
				
				//if it is not hex, add error
				else
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("dataNotHex");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			
			}
			
			//if it is missing quotes, add error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("missingQuotes");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
		}
		
		//if the syntax is incorrect, generate errorData object and add to errorsFound
		else
		{
			ErrorData error = new ErrorData();
			String code;
			if (line.size() < 2)
			{	
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseBinDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check number of parameters
		if (line.size() == 2)
		{
			String value = line.get(1);
			
			//check for single quotes
			if (value.startsWith("'") && value.endsWith("'"))
			{
				
				//remove quotes
				value = value.substring(1, value.length() - 1);
				
				Boolean binFlag = true;
				int inc = 0;
				
				//loop to check if it is binary
				while ( inc < value.length())
				{
					if ((value.charAt(inc) != 0) && (value.charAt(inc) != 1)){
						binFlag = false;
					}
					inc++;
				}
				
				//if it is binary, check length
				if ( binFlag){
					
					if (value.length() <= 32 && value.length() >= 1)
					{
						
						//if it is valid, add to data
						outputData.AddLine(lc, value.toString());
						
						//increase the lc by 1 word
						lc++;
					}
					
					// if it has too many characters, add error
					else
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("invalidOperand");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,Integer.parseInt(code), message);
						errorsFound.add(error);
					}
				}
				
				//if it is not binary, add error
				else
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("dataNotBinary");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			
			}
			
			//if it is missing quotes, add error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("missingQuotes");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
		}
		
		//if the syntax is incorrect, generate errorData object and add to errorsFound
		else
		{
			ErrorData error = new ErrorData();
			String code;
			if (line.size() < 2)
			{	
				code = errorsPossible.getErrorCode("missingParameter");
			}
			else
			{
				code = errorsPossible.getErrorCode("tooManyParameters");
			}
			
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
	}

	@Override
	public void parseAdrDataDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseAdrExpDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseEntDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseExtDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseNopDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseExecStartDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseMemSkipDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseEquDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseEquExpDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseResetLCDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseDebugDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseSTypeCommand(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseRTypeCommand(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseJTypeCommand(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseITypeCommand(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseIOTypeCommand(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
	}

}
