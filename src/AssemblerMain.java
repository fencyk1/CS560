
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
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
		File sourceFile = new File(args[0]);
		
		//check if sourceFile is real
		if (! sourceFile.exists())
		{
			System.out.println("invalid file");
			
		}
		
		sourceCodeFile.importSourceCode(sourceFile);

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
		userReport.createUserReport(sourceCodeFile, errorsFound);
		
		//create output file
		File outputFile = new File("outputFile.txt");
		
		//if the outputfile doesn't exist, create it
		if (!outputFile.exists())
		{
			outputFile.createNewFile();
		}
		
		//create the user report
		userReport.outputUserReport(sourceCodeFile, outputFile);

		//create intermediate file
		File intermediate = new File("intermediate.txt");
		
		//if the intermediate doesn't exist, create it
		if (!intermediate.exists())
		{
			intermediate.createNewFile();
		}
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(intermediate)));
		
		//create a info holder object
		InfoHolder info = parser.getBinaryData();
		
		//create header for intermediate file of the form
		// "Line  LC    Binary"
		out.println("Line  LC    Binary");
		
		//iterate through info holder object
		for (int inc = 0; inc < lineNum; inc ++)
		{
			
			//check for LC = 0
			int line = info.findLineByLC(inc, 0);
			
			//make sure LC exists, and then print to file
			if (! (line == -1))
			{
				
				//print line number followed by four spaces
				out.print(line);
				out.print("    ");
				
				//print LC followed by four spaces
				out.print(inc);
				out.print("    ");
				
				//print binary and end line
				out.println(info.findBinaryByLine(line));
				
			}
			
			
		}
		
		//close the output file for the intermediate object
		out.close();
		
		
	}

}
