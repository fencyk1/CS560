import java.util.ArrayList;

/**
 * holds all the methods to encode the different instruction types and produce the intermediate file
 * @author Aaron D'Amico
 *
 */
public class Encoder implements EncoderInterface {

	
	private void encodeIntData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}

	private void encodeStrData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
		
		// NOTE: remove the ' on the ends first.

	}
	
	private void encodeHexData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}
	
	private void encodeBinData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
 
	}

	private void encodeAdrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
	}
	
	private void encodeAdrDotExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile,
			ArrayList<String> nestedExpressionValue) {
		// TODO Auto-generated method stub
		
		// NOTE: anything here is syntactically correct save for label names
		// so everytime you hit a '(', take the next expression out of
		// nested expression value, and encode it accordingly. Ask Jeff for
		// clarification
	}

	private void encodeNOP(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
	}

	private void encodeMemDotSkip(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
	}
	
	private void encodeResetDotLC(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub
	}

	private void encodeRType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}

	private void encodeJType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}

	private void encodeIType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}

	private void encodeSType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}

	private void encodeIOType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter, IntermediateFile intermediateFile) {
		// TODO Auto-generated method stub

	}
///////////////////////////////////////////////////////////////////////////////////////
//*******************Public method to encode//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	@Override
	public void encodeLine(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineNumber,
			int locationCounter, IntermediateFile intermediateFile,
			String opName) {
		//String to hold the binary representation of the data
		String encodedLineBin = new String();
		
		//look up opName in instructions table
			//if there then look up the type
				//call the encode method for that type
				//add to intermediateFile
		//look up opName in directives table
			//check if it impacts memory
				//if so then add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
		
		
		
		

	}
	
}
