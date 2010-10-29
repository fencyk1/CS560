import java.util.ArrayList;


public class SourceCodeParser implements SourceCodeParserInterface {
	private boolean inDotText = false;
	private boolean inDotData = false;
	
	
	@Override
	public void parseLine(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn) {
	
		//Check the first token of each line for the .data or .text flags
		if (line.get(0).equalsIgnoreCase(".data"))
		{
			//If it's a .data flag, set our booleans appropriately
			this.inDotData = true;
			this.inDotText = false;
		}
		if (line.get(0).equalsIgnoreCase(".text"))
		{
			//If it's a .text flag, set our booleans appropriately
			this.inDotData = false;
			this.inDotText = true;
		}
		
		
		
		//If in the .data section, call parseDotData; if in the .text section,
		//call parseDotText; otherwise, parse as in .start
		
		if (this.inDotData)
		{
			
		}
		else if (this.inDotText)
		{
			
		}
		else
		{
			
		}
		
	}

	private void parseDotData() {
		// TODO Auto-generated method stub

	}

	private void parseDotText() {
		// TODO Auto-generated method stub

	}


	private void parseRType() {
		// TODO Auto-generated method stub

	}

	private void parseJType() {
		// TODO Auto-generated method stub

	}

	private void parseIType() {
		// TODO Auto-generated method stub

	}

	private void parseSType() {
		// TODO Auto-generated method stub

	}

	private void parseIOType() {
		// TODO Auto-generated method stub

	}

}
