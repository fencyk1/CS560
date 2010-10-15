import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mike and Kyle
 *
 */
public class Parser implements ParserInterface {
	private DirectiveTableInterface directives;
	private ErrorOutInterface errorsFound;
	private ErrorTableInterface errorsPossible;
	private InstructTableInterface commands;
	private SymbolTableInterface symbols;
	private Boolean endOfProgram;
	private String programName;
	private List<String> undefinedVariables;
	private int lc;
	private InfoHolder outputData;
	private static final String NOPBINARY = "00001000000000000000000000000000";

	
	

	
	
	/**
	 * The constructer takes in the list of found errors, the master error list,
	 * the directives list, and the instructions list. It stores these in private
	 * variables. It then creates a symbol table(type SymbolTable).
	 * 
	 * @param errorOut is the list of all errors found in the source code
	 * @param errorT is the master list of all possible errors
	 * @param directiveT is the master list of all the directives
	 * @param instructT is the master list of all the instructions
	 */
	public Parser(ErrorOutInterface errorOut, ErrorTableInterface errorT,
			DirectiveTableInterface directiveT, InstructTableInterface instructT){
		commands = instructT;
		directives = directiveT;
		errorsFound = errorOut;
		errorsPossible = errorT;		
		
		//set end of program flag to false
		endOfProgram = false;
		
		//initialize undefinedVariables
		undefinedVariables = new ArrayList<String>();
		
		//maybe make errorout here?
		//make symbol table here
		symbols = new SymbolTableInterface();
		
		//make InfoHolder to store binary data
		outputData = new InfoHolder();
		
	}

	/**
	 * 
	 */
	@Override
	public Boolean parse(ArrayList<String> line, int lineNumber) {
		int size = line.size();
		String token = line.get(0);
		
		
		//check if first token is a directive
		if (directives.hasDirective(token.toLowerCase())){
			
			//.start directive
			if (token.compareToIgnoreCase(".start") == 0){
				//number of correct items in the syntax
				if (size == 3) {
					programName = line.get(1);
					int startPoint = Integer.parseInt(line.get(2));
					lc = startPoint;
					
				}
				else {
					if (size < 3) {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					else {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("too many parameters");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
				}
				
			}
			//.end directive
			else if (token.compareToIgnoreCase(".end") == 0){
				if (size == 2) {
					if ( programName.compareTo(line.get(1)) != 0) {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("invalid program name");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					
				}
				else {
					if (size < 2) {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					else {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("too many parameters");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
				}
				
				endOfProgram = true;
			}
			//.data directive
			else if (token.compareToIgnoreCase(".data") == 0) {
				
			}
			//.text directive
			else if (token.compareToIgnoreCase(".text") == 0) {
				
			}
			//int.data directive
			else if (token.compareToIgnoreCase("int.data") == 0) {
				if (size == 2){
					Integer value = Integer.parseInt(line.get(1));
					if ((value < 231) && (value > -231)){
						outputData.AddLine(lc, value.toString());
					}
					else {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("invalid opperand");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else if (size == 1){
					ErrorDataInterface error = new ErrorDataInterface();
					int code = errorsPossible.getErrorCode("missing parameter");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				else {
					ErrorDataInterface error = new ErrorDataInterface();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				//increase the lc by 1 word
				lc++;
			}
			//str.data directive
			else if (token.compareToIgnoreCase("str.data") == 0) {
				
			}
			//hex.data directive
			else if (token.compareToIgnoreCase("hex.data") == 0) {
				
			}
			//bin.data directive
			else if (token.compareToIgnoreCase("bin.data") == 0) {
				//check number of parameters
				if (size == 2){
					String value = line.get(1);
					//check for single quotes
					if (value.startsWith("'") && value.endsWith("'")){
						Boolean binFlag = true;
						int inc = 0;
						//loop to check if it is binary
						while ( inc < value.length()){
							if ((value.charAt(inc) != 0) && (value.charAt(inc) != 1)){
								binFlag = false;
							}
							inc++;
						}
						//if it is binary, check length
						if ( binFlag){
							value = value.substring(1, value.length() - 1);
							if (value.length() <= 32 && value.length() >= 1){
								//add to data
								outputData.AddLine(lc, value.toString());
							}
							else {
								ErrorDataInterface error = new ErrorDataInterface();
								int code = errorsPossible.getErrorCode("invalid opperand");
								String message = errorsPossible.getErrorMessage(code);
								error.add(lineNumber,code, message);
								errorsFound.add(error);
								//put nop in data
								outputData.AddLine(lc, NOPBINARY);
							}
						}
						else {
							ErrorDataInterface error = new ErrorDataInterface();
							int code = errorsPossible.getErrorCode("data not binary");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
							//put nop in data
							outputData.AddLine(lc, NOPBINARY);
						}
					
					}
					else {
						ErrorDataInterface error = new ErrorDataInterface();
						int code = errorsPossible.getErrorCode("missing quotes");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else if (size == 1){
					ErrorDataInterface error = new ErrorDataInterface();
					int code = errorsPossible.getErrorCode("missing parameter");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				else {
					ErrorDataInterface error = new ErrorDataInterface();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
				}
				
				//increase the lc by 1 word
				lc++;
			}
			//adr.data directive
			else if (token.compareToIgnoreCase("adr.data") == 0) {
				
			}
			//adr.exp directive
			else if (token.compareToIgnoreCase("adr.exp") == 0) {
				
			}
			//ent directive
			else if (token.compareToIgnoreCase("ent") == 0) {
				
			}
			//ext directive
			else if (token.compareToIgnoreCase("exp") == 0) {
				
			}
			//nop directive
			else if (token.compareToIgnoreCase(".start") == 0) {
				//check for extra parameters
				if (size > 1){
					ErrorDataInterface error = new ErrorDataInterface();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
				}
				//put nop in data
				outputData.AddLine(lc, NOPBINARY);
				//increase the lc by 1 word
				lc++;
				
			}
			//exec.start
			else if (token.compareToIgnoreCase("exec.start") == 0) {
				
			}
			//mem.skip directive
			else if (token.compareToIgnoreCase("mem.skip") == 0) {
				
			}
			//debug directive
			else if (token.compareToIgnoreCase("debug") == 0) {
				
			}
			//all other directives have a required label
			//if they get to here, there is a missing label
			else {
				ErrorDataInterface error = new ErrorDataInterface();
				int code = errorsPossible.getErrorCode("missing label");
				String message = errorsPossible.getErrorMessage(code);
				error.add(lineNumber,code, message);
				errorsFound.add(error);
				//put nop in data
				outputData.AddLine(lc, NOPBINARY);
				//increase the lc by 1 word
				lc++;
			}
			
			
		}
		//check if first token is an instruction
		else if ( commands.hasInstruction(token.toLowerCase()))
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
				
				/*
				 *TODO for each instruction block:
				 *	-create a string of binary code
				 *	-add to it with each succeeding operand and stuff
				 *	-add to infoholder object 
				 */
				
				
				
				//parsing for the "load immediate" instruction
				if(insOp.compareToIgnoreCase("LUI") == 0)
				{
					
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
						// checking if the register is legal
						String reg1 = line.get(2); 
						
						// first, check if the register to be stored is register zero
						if (reg1 == "$r0" || reg1 == "$R0")
						{
							// if so, generate the corresponding error
							ErrorDataClass error = new ErrorDataClass();
							String code = errorsPossible.getErrorCode("cannot store value in register zero");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber, code, message);
							errorsFound.add(error);
						}
						
						// next, check for invalid syntax with the register format, can only be register 1 - 7 for storing values
						else if (!((reg1 == "$r1") || (reg1 == "$R1") || 
								   (reg1 == "$r2") || (reg1 == "$R2") ||
								   (reg1 == "$r3") || (reg1 == "$R3") ||
								   (reg1 == "$r4") || (reg1 == "$R4") ||
								   (reg1 == "$r5") || (reg1 == "$R5") ||
								   (reg1 == "$r6") || (reg1 == "$R6") ||
								   (reg1 == "$r7") || (reg1 == "$R7") ))
						{
							ErrorDataClass error = new ErrorDataClass();
							String code = errorsPossible.getErrorCode("improper register syntax");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber, code, message);
							errorsFound.add(error);
						}
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "add immediate" instruction
				if(insOp.compareToIgnoreCase("ADDI") == 0)
				{
					// TODO check each field; operand format: reg, reg, imm
					// otherwise produce error: illegal operands
					
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				
				}
				
				//parsing for the "add immediate unsigned" instruction
				else if(insOp.compareToIgnoreCase("ADDIU") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "subtract immediate" instruction
				else if(insOp.compareToIgnoreCase("SUBI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "subtract immediate unsigned" instruction
				else if(insOp.compareToIgnoreCase("SUBIU") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "multiply immediate" instruction
				else if(insOp.compareToIgnoreCase("MULI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "multiply immediate unsigned" instruction
				else if(insOp.compareToIgnoreCase("MULIU") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "divide immediate" instruction
				else if(insOp.compareToIgnoreCase("DIVI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "divide immediate unsigned" instruction
				else if(insOp.compareToIgnoreCase("DIVIU") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "or immediate" instruction
				else if(insOp.compareToIgnoreCase("ORI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}	
				}
				
				//parsing for the "exclusive or immediate" instruction
				else if(insOp.compareToIgnoreCase("XORI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "nor immediate" instruction
				else if(insOp.compareToIgnoreCase("NORI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}	
				}
				//parsing for the "and immediate" instruction
				else if(insOp.compareToIgnoreCase("ANDI") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				//parsing for the "set register values" instruction
				else if(insOp.compareToIgnoreCase("SRV") == 0)
				{
					// check the number of operands 
					
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
						
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("OUTNI") == 0)
				{
					
				}
				else if(insOp.compareToIgnoreCase("OUTCI") == 0)
				{
					
				}
				
				else
				{
					
				}
			}
			
			// Reg2Reg2Reg instructions will be parsed here
			else if (insType.compareToIgnoreCase("Reg2Reg2Reg") == 0)
			{
			
				// "" commands parsed here
				if (insOp.compareToIgnoreCase("NOR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						// parse the thing ..... 
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			
				// "power" commands parsed here
				if (insOp.compareToIgnoreCase("PWR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// "shift left logical" commands parsed here
				if (insOp.compareToIgnoreCase("SLL") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// "shift right logical" commands parsed here
				if (insOp.compareToIgnoreCase("SRL") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			
				// "shift right arithmetic" commands parsed here
				if (insOp.compareToIgnoreCase("SRA") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			
				// "and" commands parsed here
				if (insOp.compareToIgnoreCase("AND") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			
				// "or" commands parsed here
				if (insOp.compareToIgnoreCase("OR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			
			
				// "xor" commands parsed here
				if (insOp.compareToIgnoreCase("XOR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// "Nor" commands parsed here
				if (insOp.compareToIgnoreCase("NOR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// "jump register" commands will be parsed here
				if (insOp.compareToIgnoreCase("JR") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 1)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 1)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// dump commands will be parsed here
				if (insOp.compareToIgnoreCase("DUMP") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
			}
			
			// S-Type instructions will be parsed here
			else if (insType.compareToIgnoreCase("S-Type"))
			{
				
				
				// parse the Jump On Equal instruction
				 if(insOp.compareToIgnoreCase("JEQ") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("too many parameters");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
				}
				
				// parse the Jump Not Equal instruction
				else  if(insOp.compareToIgnoreCase("JNE") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Jump Greather Than instruction
				else  if(insOp.compareToIgnoreCase("JGT") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Jump Less Than instruction
				else  if(insOp.compareToIgnoreCase("JLT") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Jump Less than Or Equal instruction
				else  if(insOp.compareToIgnoreCase("JLE") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Jump And Link instruction
				else  if(insOp.compareToIgnoreCase("JAL") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Add Register and Storage instruction
				else  if(insOp.compareToIgnoreCase("ADDS") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Subtract Register and Storage instruction
				else  if(insOp.compareToIgnoreCase("SUBS") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Multiply Register and Storage instruction
				else  if(insOp.compareToIgnoreCase("MULS") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parse the Divide Register and Storage instruction
				else  if(insOp.compareToIgnoreCase("DIVS") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 3)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for the "load address of word into register" instruction
				else  if(insOp.compareToIgnoreCase("LA") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for the "store address in word" instruction
				else  if(insOp.compareToIgnoreCase("SA") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for the "and register to storage" instruction
				else  if(insOp.compareToIgnoreCase("ANDS") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for the "or register to storage" instruction
				else  if(insOp.compareToIgnoreCase("LA") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			}
			// Jump instructions will be parsed here
			else if (insType.compareToIgnoreCase("Jump"))
			{
				
				//parsing for the HALT instruction 
				if(insOp.compareToIgnoreCase("HALT") == 0)
				{
					
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 1)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 1)
					{
					
						// this object saves the value of the integer value in the hald instruction
						int haltAt = line.get(2);
						
						// checking bound limit for integer value
						if (!(0 <= haltAt && haltAt <= 255))
						{
							ErrorDataClass error = new ErrorDataClass();
							
							//The range violation shows what the bounds should have been.
							String code= errorsPossible.getErrorCode("halt value range violation (0 <= n <= 255)");
							
							// adds the error to the list of errors found for future printing out.
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
						}
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			}
			// IO-Type instructions will be parsed here
			else if (insType.compareToIgnoreCase("IO-Type"))
			{
			
				// parsing for INN instruction
				if(insOp.compareToIgnoreCase("INN") == 0)
				{
	
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for INC instruction 
				else if(insOp.compareToIgnoreCase("INC") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for OUTN instruction
				else if(insOp.compareToIgnoreCase("OUTN") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				// parsing for OUTC instruction
				else if(insOp.compareToIgnoreCase("OUTC") == 0)
				{
				
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						ErrorDataClass error = new ErrorDataClass();
						String code= errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					else if (opsCount == 2)
					{
						
					}
					
					// if too many operands, produce the corresponding 
					// error in the errortable
					else 
					{		
							ErrorDataClass error = new ErrorDataClass();
							String code= errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(code);
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
			}
		}
		//token must be a symbol. handle it
		else {
			//check if it is defined
			if (symbols.symbolIsDefined(token)){
				
			}
			//else check if this is a label
			else {
				if (size > 1){
					//if it is a label
					if (directives.hasDirective(line.get(1).toLowerCase())){
					
					}
					//if it isn't a label
					else {
						ErrorDataInterface error = new ErrorDataInterface();
						String code= errorsPossible.getErrorCode("unknown symbol");
						String message = errorsPossible.getErrorMessage(code);
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else {
					ErrorDataInterface error = new ErrorDataInterface();
					String code= errorsPossible.getErrorCode("unknown command");
					String message = errorsPossible.getErrorMessage(code);
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				//increase the lc by one word
				lc++;
			}
			
		}
		
		
		
			
		return endOfProgram;
		
	}

	
	@Override
	public ErrorOutInterface getErrors() {
		return errorsFound;
	}

	@Override
	public SymbolTableInterface getSymbols() {
		return symbols;
	}
	
	

}


