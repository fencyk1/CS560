// ---Parser notes: check error.tbl / ErrorTable.java's utility.
// ---Changed representation of symbols from bits to words
// ---Might need to change the length of hex.data/bin.data

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
		else if (!line.get(0).equalsIgnoreCase(".data") && line.size() > 1)
		{
			if (line.get(1).equalsIgnoreCase("int.data"))
			{
				//Create a new symbol to store the int.data label
				Symbol intDotData = new Symbol();
				intDotData.setLabel(line.get(0));
				intDotData.setLength(1);
				intDotData.setLocation(lineCounter);
				intDotData.setUsage("int.data");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(intDotData);
				//Remove the int.data label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseIntDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);		
			}
			else if (line.get(1).equalsIgnoreCase("str.data"))
			{
				//Create a new symbol to store the str.data label
				Symbol strDotData = new Symbol();
				strDotData.setLabel(line.get(0));
				strDotData.setLocation(lineCounter);
				strDotData.setUsage("str.data");
				
				//Set the length of the string
				String stringHolder = "''";
				
				if (line.size() > 2)
				{
					stringHolder = line.get(3);
				}
				
				// The length of the String data in memory is based on the length of the string.
				// Every 4 characters = 1 word (32 bits), thus to get the length
				// of the string in memory, we take the number of characters in
				// the string, subtract the 2 ' characters, divide that length 
				// by 4 and take the ceiling function to get the number of words the
				// string takes up.
				if (((stringHolder.length() - 2) % 4) == 0)
				{
					strDotData.setLength((stringHolder.length() - 2) / 4);
				}
				else
				{
					strDotData.setLength(((stringHolder.length() - 2) / 4) + 1);
				}
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(strDotData);
				//Remove the str.data label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseStrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);		
			}
			else if (line.get(1).equalsIgnoreCase("hex.data"))
			{
				//Create a new symbol to store the hex.data label
				Symbol hexDotData = new Symbol();
				hexDotData.setLabel(line.get(0));
				hexDotData.setLength(1);
				hexDotData.setLocation(lineCounter);
				hexDotData.setUsage("hex.data");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(hexDotData);
				//Remove the hex.data label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseHexDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("bin.data"))
			{
				//Create a new symbol to store the bin.data label
				Symbol binDotData = new Symbol();
				binDotData.setLabel(line.get(0));
				binDotData.setLength(1);
				binDotData.setLocation(lineCounter);
				binDotData.setUsage("bin.data");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(binDotData);
				//Remove the bin.data label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseBinDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("adr.data"))
			{
				//Create a new symbol to store the adr.data label
				Symbol adrDotData = new Symbol();
				adrDotData.setLabel(line.get(0));
				adrDotData.setLength(1);
				adrDotData.setLocation(lineCounter);
				adrDotData.setUsage("adr.data");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(adrDotData);
				//Remove the adr.data label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAdrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}	
			else if (line.get(1).equalsIgnoreCase("adr.exp"))
			{
				//Create a new symbol to store the adr.exp label
				Symbol adrDotExp = new Symbol();
				adrDotExp.setLabel(line.get(0));
				adrDotExp.setLength(1);
				adrDotExp.setLocation(lineCounter);
				adrDotExp.setUsage("adr.exp");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(adrDotExp);
				//Remove the adr.exp label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAdrDotExp(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("mem.skip"))
			{
				//Create a new symbol to store the mem.skip label
				Symbol memDotSkip = new Symbol();
				memDotSkip.setLabel(line.get(0));
				memDotSkip.setLength(1);
				memDotSkip.setLocation(lineCounter);
				memDotSkip.setUsage("mem.skip");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(memDotSkip);
				//Remove the mem.skip label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMemSkip(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			//If the token contains none of the aforementioned directives,
			//check spot 0, in case they don't have labels.
			if (line.get(0).equalsIgnoreCase("int.data"))
			{
				//Send remaining line to be parsed
				parseIntDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);		
			}
			else if (line.get(0).equalsIgnoreCase("str.data"))
			{
				//Send remaining line to be parsed
				parseStrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);		
			}
			else if (line.get(0).equalsIgnoreCase("hex.data"))
			{
				//Send remaining line to be parsed
				parseHexDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("bin.data"))
			{
				//Send remaining line to be parsed
				parseBinDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("adr.data"))
			{
				//Send remaining line to be parsed
				parseAdrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}	
			else if (line.get(0).equalsIgnoreCase("adr.exp"))
			{
				//Send remaining line to be parsed
				parseAdrDotExp(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mem.skip"))
			{
				//Send remaining line to be parsed
				parseMemSkip(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else
			{
				//Create an error because the directive in the .data section
				//cannot be parsed
				ErrorData invalidDirective = new ErrorData();
				invalidDirective.add(lineCounter, 6, "directive syntax invalid");
				
				//Add it to the ErrorOut table.
				errorsFound.add(invalidDirective);
			}
		}
		else
		{
			//Create an error because the directive in the .data section
			//cannot be parsed
			ErrorData syntaxError = new ErrorData();
			syntaxError.add(lineCounter, 8, "Syntax error");
			
			//Add it to the ErrorOut table.
			errorsFound.add(syntaxError);
		}

	}

	private void parseDotText(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {

		//If the line is a .text, but it has more than one token, throw an error
		if (line.get(0).equalsIgnoreCase(".text") && line.size() != 1)
		{
			//Create an error because .data line is followed by extra code
			ErrorData dotTextExtra = new ErrorData();
			dotTextExtra.add(lineCounter, 7, ".text line should stand alone");
			
			//Add it to the ErrorOut table.
			errorsFound.add(dotTextExtra);
		}
		// If the line is in the .data section but is not the .data line,
		// parse it.
		else if (!line.get(0).equalsIgnoreCase(".text") && line.size() > 1)
		{
			if (line.get(1).equalsIgnoreCase("addi"))
			{
				//Create a new symbol to store the addi label
				Symbol addi = new Symbol();
				addi.setLabel(line.get(0));
				addi.setLength(1);
				addi.setLocation(lineCounter);
				addi.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(addi);
				//Remove the addi label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("addiu"))
			{
				//Create a new symbol to store the addiu label
				Symbol addiu = new Symbol();
				addiu.setLabel(line.get(0));
				addiu.setLength(1);
				addiu.setLocation(lineCounter);
				addiu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(addiu);
				//Remove the addiu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAddiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("subi"))
			{
				//Create a new symbol to store the subi label
				Symbol subi = new Symbol();
				subi.setLabel(line.get(0));
				subi.setLength(1);
				subi.setLocation(lineCounter);
				subi.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(subi);
				//Remove the subi label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSubi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("subiu"))
			{
				//Create a new symbol to store the subiu label
				Symbol subiu = new Symbol();
				subiu.setLabel(line.get(0));
				subiu.setLength(1);
				subiu.setLocation(lineCounter);
				subiu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(subiu);
				//Remove the subiu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSubiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("muli"))
			{
				//Create a new symbol to store the muli label
				Symbol muli = new Symbol();
				muli.setLabel(line.get(0));
				muli.setLength(1);
				muli.setLocation(lineCounter);
				muli.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(muli);
				//Remove the muli label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMuli(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("muliu"))
			{
				//Create a new symbol to store the muliu label
				Symbol muliu = new Symbol();
				muliu.setLabel(line.get(0));
				muliu.setLength(1);
				muliu.setLocation(lineCounter);
				muliu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(muliu);
				//Remove the muliu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMuliu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("divi"))
			{
				//Create a new symbol to store the divi label
				Symbol divi = new Symbol();
				divi.setLabel(line.get(0));
				divi.setLength(1);
				divi.setLocation(lineCounter);
				divi.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(divi);
				//Remove the divi label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDivi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("diviu"))
			{
				//Create a new symbol to store the diviu label
				Symbol diviu = new Symbol();
				diviu.setLabel(line.get(0));
				diviu.setLength(1);
				diviu.setLocation(lineCounter);
				diviu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(diviu);
				//Remove the diviu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDiviu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jeq"))
			{
				//Create a new symbol to store the jeq label
				Symbol jeq = new Symbol();
				jeq.setLabel(line.get(0));
				jeq.setLength(1);
				jeq.setLocation(lineCounter);
				jeq.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jeq);
				//Remove the jeq label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJeq(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jne"))
			{
				//Create a new symbol to store the jne label
				Symbol jne = new Symbol();
				jne.setLabel(line.get(0));
				jne.setLength(1);
				jne.setLocation(lineCounter);
				jne.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jne);
				//Remove the jne label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJne(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jgt"))
			{
				//Create a new symbol to store the jgt label
				Symbol jgt = new Symbol();
				jgt.setLabel(line.get(0));
				jgt.setLength(1);
				jgt.setLocation(lineCounter);
				jgt.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jgt);
				//Remove the jgt label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJgt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jlt"))
			{
				//Create a new symbol to store the jlt label
				Symbol jlt = new Symbol();
				jlt.setLabel(line.get(0));
				jlt.setLength(1);
				jlt.setLocation(lineCounter);
				jlt.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jlt);
				//Remove the jlt label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJlt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jle"))
			{
				//Create a new symbol to store the jle label
				Symbol jle = new Symbol();
				jle.setLabel(line.get(0));
				jle.setLength(1);
				jle.setLocation(lineCounter);
				jle.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jle);
				//Remove the jle label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJle(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("sw"))
			{
				//Create a new symbol to store the sw label
				Symbol sw = new Symbol();
				sw.setLabel(line.get(0));
				sw.setLength(1);
				sw.setLocation(lineCounter);
				sw.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(sw);
				//Remove the sw label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSW(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("lw"))
			{
				//Create a new symbol to store the lw label
				Symbol lw = new Symbol();
				lw.setLabel(line.get(0));
				lw.setLength(1);
				lw.setLocation(lineCounter);
				lw.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(lw);
				//Remove the lw label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseLw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("lnw"))
			{
				//Create a new symbol to store the lnw label
				Symbol lnw = new Symbol();
				lnw.setLabel(line.get(0));
				lnw.setLength(1);
				lnw.setLocation(lineCounter);
				lnw.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(lnw);
				//Remove the lnw label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseLnw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("lwi"))
			{
				//Create a new symbol to store the lwi label
				Symbol lwi = new Symbol();
				lwi.setLabel(line.get(0));
				lwi.setLength(1);
				lwi.setLocation(lineCounter);
				lwi.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(lwi);
				//Remove the lwi label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseLwi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("lui"))
			{
				//Create a new symbol to store the lui label
				Symbol lui = new Symbol();
				lui.setLabel(line.get(0));
				lui.setLength(1);
				lui.setLocation(lineCounter);
				lui.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(lui);
				//Remove the lui label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseLui(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("ori"))
			{
				//Create a new symbol to store the ori label
				Symbol ori = new Symbol();
				ori.setLabel(line.get(0));
				ori.setLength(1);
				ori.setLocation(lineCounter);
				ori.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(ori);
				//Remove the ori label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOri(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("xori"))
			{
				//Create a new symbol to store the xori label
				Symbol xori = new Symbol();
				xori.setLabel(line.get(0));
				xori.setLength(1);
				xori.setLocation(lineCounter);
				xori.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(xori);
				//Remove the xori label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseXori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("nori"))
			{
				//Create a new symbol to store the nori label
				Symbol nori = new Symbol();
				nori.setLabel(line.get(0));
				nori.setLength(1);
				nori.setLocation(lineCounter);
				nori.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(nori);
				//Remove the nori label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseNori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("andi"))
			{
				//Create a new symbol to store the andi label
				Symbol andi = new Symbol();
				andi.setLabel(line.get(0));
				andi.setLength(1);
				andi.setLocation(lineCounter);
				andi.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(andi);
				//Remove the andi label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAndi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("la"))
			{
				//Create a new symbol to store the la label
				Symbol la = new Symbol();
				la.setLabel(line.get(0));
				la.setLength(1);
				la.setLocation(lineCounter);
				la.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(la);
				//Remove the la label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseLa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("sa"))
			{
				//Create a new symbol to store the sa label
				Symbol sa = new Symbol();
				sa.setLabel(line.get(0));
				sa.setLength(1);
				sa.setLocation(lineCounter);
				sa.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(sa);
				//Remove the sa label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("ands"))
			{
				//Create a new symbol to store the ands label
				Symbol ands = new Symbol();
				ands.setLabel(line.get(0));
				ands.setLength(1);
				ands.setLocation(lineCounter);
				ands.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(ands);
				//Remove the ands label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAnds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("ors"))
			{
				//Create a new symbol to store the ors label
				Symbol ors = new Symbol();
				ors.setLabel(line.get(0));
				ors.setLength(1);
				ors.setLocation(lineCounter);
				ors.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(ors);
				//Remove the ors label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOrs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("j"))
			{
				//Create a new symbol to store the j label
				Symbol j = new Symbol();
				j.setLabel(line.get(0));
				j.setLength(1);
				j.setLocation(lineCounter);
				j.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(j);
				//Remove the j label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJ(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jal"))
			{
				//Create a new symbol to store the jal label
				Symbol jal = new Symbol();
				jal.setLabel(line.get(0));
				jal.setLength(1);
				jal.setLocation(lineCounter);
				jal.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jal);
				//Remove the jal label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJal(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("halt"))
			{
				//Create a new symbol to store the halt label
				Symbol halt = new Symbol();
				halt.setLabel(line.get(0));
				halt.setLength(1);
				halt.setLocation(lineCounter);
				halt.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(halt);
				//Remove the halt label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseHalt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("mul"))
			{
				//Create a new symbol to store the mul label
				Symbol mul = new Symbol();
				mul.setLabel(line.get(0));
				mul.setLength(1);
				mul.setLocation(lineCounter);
				mul.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(mul);
				//Remove the mul label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMul(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("mulu"))
			{
				//Create a new symbol to store the lwi label
				Symbol mulu = new Symbol();
				mulu.setLabel(line.get(0));
				mulu.setLength(1);
				mulu.setLocation(lineCounter);
				mulu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(mulu);
				//Remove the mulu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMulu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("add"))
			{
				//Create a new symbol to store the add label
				Symbol add = new Symbol();
				add.setLabel(line.get(0));
				add.setLength(1);
				add.setLocation(lineCounter);
				add.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(add);
				//Remove the add label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAdd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("addu"))
			{
				//Create a new symbol to store the addu label
				Symbol addu = new Symbol();
				addu.setLabel(line.get(0));
				addu.setLength(1);
				addu.setLocation(lineCounter);
				addu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(addu);
				//Remove the addu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAddu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("sub"))
			{
				//Create a new symbol to store the sub label
				Symbol sub = new Symbol();
				sub.setLabel(line.get(0));
				sub.setLength(1);
				sub.setLocation(lineCounter);
				sub.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(sub);
				//Remove the sub label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSub(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("subu"))
			{
				//Create a new symbol to store the subu label
				Symbol subu = new Symbol();
				subu.setLabel(line.get(0));
				subu.setLength(1);
				subu.setLocation(lineCounter);
				subu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(subu);
				//Remove the subu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSubu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("div"))
			{
				//Create a new symbol to store the div label
				Symbol div = new Symbol();
				div.setLabel(line.get(0));
				div.setLength(1);
				div.setLocation(lineCounter);
				div.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(div);
				//Remove the div label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDiv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("divu"))
			{
				//Create a new symbol to store the divu label
				Symbol divu = new Symbol();
				divu.setLabel(line.get(0));
				divu.setLength(1);
				divu.setLocation(lineCounter);
				divu.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(divu);
				//Remove the divu label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDivu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("pwr"))
			{
				//Create a new symbol to store the pwr label
				Symbol pwr = new Symbol();
				pwr.setLabel(line.get(0));
				pwr.setLength(1);
				pwr.setLocation(lineCounter);
				pwr.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(pwr);
				//Remove the pwr label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parsePwr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("sll"))
			{
				//Create a new symbol to store the sll label
				Symbol sll = new Symbol();
				sll.setLabel(line.get(0));
				sll.setLength(1);
				sll.setLocation(lineCounter);
				sll.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(sll);
				//Remove the sll label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSll(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("srl"))
			{
				//Create a new symbol to store the srl label
				Symbol srl = new Symbol();
				srl.setLabel(line.get(0));
				srl.setLength(1);
				srl.setLocation(lineCounter);
				srl.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(srl);
				//Remove the srl label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSrl(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("sra"))
			{
				//Create a new symbol to store the sra label
				Symbol sra = new Symbol();
				sra.setLabel(line.get(0));
				sra.setLength(1);
				sra.setLocation(lineCounter);
				sra.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(sra);
				//Remove the sra label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSra(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("and"))
			{
				//Create a new symbol to store the and label
				Symbol and = new Symbol();
				and.setLabel(line.get(0));
				and.setLength(1);
				and.setLocation(lineCounter);
				and.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(and);
				//Remove the and label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAnd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("or"))
			{
				//Create a new symbol to store the or label
				Symbol or = new Symbol();
				or.setLabel(line.get(0));
				or.setLength(1);
				or.setLocation(lineCounter);
				or.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(or);
				//Remove the or label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("xor"))
			{
				//Create a new symbol to store the xor label
				Symbol xor = new Symbol();
				xor.setLabel(line.get(0));
				xor.setLength(1);
				xor.setLocation(lineCounter);
				xor.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(xor);
				//Remove the xor label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseXor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("nor"))
			{
				//Create a new symbol to store the nor label
				Symbol nor = new Symbol();
				nor.setLabel(line.get(0));
				nor.setLength(1);
				nor.setLocation(lineCounter);
				nor.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(nor);
				//Remove the nor label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseNor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("jr"))
			{
				//Create a new symbol to store the jr label
				Symbol jr = new Symbol();
				jr.setLabel(line.get(0));
				jr.setLength(1);
				jr.setLocation(lineCounter);
				jr.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(jr);
				//Remove the jr label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseJr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("srv"))
			{
				//Create a new symbol to store the srv label
				Symbol srv = new Symbol();
				srv.setLabel(line.get(0));
				srv.setLength(1);
				srv.setLocation(lineCounter);
				srv.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(srv);
				//Remove the srv label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSrv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("dump"))
			{
				//Create a new symbol to store the dump label
				Symbol dump = new Symbol();
				dump.setLabel(line.get(0));
				dump.setLength(1);
				dump.setLocation(lineCounter);
				dump.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(dump);
				//Remove the dump label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDump(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("inn"))
			{
				//Create a new symbol to store the inn label
				Symbol inn = new Symbol();
				inn.setLabel(line.get(0));
				inn.setLength(1);
				inn.setLocation(lineCounter);
				inn.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(inn);
				//Remove the inn label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseInn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("inc"))
			{
				//Create a new symbol to store the inc label
				Symbol inc = new Symbol();
				inc.setLabel(line.get(0));
				inc.setLength(1);
				inc.setLocation(lineCounter);
				inc.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(inc);
				//Remove the inc label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseInc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("outn"))
			{
				//Create a new symbol to store the outn label
				Symbol outn = new Symbol();
				outn.setLabel(line.get(0));
				outn.setLength(1);
				outn.setLocation(lineCounter);
				outn.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(outn);
				//Remove the outn label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOutn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("outc"))
			{
				//Create a new symbol to store the outc label
				Symbol outc = new Symbol();
				outc.setLabel(line.get(0));
				outc.setLength(1);
				outc.setLocation(lineCounter);
				outc.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(outc);
				//Remove the outc label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOutc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("outni"))
			{
				//Create a new symbol to store the Outni label
				Symbol Outni = new Symbol();
				Outni.setLabel(line.get(0));
				Outni.setLength(1);
				Outni.setLocation(lineCounter);
				Outni.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(Outni);
				//Remove the Outni label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOutni(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("outci"))
			{
				//Create a new symbol to store the outci label
				Symbol outci = new Symbol();
				outci.setLabel(line.get(0));
				outci.setLength(1);
				outci.setLocation(lineCounter);
				outci.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(outci);
				//Remove the outci label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseOutci(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("adds"))
			{
				//Create a new symbol to store the adds label
				Symbol adds = new Symbol();
				adds.setLabel(line.get(0));
				adds.setLength(1);
				adds.setLocation(lineCounter);
				adds.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(adds);
				//Remove the adds label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("subs"))
			{
				//Create a new symbol to store the subs label
				Symbol subs = new Symbol();
				subs.setLabel(line.get(0));
				subs.setLength(1);
				subs.setLocation(lineCounter);
				subs.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(subs);
				//Remove the subs label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseSubs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("muls"))
			{
				//Create a new symbol to store the muls label
				Symbol muls = new Symbol();
				muls.setLabel(line.get(0));
				muls.setLength(1);
				muls.setLocation(lineCounter);
				muls.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(muls);
				//Remove the muls label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseMuls(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(1).equalsIgnoreCase("divs"))
			{
				//Create a new symbol to store the divs label
				Symbol divs = new Symbol();
				divs.setLabel(line.get(0));
				divs.setLength(1);
				divs.setLocation(lineCounter);
				divs.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(divs);
				//Remove the divs label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseDivs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}

			else if (line.get(0).equalsIgnoreCase("addi"))
			{
				
				//Send remaining line to be parsed
				parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("addiu"))
			{
				
				//Send remaining line to be parsed
				parseAddiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subi"))
			{
			
				//Send remaining line to be parsed
				parseSubi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subiu"))
			{
				
				//Send remaining line to be parsed
				parseSubiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muli"))
			{
				//Send remaining line to be parsed
				parseMuli(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muliu"))
			{
				
				//Send remaining line to be parsed
				parseMuliu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divi"))
			{
				
				//Send remaining line to be parsed
				parseDivi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("diviu"))
			{
				
				//Send remaining line to be parsed
				parseDiviu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jeq"))
			{
				//Send remaining line to be parsed
				parseJeq(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jne"))
			{
				//Send remaining line to be parsed
				parseJne(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jgt"))
			{
				//Send remaining line to be parsed
				parseJgt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jlt"))
			{
				//Send remaining line to be parsed
				parseJlt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jle"))
			{
				//Send remaining line to be parsed
				parseJle(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sw"))
			{
				//Send remaining line to be parsed
				parseSW(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lw"))
			{
				//Send remaining line to be parsed
				parseLw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lnw"))
			{
				//Send remaining line to be parsed
				parseLnw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lwi"))
			{
				//Send remaining line to be parsed
				parseLwi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lui"))
			{
				//Send remaining line to be parsed
				parseLui(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ori"))
			{
				//Send remaining line to be parsed
				parseOri(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("xori"))
			{
				//Send remaining line to be parsed
				parseXori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("nori"))
			{
				//Send remaining line to be parsed
				parseNori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("andi"))
			{
				//Send remaining line to be parsed
				parseAndi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("la"))
			{
				//Send remaining line to be parsed
				parseLa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sa"))
			{
				//Send remaining line to be parsed
				parseSa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ands"))
			{
				//Send remaining line to be parsed
				parseAnds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ors"))
			{
				//Send remaining line to be parsed
				parseOrs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("j"))
			{
				//Send remaining line to be parsed
				parseJ(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jal"))
			{
				//Send remaining line to be parsed
				parseJal(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("halt"))
			{
				//Send remaining line to be parsed
				parseHalt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mul"))
			{
				//Send remaining line to be parsed
				parseMul(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mulu"))
			{
				//Send remaining line to be parsed
				parseMulu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("add"))
			{
				//Send remaining line to be parsed
				parseAdd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("addu"))
			{
				//Send remaining line to be parsed
				parseAddu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sub"))
			{
				//Send remaining line to be parsed
				parseSub(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subu"))
			{
				//Send remaining line to be parsed
				parseSubu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("div"))
			{
				//Send remaining line to be parsed
				parseDiv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divu"))
			{
				//Send remaining line to be parsed
				parseDivu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("pwr"))
			{
				//Send remaining line to be parsed
				parsePwr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sll"))
			{
				//Send remaining line to be parsed
				parseSll(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("srl"))
			{
				//Send remaining line to be parsed
				parseSrl(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sra"))
			{
				//Send remaining line to be parsed
				parseSra(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("and"))
			{
				//Send remaining line to be parsed
				parseAnd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("or"))
			{
				//Send remaining line to be parsed
				parseOr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("xor"))
			{
				//Send remaining line to be parsed
				parseXor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("nor"))
			{
				//Send remaining line to be parsed
				parseNor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jr"))
			{
				//Send remaining line to be parsed
				parseJr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("srv"))
			{
				//Send remaining line to be parsed
				parseSrv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("dump"))
			{
				//Send remaining line to be parsed
				parseDump(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("inn"))
			{
				//Send remaining line to be parsed
				parseInn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("inc"))
			{
				//Send remaining line to be parsed
				parseInc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outn"))
			{
				//Send remaining line to be parsed
				parseOutn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outc"))
			{
				//Send remaining line to be parsed
				parseOutc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outni"))
			{
				//Send remaining line to be parsed
				parseOutni(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outci"))
			{
				//Send remaining line to be parsed
				parseOutci(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("adds"))
			{
				//Send remaining line to be parsed
				parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subs"))
			{
				//Send remaining line to be parsed
				parseSubs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muls"))
			{
				//Send remaining line to be parsed
				parseMuls(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divs"))
			{
				//Send remaining line to be parsed
				parseDivs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter);
			}
			else
			{
				//Create an error because the directive in the .text section
				//cannot be parsed
				ErrorData invalidLine = new ErrorData();
				invalidLine.add(lineCounter, 9, "instruction syntax invalid");
				
				//Add it to the ErrorOut table.
				errorsFound.add(invalidLine);
			}					
		}
		
		else
		{
			//Create an error because the directive in the .data section
			//cannot be parsed
			ErrorData syntaxError = new ErrorData();
			syntaxError.add(lineCounter, 8, "Syntax error");
			
			//Add it to the ErrorOut table.
			errorsFound.add(syntaxError);
		}
	}

	private void encodeIntData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}
	
	private void encodeStrData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub
		
		// NOTE: remove the ' on the ends first.

	}
	
	private void encodeHexData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

	}
	
	private void encodeBinData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		// TODO Auto-generated method stub

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
		if (line.size() == 2)
		{
			//declare the integer object that holds the value of the int.Data
			int intDotDataValue = 0;
			
			// Create a string object to hold the integer value of the int.Data
			String integerString = line.get(1);
			// Check the first character of the string; if it is a '+', remove it.
			if (integerString.charAt(0) == '+')
			{
				// Remove the first character from the String.
				integerString = integerString.substring(1);
			}
			
			//Make sure that the starting location is a number
			try
			{
				intDotDataValue = Integer.parseInt(integerString);
			}
			catch(NumberFormatException e)
			{
				//Create an error regarding invalid integer.
				ErrorData invalidInteger = new ErrorData();
				invalidInteger.add(lineCounter, 10, "Integer value is not valid (Can only start with a '+' or '-' followed by numeric characters)");
				
				//Add it to the ErrorOut table.
				errorsFound.add(invalidInteger);
			}
			
			//Make sure the starting location is a valid number of a certain size
			if ((intDotDataValue > 65535) || (intDotDataValue < -65536))
			{
				//Create an error regarding invalid starting location.
				ErrorData integerOutOfBounds = new ErrorData();
				integerOutOfBounds.add(lineCounter, 11, "Integers must be between -65536 and 65535");
				
				//Add it to the ErrorOut table.
				errorsFound.add(integerOutOfBounds);
			}
			
			encodeIntData(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter);
		}
	}
	
	private void parseStrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
		// A String object to hold the String data
		String stringHolder = line.get(1);
		// Check the first and last characters for '
		if (!(stringHolder.charAt(0) == '\'') || !(stringHolder.charAt(stringHolder.length()-1) == '\''))
		{
			//Create an error regarding invalid String.
			ErrorData invalidString = new ErrorData();
			invalidString.add(lineCounter, 12, "String value is not valid (Must start and end with a ' character)");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidString);
		}
		// Otherwise, we check the content for ' and then send the rest to the
		// encoder method
		else
		{
			// Generic integer counter
			int i = 0;
			
			// boolean to prevent multiple errors for one instance
			Boolean error = false;
			
			// Remove the quotes at the end of the string.
			stringHolder = stringHolder.substring(1 , (stringHolder.length() - 1));
			
			while (i < stringHolder.length() && !error)
			{
				if (stringHolder.charAt(i) == '\'')
				{
					// Flag that an error was thrown to prevent multiple occurrences
					// of the same error. (Less spammy)
					error = true;
					//Create an error regarding invalid String.
					ErrorData singleQuoteError = new ErrorData();
					singleQuoteError.add(lineCounter, 13, "String cannot contain a ' character in its content");
					
					//Add it to the ErrorOut table.
					errorsFound.add(singleQuoteError);
				}
				i++;
			}
			
			// Sends the string.Data line object to be encoded.
			encodeStrData(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter);
		}
		
	}
	
	private void parseHexDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
		// A String object to hold the Hex data
		String hexHolder = line.get(1);
		
		// Check the first and last characters for '; check the length is 
		// between 1 and 8 characters then throw an invalid hex value error.
		if (!(hexHolder.charAt(0) == '\'') || !(hexHolder.charAt(hexHolder.length()-1) == '\'')
				|| (hexHolder.length() < 3) || (hexHolder.length() > 10))
		{
			//Create an error regarding invalid Hex syntax.
			ErrorData invalidHexSyntax = new ErrorData();
			invalidHexSyntax.add(lineCounter, 14, "Hex value is not valid (Must start and end with a ' character)");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidHexSyntax);
		}
		else
		{
			// Generic counter variable
			int i = 0;
			
			// Remove the quotes for easier error checking
			hexHolder = hexHolder.substring(1, hexHolder.length() - 1);
			
			while ((hexHolder.length() > i))
			{
				// Create a 1 character long substring representing the Hex
				// character at the index i.
				String hexChar = hexHolder.substring(i, i + 1);
				
				// Check the character to make sure it falls within the range of
				// valid hex values (0-F) and if it doesn't, throw an invalid HexValue error.
				if (!(hexChar.substring(i, i + 1).equalsIgnoreCase("0")) 
						&& !(hexChar.equalsIgnoreCase("1")) 
						&& !(hexChar.equalsIgnoreCase("2"))
						&& !(hexChar.equalsIgnoreCase("3"))
						&& !(hexChar.equalsIgnoreCase("4"))
						&& !(hexChar.equalsIgnoreCase("5"))
						&& !(hexChar.equalsIgnoreCase("6"))
						&& !(hexChar.equalsIgnoreCase("7"))
						&& !(hexChar.equalsIgnoreCase("8"))
						&& !(hexChar.equalsIgnoreCase("9"))
						&& !(hexChar.equalsIgnoreCase("A"))
						&& !(hexChar.equalsIgnoreCase("B"))
						&& !(hexChar.equalsIgnoreCase("C"))
						&& !(hexChar.equalsIgnoreCase("D"))
						&& !(hexChar.equalsIgnoreCase("E"))
						&& !(hexChar.equalsIgnoreCase("F")))
				{
					//Create an error regarding invalid Hex syntax.
					ErrorData invalidHexSyntax = new ErrorData();
					invalidHexSyntax.add(lineCounter, 14, "Hex value is not valid (Must start and end with a ' character)");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidHexSyntax);
				}
			}
			// *********************************
			// Check for Hex values out of bounds
			// *********************************
			if (true)
			{
				//Holder if for out of bounds checking
			}
			else
			{
				// Send the hex number to be encoded.
				encodeHexData(line, errorsFound, symbolsFound, errorIn, instructIn,
						directIn, lineCounter);
			}
		}
		
	}
	
	private void parseBinDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter) {
		
		// A String object to hold the Binary data
		String binHolder = line.get(1);
		
		// Check the first and last characters for '; check the length is 
		// between 1 and 8 characters then throw an invalid hex value error.
		if (!(binHolder.charAt(0) == '\'') || !(binHolder.charAt(binHolder.length()-1) == '\'')
				|| (binHolder.length() < 3) || (binHolder.length() > 34))
		{
			//Create an error regarding invalid Binary syntax.
			ErrorData invalidBinSyntax = new ErrorData();
			invalidBinSyntax.add(lineCounter, 14, "Binary value is not valid (Must start and end with a ' character and only consist of 0's and/or 1's)");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidBinSyntax);
		}
		else
		{
			// Generic counter variable
			int i = 0;
			
			// Creates a flag that changes to false if the binary number is not
			// in the correct format. Should make encoding easier.
			Boolean encodable = true;
			
			// Remove the quotes for easier error checking
			binHolder = binHolder.substring(1, binHolder.length() - 1);
			
			while ((binHolder.length() > i))
			{
				// Create a 1 character long substring representing the Hex
				// character at the index i.
				String binChar = binHolder.substring(i, i + 1);
				
				// Check the character to make sure it falls within the range of
				// valid hex values (0-F) and if it doesn't, throw an invalid HexValue error.
				if (!(binChar.substring(i, i + 1).equalsIgnoreCase("0")) 
						&& !(binChar.equalsIgnoreCase("1")))
				{
					//Create an error regarding invalid Binary syntax.
					ErrorData invalidBinSyntax = new ErrorData();
					invalidBinSyntax.add(lineCounter, 14, "Binary value is not valid (Must start and end with a ' character and only consist of 0's and/or 1's)");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidBinSyntax);
					
					encodable = false;
				}
			}
			
			// Send the binary number to be encoded.
			encodeBinData(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter);
		}
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
