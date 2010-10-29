//Parser notes: check error.tbl / ErrorTable.java's utility.

import java.util.ArrayList;


public class SourceCodeParser implements SourceCodeParserInterface {
	private boolean inDotText = false;
	private boolean inDotData = false;
	private boolean haveDotStart = false;
	
	
	@Override
	public void parseLine(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
	
		//Check the first token of each line for the .data or .text flags
		if (line.get(0).equalsIgnoreCase(".data") && haveDotStart)
		{
			//If it's a .data flag, set our booleans appropriately
			this.inDotData = true;
			this.inDotText = false;
			
		}
		if (line.get(0).equalsIgnoreCase(".text") && haveDotStart)
		{
			//If it's a .text flag, set our booleans appropriately
			this.inDotData = false;
			this.inDotText = true;
			
		}
				
		
		//If in the .data section, call parseDotData; if in the .text section,
		//call parseDotText; otherwise, parse as in .start
		
		if (this.inDotData)
		{
			this.parseDotData(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter);
		}
		else if (this.inDotText)
		{
			this.parseDotText(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter);
		}
		//If in neither, parse as a .start
		else
		{
			//deal with .start and .end methods
			this.parseOther(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter);
			
		}
		
	}

	
	private void parseOther (ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter)
	{
		//If the line is syntactically correct, add the name to the symbol table and do further detailed error checking
		if (line.get(0).equalsIgnoreCase(".start") && line.size() == 3 && !this.haveDotStart)
		{
			//Set the flag to show that we have already encountered a .start
			this.haveDotStart = true;
			Symbol prgmName = new Symbol();
			prgmName.setLabel(line.get(1));
			prgmName.setLength(0);
			prgmName.setUsage("Progam Name");
			prgmName.setLocation(0);
			
			//declare the starting location
			int startingLocation = 0;
			
			//Make sure that the starting location is a number
			try
			{
				startingLocation = Integer.parseInt(line.get(2));
			}
			catch(NumberFormatException e)
			{
				//Create an error regarding invalid starting location.
				ErrorData invalidStartingLocation = new ErrorData();
				invalidStartingLocation.add(lineCounter, 1, "Staring location is not valid");
				
				//Add it to the ErrorOut table.
				errorsFound.add(invalidStartingLocation);
			}
			
			//Make sure the starting location is a valid number of a certain size
			if (startingLocation > 65535)
			{
				//Create an error regarding invalid starting location.
				ErrorData largeStartingLocation = new ErrorData();
				largeStartingLocation.add(lineCounter, 2, "Staring location is too large");
				
				//Add it to the ErrorOut table.
				errorsFound.add(largeStartingLocation);
			}
			else
			{
				//sets the starting location of the program in memory 
				//this is a result of it passing the error checks
				prgmName.setLocation(startingLocation);
			}
			//Add the program name to the symbol table
			symbolsFound.defineSymbol(prgmName);

		}
		//if the line is a .start line but there are duplicate .start directives
		else if (line.get(0).equalsIgnoreCase(".start") && line.size() == 3 && this.haveDotStart)
		{
			//Create an error regarding duplicated start directives.
			ErrorData duplicateStart = new ErrorData();
			duplicateStart.add(lineCounter, 0, "Duplicate start directive detected");
			
			//Add it to the ErrorOut table.
			errorsFound.add(duplicateStart);
			
		}
		//if the first token is equal to .end then we will parse it normal
		else if (line.get(0).equalsIgnoreCase(".end") && line.size() == 2 && this.haveDotStart)
		{
			if (!symbolsFound.symbolIsDefined(line.get(1)))
			{
				//Create an error program names do not match up
				ErrorData programNameIncorrect = new ErrorData();
				programNameIncorrect.add(lineCounter, 4, "Program Names do not match up");
				
				//Add it to the ErrorOut table.
				errorsFound.add(programNameIncorrect);
			}
				
		}		
		//any other line isnt with in the .data the .text and this line isnt a .start
		//output the below error
		else
		{
			//Create an error because the code doesnt follow the .start .data and .text convention
			ErrorData rogueLine = new ErrorData();
			rogueLine.add(lineCounter, 3, ".data and/or .text and/or .start never defined");
			
			//Add it to the ErrorOut table.
			errorsFound.add(rogueLine);
		}
		
	}
	
	private void parseDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		//If the line is a .data, but it has more than one token, throw an error
		if (line.get(0).equalsIgnoreCase(".data") && line.size() != 1)
		{
			//Create an error because .data line is followed by extra code
			ErrorData dotDataExtra = new ErrorData();
			dotDataExtra.add(lineCounter, 5, ".data line should stand alone");
			
			//Add it to the ErrorOut table.
			errorsFound.add(dotDataExtra);
		}
		// If the line is in the .data section but is not the .data line,
		// parse it.
		else if (!line.get(0).equalsIgnoreCase(".data"))
		{
			
		}

	}

	private void parseDotText(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		

	}


	private void encodeRType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeJType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeIType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeSType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeIOType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}
	
	private void parseIntDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseStrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseHexDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseBinDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAdrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAdrDotExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMemSkip(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseEnt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseExt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseNop(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseExecStart(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseEqu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseEquExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseResetLC(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDebug(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAddi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAddiu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSubi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSubiu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMuli(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMuliu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDivi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDiviu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJeq(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJne(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJgt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJlt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJle(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSW(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseLw(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseLnw(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseLwi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseLui(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOri(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseXori(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseNori(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAndi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseLa(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSa(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAnds(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOrs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJ(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJal(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseHalt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMul(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMulu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAdd(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAddu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSub(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSubu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDiv(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDivu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parsePwr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSll(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSrl(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSra(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAnd(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseXor(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseNor(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseJr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSrv(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDump(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseInn(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseInc(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOutn(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOutc(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOutni(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseOutci(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseAdds(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseSubs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseMuls(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}
	
	private void parseDivs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
	}

}
