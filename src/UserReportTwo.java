import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class UserReportTwo implements UserReportInterface {

	public ArrayList<UserReportElements> userReport;

	
	public UserReportTwo() {
		this.userReport = new ArrayList<UserReportElements>();
	}
	
	@Override
	public void createUserReport(InSourceCode SourceCodeArray,
			ErrorOut foundErrorsTable, ObjectFile objectFile,
			InstructTable instructionsTable, DirectiveTable directivesTable)
			throws IOException {
		
		//First, parse through the source code and check if things impact memory or not.
		//If they do, get the next line from the object file (unless it's a mem.skip, then do
		//fun stuff.
		//If they don't, fill out the rest of the fields with "none"
		
		int counter = 0;
		
		//Create a counter for what line of the source code we are currently dealing with.
		int sourceLineNumber = 1;
		
		//Create a header tokenizer to get the starting location in hex
		StringTokenizer headerTokenizer = new StringTokenizer(objectFile.headerLine, "|");
		
		//Create a counter to help get the starting location
		int headerCounter = 0;
		
		//Get ready to deal with the location starting token
		while (headerCounter < 2)
		{
			headerTokenizer.nextToken();
			headerCounter++;
		}
		
		//Create a memory counter, needs to be turned into hex.
		int decimalLocationInMemory = Integer.parseInt(headerTokenizer.nextToken(), 16);
		
		//Tokenize each piece of the source code array.
		while (counter < SourceCodeArray.source.size())
		{
			//Create a new user report element for this line
			UserReportElements reportLine = new UserReportElements();
			//Set the line of source and the source line number into the element
			reportLine.setSourceCode(SourceCodeArray.source.get(counter));
			reportLine.setSourceLineNumber(Integer.toString(sourceLineNumber));
			//Set the location in memory of the current line
			reportLine.setHexLocation(Integer.toHexString(decimalLocationInMemory));
			
			//Make a new tokenizer for this line
			StringTokenizer tokenizer = new StringTokenizer(SourceCodeArray.source.get(counter));
			
			String tokenOne = new String();
			String tokenTwo = new String();
			
			//Get the first thing out of the tokenizer, to check if it impacts memory
			if(tokenizer.hasMoreTokens())
			{
				tokenOne = tokenizer.nextToken().toLowerCase();
			}
			//Get the second thing too, if there is one.
			if(tokenizer.hasMoreTokens())
			{
				tokenTwo = tokenizer.nextToken().toLowerCase();
			}
			
			//Check if we have a reset.lc
			if(tokenTwo.equalsIgnoreCase("reset.lc"))
			{
				//Make sure there are no errors with the line
				if (!foundErrorsTable.errorAtLine(sourceLineNumber))
				{
					//Get the location counter reset value
					String tokenThree = tokenizer.nextToken();
					
					//Change the location in memory
					decimalLocationInMemory = Integer.parseInt(tokenThree);
				}
			}
			//Check if we have a mem.skip
			else if(tokenOne.equalsIgnoreCase("mem.skip") || tokenTwo.equalsIgnoreCase("mem.skip"))
			{
				//Make Sure there are no errors with the line
				if(!foundErrorsTable.errorAtLine(sourceLineNumber))
				{
					//If there is no label preceeding the mem.skip, tokenTwo has the value
					if (tokenOne.equalsIgnoreCase("mem.skip"))
					{
						//update the location in memory accordingly
						decimalLocationInMemory = decimalLocationInMemory + Integer.parseInt(tokenTwo);
					}
					//Otherwise, tokenThree has the value
					else
					{
						String tokenThree = tokenizer.nextToken();
						
						//update the location in memory accordingly
						decimalLocationInMemory = decimalLocationInMemory + Integer.parseInt(tokenThree);
					}
				}
			}
			//Check if one of the tokens is a directive
			else if(directivesTable.hasDirective(tokenOne) || directivesTable.hasDirective(tokenTwo))
			{
				//Check if either of the tokens impact memory, as one may be a label
				if(directivesTable.impactsMemory(tokenOne) || directivesTable.impactsMemory(tokenTwo))
				{
					//They impact memory, so set the user elements properly.
					
					
					
					//Increment the decimal location in memory.
					decimalLocationInMemory++;
				}
				
				//If it's a directive that doesn't impact memory, do nothing.
			}
			else if(instructionsTable.hasInstruction(tokenOne) || instructionsTable.hasInstruction(tokenTwo))
			{
				//If it's an instruction, set the user elements properly.
				
				
				//Increment the decimal location in memory.
				decimalLocationInMemory++;
			}
			
			//Increment the source line number
			sourceLineNumber++;
			
			//Increment the counter number
			counter++;
			
			//Add the element to the user report array
			this.userReport.add(reportLine);
		}
		
		
	}

	@Override
	public void outputUserReport(InSourceCode SourceCodeArray, ErrorOut foundErrorsTable, 
			File outputFileName) throws IOException {
		
		//Output a line from the User report, followed by a line from the error table 
		//iff there exists an error at that line.
		
		System.out.println(">>>>>>>>>>>>> 		Outputting the User Report file.");
		
		//Make a new printwriter for the new file.
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter("output/" + outputFileName)));
		
		//Print a header line
		out.println("LOC(HEX)|OBJ CODE(HEX)|A/R/E|STMT LINE (DEC)|SOURCE STATEMENT");
		
		//Create a counter for print iterations.
		int printCounter = 0;
		
		//Create a userReport element for printing purposes.
		UserReportElements printLine = new UserReportElements();
		
		//Create strings to hold all of the information in the user report
		String hexLocationPrint = new String();
		String dataWordPrint = new String();
		String type = new String();
		String sourceLine = new String();
		String sourceStatement = new String();
		
		
		while (printCounter < SourceCodeArray.source.size())
		{
			//Set the elements equal to the current spot in the source array.
			printLine = this.userReport.get(printCounter);
			
			//Get all of the information we need to print ready.
			hexLocationPrint = printLine.getHexLocation();
			dataWordPrint = printLine.getDataWord();
			type = printLine.getType();
			sourceLine = printLine.getSourceLineNumber();
			sourceStatement = printLine.getSourceCode();
			
			//Print out a line of the user report.
			out.println(hexLocationPrint + "\t" + dataWordPrint + "\t" + type + "\t"
					+ sourceLine + "\t" + sourceStatement);
			
			//Print out any error messages that exist
			if(foundErrorsTable.errorAtLine(Integer.parseInt(sourceLine)))
			{
				//If an error exists at that line, get it and invoke the output method on it, and print it.
				out.println(foundErrorsTable.output
						(foundErrorsTable.search(Integer.parseInt(sourceLine))));
			}
			printCounter++;
		}
		//Close the file.
		out.close();
	}

}
