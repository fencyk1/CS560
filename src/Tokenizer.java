import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Class that implements the TokenizerInterface. The returned tokens are
 * represented by an ArrayList of Strings, where each String is one token
 * tokenized by comments, spaces, and commas.
 * 
 * @author Austin
 *
 */
public class Tokenizer implements TokenizerInterface {
	// The array list that will be passed back to the method caller.
	private ArrayList<String> tokenArray;
	// A temporary array list for storage of comment tokens.
	private ArrayList<String> commentArray;
	// A temporary array list for storage of space tokens.
	private ArrayList<String> spaceArray;
	// A temporary array list for storage of comma tokens.
	private ArrayList<String> commaArray;
	
	public Tokenizer() {
		
	}
	
	/* The main method of the Tokenizer class. This method will take in a line
	 * from the parser and tokenize it by comments, spaces, then commas, and 
	 * return the created tokens via an array list of Strings. 
	 */
	@Override
	public ArrayList<String> tokenizeLine(String line) 
	{
		System.out.println(">>>>>>>>>  tokenizing");
		
		if (line.length() > 0)
		{
			// Create a new array to store the tokens from this line.
			// This should cause tokens to only be stored for each line tokenized.
			this.tokenArray = new ArrayList<String>();
			// Seperate the line into tokens based on a comment
			tokenizeComment(line);
			//Create the temp variables which will store position in the arrays.
			int i = 0, j = 0, k = 0;
			// Temporary String object to store tokens.
			String temp;
		
			// Loop that will tokenize each token created by tokenizeComment.
			while(commentArray.size() > i)
			{
				// Grab the token from the array.
				temp = commentArray.get(i);
				// Tokenize the selected token by spaces.
				tokenizeSpace(temp);
				j = 0;
			
				// Loop that will tokenize each token created by tokenizeSpace.
				while(spaceArray.size() > j)
				{
					// check if there is a str.data in the first 2 spots, and
					// if so, call the tokenizeStrDotData method with the
					// appropriate location parameter, then set spaceArray = the
					// returned array. Otherwise, tokenize the line like normal
					if ((spaceArray.size() > 1) && (spaceArray.get(1).equalsIgnoreCase("str.data")))
					{
						return tokenizeStrDotData (spaceArray, 2);
					}
					else if ((spaceArray.size() > 1) && (spaceArray.get(0).equalsIgnoreCase("str.data")))
					{
						return tokenizeStrDotData (spaceArray, 1);						
					}
					else
					{
						// Grab the token from the array.
						temp = spaceArray.get(j);
						// Tokenize the token by commas.
						tokenizeComma(temp);
						k = 0;

						// Loop that takes each token from the commaArray and adds it
						// to the tokenArray to be output to the caller.
						while(commaArray.size() > k)
						{
							// Grab the token from the array.
							temp = commaArray.get(k);
							// Add the token to the return array.
							tokenArray.add(temp);
							k++;
						}
						j++;
					}
				}
				i++;
			}
		}
		// Return the array containing all of the tokens retrieved from the
		// line in order of occurrence in the line.
		return tokenArray;
	}

	// Takes in a string and tokenizes the string by commas.
	@Override
	public void tokenizeComma(String line) {
		// The delimiter to be used with the tokenizer.
		String delim = ",";
		// Create a new tokenizer using <line> and <delim> for params.
		StringTokenizer comma = new StringTokenizer(line, delim);
		// Set <i> to be the number of tokens formed from <line>.
		int i = comma.countTokens();
		// Create a new temporary array to store the tokens.
		commaArray = new ArrayList<String>(i);
		
		// Adds each token from the tokenizer into the temporary array.
		while (comma.countTokens() > 0)
		{
			// Adds a token to the array in the order they appear in the input.
			commaArray.add((i - comma.countTokens()), comma.nextToken());
		}

	}

	// Takes in a string and tokenizes the string by comment tags "//".
	@Override
	public void tokenizeComment(String line) {
		// The delimiter to be used with the tokenizer.
		String delim = "|";
		// Create a new tokenizer using <line> and <delim> for params.
		StringTokenizer comment = new StringTokenizer(line, delim);
		// Set <i> to be the number of tokens formed from <line>.
		// Create a new temporary array to store the tokens.
		commentArray = new ArrayList<String>(1);
			
		// Adds a token to the array in the order they appear in the input.
		commentArray.add(comment.nextToken());
	}

	// Takes in a string and tokenizes the string by spaces.
	@Override
	public void tokenizeSpace(String line) {
		// The delimiter to be used with the tokenizer.
		// Create a new tokenizer using <line> for the param.
		StringTokenizer space = new StringTokenizer(line);
		// Set <i> to be the number of tokens formed from <line>.
		int i = space.countTokens();
		// Create a new temporary array to store the tokens.
		spaceArray = new ArrayList<String>(i);
		
		// Adds each token from the tokenizer into the temporary array.
		while (space.countTokens() > 0)
		{
			// Adds a token to the array in the order they appear in the input.
			spaceArray.add((i - space.countTokens()), space.nextToken());
		}
	}

	// Method that fixes a tokenizing issue that occurs when str.data contains
	// spaces.
	@Override
	public ArrayList<String> tokenizeStrDotData(ArrayList<String> line, int loc) {
		
		// counter to get all of the string data tokens
		int count = loc;
		
		// create a string object to hold the new string data.
		String data = "";
		
		// iterate through the array and create the new string data
		while (line.size() > count)
		{
			// if we already 
			if (data.length() > 0)
			{
				// concatenate the tokens with a space
				data = data + " " + line.get(count);
			}
			else
			{
				// if this is the first token to be added, we don't put a space
				// at the beginning.
				data = data + line.get(count);
			}
			
			count++;
		}
		
		// counter for the while loop
		int i = 0;
		
		// create an array to be returned
		ArrayList<String> returnArray = new ArrayList<String>();
		// create an array that will be tokenized by commas, leaving out the data
		ArrayList<String> nextArray = new ArrayList<String>();
		
		
		// iterate through the line array up to the str.data and add the tokens
		// to the new array
		while (i < loc)
		{
			nextArray.add(line.get(i));
			i++;
		}
		
		// dummy variables
		int j = 0, k = 0;
		String temp = "";
		
		while(nextArray.size() > j)
		{
			// Grab the token from the array.
			temp = nextArray.get(j);
			// Tokenize the token by commas.
			tokenizeComma(temp);
			k = 0;
		
			// Loop that takes each token from the commaArray and adds it
			// to the tokenArray to be output to the caller.
			while(commaArray.size() > k)
			{
				// Grab the token from the array.
				temp = commaArray.get(k);
				// Add the token to the return array.
				returnArray.add(temp);
				k++;
			}
			j++;
		}
		
		// add the data string to the end of the array
		returnArray.add(data);
		
		return returnArray;
	}
}
