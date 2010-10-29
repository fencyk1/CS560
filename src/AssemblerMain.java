import java.util.*;
import java.io.*;

public class AssemblerMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//Poll/retrieve source code from the first argument from command line
		File sourceCodeFileName = new File(args[0]);
		
		//Check if the source code is a directory or a readable file.
		if (sourceCodeFileName.isDirectory())
		{
			//TODO: Recursively iterate through the directory and return the file(s)
			//to parse (SP3)
		}
		
		//Make source code object using InSourceCode class assuming we were not
		//passed a directory 
		
		InSourceCode sourceCode = new InSourceCode();
		sourceCode.importSourceCode(sourceCodeFileName);
		
		//Import all other tables (error table, directives table, instructions
		//table.
		
		ErrorTable errorIn = new ErrorTable(new File ("error.tbl"));
		InstructTable instructIn = new InstructTable(new File ("instructions.tbl"));
		DirectiveTable directIn = new DirectiveTable(new File ("directives.tbl"));
		
		//Create and pass in empty objects of ErrorData/ErrorOut, Symbol/SymbolTable,
		//and Tokenizer.
		
		ErrorOut errorsFound = new ErrorOut();
		SymbolTable symbolsFound = new SymbolTable();
		Tokenizer tokenizer = new Tokenizer();
		
		
		//Create parser.
		
		SourceCodeParser parser = new SourceCodeParser();
		
		//Create a new counter object.
		int i = 0;
		//Create a new arraylist for storage for the tokenizer.
		ArrayList<String> line = new ArrayList<String>();
		
		//Tokenize the source code and send to the Parser.
		while (sourceCode.source.size() > i)
		{
			line = tokenizer.tokenizeLine(sourceCode.source.get(i));
			parser.parseLine(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, i+1);
			i++;
		}
		
		//Create intermediate file, symbol table, user report (src+errors)
		
		UserReport report = new UserReport();
		report.createUserReport(sourceCode, errorsFound);
		report.outputUserReport(sourceCode, new File ("userReport.txt"));
		
		symbolsFound.sort();
		symbolsFound.outputTable(new File ("symbolTable.txt"));
		

		
		//Take intermediate file and reference that in the symbol table to make
		//a new output to be changed into hexadecimal code (possibly byte code)
		//to print out to a file.
		
		

	}

}
