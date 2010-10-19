import java.util.ArrayList;

/**
 * 
 * @author Mike and Kyle
 * 
 */
public class Parser implements ParserInterface {
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
	public Parser(ErrorTable errorT, DirectiveTable directiveT, InstructTable instructT) {
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

	/**
	 * 
	 */
	@Override
	public Boolean parse(ArrayList<String> line, int lineNumber, ErrorOut errorsFound)
	{
		int size = line.size();
		String token = line.get(0);
		
		//check if first token is a directive
		if (directives.hasDirective(token.toLowerCase()))
		{
			
			//.start directive
			if (token.compareToIgnoreCase(".start") == 0)
			{
				
				// check the number of correct items in the syntax
				if (size == 3)
				{
					
					//if syntax is correct, set program name and lc start point
					programName = line.get(1);
					int startPoint = Integer.parseInt(line.get(2));
					lc = startPoint;
					
				}
				
				//if the syntax is incorrect, create ErrorData object
				else
				{	
					ErrorData error = new ErrorData();
					String code;
					
					//get the correct error code depending on the violation of syntax
					if (size < 3)
					{
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					//add the errorData object to errorsFound
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
			//.end directive
			else if (token.compareToIgnoreCase(".end") == 0)
			{
				
				//if the syntax has the correct amount of tokens
				if (size == 2)
				{
					
					//compare the program name to the one entered in the .start directive
					//if the name doesn't match, get error code
					if ( programName.compareTo(line.get(1)) != 0)
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("invalid program name");
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
					if (size < 2)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
				//flag that the .end was found
				endOfProgram = true;
			}
			
			//.data directive
			else if (token.compareToIgnoreCase(".data") == 0)
			{
				
				//check syntax
				if (size > 1)
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//.text directive
			else if (token.compareToIgnoreCase(".text") == 0)
			{
				
				//check syntax
				if (size > 1)
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
			//int.data directive
			else if (token.compareToIgnoreCase("int.data") == 0)
			{
				
				//check if syntax is correct
				if (size == 2)
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
						String code = errorsPossible.getErrorCode("invalid operand");
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
				
			}
			
			//str.data directive
			else if (token.compareToIgnoreCase("str.data") == 0)
			{
				if (size == 2)
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
						String code = errorsPossible.getErrorCode("missing quotes");
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
					if (size < 2)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
				
			}
			
			//hex.data directive
			else if (token.compareToIgnoreCase("hex.data") == 0)
			{
				
				//check number of parameters
				if (size == 2)
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
								String code = errorsPossible.getErrorCode("invalid operand");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						//if it is not hex, add error
						else
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("data not hex");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
					
					}
					
					//if it is missing quotes, add error
					else
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("missing quotes");
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
					if (size < 2)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
			//bin.data directive
			else if (token.compareToIgnoreCase("bin.data") == 0)
			{
				
				//check number of parameters
				if (size == 2)
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
								String code = errorsPossible.getErrorCode("invalid operand");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						//if it is not binary, add error
						else
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("data not binary");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
					
					}
					
					//if it is missing quotes, add error
					else
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("missing quotes");
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
					if (size < 2)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//adr.data directive
			else if (token.compareToIgnoreCase("adr.data") == 0)
			{
				
				//check syntax
				if ( size == 2)
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
					if (size < 2)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//adr.exp directive
			else if (token.compareToIgnoreCase("adr.exp") == 0)
			{
				
				//check syntax
				if ((size == 2))
				{
					
					//complete in pass 2
					
				}
				
				//if invalid syntax
				else
				{
					ErrorData error = new ErrorData();
					String code;
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
			//ent directive
			else if (token.compareToIgnoreCase("ent") == 0)
			{
				
				//check syntax
				if ((size > 1) && (size < 6))
				{
					
					//check if variables are defined
					for (int inc = 1; inc < size; inc ++)
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
			}
			
			//ext directive
			else if (token.compareToIgnoreCase("exp") == 0)
			{
				
				//check syntax
				if ((size > 1) && (size < 6))
				{
					
					//check if variables are defined
					//if they are, flag error
					//if not, add them to table
					for (int inc = 1; inc < size; inc ++)
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
							String code = errorsPossible.getErrorCode("variable already defined");
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//nop directive
			else if (token.compareToIgnoreCase(".start") == 0)
			{
				
				//check for extra parameters
				if (size > 1)
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
				//put nop in data
				outputData.AddLine(lc, NOPBINARY);
				
				//increase the lc by 1 word
				lc++;
				
			}
			
			//exec.start
			else if (token.compareToIgnoreCase("exec.start") == 0)
			{
				
				//check syntax
				if (size == 2)
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//mem.skip directive
			else if (token.compareToIgnoreCase("mem.skip") == 0)
			{
				
				//check syntax
				if (size == 2)
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
						String code = errorsPossible.getErrorCode("invalid integer");
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//debug directive
			else if (token.compareToIgnoreCase("debug") == 0)
			{
				
				//check syntax
				if (size == 2)
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
						String code = errorsPossible.getErrorCode("invalid boolean");
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
					if (size == 1)
					{	
						code = errorsPossible.getErrorCode("missing parameter");
					}
					else
					{
						code = errorsPossible.getErrorCode("too many parameters");
					}
					
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
			//all other directives have a required label
			//if they get to here, there is a missing label
			else
			{
				ErrorData error = new ErrorData();
				String code = errorsPossible.getErrorCode("missing label");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,Integer.parseInt(code), message);
				errorsFound.add(error);

			}
			
			
		}
		
		//check if first token is an instruction
		else if ( commands.hasInstruction(token.toLowerCase()))
		{
			// parsing for instructions
			
			//check if first token is an instruction
			 if ( commands.hasInstruction(token.toLowerCase()))
			{
				
				/*
				 * For the instructions, the parser will filter by the type of instruction,
				 * then by the actual instruction itself. This algorithm is more efficiency
				 * minded than anything else. 
				 */	
				
				
				// this variable holds the type of instruction for parser filtering.
				String insType = commands.getInstructionType(token.toLowerCase());

				// this variable holds the opcode for the instruction to be parsed
				String insOp = commands.getInstructionOpcode(line.get(1));
				
				// save the number of operands for future parsing use
				 int opsCount = line.size() - 2;
				
				// the binary encoding for the instruction
				String binEnc = null;
				
				/* 
				 * Check for what type the instruction belongs to. According to the 
				 * type of instruction, different procedures are followed.
				 */
				if (insType.compareToIgnoreCase("I-Type") == 0)
				{
					/*
						I-type instructions will be parsed here. This is where the parser
						will check which opcode the line has, and updates each table 
						accordingly. 
					*/
					
					//parsing for the "load immediate" instruction
					if(insOp.compareToIgnoreCase("LUI") == 0)
					{
						
						// check the number of operands 
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							String reg1 = line.get(2); 
							String imm = line.get(3);
							
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("33");
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
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "add immediate" instruction
					if(insOp.compareToIgnoreCase("ADDI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("10"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					
					}
					
					//parsing for the "add immediate unsigned" instruction
					else if(insOp.compareToIgnoreCase("ADDIU") == 0)
					{

						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("11"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "subtract immediate" instruction
					else if(insOp.compareToIgnoreCase("SUBI") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("12"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "subtract immediate unsigned" instruction
					else if(insOp.compareToIgnoreCase("SUBIU") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("13"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "multiply immediate" instruction
					else if(insOp.compareToIgnoreCase("MULI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("14"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "multiply immediate unsigned" instruction
					else if(insOp.compareToIgnoreCase("MULIU") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("15"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "divide immediate" instruction
					else if(insOp.compareToIgnoreCase("DIVI") == 0)
					{

						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("16"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "divide immediate unsigned" instruction
					else if(insOp.compareToIgnoreCase("DIVIU") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("17"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "or immediate" instruction
					else if(insOp.compareToIgnoreCase("ORI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("34"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}	
					}
					
					//parsing for the "exclusive or immediate" instruction
					else if(insOp.compareToIgnoreCase("XORI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc.concat(converter.hexToBinary("35"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "nor immediate" instruction
					else if(insOp.compareToIgnoreCase("NORI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc.concat(converter.hexToBinary("37"));
							binEnc.concat("00");
							binEnc = binEnc + reg1.charAt(2);
							binEnc = binEnc + reg2.charAt(2);
							binEnc.concat("00");
							binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}	
					}
					//parsing for the "and immediate" instruction
					else if(insOp.compareToIgnoreCase("ANDI") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc = converter.hexToBinary("37");
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
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					//parsing for the "set register values" instruction
					else if(insOp.compareToIgnoreCase("SRV") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc = converter.hexToBinary("3D");
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
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
				}
			
				// Reg2Reg2Reg instructions will be parsed here
				else if (insType.compareToIgnoreCase("Reg2Reg2Reg") == 0)
				{
				
					// "add" instructions parsed here
					if (insOp.compareToIgnoreCase("ADD") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
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
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "add unsigned" commands parsed here
					if (insOp.compareToIgnoreCase("ADDU") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("21"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "subtract" commands parsed here
					if (insOp.compareToIgnoreCase("SUB") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("22"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "subtract" commands parsed here
					if (insOp.compareToIgnoreCase("SUBU") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("23"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "multiply" commands parsed here
					if (insOp.compareToIgnoreCase("MUL") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("18"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "multiply unsigned" commands parsed here
					if (insOp.compareToIgnoreCase("MULU") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("19"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "dvide" commands parsed here
					if (insOp.compareToIgnoreCase("DIV") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("1A"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "divide unsigned" commands parsed here
					if (insOp.compareToIgnoreCase("DIVU") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("1B"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
						
						
					}
					
					// "nor" commands parsed here
					if (insOp.compareToIgnoreCase("NOR") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						// if the number of operands is correct,  
						// parse them each for register errors
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("27"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
					// "power" commands parsed here
					if (insOp.compareToIgnoreCase("PWR") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("1C"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "shift left logical" commands parsed here
					if (insOp.compareToIgnoreCase("SLL") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							//binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat(converter.decimalToBinary(imm));
							binEnc = binEnc.concat(converter.decimalToBinary("20"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "shift right logical" commands parsed here
					if (insOp.compareToIgnoreCase("SRL") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc = converter.hexToBinary("01");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							//binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat(converter.decimalToBinary(imm));
							binEnc = binEnc.concat(converter.decimalToBinary("02"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
					// "shift right arithmetic" commands parsed here
					if (insOp.compareToIgnoreCase("SRA") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String imm = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							//binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat(converter.decimalToBinary(imm));
							binEnc = binEnc.concat(converter.decimalToBinary("18"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
					// "and" commands parsed here
					if (insOp.compareToIgnoreCase("AND") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("24"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
					// "or" commands parsed here
					if (insOp.compareToIgnoreCase("OR") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("25"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
				
					// "xor" commands parsed here
					if (insOp.compareToIgnoreCase("XOR") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String reg3 = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg3 == "$r1" || reg3 == "$R1" ||
								 reg3 == "$r2" || reg3 == "$R2" ||
								 reg3 == "$r3" || reg3 == "$R3" ||
								 reg3 == "$r4" || reg3 == "$R4" ||
								 reg3 == "$r5" || reg3 == "$R5" ||
								 reg3 == "$r6" || reg3 == "$R6" ||
								 reg3 == "$r7" || reg3 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("02");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("26"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
						
					// "jump register" commands will be parsed here
					if (insOp.compareToIgnoreCase("JR") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 1)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 1)
						{
							String reg1 = line.get(2); 
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("03");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
						//	binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
						//	binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000000000");
							binEnc = binEnc.concat(converter.decimalToBinary("08"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// "jump" commands will be parsed here
					if (insOp.compareToIgnoreCase("J") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 1)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 1)
						{
							String reg1 = line.get(2); 
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// create the binary encoding
							binEnc = converter.hexToBinary("06");
							binEnc = binEnc.concat("00");
							binEnc = binEnc + converter.decimalToBinary(reg1.substring(2));
							//binEnc = binEnc + converter.decimalToBinary(reg2.substring(2));
							//binEnc = binEnc + converter.decimalToBinary(reg3.substring(2));
							binEnc = binEnc.concat("000000");
							binEnc = binEnc.concat(converter.decimalToBinary("18"));
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// dump commands will be parsed here
					if (insOp.compareToIgnoreCase("DUMP") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							String amt1 = line.get(2);
							String amt2 = line.get(3);
							String amt3 = line.get(4);
							
							// TODO parse these? ..... 
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
				}
				
				// S-Type instructions will be parsed here
				else if (insType.compareToIgnoreCase("S-Type") == 0)
				{
				
					// parse the Jump On Equal instruction
					 if(insOp.compareToIgnoreCase("JEQ") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{
							
							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// if the memRef section is 
							
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("20");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
					}
					
					// parse the Jump Not Equal instruction
					else  if(insOp.compareToIgnoreCase("JNE") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("21");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Jump Greather Than instruction
					else  if(insOp.compareToIgnoreCase("JGT") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("22");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Jump Less Than instruction
					else  if(insOp.compareToIgnoreCase("JLT") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("23");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Jump Less than Or Equal instruction
					else  if(insOp.compareToIgnoreCase("JLE") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("24");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{							
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Jump And Link instruction
					else  if(insOp.compareToIgnoreCase("JAL") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("07");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Add Register and Storage instruction
					else  if(insOp.compareToIgnoreCase("ADDS") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("1A");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Subtract Register and Storage instruction
					else  if(insOp.compareToIgnoreCase("SUBS") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("1B");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Multiply Register and Storage instruction
					else  if(insOp.compareToIgnoreCase("MULS") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("1C");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parse the Divide Register and Storage instruction
					else  if(insOp.compareToIgnoreCase("DIVS") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 3)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 3)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String reg2 = line.get(3);
							String addr = line.get(4);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							if(!(reg2 == "$r1" || reg2 == "$R1" ||
								 reg2 == "$r2" || reg2 == "$R2" ||
								 reg2 == "$r3" || reg2 == "$R3" ||
								 reg2 == "$r4" || reg2 == "$R4" ||
								 reg2 == "$r5" || reg2 == "$R5" ||
								 reg2 == "$r6" || reg2 == "$R6" ||
								 reg2 == "$r7" || reg2 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("1D");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc + reg2.charAt(2);
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "load address of word into register" instruction
					else  if(insOp.compareToIgnoreCase("LA") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}

							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("38");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "load word address" instruction
					else  if(insOp.compareToIgnoreCase("LW") == 0)
					{
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("30");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "store word address " instruction
					else  if(insOp.compareToIgnoreCase("SW") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("27");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "load negaticve word " instruction
					else  if(insOp.compareToIgnoreCase("LNW") == 0)
					{
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("31");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "load word immediate " instruction
					else  if(insOp.compareToIgnoreCase("LWI") == 0)
					{
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("32");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "store address in word" instruction
					else  if(insOp.compareToIgnoreCase("SA") == 0)
					{
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("39");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "and register to storage" instruction
					else  if(insOp.compareToIgnoreCase("ANDS") == 0)
					{
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("3A");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for the "or register to storage" instruction
					else  if(insOp.compareToIgnoreCase("ORS") == 0)
					{
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{

							// store the string representing each operand
							String reg1 = line.get(2); 
							String addr = line.get(3);
							
							// if the first register is r0, give an error
							if (reg1 == "$r0" || reg1 == "$R0")
							{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
							}
							
							// checking for correct register usage [only between 1 and 7 allowed]
							else if(!(reg1 == "$r1" || reg1 == "$R1" ||
									  reg1 == "$r2" || reg1 == "$R2" ||
									  reg1 == "$r3" || reg1 == "$R3" ||
									  reg1 == "$r4" || reg1 == "$R4" ||
									  reg1 == "$r5" || reg1 == "$R5" ||
									  reg1 == "$r6" || reg1 == "$R6" ||
									  reg1 == "$r7" || reg1 == "$R7" ))
							{
								
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid register syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							// if addr is in the symbol table, pull that value and encode it
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLength(addr);
							
								// create the binary encoding
								binEnc = converter.hexToBinary("3B");
								binEnc = binEnc.concat("01");
								binEnc = binEnc + reg1.charAt(2);
								binEnc = binEnc.concat("00000");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				}
				// Jump instructions will be parsed here
				else if (insType.compareToIgnoreCase("Jump") == 0)
				{
					
					//parsing for the HALT instruction 
					if(insOp.compareToIgnoreCase("HALT") == 0)
					{
						
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 1)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 1)
						{
						
							// this object saves the value of the integer value in the hald instruction
							String haltAt = line.get(2);
							
							// holds value for * operation if it applies
							int value = 0;
							
							//booleans for iterative error checking
							boolean errd = false;
							boolean star = false;
							
							
							if(haltAt.charAt(0) == '*')
							{
								
								star = true;
								// adds binary at lc to numeric value to be
								if(haltAt.charAt(1) == '-')
								{
									String vin = outputData.findBinaryByLC(lc, 0);
									value = Integer.parseInt(converter.binaryToDecimal(vin)) 
									- Integer.parseInt(haltAt.substring(2,haltAt.length()-1));
								}
								
								// subtract numeric value from binary representation at lc
								else if (haltAt.charAt(1) == '+')
								{
									String vin = outputData.findBinaryByLC(lc, 0);
									value = Integer.parseInt(converter.binaryToDecimal(vin)) 
									- Integer.parseInt(haltAt.substring(2,haltAt.length()-1));
								}
								// if neither case is present, give error
								else 
								{
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("invalid star syntax");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
								}
							}
							
							// if the operand isn't a label, check to see if it is a number 
							else if (!(symbols.symbolIsDefined(haltAt)))
							{
								for (int i =0; i<haltAt.length(); i++)
								{
									if (!Character.isDigit(haltAt.charAt(i)));
									{
										// give error here
										errd = true;
									}
									if(errd)
									{
										i = haltAt.length();
									}
								}
							}
							if (!errd)
							{
							// checking bound limit for integer value
							if (!(0 <= Integer.parseInt(haltAt) && Integer.parseInt(haltAt) <= 255))
							{
								ErrorData error = new ErrorData();
								
								//The range violation shows what the bounds should have been.
								String code = errorsPossible.getErrorCode("halt value range violation (0 <= n <= 255)");
								
								// adds the error to the list of errors found for future printing out.
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						
							
							// create the binary encoding
							binEnc = converter.hexToBinary("08");
							binEnc = binEnc.concat("00000000");
							// TODO modify for bit count
							binEnc = binEnc.concat(converter.decimalToBinary(haltAt));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
							}
						}
					}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
				}
				
				// IO-Type instructions will be parsed here
				else if (insType.compareToIgnoreCase("IO-Type") == 0)
				{
				
					// parsing for INN instruction
					if(insOp.compareToIgnoreCase("INN") == 0)
					{
		
						// check the number of operands 
						 
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							String num = line.get(2);
							String addr = line.get(3);
							
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("20");
								binEnc = binEnc.concat("01001");
							
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for INC instruction 
					else if(insOp.compareToIgnoreCase("INC") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							String num = line.get(2);
							String addr = line.get(3);
							
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("20");
								binEnc = binEnc.concat("01001");
							
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for OUTN instruction
					else if(insOp.compareToIgnoreCase("OUTN") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							String num = line.get(2);
							String addr = line.get(3);
							
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("20");
								binEnc = binEnc.concat("01001");
							
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
					// parsing for OUTC instruction
					else if(insOp.compareToIgnoreCase("OUTC") == 0)
					{
					
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							String num = line.get(2);
							String addr = line.get(3);
							
							if(symbols.symbolIsDefined(addr))
							{
								int len = symbols.GetLocation(addr);
							
							
								// create the binary encoding
								binEnc = converter.hexToBinary("20");
								binEnc = binEnc.concat("01001");
							
								binEnc = binEnc.concat("00");
								binEnc = binEnc.concat(converter.decimalToBinary(Integer.toString(len)));
							
								// put data into the infoholder for future use
								lc++;
								outputData.AddLine(lc, binEnc);
							}
							
							else 
							{
								// if trying to use an incorrect register number, give an error
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("This symbol is not recognized");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
				
			
					// "outni" instructions parsed here
					else if(insOp.compareToIgnoreCase("OUTNI") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							
							// store the string representing each operand
							String num = line.get(2); 
							String imm = line.get(3);
							
							
							// create the binary encoding
							binEnc = converter.hexToBinary("20");
							binEnc = binEnc.concat("00000");
							binEnc = binEnc.concat(converter.decimalToBinary(num));
							binEnc = binEnc.concat(converter.decimalToBinary(imm));
							
							// put data into the infoholder for future use
							lc++;
							outputData.AddLine(lc, binEnc);
							}
							
							// else 
							// {
								// // if trying to use an incorrect register number, give an error
								// ErrorData error = new ErrorData();
								// String code = errorsPossible.getErrorCode("This symbol is not recognized");
								// String message = errorsPossible.getErrorMessage(code);
								// error.add(lineNumber,Integer.parseInt(code), message);
								// errorsFound.add(error);
							// }
							
							//TODO parse immediate value
						
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					
					}
					
					// "outci" instructions parsed here				
					else if(insOp.compareToIgnoreCase("OUTCI") == 0)
					{
						
						
						// if not enough operands, produce an error in the error table
						if (opsCount < 2)
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("missing parameter");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);
						}
						
						else if (opsCount == 2)
						{
							
							// store the string representing each operand
							String num = line.get(2); 
							String imm = line.get(3);
							
							// create the binary encoding
							binEnc = converter.hexToBinary("20");
							binEnc = binEnc.concat("00000");
							binEnc = binEnc.concat(converter.decimalToBinary(num));
							binEnc = binEnc.concat(converter.decimalToBinary(imm));
							
						}
						
						// if too many operands, produce the corresponding 
						// error in the errortable
						else 
						{		
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
						}
					}
					
				}	
				

			}
			
			 // token isn't an instruction, it must be a symbol; 
			 // remove it from the line and parse the rest of the line
			 else 
			{
				token = line.remove(0);
				boolean parseRec = parse(line,lineNumber,errorsFound);
				Symbol sym = new Symbol(token,lc,32);
				symbols.defineSymbol(sym);
			}
			
			
			
		}
		
		//token must be a symbol. handle it
		else
		{
			//check if it is defined
			if (symbols.symbolIsDefined(token))
			{
				
			}
			
			//else check if this is a label
			else
			{
				if (size > 1)
				{
				
					//if it is a label, check for what
					if (directives.hasDirective(line.get(1).toLowerCase()))
					{
						
						
						//int.data with label
						if (line.get(1).compareToIgnoreCase("int.data") == 0)
						{
							
							//check if syntax is correct
							if (size == 3)
							{
								
								// check if int value is in accepted range
								Integer value = Integer.parseInt(line.get(2));
								if ((value < 231) && (value > -231))
								{
									String code = converter.decimalToBinary(value.toString());
									outputData.AddLine(lc, code);
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);
									
									//increase the lc by 1 word
									lc++;
								}
								
								//if the int is invalid create error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("invalid operand");
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
								if (size == 2)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
						}
						
						//str.data with label
						else if (token.compareToIgnoreCase("str.data") == 0)
						{
							if (size == 3)
							{
								String value = line.get(2);
								if (value.startsWith("'") && value.endsWith("'"))
								{
									
									//remove quotes
									value = value.substring(1, value.length() - 1);
									
									
									//convert to binary
									String valueBin;
									int inc = 0;
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//var to count length for symbol table
									int symLength = 0;
									
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
											
											//increase symLength
											symLength++;
										}
										
										//else, take a substring of four, convert and add to data
										//also update the lc
										else
										{
											valueBin = value.substring(inc, inc + 3);
											valueBin = converter.asciiToBinary(valueBin);
											outputData.AddLine(lc, valueBin);
											lc++;	
											
											//increase symLength
											symLength++;
										}
										
										//move inc past substring
										inc = inc + 4;
										
									}
									
									//set length of symbol
									newSymbol.setLength(symLength);
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);	
								}
								
								// if it is missing quotes, add error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("missing quotes");
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							
							
						}
						
						//hex.data with label
						else if (token.compareToIgnoreCase("hex.data") == 0)
						{
							
							//check number of parameters
							if (size == 3)
							{
								String value = line.get(2);
								
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
									if (hexFlag){
										
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
											String code = errorsPossible.getErrorCode("invalid operand");
											String message = errorsPossible.getErrorMessage(code);
											error.add(lineNumber,Integer.parseInt(code), message);
											errorsFound.add(error);
										}
									}
									
									//if it is not hex, add error
									else
									{
										ErrorData error = new ErrorData();
										String code = errorsPossible.getErrorCode("data not hex");
										String message = errorsPossible.getErrorMessage(code);
										error.add(lineNumber,Integer.parseInt(code), message);
										errorsFound.add(error);
									}
								
								}
								
								//if it is missing quotes, add error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("missing quotes");
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
						}
						
						//bin.data with label
						else if (token.compareToIgnoreCase("bin.data") == 0)
						{
							
							//check number of parameters
							if (size == 3)
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
											
											//create symbol
											Symbol newSymbol = new Symbol();
											newSymbol.setLabel(token);
											newSymbol.setLocation(lc);
											newSymbol.setUsage("Label");
											
											//enter symbol in symbol table
											symbols.defineSymbol(newSymbol);
											
											//increase the lc by 1 word
											lc++;
										}
										
										// if it has too many characters, add error
										else
										{
											ErrorData error = new ErrorData();
											String code = errorsPossible.getErrorCode("invalid operand");
											String message = errorsPossible.getErrorMessage(code);
											error.add(lineNumber,Integer.parseInt(code), message);
											errorsFound.add(error);
										}
									}
									
									//if it is not binary, add error
									else
									{
										ErrorData error = new ErrorData();
										String code = errorsPossible.getErrorCode("data not binary");
										String message = errorsPossible.getErrorMessage(code);
										error.add(lineNumber,Integer.parseInt(code), message);
										errorsFound.add(error);
									}
								
								}
								
								//if it is missing quotes, add error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("missing quotes");
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}	
							
						}
						
						//adr.data with label
						else if (token.compareToIgnoreCase("adr.data") == 0)
						{
							
							//check syntax
							if ( size == 3)
							{
								String label = line.get(2);
								
								//check if symbol is defined
								if (symbols.symbolIsDefined(label))
								{
									//if it is, add to data
									outputData.AddLine(lc, String.valueOf(symbols.GetLocation(label)));
									
									//increase the lc by 1 word
									lc++;
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);
									
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						//adr.exp with label
						else if (token.compareToIgnoreCase("adr.exp") == 0)
						{
							
							//check syntax
							if (size == 3)
							{
								
								//complete in pass 2
							}
							
							//if invalid syntax
							else
							{
								ErrorData error = new ErrorData();
								String code;
								if (size == 1)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
						}
						
						//nop with label
						else if (token.compareToIgnoreCase("nop") == 0)
						{
							
							//check for extra parameters
							if (size > 2)
							{
								ErrorData error = new ErrorData();
								String code = errorsPossible.getErrorCode("too many parameters");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
							//create symbol
							Symbol newSymbol = new Symbol();
							newSymbol.setLabel(token);
							newSymbol.setLocation(lc);
							newSymbol.setUsage("Label");
							
							//enter symbol in symbol table
							symbols.defineSymbol(newSymbol);
							
							//put nop in data
							outputData.AddLine(lc, NOPBINARY);
							
							//increase the lc by 1 word
							lc++;
						}
						
						//mem.skip with label
						else if (token.compareToIgnoreCase("mem.skip") == 0)
						{
							
							//check syntax
							if (size == 3)
							{
								
								//check if mem location is valid
								String value = line.get(2);
								
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
								
								//if valid integer, create symbol and move lc
								if (intFlag && ((Integer.parseInt(value) + lc) < 65536 ) && (Integer.parseInt(value) > 0))
								{
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);
									
									//move lc
									lc = lc + Integer.parseInt(value);
								}
								
								//else add error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("invalid integer");
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
								if (size < 3 )
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						//equ with label
						else if (token.compareToIgnoreCase("equ") == 0)
						{
							
							//check syntax
							if ( size == 3)
							{
								String label = line.get(2);
								
								//check if symbol is defined
								if (symbols.symbolIsDefined(label))
								{
									//if it is, add to data
									outputData.AddLine(lc, String.valueOf(symbols.GetLocation(label)));
									
									//increase the lc by 1 word
									lc++;
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);
									
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
						}
						
						//equ.exp with label
						else if (token.compareToIgnoreCase("equ.exp") == 0)
						{
							
							//check syntax
							if (size == 3)
							{
								
								//complete in pass 2
							}
							
							//if invalid syntax
							else
							{
								ErrorData error = new ErrorData();
								String code;
								if (size == 1)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}
							
						}
						
						//reset.lc with label
						else if (token.compareToIgnoreCase("reset.lc") == 0)
						{
							
							//if there are the correct number of arguments
							if ( size == 3)
							{
								
								//convert string to integer
								int value = Integer.parseInt(line.get(2));
								
								// if the value is valid, change lc and add symbol to table
								if (value > lc)
								{
									
									//change lc
									lc = value;
									
									//create symbol
									Symbol newSymbol = new Symbol();
									newSymbol.setLabel(token);
									newSymbol.setLocation(lc);
									newSymbol.setUsage("Label");
									
									//enter symbol in symbol table
									symbols.defineSymbol(newSymbol);
									
								}
								
								//if the new position is invalid, add error
								else
								{
									ErrorData error = new ErrorData();
									String code = errorsPossible.getErrorCode("invalid location");
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
								if (size < 3)
								{	
									code = errorsPossible.getErrorCode("missing parameter");
								}
								else
								{
									code = errorsPossible.getErrorCode("too many parameters");
								}
								
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,Integer.parseInt(code), message);
								errorsFound.add(error);
							}	
							
							
						}
						
						//all other directives do not take a label
						//if they get to here, there is a label out of place
						else
						{
							ErrorData error = new ErrorData();
							String code = errorsPossible.getErrorCode("misplaced label");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,Integer.parseInt(code), message);
							errorsFound.add(error);

						}
						
					}
					
					//if it isn't a label
					else
					{
						ErrorData error = new ErrorData();
						String code = errorsPossible.getErrorCode("unknown symbol");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,Integer.parseInt(code), message);
						errorsFound.add(error);
					}
				}
				else
				{
					ErrorData error = new ErrorData();
					String code = errorsPossible.getErrorCode("unknown command");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,Integer.parseInt(code), message);
					errorsFound.add(error);
				}
				
			}
			
		}
		
		

		return endOfProgram;
		
	}

	@Override
	public ArrayList<String> getUndefinedVariables() {

		return undefinedVariables;
	}

	@Override
	public SymbolTable getSymbols() {
		return symbols;
	}

	@Override
	public InfoHolder getBinaryData() {
		return outputData;
	}

}
