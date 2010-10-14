import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mike
 *
 */
public class Parser implements ParserInterface {
	private DirectiveTableInterface directives;
	private ErrorOut errorsFound;
	private ErrorTableInterface errorsPossible;
	private InstructTableInterface commands;
	private SymbolTable symbols;
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
	public Parser(ErrorOut errorOut, ErrorTableInterface errorT,
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
		symbols = new SymbolTable();
		
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
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					else {
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("too many parameters");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
				}
				
			}
			//.end directive
			else if (token.compareToIgnoreCase(".end") == 0){
				if (size == 2) {
					if ( programName.compareTo(line.get(1)) != 0) {
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("invalid program name");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					
					
				}
				else {
					if (size < 2) {
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
					}
					else {
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("too many parameters");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("invalid opperand");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else if (size == 1){
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("missing parameter");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				else {
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
								ErrorData error = new ErrorData();
								int code = errorsPossible.getErrorCode("invalid opperand");
								String message = errorsPossible.getErrorMessage(Integer.toString(code));
								error.add(lineNumber,code, message);
								errorsFound.add(error);
								//put nop in data
								outputData.AddLine(lc, NOPBINARY);
							}
						}
						else {
							ErrorData error = new ErrorData();
							int code = errorsPossible.getErrorCode("data not binary");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
							//put nop in data
							outputData.AddLine(lc, NOPBINARY);
						}
					
					}
					else {
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("missing quotes");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else if (size == 1){
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("missing parameter");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
					error.add(lineNumber,code, message);
					errorsFound.add(error);
					//put nop in data
					outputData.AddLine(lc, NOPBINARY);
				}
				else {
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("too many parameters");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
				ErrorData error = new ErrorData();
				int code = errorsPossible.getErrorCode("missing label");
				String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
			String insType = commands.getInstructionType(token);

			// this variable holds the opcode for the instruction to be parsed
			String insOp = commands.getInstructionOpcode(line.get(1));
			
			/* 
			 * Check for what type the instruction belongs to. According to the 
			 * type of instruction, different procedures are followed.
			 */
			if (insType == "I-Type")
			{
				/*
					I-type instructions will be parsed here. This is where the parser
					will check which opcode the line has, and updates each table 
					accordingly. 
				*/
				
				//parsing for the "load immediate" instruction
				if(insOp.compareToIgnoreCase"LUI")
				{
					
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 2)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
					
					
					
				}
				
				
				if(insOp.compareToIgnoreCase("ADDI") == 0)
				{
					// TODO check each field; operand format: reg, reg, imm
					// otherwise produce error: illegal operands
					
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				
				}
				
				else if(insOp.compareToIgnoreCase("ADDIU") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("SUBI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("SUBIU") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("MULI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("MULIU") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("DIVI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("DIVIU") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("ORI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}	
				}
				
				else if(insOp.compareToIgnoreCase("XORI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("NORI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}	
				}
				
				else if(insOp.compareToIgnoreCase("ANDI") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
							error.add(lineNumber,code, message);
							errorsFound.add(error);
					}
				}
				
				else if(insOp.compareToIgnoreCase("SRV") == 0)
				{
					// check the number of operands 
					int opsCount = line.size() - 2;
					
					// if not enough operands, produce an error in the error table
					if (opsCount < 3)
					{
						SmallErrorData error = new SmallErrorData();
						int code = errorsPossible.getErrorCode("missing parameter");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
						
							SmallErrorData error = new SmallErrorData();
							int code = errorsPossible.getErrorCode("too many parameters");
							String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
			
			else if (insType == "Reg2Reg2Reg")
			{
				// Reg2Reg2Reg instructions will be parsed here
			}
			
			else if (insType == "S-Type")
			{
				// S-Type instructions will be parsed here
			}
			
			else if (insType == "Jump")
			{
				// Jump instructions will be parsed here
			}
			
			else if (insType == "IO-Type")
			{
				// IO-Type instructions will be parsed here
			}
		}
		//token must be a symbol. handle it
		else {{
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
						ErrorData error = new ErrorData();
						int code = errorsPossible.getErrorCode("unknown symbol");
						String message = errorsPossible.getErrorMessage(Integer.toString(code));
						error.add(lineNumber,code, message);
						errorsFound.add(error);
						//put nop in data
						outputData.AddLine(lc, NOPBINARY);
					}
				}
				else {
					ErrorData error = new ErrorData();
					int code = errorsPossible.getErrorCode("unknown command");
					String message = errorsPossible.getErrorMessage(Integer.toString(code));
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
	public ErrorOut getErrors() {
		return errorsFound;
	}

	@Override
	public SymbolTable getSymbols() {
		return symbols;
	}
	
	

}


