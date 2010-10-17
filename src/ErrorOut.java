import java.util.ArrayList;

/**
 * SmallErrorOut lists all of the SmallErrorData objects inside of an array <br />
 * for ease of access and printing error messages on correct lines.
 * 
 * @author Jeff W
 */
public class ErrorOut implements ErrorOutInterface {
	private ArrayList<ErrorDataInterface> errorList;
	
	//Default constructor initializing the arraylist.
	public ErrorOut () {
		 this.errorList = new ArrayList<ErrorDataInterface>();
	}
	
	@Override
	public void add(ErrorDataInterface erroneousEntry) {
		//As long as the error does not already exist in the database, add it.
		//That is to say, multiple errors of the same type can exist inside
		//of the database, but multiple ErrorData objects with the same java-name
		//cannot.
		if (!this.errorList.contains(erroneousEntry))
		{
			this.errorList.add(erroneousEntry);
		}

	}

	@Override
	public ErrorDataInterface search(int lineNumber) {
		int counter = 0;
		int line;
		
		//Check each entry in the errorlist to see if the line numbers match.
		while (this.errorList.size() > counter)
		{
			//Get the line number of the current entry.
			line = this.errorList.get(counter).getLineNumber();
			//If it is equal to the line number in question, break from the
			//loop and return the corresponding error.
			if (line == lineNumber)
			{
				break;
			}
			counter++;
		}
		//Return the corresponding error.
		return this.errorList.get(counter);
	}

	@Override
	public boolean errorExists(ErrorData err) {
		//Just return whether or not it exists.
		return this.errorList.contains(err);
	}

	@Override
	public String output(ErrorData entry) {
		//Return the formatted line for the error message into a string.
		return "Error " + entry.getErrorCode() + " at line " + entry.getLineNumber() + ": " + entry.errorMessage();
	}
	
	public boolean errorAtLine(int lineNumber) {
		int counter = 0;
		int line;
		boolean exists = false;
		
		//Check each entry in the errorlist to see if the line numbers match.
		while (this.errorList.size() > counter)
		{
			//Get the line number of the current entry.
			line = this.errorList.get(counter).getLineNumber();
			
			//If it is equal to the line number in question, break from the 
			//loop and return true.
			if (line == lineNumber)
			{
				exists = true;
				break;
			}
		
			counter++;
		}
		
		return exists;
	}

}
