import java.util.ArrayList;

/**\
 * This is the main class of the assembler.
 * 
 * 
 * 
 * @author Mike Fencyk
 *
 */
public class AssemblerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		// Bring in files
		
		//import instruction tabel and create object
		InstructTableInterface instructionTable = new InstructTable();
		instructionTable.importTable(instructions.tbl);
		
		//import directive table and create object
		DirectiveTableInterface directiveTable = new DirectiveTable();
		directiveTable.importTable(directives.tbl);
		
		//import error table and create object
		ErrorTableInterface errorTable = new ErrorTable();
		errorTable.importTable(error.tbl);
		
		//import source file and create object
		InSourceCodeInterface sourceCode = new InSourceCode();
		sourceCode.importSourceCode(sourceFile);
		ArrayList<String> sourceArray = sourceCode.getSourceCodeArray();
		
		//create errorOut object
		ErrorOutInterface errorsFound = new ErrorOut();
		
		
		// create tokenizer
		TokenizerInterface tokenizer = new Tokenizer();
		
		//create parser
		ParserInterface parser = new Parser(errorTable, directiveTable, instructionTable);
		
		//create end boolean
		boolean endOfProgram = false;
		
		//create line counter
		int lineNum = 0;
		
		//tokeize and parse
		while (endOfProgram == false)
		{
			ArrayList<String> line;
			//tokenize
			line = tokenizer.tokenizeLine(sourceArray.get(lineNum));
			
			//parse
			endOfProgram = parser.parse(line, lineNum, errorsFound);
			
			//increment line number
			lineNum++;
			
			
			
		}

	}

}
