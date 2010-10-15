import java.io.*;
import java.util.StringTokenizer;


public class UserReport implements UserReportInterface {

	/*
	 * 
	 * This method creates an ArrayList of strings. Each index is associated with a line of output. That output will eventually be to a file in the second pass. It creates
	 * this Array by merging the source code with the errors found in the source code. This maintains the source code in full but just adds the associated errors.
	 * The method doesnt return a value. Instead it stores the ArrayList<String>  as a member variable.
	 */
	public void createUserReport (InSourceCode SourceCodeArray, ErrorOut foundErrorsTable)
	{
		int i = 0;
		
		//iterate throught the source code looking into the foundErrorsTable for errors at each line. if they exist then add them in at that index plus 1
		while (SourceCodeArray.source.size() > i)
		{
			//if error at line is in table
			//add to array at that index plus 1
			//j+2
			//else j+1
			//check to see if there is an error at the line. note source code line starts at 1 and array starts at 0
			if (foundErrorsTable.errorAtLine(i+1) == true)
			{

				//error object to hold error message
				ErrorData errorEntry = new ErrorData();
				
				//the error entry at the line
				errorEntry = foundErrorsTable.search(i+1);


				// output errorEntry as a sting to the array.
				SourceCodeArray.source.add(i+1, foundErrorsTable.output(errorEntry));
				i = i+2;
			}
			else
			{
				i++;
			}

		}
	}
	
	/*
	 * This outputs the ArrayList<String> member variable to a file with the name as given in the parameter. Each index of the Array becomes its own line of output in the file. 
	 * 
	 */
	public void outputUserReport (InSourceCode SourceCodeArray, File outputFileName) throws IOException
	{
		
		
		//get input from file, normally that file will be directives.tbl and be located in the src directory of the code
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter(outputFileName)));
		
		int i = 0;
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		while (SourceCodeArray.source.size() > i)
		{

			out.println(SourceCodeArray.source.get(i));
			i++;
		}
		
		//close the input
		out.close();
	}


}
