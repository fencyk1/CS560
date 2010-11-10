import java.util.ArrayList;

/**
 * holds all the methods to encode the different instruction types and produce the intermediate file
 * @author Aaron D'Amico, Jeff Wolfe, and Austin Lohr
 *
 */
public interface EncoderInterface {

	/**
	 * encodeLine takes input from the parser and encodes into binary the current <br />
	 * line we are on; in the case of symbols, they are represented as [symbolname] <br />
	 * and are evaluated in the object file creation.
	 * 
	 * @param line The tokenized line we are encoding.
	 * @param errorsFound An array of all of the errors we have encountered.
	 * @param symbolsFound An array of all of the symbols we have encountered.
	 * @param errorIn The error table for reference.
	 * @param instructIn The instruction table for reference.
	 * @param directIn The directives table for reference.
	 * @param lineNumber The line number we are currently encoding.
	 * @param locationCounter The location in memory we are currently writing to.
	 * @param intermediateFile The intermediate file we are encoding to.
	 * @param opName The name of the operation we are encoding.
	 */
	void encodeLine(ArrayList<String> line, ErrorOut errorsFound, SymbolTable symbolsFound,
			ErrorTable errorIn, InstructTable instructIn, DirectiveTable directIn, int lineNumber,
			int locationCounter, IntermediateFile intermediateFile, String opName);

	
}
