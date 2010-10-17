import java.io.File;
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
		InstructTable instructionsTable = new InstructTable();
		instructionsTable.importTable(new File ("instructions.tbl"));
		
		//import directive table and create object
		DirectiveTable directivesTable = new DirectiveTable();
		directivesTable.importTable(new File ("directives.tbl"));
		
		//import error table and create object
		ErrorTable errorsTable = new ErrorTable();
		errorsTable.importTable(new File ("error.tbl"));
		
	
		//import source file and create object
		InSourceCode sourceCodeFile = new InSourceCode();
		sourceCodeFile.importSourceCode(new File (sourceFile);
		ArrayList<String> sourceArray = sourceCodeFile.getSourceCodeArray();
		
		//create errorOut object
		ErrorOutInterface errorsFound = new ErrorOut();
		
		
		// create tokenizer
		Tokenizer tokenizer = new Tokenizer();
		
		//create parser
		ParserInterface parser = new Parser(errorsTable, directivesTable, instructionsTable);
		
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
