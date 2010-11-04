import java.util.ArrayList;

/**
 * holds all the methods to encode the different instruction types and produce the intermediate file
 * @author Aaron D'Amico
 *
 */
public interface EncoderInterface {

	
	void encodeLine(ArrayList<String> line, ErrorOut errorsFound, SymbolTable symbolsFound,
			ErrorTable errorIn, InstructTable instructIn, DirectiveTable directIn, int lineNumber,
			int locationCounter, IntermediateFile intermediateFile, String opName);

	
}
