import java.io.File;


public class UserReport implements UserReportInterface {

	/*
	 * 
	 * This method creates an ArrayList of strings. Each index is associated with a line of output. That output will eventually be to a file in the second pass. It creates
	 * this Array by merging the source code with the errors found in the source code. This maintains the source code in full but just adds the associated errors.
	 * The method doesnt return a value. Instead it stores the ArrayList<String>  as a member variable.
	 */
	public void createUserReport (InSourceCode SourceCodeArray, ErrorOutInterface foundErrorsTable)
	{
		int i = 0;
		
		//iterate throught the source code looking into the foundErrorsTable for errors at each line. if they exist then add them in at that index plus 1
		while (SourceCodeArray.source.size() > i)
		{
			//if error at line is in table
			//add to array at that index plus 1
			//j+2
			//else j+1
		}
	}
	
	/*
	 * This outputs the ArrayList<String> member variable to a file with the name as given in the parameter. Each index of the Array becomes its own line of output in the file. 
	 * 
	 */
	public void outputUserReport (File outputFileName)
	{
		//open up out put to a file of the name UserReport
		//while array size > i=0
		//print line
		//iterate i
		//close output
	}


}
