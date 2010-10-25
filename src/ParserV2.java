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
		
		//check syntax
		if ( line.size() == 2)
		{
			String label = line.get(1);
			
			//check if symbol is defined
			if (symbols.symbolIsDefined(label))
			{
				//if it is, add to data
				outputData.AddLine(lc, String.valueOf(symbols.GetLocation(label)));
				
				//increase the lc by 1 word
				lc++;
			}
			
			//if not already defined
			else
			{
				undefinedVariables.add(label);
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
	public void parseAdrExpDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if ((line.size() == 2))
		{
			
			//complete in pass 2
			
		}
		
		//if invalid syntax
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
	public void parseEntDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if ((line.size() > 1) && (line.size() < 6))
		{
			
			//check if variables are defined
			for (int inc = 1; inc < line.size(); inc ++)
			{
				
				//if not defined add to undefinedVariables
				if ( !(symbols.symbolIsDefined(line.get(inc) ) ) )
				{
					undefinedVariables.add(line.get(inc));
				}
			}
			
		}
		
		//if invalid syntax
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
	public void parseExtDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if ((line.size() > 1) && (line.size() < 6))
		{
			
			//check if variables are defined
			//if they are, flag error
			//if not, add them to table
			for (int inc = 1; inc < line.size(); inc ++)
			{
				
				//if not defined add to symbol table
				if ( !(symbols.symbolIsDefined(line.get(inc) ) ) )
				{

					//create symbol
					Symbol newSymbol = new Symbol();
					newSymbol.setLabel(line.get(inc));
					newSymbol.setLocation(99999);
					newSymbol.setUsage("ext");
					
					//enter symbol in symbol table
					symbols.defineSymbol(newSymbol);
				}
				
				//if they are already defined add error
				else
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("variableAlreadyDefined");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
		}
		
		//if invalid syntax
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
	public void parseNopDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check for extra parameters
		if (line.size() > 1)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("tooManyParameters");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		//put nop in data
		outputData.AddLine(lc, NOPBINARY);
		
		//increase the lc by 1 word
		lc++;
	}

	@Override
	public void parseExecStartDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if (line.size() == 2)
		{
			
			//check if mem location is valid
			String value = line.get(1);
			
			//check if defined symbol
			//if it is, create new symbol object for exec.start
			if ( symbols.symbolIsDefined(value))
			{
				//create symbol
				Symbol newSymbol = new Symbol();
				newSymbol.setLabel("exec.start");
				newSymbol.setLocation(symbols.GetLocation(value));
				newSymbol.setUsage("exec.start");
				
				//enter symbol in symbol table
				symbols.defineSymbol(newSymbol);
			}
			
			//else check if integer
			else
			{
				boolean intFlag = true;
				
				//check if each char is a digit
				for ( int inc = 0; inc < value.length(); inc ++ )
				{
					
					// if NOT between 48 and 57 intFlag = false
					if (  !(value.charAt(inc) >= 48 && value.charAt(inc) <= 57))
					{
							intFlag = false;
					}
				}
				
				//if valid integer, set into symbol table
				if (intFlag && ((Integer.parseInt(value) + lc) < 65536 ) && (Integer.parseInt(value) > 0))
				{
					
					//create symbol
					Symbol newSymbol = new Symbol();
					newSymbol.setLabel("exec.start");
					newSymbol.setLocation(Integer.parseInt(value));
					newSymbol.setUsage("exec.start");
					
					//enter symbol in symbol table
					symbols.defineSymbol(newSymbol);
					
				}
				
				//if not integer, then assume it is undefined symbol
				//and add to to undefinedVariables list
				else
				{
					undefinedVariables.add(value);
				}
			}
			
			
		}
		
		//if bad syntax, add error
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
	public void parseMemSkipDirective(ArrayList<String> line, int lineNumber,
			ErrorOut errorsFound) {
		// TODO Auto-generated method stub
		
		//check syntax
		if (line.size() == 2)
		{
			
			//check if mem location is valid
			String value = line.get(1);
			
			//check if integer
			boolean intFlag = true;
			for ( int inc = 0; inc < value.length(); inc ++ )
			{
				
				// if NOT between 48 and 57 intFlag = false
				if (  !(value.charAt(inc) >= 48 && value.charAt(inc) <= 57))
				{
						intFlag = false;
				}
			}
			
			//if valid integer, move lc
			if (intFlag && ((Integer.parseInt(value) + lc) < 65536 ) && (Integer.parseInt(value) > 0))
			{
				lc = lc + Integer.parseInt(value);
			}
			
			//else add error
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("invalidInteger");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
		}
		
		//if bad syntax, add error
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
		
		//check syntax
		if (line.size() == 2)
		{
			
			//check if number is valid
			String value = line.get(1);
			Boolean errFlag = true;
			if (value.length() == 1  ) 
			{
				if ( (value.charAt(0) == '0') || (value.charAt(0) == 1))
				{
					errFlag = false;
				}
			}
			
			//if there is error, add it to errorsFound
			if (errFlag)
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("invalidBoolean");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
		}
		
		//if bad syntax, add error
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

	public void parseLUICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		//TODO:
		
		// check the number of operands 
		// if not enough operands, produce an error in the error table
		if (line.size() < 3)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 3)
		{
			String reg1 = line.get(1); 
			String imm = line.get(2);
			
			if (reg1 == "$r0" || reg1 == "$R0")
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("33");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc.concat("00000");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseADDICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$r0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("10");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseADDIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			// create the binary encoding
			String binEnc = converter.hexToBinary("11");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseSUBICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			// create the binary encoding
			String binEnc = converter.hexToBinary("12");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseSUBIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			// create the binary encoding
			String binEnc = converter.hexToBinary("13");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}
	
	public void parseMULICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			// create the binary encoding
			String binEnc = converter.hexToBinary("14");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseMULIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("15");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseDIVICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{

		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("16");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseDIVIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("17");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("34");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}	
	}

	public void parseXORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$r0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("35");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}
	
	public void parseNORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}

			// create the binary encoding
			String binEnc = converter.hexToBinary("37");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}
	
	public void parseANDICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}

			// create the binary encoding
			String binEnc = converter.hexToBinary("37");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}
	
	public void parseSRVCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String imm = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}

			// create the binary encoding
			String binEnc = converter.hexToBinary("3D");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + reg1.charAt(2);
			binEnc = binEnc + reg2.charAt(2);
			binEnc = binEnc.concat("00");
			binEnc = binEnc.concat(converter.decimalToBinary(imm));
			
			// put data into the infoholder for future use
			lc++;
			outputData.AddLine(lc, binEnc);
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
			
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}

	public void parseADDCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		
		// if not enough operands, produce an error in the error table
		if (line.size() < 4)
		{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("missingParameter");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
		}
		
		else if (line.size() == 4)
		{
			
			// store the string representing each operand
			String reg1 = line.get(1); 
			String reg2 = line.get(2);
			String reg3 = line.get(3);
			
			// if the first register is r0, give an error
			if (reg1.equals("$0"))
			{
			ErrorData error = new ErrorData();
			String code = errorsPossible.getErrorCode("storeValueInRegZero");
			String message = errorsPossible.getErrorMessage(code);
			error.add(lineNumber,Integer.parseInt(code), message);
			errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			else if(!(reg1.equals("$1") || reg1.equals("$2") ||
					  reg1.equals("$3") || reg1.equals("$4") ||
					  reg1.equals("$5") || reg1.equals("$6") || reg1.equals("$7") ))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			if(!(reg2.equals("$0") ||  reg2.equals("$1") ||
					 reg2.equals("$2") || reg2.equals("$3") || reg2.equals("$4") ||
					 reg2.equals("$5") || reg2.equals("$6") || reg2.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// checking for correct register usage [only between 1 and 7 allowed]
			if(!(reg3.equals("$0") ||  reg3.equals("$1") ||
					 reg3.equals("$2") || reg3.equals("$3") || reg3.equals("$4") ||
					 reg3.equals("$5") || reg3.equals("$6") || reg3.equals("$7")))
			{
				
				// if trying to use an incorrect register number, give an error
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("wrongRegSyntax");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
			}
			
			// create the binary encoding
			String binEnc = converter.hexToBinary("01");
			binEnc = binEnc.concat("00");
			binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
			binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
			binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
			binEnc = binEnc.concat("000000");
			binEnc = binEnc.concat(converter.decimalToBinary("20"));
		}
		
		// if too many operands, produce the corresponding 
		// error in the errortable
		else 
		{		
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("tooManyParameters");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);
		}
	}
	
	


}
