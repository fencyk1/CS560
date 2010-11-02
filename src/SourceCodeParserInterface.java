import java.util.*;
/**
 * The parser takes a tokenized line at a time, checks the second token for
 * syntactical correctness with one of our 5 types of operations (R,J,I,S,IO).
 * 
 * @author
 * 
 */
public interface SourceCodeParserInterface {

	/**
	 * 
	 * 
	 * @param line
	 * @param errorsFound
	 * @param symbolsFound
	 * @param errorIn
	 * @param instructIn
	 * @param directIn
	 */
	void parseLine(ArrayList<String> line, ErrorOut errorsFound, SymbolTable symbolsFound,
			ErrorTable errorIn, InstructTable instructIn, DirectiveTable directIn, int lineNumber,
			int locationCounter, IntermediateFile intermediateFile);
	
	
}
