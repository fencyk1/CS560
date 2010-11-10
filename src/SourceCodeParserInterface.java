import java.util.*;
/**
 * The parser takes a tokenized line at a time, checks the second token for
 * syntactical correctness with one of our 5 types of operations (R,J,I,S,IO).
 * 
 * @author Jeff Wolfe, Aaron D'Amico, Austin Lohr
 * 
 */
public interface SourceCodeParserInterface {

	/**
	 * parseLine parses one line of the tokenized source code at a time in order to check
	 * for syntactical correctness, errors, and prep for encoding.
	 * 
	 * @param line The tokenized line we are parsing.
	 * @param errorsFound An array of the errors we have found.
	 * @param symbolsFound An array of the symbols we have found.
	 * @param errorIn An imported table of all of the errors we have to work with.
	 * @param instructIn An imported table of all of the instructions we have to work with.
	 * @param directIn An imported table of all of the directives we have to work with.
	 * @param lineNumber The current line we are parsing
	 * @param intermediateFile The intermediate file we are writing to.
	 */
	void parseLine(ArrayList<String> line, ErrorOut errorsFound, SymbolTable symbolsFound,
			ErrorTable errorIn, InstructTable instructIn, DirectiveTable directIn, int lineNumber,
			IntermediateFile intermediateFile);
	
	
}
