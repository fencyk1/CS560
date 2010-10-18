import java.io.File;
import java.io.IOException;
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
	public static void main(File sourceFile) {
		
		
		// Bring in files
		
		//import instruction tabel and create object
		InstructTable instructionsTable = new InstructTable();
		try {
			instructionsTable.importTable(new File ("instructions.tbl"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//import directive table and create object
		DirectiveTable directivesTable = new DirectiveTable();
		try {
			directivesTable.importTable(new File ("directives.tbl"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//import error table and create object
		ErrorTable errorsTable = new ErrorTable();
		try {
			errorsTable.importTable(new File ("error.tbl"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		//import source file and create object
		InSourceCode sourceCodeFile = new InSourceCode();
		try {
			sourceCodeFile.importSourceCode(sourceFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> sourceArray = sourceCodeFile.getSourceCodeArray();
		
		//create errorOut object
		ErrorOut errorsFound = new ErrorOut();
		
		
		// create tokenizer
		Tokenizer tokenizer = new Tokenizer();
		
		//create parser
		Parser parser = new Parser(errorsTable, directivesTable, instructionsTable);
		
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
		
		
		//create user report object
		UserReport userReport = new UserReport();
		
		//create user report
		//userReport.createUserReport(sourceArray, errorsFound);
		
		//create output file
		//userReport.outputUserReport(sourceArray, outputFileName);
		
		

	}

}
