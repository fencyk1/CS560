import java.util.ArrayList;



/**
 * 
 * The parser almost acts like a main. This is because it interacts with almost all the other classes we have created. It takes in all the reference tables, the source code, tokenized source
 *  and output classes to create the input for pass two (most, if not all of these classes should be passed as arguments because we want the objects to pass by reference. 
 *  This way after the parser is done all the objects that it outputs will already be in main (assuming it is main that calls the parser) for use in the final stages of the pass.
 * 
 * It parses by going through a array of Strings (each string being a token. Note that this isnt currently a requirement of the tokenizer class. It is only an idea and I am open to other options.
 * Infact Im sure there are much better ones but I could think of any at the time). As it goes through the tokens is will check all the tables (ie the Symbol, directive, error, instruction, MOT)
 * it does this to check for syntax errors and to properly compile the source code in the first pass. At the first pass, the parser should have produced a symbol table, error out object, User report
 * object (I might have missed one or two but this should be close).
 * 
 * @author Mike Fencyk and Aaron D'Amico
 *
 * 
 */
public interface ParserInterface {

	/**
	 * This method will check the line for syntax and errors. If it contains
	 * an error it will be added to the error table. If a symbol is undefined it will be added to an internal list
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 *  
	 * @return returns a boolean value of true if and only if it parses the .end directive
	 */
	Boolean parse(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This will return the collection of undefined variables
	 * 
	 * @return ArrayList<String> of undefined variables
	 */
	ArrayList<String> getUndefinedVariables();
	
	/**
	 * This will return an object containing all the symbols the
	 * parser found along with their usage.
	 * 
	 * @return completed symbol table of type Symbol
	 */
	SymbolTable getSymbols();
	
	/**
	 * This will return an InfoHolder collection of the code converted into binary.
	 * 
	 * @return InfoHolder containing line numbers and binary data
	 */
	InfoHolder getBinaryData();
	
	
	
}
