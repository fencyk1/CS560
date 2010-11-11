import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class UserReport implements UserReportInterface {
	
	//member variable
	
	public ArrayList<String[]> userReport = new ArrayList<String[]>();


	/*
	 * 
	 * This method creates an ArrayList of strings. Each index is associated with a line of output. That output will eventually be to a file in the second pass. It creates
	 * this Array by merging the source code with the errors found in the source code. This maintains the source code in full but just adds the associated errors.
	 * The method doesnt return a value. Instead it stores the ArrayList<String>  as a member variable.
	 */
	@Override
	public void createUserReport (InSourceCode sourceCodeArray, ErrorOut foundErrorsTable, ObjectFile objectFile, InstructTable instructionsTable, DirectiveTable directivesTable)
	{
		System.out.println(">>>>>>>>>>>>> 		Creating the user report file.");
		//data structure =  array[5] (size 5, not an array list)
		//@index [0] = location in memeory in hex, [1] = object code in hex, [2] = A/R/E, 
		//[3] = Line of source code in dec, [4] = source code statement
		
		//*********add source code
		//iterate through the source code adding the source code statements at each slot into the ArrayList
		//array[4] = the string in the sourceCodeArray, array[3] = index of that string +1
		int i = 0;
		//currently add the comment as well. tokenize on '|' if needed
		while( sourceCodeArray.source.size()>i)
		{
			String[] line = new String [5];
			
			StringTokenizer sourceLine = new StringTokenizer (sourceCodeArray.source.get(i));
			String veryFirstToken = sourceLine.nextToken();
			StringTokenizer sourceCommaLine = new StringTokenizer (veryFirstToken, ",");
			String firstToken = sourceCommaLine.nextToken();
			String secondToken = "";
			String thirdToken = "";
			if (sourceLine.hasMoreTokens())
			{
				secondToken = sourceLine.nextToken();
			}
			if (sourceLine.hasMoreTokens())
			{
				thirdToken = sourceLine.nextToken();
			}
			
			//TODO remove this check to deal add this to the userReport
			//cant be a .data or .text
			if ( !((firstToken.equalsIgnoreCase(".start") ||
					firstToken.equalsIgnoreCase(".data") || 
					firstToken.equalsIgnoreCase(".text") ||
						firstToken.equalsIgnoreCase(".end") ||
						firstToken.equalsIgnoreCase("ent") ||
						firstToken.equalsIgnoreCase("ext") ||
						firstToken.equalsIgnoreCase("debug") ||
						firstToken.equalsIgnoreCase("exec.start")) ||
						secondToken.equalsIgnoreCase("equ") ||
						secondToken.equalsIgnoreCase("equ.exp")))
			{
				// Check to see if the second token is a mem.skip
				// if it is, we repeat the mem.skip line equal to the amount of
				// words we are skipping.
				if (secondToken.equalsIgnoreCase("mem.skip"))
				{
					int skips = 0;
					
					// Attempt to set the skips to the number of word we're skipping
					try
					{
						skips = Integer.parseInt(thirdToken);
					}
					catch (NumberFormatException e)
					{
						skips = 1;
					}
					
					// Output the mem.skip line for each word skipped to match our
					// object file.
					while (skips > 0)
					{
						String source = sourceCodeArray.source.get(i);
						String lineNumber = Integer.toString(i + 1);

						line[3] = lineNumber;
						line[4] = source;

						this.userReport.add(line);
						skips--;
					}
					i++;	
				}
				// check to see if the first token is a mem.skip
				else if (firstToken.equalsIgnoreCase("mem.skip"))
				{
					int skips = 0;
					
					// Attempt to set the skips to the number of word we're skipping
					try
					{
						skips = Integer.parseInt(secondToken);
					}
					catch (NumberFormatException e)
					{
						skips = 1;
					}
					
					// Output the mem.skip line for each word skipped to match our
					// object file.
					while (skips > 0)
					{
						String source = sourceCodeArray.source.get(i);
						String lineNumber = Integer.toString(i + 1);

						line[3] = lineNumber;
						line[4] = source;

						this.userReport.add(line);
						skips--;
					}
					i++;
				}
				else
				{
					String source = sourceCodeArray.source.get(i);
					String lineNumber = Integer.toString(i + 1);

					line[3] = lineNumber;
					line[4] = source;

					this.userReport.add(line);
					i++;
				}
			}
			else
			{
				i++;
			}
		}
	
		
		//add object data in hex from ObjectFile
		//add location in memory in hex from ObjectFile
		//add a/r/e from symbol table from objectFile
		//iterate throught the userReport array List. if the source code statement effects memory then get the above data from the 
		//object file. you will have to parse the object file. do this with a tokenizer on '|' and just go to the exact token you need
		//if it doesnt effect memory then fill in the other slots with know info
	
		i = 0;
		int j = 0;
		while (this.userReport.size() > j)
		{
			
			String op1 = "";
			String op2 = "";
			
			//get the instruction of directive
			StringTokenizer instDirect = new StringTokenizer (this.userReport.get(j)[4]);
			if (instDirect.hasMoreTokens())
			{
				op1 = instDirect.nextToken();
			}
			if (instDirect.hasMoreTokens())
			{
				op2 = instDirect.nextToken();
			}
			
			//check if they are in the tables, need to check the first two tokens because one could be a label
			if(directivesTable.hasDirective(op2.toLowerCase()))
			{
				if (directivesTable.impactsMemory(op2.toLowerCase()))
				{
					getOtherFields (sourceCodeArray, foundErrorsTable, objectFile, i);
					i++;
				}

			}
			else if (instructionsTable.hasInstruction(op2.toLowerCase()))
			{
				getOtherFields (sourceCodeArray, foundErrorsTable, objectFile, i);
				i++;
			}
			else if (directivesTable.hasDirective(op1.toLowerCase()))
			{
				//find out if it effect memory
				if (directivesTable.impactsMemory(op1.toLowerCase()))
				{
					getOtherFields (sourceCodeArray, foundErrorsTable, objectFile, i);
					i++;
				}

			}
			else if (instructionsTable.hasInstruction(op1.toLowerCase()))
			{
				getOtherFields (sourceCodeArray, foundErrorsTable, objectFile, i);
				i++;
			}
			j++;
		}
		
		
		//************add errors
		//iterate through the this.userReport. look up the value at array[3]
		//if an error exists there then add the error string into the arrayList and the current index +1 and iterate
		//something like below
		i = 0;

		while (this.userReport.size() > i)
		{
			if (foundErrorsTable.errorAtLine(Integer.parseInt(this.userReport.get(i)[3])) == true)
			{
	
				//error object to hold error message
				ErrorData errorEntry = new ErrorData();
					
				//the error entry at the line
				errorEntry = foundErrorsTable.search(Integer.parseInt(this.userReport.get(i)[3]));
	
				//make new array to add to arrayList, this array has the errors
				String[] errorLine = new String [1];
				errorLine [0] = "error:" + foundErrorsTable.output(errorEntry);
					
				// output errorEntry as a sting to the array.
				this.userReport.add(i+1,errorLine );
				i = i+2;	
			}
			else
			{
				i++;
			}

		}
		
	}	

	private void getOtherFields (InSourceCode sourceCodeArray, ErrorOut foundErrorsTable, ObjectFile objectFile, int i)
	{
		if (objectFile.textLines.size() > i)
		{
			//get the first text line to get the loc, obj code and a/r/e
			StringTokenizer objectData = new StringTokenizer(objectFile.textLines.get(i),"|");
			
			//first token
			objectData.nextToken();
			
			//get location in hex
			userReport.get(i)[0] = objectData.nextToken();
			
			//skip debug flag
			objectData.nextToken();
			
			//get data in hex
			userReport.get(i)[1] = objectData.nextToken();
			
			//skip adjustments
			objectData.nextToken();
			
			//get type
			userReport.get(i)[2] = objectData.nextToken();
		}
		
		
	}
	
	
	/*
	 * This outputs the ArrayList<String> member variable to a file with the name as given in the parameter. Each index of the Array becomes its own line of output in the file. 
	 * 
	 */
	@Override
	public void outputUserReport (InSourceCode SourceCodeArray, File outputFileName) throws IOException
	{
		
		System.out.println(">>>>>>>>>>>>> 		Outputting the User Report file.");
		
		//get input from file, normally that file will be directives.tbl and be located in the src directory of the code
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter("output/" + outputFileName)));
		
		out.println("LOC(HEX)|OBJ CODE(HEX)|A/R/E|STMT LINE (DEC)|SOURCE STATEMENT");
		
		//start at 1 to skip the .start
		int i = 0;
		boolean austinIsAFlag = false;
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		while (this.userReport.size() > i)
		{			
			//need to turn the above two lines into another while loop that iterates through a array[5]
			int j =0;
			while (j < this.userReport.get(i).length)
			{
				
				//cant be an error line
				if (this.userReport.get(i).length >2 )
				{
					
					StringTokenizer sourceLine = new StringTokenizer (this.userReport.get(i)[4]);
					String veryFirstToken = sourceLine.nextToken();
					StringTokenizer sourceCommaLine = new StringTokenizer (veryFirstToken, ",");
					String firstToken = sourceCommaLine.nextToken();
					String secondToken = "";
					
					
					//TODO this no longer matters as instructions and directives that do not effect memory need to be in the user report
					//also need to consider the flag as it may no longer be needed or at least change what happens when things get flagged.
					//cant be a .data or .text
					if ( !(firstToken.equalsIgnoreCase(".data") || 
							firstToken.equalsIgnoreCase(".text") ||
								firstToken.equalsIgnoreCase(".end") ||
								firstToken.equalsIgnoreCase("ent") ||
								firstToken.equalsIgnoreCase("ext") ||
								firstToken.equalsIgnoreCase("debug") ||
								firstToken.equalsIgnoreCase("exec.start")))
					{

						out.print(this.userReport.get(i)[j] + "|");
						austinIsAFlag = true; 
					}
				}
				else
				{
					out.println(this.userReport.get(i)[j] + "|");
					
				}
				j++;
			}
			if (austinIsAFlag)
			{
				out.println();
				austinIsAFlag = false;
			}

			i++;
		}
		
		//close the input
		out.close();
	}


}