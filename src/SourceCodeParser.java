// ---Parser notes: check error.tbl / ErrorTable.java's utility.
// ---Might need to change the length of hex.data/bin.data
// ---Change encode helper operations to include an operand for the parsed line's data?
// ---Only enter encode methods if we have syntactically valid commands?
// ---ResetLC needs the location in memory as well
// ---Change symbol's location to be a string for hex values.
// ---Change lineCounter symbols to location
// ---Two's comp to integer converter
// ---Update hex.data
// ---Finish stuff

import java.util.ArrayList;


public class SourceCodeParser implements SourceCodeParserInterface {
	private boolean inDotText = false;
	private boolean inDotData = false;
	private boolean haveDotStart = false;
	
	
	@Override
	public void parseLine(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
	
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
			this.parseDotData(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		}
		else if (this.inDotText)
		{
			this.parseDotText(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		}
		//If in neither, parse as a .start
		else
		{
			//deal with .start and .end methods
			this.parseOther(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
			
		}
		
	}

	
	private void parseOther (ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter)
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
			int startingLocation = locationCounter;
			
			int i = 0;
			
			//Create string object for converting purposes
			String lcConverter = line.get(2);
			
			//If the location in memory is too large, throw an error
			if (lcConverter.length() > 2)
			{
				//Create an error regarding invalid starting location.
				ErrorData invalidStartingLocation = new ErrorData();
				invalidStartingLocation.add(lineCounter, 1, "Staring location is not valid");
				
				//Add it to the ErrorOut table.
				errorsFound.add(invalidStartingLocation);
			}
			//Otherwise check for hex syntax
			else
			{
				//Create a new string for conversion purposes...again
				String lcToHex = new String();
				
				//Check syntax for both potential digits
				while (lcConverter.length() < i)
				{
					//Check for valid hex possibilities
					if(!(lcConverter.substring(i, i + 1).equalsIgnoreCase("0")) 
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("1")) 
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("2"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("3"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("4"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("5"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("6"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("7"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("8"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("9"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("A"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("B"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("C"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("D"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("E"))
							&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("F")))
					{
						//Create an error regarding invalid starting location.
						ErrorData invalidStartingLocation = new ErrorData();
						invalidStartingLocation.add(lineCounter, 1, "Staring location is not valid");
						
						//Add it to the ErrorOut table.
						errorsFound.add(invalidStartingLocation);
					}
					else
					{
						//Concatenate the two digits together if they are syntactically correct
						lcToHex = lcToHex + lcConverter.substring(i,i + 1);
					}
					//Increment the counter
					i++;
				}
				Converter converter = new Converter();
				
				//Convert the location counter into binary, then convert that into decimal, then parse
				//that into an integer and store it in the locationCounter.
				locationCounter = Integer.parseInt(converter.binaryToDecimal(converter.hexToBinary(lcToHex)));
				
				//This line has cooties.
				startingLocation = locationCounter;
			}
			
			
			
			//Make sure the starting location is a valid number of a certain size
			if ((startingLocation > 65535) || (startingLocation < 0))
			{
				//Create an error regarding invalid starting location.
				ErrorData largeStartingLocation = new ErrorData();
				largeStartingLocation.add(lineCounter, 2, "Starting location must be between 0 and 65535 decimal value");
				
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
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {

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
						directIn, lineCounter, locationCounter);		
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
						directIn, lineCounter, locationCounter);		
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
			}
			//If the token contains none of the aforementioned directives,
			//check spot 0, in case they don't have labels.
			if (line.get(0).equalsIgnoreCase("int.data"))
			{
				//Send remaining line to be parsed
				parseIntDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);		
			}
			else if (line.get(0).equalsIgnoreCase("str.data"))
			{
				//Send remaining line to be parsed
				parseStrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);		
			}
			else if (line.get(0).equalsIgnoreCase("hex.data"))
			{
				//Send remaining line to be parsed
				parseHexDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("bin.data"))
			{
				//Send remaining line to be parsed
				parseBinDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("adr.data"))
			{
				//Send remaining line to be parsed
				parseAdrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}	
			else if (line.get(0).equalsIgnoreCase("adr.exp"))
			{
				//Send remaining line to be parsed
				parseAdrDotExp(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mem.skip"))
			{
				//Send remaining line to be parsed
				parseMemSkip(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
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
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {

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
			if (line.get(1).equalsIgnoreCase("nop"))
			{
				//Create a new symbol to store the nop label
				Symbol nop = new Symbol();
				nop.setLabel(line.get(0));
				nop.setLength(1);
				nop.setLocation(lineCounter);
				nop.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(nop);
				//Remove the nop label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseNop(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(1).equalsIgnoreCase("equ"))
			{
				//Send remaining line to be parsed
				parseEqu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(1).equalsIgnoreCase("equ.exp"))
			{
				//Send remaining line to be parsed
				parseEquExp(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(1).equalsIgnoreCase("reset.lc"))
			{
				//Create a new symbol to store the reset.lc label
				Symbol resetDotLC = new Symbol();
				resetDotLC.setLabel(line.get(0));
				resetDotLC.setLength(1);
				resetDotLC.setLocation(lineCounter);
				resetDotLC.setUsage("label");
				
				//Put it in the symbol table
				symbolsFound.defineSymbol(resetDotLC);
				//Remove the reset.lc label from the line
				line.remove(0);
				//Send remaining line to be parsed
				parseResetLC(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(1).equalsIgnoreCase("addi"))
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
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
						directIn, lineCounter, locationCounter);
			}

			else if (line.get(0).equalsIgnoreCase("ent"))
			{
				
				//Send remaining line to be parsed
				parseEnt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ext"))
			{
				
				//Send remaining line to be parsed
				parseExt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("nop"))
			{
				
				//Send remaining line to be parsed
				parseNop(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("exec.start"))
			{
				
				//Send remaining line to be parsed
				parseExecStart(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("debug"))
			{
				//Send remaining line to be parsed
				parseDebug(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("addi"))
			{
				
				//Send remaining line to be parsed
				parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("addiu"))
			{
				
				//Send remaining line to be parsed
				parseAddiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subi"))
			{
			
				//Send remaining line to be parsed
				parseSubi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subiu"))
			{
				
				//Send remaining line to be parsed
				parseSubiu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muli"))
			{
				//Send remaining line to be parsed
				parseMuli(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muliu"))
			{
				
				//Send remaining line to be parsed
				parseMuliu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divi"))
			{
				
				//Send remaining line to be parsed
				parseDivi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("diviu"))
			{
				
				//Send remaining line to be parsed
				parseDiviu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jeq"))
			{
				//Send remaining line to be parsed
				parseJeq(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jne"))
			{
				//Send remaining line to be parsed
				parseJne(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jgt"))
			{
				//Send remaining line to be parsed
				parseJgt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jlt"))
			{
				//Send remaining line to be parsed
				parseJlt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jle"))
			{
				//Send remaining line to be parsed
				parseJle(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sw"))
			{
				//Send remaining line to be parsed
				parseSW(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lw"))
			{
				//Send remaining line to be parsed
				parseLw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lnw"))
			{
				//Send remaining line to be parsed
				parseLnw(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lwi"))
			{
				//Send remaining line to be parsed
				parseLwi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("lui"))
			{
				//Send remaining line to be parsed
				parseLui(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ori"))
			{
				//Send remaining line to be parsed
				parseOri(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("xori"))
			{
				//Send remaining line to be parsed
				parseXori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("nori"))
			{
				//Send remaining line to be parsed
				parseNori(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("andi"))
			{
				//Send remaining line to be parsed
				parseAndi(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("la"))
			{
				//Send remaining line to be parsed
				parseLa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sa"))
			{
				//Send remaining line to be parsed
				parseSa(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ands"))
			{
				//Send remaining line to be parsed
				parseAnds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("ors"))
			{
				//Send remaining line to be parsed
				parseOrs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("j"))
			{
				//Send remaining line to be parsed
				parseJ(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jal"))
			{
				//Send remaining line to be parsed
				parseJal(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("halt"))
			{
				//Send remaining line to be parsed
				parseHalt(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mul"))
			{
				//Send remaining line to be parsed
				parseMul(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("mulu"))
			{
				//Send remaining line to be parsed
				parseMulu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("add"))
			{
				//Send remaining line to be parsed
				parseAdd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("addu"))
			{
				//Send remaining line to be parsed
				parseAddu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sub"))
			{
				//Send remaining line to be parsed
				parseSub(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subu"))
			{
				//Send remaining line to be parsed
				parseSubu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("div"))
			{
				//Send remaining line to be parsed
				parseDiv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divu"))
			{
				//Send remaining line to be parsed
				parseDivu(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("pwr"))
			{
				//Send remaining line to be parsed
				parsePwr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sll"))
			{
				//Send remaining line to be parsed
				parseSll(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("srl"))
			{
				//Send remaining line to be parsed
				parseSrl(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("sra"))
			{
				//Send remaining line to be parsed
				parseSra(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("and"))
			{
				//Send remaining line to be parsed
				parseAnd(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("or"))
			{
				//Send remaining line to be parsed
				parseOr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("xor"))
			{
				//Send remaining line to be parsed
				parseXor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("nor"))
			{
				//Send remaining line to be parsed
				parseNor(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("jr"))
			{
				//Send remaining line to be parsed
				parseJr(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("srv"))
			{
				//Send remaining line to be parsed
				parseSrv(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("dump"))
			{
				//Send remaining line to be parsed
				parseDump(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("inn"))
			{
				//Send remaining line to be parsed
				parseInn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("inc"))
			{
				//Send remaining line to be parsed
				parseInc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outn"))
			{
				//Send remaining line to be parsed
				parseOutn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outc"))
			{
				//Send remaining line to be parsed
				parseOutc(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outni"))
			{
				//Send remaining line to be parsed
				parseOutni(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("outci"))
			{
				//Send remaining line to be parsed
				parseOutci(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("adds"))
			{
				//Send remaining line to be parsed
				parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("subs"))
			{
				//Send remaining line to be parsed
				parseSubs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("muls"))
			{
				//Send remaining line to be parsed
				parseMuls(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
			}
			else if (line.get(0).equalsIgnoreCase("divs"))
			{
				//Send remaining line to be parsed
				parseDivs(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter);
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
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeStrData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub
		
		// NOTE: remove the ' on the ends first.

	}
	
	private void encodeHexData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}
	
	private void encodeBinData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeAdrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub
	}
	
	private void encodeAdrDotExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter,
			ArrayList<String> nestedExpressionValue) {
		// TODO Auto-generated method stub
		
		// NOTE: anything here is syntactically correct save for label names
		// so everytime you hit a '(', take the next expression out of
		// nested expression value, and encode it accordingly. Ask Jeff for
		// clarification
	}

	private void encodeNOP(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub
	}

	private void encodeMemDotSkip(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub
	}
	
	private void encodeResetDotLC(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub
	}

	private void encodeRType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeJType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeIType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeSType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}

	private void encodeIOType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		// TODO Auto-generated method stub

	}
		
	private void parseIntDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
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
			
			//Make sure the value of int.data is a valid number of a certain size
			if ((intDotDataValue > 65535) || (intDotDataValue < -65535))
			{
				//Create an error regarding invalid starting location.
				ErrorData integerOutOfBounds = new ErrorData();
				integerOutOfBounds.add(lineCounter, 11, "Integers must be between -65536 and 65535");
				
				//Add it to the ErrorOut table.
				errorsFound.add(integerOutOfBounds);
			}
			
			encodeIntData(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter, locationCounter);
		}
	}
	
	private void parseStrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
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
					directIn, lineCounter, locationCounter);
		}
		
	}
	
	private void parseHexDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
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
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("1")) 
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("2"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("3"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("4"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("5"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("6"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("7"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("8"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("9"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("A"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("B"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("C"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("D"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("E"))
						&& !(hexChar.substring(i, i + 1).equalsIgnoreCase("F")))
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
						directIn, lineCounter, locationCounter);
			}
		}
		
	}
	
	private void parseBinDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
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
					directIn, lineCounter, locationCounter);
		}
	}
	
	private void parseAdrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there is only one operand
		if(line.size() > 2)
		{
			//Create an error because there is more than one operand
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		else
		{
			//If there is, send it to be encoded
			encodeAdrDotData(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter, locationCounter);
		}
		
	}
	
	/*This parses Address Arithmetic Expressions. These expressions can contain
	 *constants, previously equated strings representing numbers, and labels.
	 *It evaluates add/subtract/multiply/divide operations up to one level of nesting.
	 *Max labels/references is 3.
	 *
	 *Checking label validity is done in the encoder
	 */
	private void parseAdrDotExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
				
		//Make sure there is only one expression included with adr.exp
		if(line.size() > 2)
		{
			//Create an error because there is more than one operand
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		else
		{
			//Create a string to hold the entire expression
			String expression = line.get(1);
			//Create an arraylist to hold all nested expressions we find
			ArrayList<String> nestedExpressionValue = new ArrayList<String>();
			
			//Create a boolean to flag whether we found an error or not
			Boolean error = false;
			
			//Create a counter for iteration
			int counter = 0;
			
			//START: Parenthesis checking and nested expression lifting.
			
			//Check the expression for parenthesis, as according to the directives
			//table, they are allowed one level of nesting. If we find more than one
			//'(', throw an error. Otherwise continue normally.
			while(expression.length() > counter)
			{
				//Check for the start of a nested expression
				if(expression.charAt(counter) == '(')
				{
					//Set a nested counter accordingly for iteration
					int nestedCounter = counter+1;
					
					//Seek out the next parenthesis
					while (nestedCounter < expression.length())
					{
						//If the next parenthesis is another '(', create an error
						if(expression.charAt(nestedCounter) == '(')
						{
							ErrorData nestedExpression = new ErrorData();
							nestedExpression.add(lineCounter, 15, "Too many nested expressions");
							
							//Add the error to the error table
							errorsFound.add(nestedExpression);
							
							//Flag the error boolean as true
							error = true;
							
							//break from the loop, the entire expression is fubar
							break;
						}
						//If the next parenthesis is an ')', load the nested
						//expression into the array nestedExpressionValue
						//and return as normal
						else if(expression.charAt(nestedCounter) == ')')
						{
							nestedExpressionValue.add(expression.substring(counter+1, nestedCounter));
							//break from the loop, we have a valid nested expression
							break;
						}
						//If we only found one '(' and are at the end of the
						//nested expression, return an error.
						else if(nestedCounter == expression.length() -1)
						{
							ErrorData noNestedTermination = new ErrorData();
							noNestedTermination.add(lineCounter, 16, "The nested expression was never terminated");
							
							//Add the error to the error table
							errorsFound.add(noNestedTermination);
							
							//Flag the error boolean as true
							error = true;
							
							//break from the loop, the entire expression is fubar
							break;
						}
						//Increment the nestedCounter iterator
						nestedCounter++;
					}
					
				}
				//Check to see if there is a close parenthesis without an open one
				else if (expression.charAt(counter) == ')')
				{
					ErrorData noNestedStart = new ErrorData();
					noNestedStart.add(lineCounter, 17, "Nested expression terminates without being initialized");
					
					//Add the error to the error table
					errorsFound.add(noNestedStart);
					
					//Flag the error boolean as true
					error = true;
				}
				//Check to see if there was an error, if so there is no need
				//to continue parsing the line
				if (error)
				{
					//If there was an error, break out past the first loop.
					break;
				}
				//Increment the counter iterator
				counter++;
			}
			//END: Parenthesis Checking and nested expression lifting.
			
			//reset reusable variables
			counter = 0;
			
			//START: Expression evaluation and label/external reference checking.
			
			//If we haven't encountered an error, continue parsing.
			if(!error)
			{
				//Create a boolean dictating whether or not the character is a 
				//number
				boolean isNum = true;
				//Create an int to hold the number of labels
				int labels = 0;
				//Create a boolean to hold whether or not we've found an error
				//within the expressions.
				boolean expError = false;
				
				while (counter < expression.length())
				{
					//If we've found an error at some point in the iteration,
					//break from the loop
					if (expError)
					{
						break;
					}
					
					//Determine whether the character is a number or not.
					try
					{
						Integer.parseInt(expression);
					}
					catch(NumberFormatException e)
					{
						isNum = false;
					}
					
					//If it is a number, it is correct, so do nothing
					if(isNum)
					{
					}
					//Check to see if the current character is an operator
					else if(expression.charAt(counter) == '+' || expression.charAt(counter) == '-'
						|| expression.charAt(counter) == '*' || expression.charAt(counter) == '/')
					{
						//Check to make sure there is not an invalid junction of operations
						if(expression.charAt(counter+1) == '+' || expression.charAt(counter+1) == '*'
								|| expression.charAt(counter+1) == '/')
							{
								//Set teh error flag to be true
								expError = true;
							
								//Create an error
								ErrorData doubleOperation = new ErrorData();
								doubleOperation.add(lineCounter, 19, "Invalid junction of operations");
							}
					}
					//If it is not a number or operator, it is a label,
					//So determine how long it is and make sure there aren't more
					//than three labels here.
					else 
					{
						labels++;
						
						//If there are more than three labels, create an error
						if (labels > 3)
						{
							ErrorData tooManyLabels = new ErrorData();
							tooManyLabels.add(lineCounter, 19, "Too many labels");
							
							//Add the error to the error table
							errorsFound.add(tooManyLabels);
							
							//Set the error flag to true
							expError = true;
						}
						//Otherwise, find where the label ends and continue parsing
						else
						{
							//Labels are separated by expressions, so check accordingly
							while (!(expression.charAt(counter+1) == '+') && !(expression.charAt(counter+1) == '-')
								&& !(expression.charAt(counter+1) == '*') && !(expression.charAt(counter+1) == '/'))
							{
								//Move the counter forward until we are clear of the label
								//save for the last letter which the normal increment
								//will take care of
								counter++;
							}
						}
					}
					counter++;
				}
				
				//If there have been no errors, encode the operation.
				if (!expError)
				{
					encodeAdrDotExp(line, errorsFound, symbolsFound, errorIn, instructIn,
							directIn, lineCounter, locationCounter, nestedExpressionValue);
				}
				//Otherwise, encode it as a NOP
				else
				{
					encodeNOP(line, errorsFound, symbolsFound, errorIn, instructIn,
							directIn, lineCounter, locationCounter);
				}
				
				
			}
			//Otherwise, encode the operation as a NOP
			else
			{
				encodeNOP(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter, locationCounter);
			}
		}
	}
	
	private void parseMemSkip(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there is only one operand
		if(line.size() > 2)
		{
			//Create an error because there is more than one operand
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Check to make sure there are only four digits
		else if (line.get(1).length() > 4)
		{
			//Create an error because the operand has too many digits
			ErrorData excessDigits = new ErrorData();
			excessDigits.add(lineCounter, 19, "Operand has too many digits");
			
			//Add the error to the error table
			errorsFound.add(excessDigits);
		}
		//Check to make sure the digits are parsable (integers)
		else if (line.get(1).length() <= 4)
		{
			try
			{
				Integer.parseInt(line.get(1));
			}
			catch (NumberFormatException e)
			{
				//Create an error because there are non-numbers in the operand
				ErrorData nonIntegerValue = new ErrorData();
				nonIntegerValue.add(lineCounter, 20, "Value must be a decimal integer");
				
				//Add the error to the error table
				errorsFound.add(nonIntegerValue);
			}
		}
		//Otherwise, encode it!
		else
		{
			encodeMemDotSkip(line, errorsFound, symbolsFound, errorIn, instructIn,
					directIn, lineCounter, locationCounter);
		}
		
	}
	
	private void parseEnt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there are no more than 4 operands (labels)
		if(line.size() > 5)
		{
			//Create an error if there are too many operands (5 is for the Ent directive)
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//TODO: in pass two, we make sure they are actually in the symbol table.
		
	}
	
	private void parseExt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there are no more than 4 operands (labels)
		if(line.size() > 5)
		{
			//Create an error if there are too many operands (5 is for the Ent directive)
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise, add them to the symbol table
		else
		{
			//Create an iterator
			int counter = 0;
			
			//For each label, make a new entry in the symbol table
			while (counter < line.size()-1)
			{
				Symbol ext = new Symbol();
				ext.setLabel(line.get(counter+1));
				ext.setLength(1);
				ext.setLocation(99999);
				ext.setUsage("EXT");
				counter++;
			}
		}
		//TODO: Not sure what happens in pass 2 here, actually, but something does.
	}
	
	private void parseNop(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there are no operands
		if(line.size() > 1)
		{
			//Create an error if there are any operands
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise, encode it
		else
		{
			encodeNOP(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		}
	}
	
	private void parseExecStart(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there is only one operand
		if(line.size() > 2)
		{
			//Create an error if there are too many operands 
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise, add it to the symbol table
		else
		{
			Symbol execDotStart = new Symbol();
			execDotStart.setLabel(line.get(1));
			execDotStart.setLength(1);
			execDotStart.setLocation(lineCounter);
			execDotStart.setUsage("Prgm Start");
		}
	}
	
	private void parseEqu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there is only one operand
		if(line.size() > 3)
		{
			//Create an error if there are too many
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise check if the string is too long
		else if(line.get(2).length() > 32)
		{
			//Create an error if the string is too long
			ErrorData stringTooLong = new ErrorData();
			stringTooLong.add(lineCounter, 21, "String is too long");
			
			//Add the error to the error table
			errorsFound.add(stringTooLong);
		}
		//Otherwise add it to the symbol table
		else
		{
			Symbol equ = new Symbol();
			equ.setLabel(line.get(0));
			equ.setLocation(lineCounter);
			equ.setUsage("equ");
			equ.setValue(line.get(2));
			
			int length = 0;
			
			//If there is a number of characters evenly divisible by 4, set
			//the length equal to just that, as 4 characters is one word.
			if (line.get(2).length() % 4 == 0)
			{
				length = line.get(2).length() / 4;
			}
			//If there are partial words, divide by 4 and add one for the partial
			//word(s).
			else
			{
				length = line.get(2).length() / 4;
				length = length + 1;
			}
			
			//Set the length
			equ.setLength(length);
		}
	}
	
	private void parseEquExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Check to make sure there is only one operand
		if(line.size() > 3)
		{
			//Create an error if there are too many
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise, parse the expression for correctness
		else
		{
			//Create a string to hold the entire expression
			String expression = line.get(2);
			//Create an arraylist to hold all nested expressions we find
			ArrayList<String> nestedExpressionValue = new ArrayList<String>();
			
			//Create a boolean to flag whether we found an error or not
			Boolean error = false;
			
			//Create a counter for iteration
			int counter = 0;
			
			//START: Parenthesis checking and nested expression lifting.
			
			//Check the expression for parenthesis, as according to the directives
			//table, they are allowed one level of nesting. If we find more than one
			//'(', throw an error. Otherwise continue normally.
			while(expression.length() > counter)
			{
				//Check for the start of a nested expression
				if(expression.charAt(counter) == '(')
				{
					//Set a nested counter accordingly for iteration
					int nestedCounter = counter+1;
					
					//Seek out the next parenthesis
					while (nestedCounter < expression.length())
					{
						//If the next parenthesis is another '(', create an error
						if(expression.charAt(nestedCounter) == '(')
						{
							ErrorData nestedExpression = new ErrorData();
							nestedExpression.add(lineCounter, 15, "Too many nested expressions");
							
							//Add the error to the error table
							errorsFound.add(nestedExpression);
							
							//Flag the error boolean as true
							error = true;
							
							//break from the loop, the entire expression is fubar
							break;
						}
						//If the next parenthesis is an ')', load the nested
						//expression into the array nestedExpressionValue
						//and return as normal
						else if(expression.charAt(nestedCounter) == ')')
						{
							nestedExpressionValue.add(expression.substring(counter+1, nestedCounter));
							//break from the loop, we have a valid nested expression
							break;
						}
						//If we only found one '(' and are at the end of the
						//nested expression, return an error.
						else if(nestedCounter == expression.length() -1)
						{
							ErrorData noNestedTermination = new ErrorData();
							noNestedTermination.add(lineCounter, 16, "The nested expression was never terminated");
							
							//Add the error to the error table
							errorsFound.add(noNestedTermination);
							
							//Flag the error boolean as true
							error = true;
							
							//break from the loop, the entire expression is fubar
							break;
						}
						//Increment the nestedCounter iterator
						nestedCounter++;
					}
					
				}
				//Check to see if there is a close parenthesis without an open one
				else if (expression.charAt(counter) == ')')
				{
					ErrorData noNestedStart = new ErrorData();
					noNestedStart.add(lineCounter, 17, "Nested expression terminates without being initialized");
					
					//Add the error to the error table
					errorsFound.add(noNestedStart);
					
					//Flag the error boolean as true
					error = true;
				}
				//Check to see if there was an error, if so there is no need
				//to continue parsing the line
				if (error)
				{
					//If there was an error, break out past the first loop.
					break;
				}
				//Increment the counter iterator
				counter++;
			}
			//END: Parenthesis Checking and nested expression lifting.
			
			//reset reusable variables
			counter = 0;
			
			//START: Expression evaluation and label/external reference checking.
			
			//If we haven't encountered an error, continue parsing.
			if(!error)
			{
				//Create a boolean dictating whether or not the character is a 
				//number
				boolean isNum = true;
				//Create an int to hold the number of labels
				int labels = 0;
				//Create a boolean to hold whether or not we've found an error
				//within the expressions.
				boolean expError = false;
				
				while (counter < expression.length())
				{
					//If we've found an error at some point in the iteration,
					//break from the loop
					if (expError)
					{
						break;
					}
					
					//Determine whether the character is a number or not.
					try
					{
						Integer.parseInt(expression);
					}
					catch(NumberFormatException e)
					{
						isNum = false;
					}
					
					//If it is a number, it is correct, so do nothing
					if(isNum)
					{
					}
					//Check to see if the current character is an operator
					else if(expression.charAt(counter) == '+' || expression.charAt(counter) == '-'
						|| expression.charAt(counter) == '*' || expression.charAt(counter) == '/')
					{
						//Check to make sure there is not an invalid junction of operations
						if(expression.charAt(counter+1) == '+' || expression.charAt(counter+1) == '*'
								|| expression.charAt(counter+1) == '/')
							{
								//Set teh error flag to be true
								expError = true;
							
								//Create an error
								ErrorData doubleOperation = new ErrorData();
								doubleOperation.add(lineCounter, 19, "Invalid junction of operations");
							}
					}
					//If it is not a number or operator, it is a label,
					//So determine how long it is and make sure there aren't more
					//than three labels here.
					else 
					{
						labels++;
						
						//If there are more than three labels, create an error
						if (labels > 3)
						{
							ErrorData tooManyLabels = new ErrorData();
							tooManyLabels.add(lineCounter, 19, "Too many labels");
							
							//Add the error to the error table
							errorsFound.add(tooManyLabels);
							
							//Set the error flag to true
							expError = true;
						}
						//Otherwise, find where the label ends and continue parsing
						else
						{
							//Labels are separated by expressions, so check accordingly
							while (!(expression.charAt(counter+1) == '+') && !(expression.charAt(counter+1) == '-')
								&& !(expression.charAt(counter+1) == '*') && !(expression.charAt(counter+1) == '/'))
							{
								//Move the counter forward until we are clear of the label
								//save for the last letter which the normal increment
								//will take care of
								counter++;
							}
						}
					}
					counter++;
				}
				//If there have been no errors, add everything to the symbol table
				if (!expError)
				{
					Symbol equExp = new Symbol();
					equExp.setLabel(line.get(0));
					equExp.setLength(1);
					equExp.setLocation(lineCounter);
					equExp.setUsage("Equ exp");
					equExp.setValue(line.get(2));
				}
			}
		}
	}
	
	private void parseResetLC(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Create a boolean to determine if the operand field is an integer
		boolean isNum = true;
		
		//Check if the operand field is an integer
		try
		{
			Integer.parseInt(line.get(1));
		}
		catch (NumberFormatException e)
		{
			//If it isn't, set the flag false
			isNum = false;
		}
		
		//Check to make sure there is only one operand
		if(line.size() > 2)
		{
			//Create an error if there are too many operands 
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise check if it is a number
		else if (!isNum)
		{
			//Create an error if the string is not an integer
			ErrorData nonIntegerValue = new ErrorData();
			nonIntegerValue.add(lineCounter, 20, "Value must be a decimal integer");
			
			//Add the error to the error table
			errorsFound.add(nonIntegerValue);
		}
		//TODO: implement LC here
		//Otherwise check if the number is greater than the last LC
		else if (Integer.parseInt(line.get(1)) < 0)
		{
			//Create an error if the LC is lower than the previous LC
			ErrorData lowerLocationCounter = new ErrorData();
			lowerLocationCounter.add(lineCounter, 22, "New Location Counter must be greater than previous value");
			
			//Add the error to the error table
			errorsFound.add(lowerLocationCounter);
		}
		//Otherwise, encode it
		else
		{
			encodeResetDotLC(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		}
	}
	
	private void parseDebug(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//Create a boolean to determine if the operand field is an integer
		boolean isNum = true;
		
		//Check if the operand field is an integer
		try
		{
			Integer.parseInt(line.get(1));
		}
		catch (NumberFormatException e)
		{
			//If it isn't, set the flag false
			isNum = false;
		}
		
		//Check to make sure there is only one operand
		if(line.size() > 2)
		{
			//Create an error if there are too many operands 
			ErrorData extraOperands = new ErrorData();
			extraOperands.add(lineCounter, 18, "Too many operands");
			
			//Add the error to the error table
			errorsFound.add(extraOperands);
		}
		//Otherwise check if it is a number
		else if (!isNum)
		{
			//Create an error if the string is not an integer
			ErrorData nonIntegerValue = new ErrorData();
			nonIntegerValue.add(lineCounter, 20, "Value must be a decimal integer");
			
			//Add the error to the error table
			errorsFound.add(nonIntegerValue);
		}
		//Otherwise check if it is a valid number (one or two)
		else if (Integer.parseInt(line.get(1)) != 1 || Integer.parseInt(line.get(1)) != 0)
		{
			//Create an error if the number is not valid
			ErrorData falseDebug = new ErrorData();
			falseDebug.add(lineCounter, 23, "Value must be 0, or 1 only");
			
			//Add the error to the error table
			errorsFound.add(falseDebug);
		}
		else
		{
			//Set the internal debug flag?
		}
	}
	
	private void parseAddi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//immediate value
		int imm = 0; 
		
		//Determine whether the character is a number or not.
		try
		{
			imm = Integer.parseInt(line.get(3));
		}
		catch(NumberFormatException e)
		{
			//check the immediate value to be in the correct bounds
			
			//Create an error regarding invalid number which is out of bounds.
			ErrorData nonIntegerValue = new ErrorData();
			nonIntegerValue.add(lineCounter, 20, "Value must be a decimal integer");
			
			//Add it to the ErrorOut table.
			errorsFound.add(nonIntegerValue);
			
		}
		
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else if (imm < -65536  || imm > 65535   )
		{
			//check the immediate value to be in the correct bounds
			
			//Create an error regarding invalid number which is out of bounds.
			ErrorData integerOutOfBounds = new ErrorData();
			integerOutOfBounds.add(lineCounter, 11, "Integers must be between -65536 and 65535");
			
			//Add it to the ErrorOut table.
			errorsFound.add(integerOutOfBounds);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 3; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
			}
			
 
		}
		
	}
	
	private void parseAddiu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseSubi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseSubiu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseMuli(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseMuliu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseDivi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (Integer.parseInt(line.get(3)) == 0 )
		{
			//Create an error regarding invalid number which is out of bounds.
			ErrorData divideByZero = new ErrorData();
			divideByZero.add(lineCounter, 26, "Divide by zero error");
			
			//Add it to the ErrorOut table.
			errorsFound.add(divideByZero);
		}

		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseDiviu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (Integer.parseInt(line.get(3)) == 0 )
		{
			//Create an error regarding invalid number which is out of bounds.
			ErrorData divideByZero = new ErrorData();
			divideByZero.add(lineCounter, 26, "Divide by zero error");
			
			//Add it to the ErrorOut table.
			errorsFound.add(divideByZero);
		}
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseJeq(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJne(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJgt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJlt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJle(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseSW(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseLw(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseLnw(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseLwi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseLui(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseOri(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseXori(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseNori(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseAndi(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseLa(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseSa(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseAnds(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseOrs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJ(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseJal(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseHalt(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseMul(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 4; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
			}
		}
	}
	
	private void parseMulu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseAdd(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseAddu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseSub(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseSubu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
	}
	
	private void parseDiv(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 4; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
				if ((i == 3) &&  (parameter.equalsIgnoreCase("$0")))
				{
					//Create an error regarding invalid number which is out of bounds.
					ErrorData divideByZero = new ErrorData();
					divideByZero.add(lineCounter, 26, "Divide by zero error");
					
					//Add it to the ErrorOut table.
					errorsFound.add(divideByZero);
				}
			}
		}
	}
	
	private void parseDivu(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 4; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
				if ((i == 3) &&  (parameter.equalsIgnoreCase("$0")))
				{
					//Create an error regarding invalid number which is out of bounds.
					ErrorData divideByZero = new ErrorData();
					divideByZero.add(lineCounter, 26, "Divide by zero error");
					
					//Add it to the ErrorOut table.
					errorsFound.add(divideByZero);
				}
			}
		}
	}
	
	private void parsePwr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseSll(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 3; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
			}
		}
		
	}
	
	private void parseSrl(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseSll(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseSra(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseSll(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseAnd(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseOr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseXor(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseNor(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseMul(line, errorsFound, symbolsFound, errorIn,
				instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseJr(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (!(line.size() == 2))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// Create a string to hold the parameter for syntax checking
			String parameter = line.get(1);

			if ((!(parameter.equalsIgnoreCase("$0"))
					|| !(parameter.equalsIgnoreCase("$1"))
					|| !(parameter.equalsIgnoreCase("$2"))
					|| !(parameter.equalsIgnoreCase("$3"))
					|| !(parameter.equalsIgnoreCase("$4"))
					|| !(parameter.equalsIgnoreCase("$5"))
					|| !(parameter.equalsIgnoreCase("$6"))
					|| !(parameter.equalsIgnoreCase("$7"))))
			{
				//Create an error regarding invalid register syntax.
				ErrorData invalidRegisterSyntax = new ErrorData();
				invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");

				//Add it to the ErrorOut table.
				errorsFound.add(invalidRegisterSyntax);
			}
		}
		
	}
	
	private void parseSrv(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAddi(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseDump(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int i = 1; i < 4; i++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(i);
				
				if ((!(parameter.equalsIgnoreCase("0")) || !(parameter.equalsIgnoreCase("1"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidAmount = new ErrorData();
					invalidAmount.add(lineCounter, 27, "Invalid amount. Must be a 0 or 1.");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidAmount);
				}
			}
		}
		
		
	}
	
	private void parseInn(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseInc(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseOutn(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseOutc(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
	}
	
	private void parseOutni(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//immediate value
		int imm = 0; 
		
		//Determine whether the character is a number or not.
		try
		{
			imm = Integer.parseInt(line.get(2));
		}
		catch(NumberFormatException e)
		{
			//check the immediate value to be in the correct bounds
			
			//Create an error regarding invalid number which is out of bounds.
			ErrorData nonIntegerValue = new ErrorData();
			nonIntegerValue.add(lineCounter, 20, "Value must be a decimal integer");
			
			//Add it to the ErrorOut table.
			errorsFound.add(nonIntegerValue);
			
		}
		
		if (!(line.size() == 3))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else if (imm < -65536  || imm > 65535   )
		{
			//check the immediate value to be in the correct bounds
			
			//Create an error regarding invalid number which is out of bounds.
			ErrorData integerOutOfBounds = new ErrorData();
			integerOutOfBounds.add(lineCounter, 11, "Integers must be between -65536 and 65535");
			
			//Add it to the ErrorOut table.
			errorsFound.add(integerOutOfBounds);
		}
		else
		{
			// Encode
		}
		
	}
	
	private void parseOutci(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseOutni(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseAdds(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		//declare the starting location
		int addr = 0;
		
		int i = 0;
		
		//Create string object for converting purposes
		String lcConverter = line.get(3);
		
		//If the location in memory is too large, throw an error
		if (lcConverter.length() > 8)
		{
			//Create an error regarding invalid starting location.
			ErrorData invalidAddress = new ErrorData();
			invalidAddress.add(lineCounter, 28, "Address is not valid");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidAddress);
		}
		//Otherwise check for hex syntax
		else
		{
			//Create a new string for conversion purposes...again
			String lcToHex = new String();
			
			//Check syntax for both potential digits
			while (lcConverter.length() < i)
			{
				//Check for valid hex possibilities
				if(!(lcConverter.substring(i, i + 1).equalsIgnoreCase("0")) 
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("1")) 
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("2"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("3"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("4"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("5"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("6"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("7"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("8"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("9"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("A"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("B"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("C"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("D"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("E"))
						&& !(lcConverter.substring(i, i + 1).equalsIgnoreCase("F")))
				{
					//Create an error regarding invalid starting location.
					ErrorData invalidStartingLocation = new ErrorData();
					invalidStartingLocation.add(lineCounter, 1, "Staring location is not valid");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidStartingLocation);
				}
				else
				{
					//Concatenate the two digits together if they are syntactically correct
					lcToHex = lcToHex + lcConverter.substring(i,i + 1);
				}
				//Increment the counter
				i++;
			}
			Converter converter = new Converter();
			
			//Convert the location counter into binary, then convert that into decimal, then parse
			//that into an integer and store it in the locationCounter.
			locationCounter = Integer.parseInt(converter.binaryToDecimal(converter.hexToBinary(lcToHex)));
			
			//This line has cooties.
			addr = locationCounter;
		}
		
		if (!(line.size() == 4))
		{
			//Create an error regarding invalid number of parameters.
			ErrorData invalidParameterCount = new ErrorData();
			invalidParameterCount.add(lineCounter, 24, "Invalid number of parameters");
			
			//Add it to the ErrorOut table.
			errorsFound.add(invalidParameterCount);
		}
		else if ((addr > 65535) || (addr < 0))
		{
			//Create an error regarding invalid addressing.
			ErrorData addressOutOfBounds = new ErrorData();
			addressOutOfBounds.add(lineCounter, 2, "Address must be between 0 and 65535 decimal value");
			
			//Add it to the ErrorOut table.
			errorsFound.add(addressOutOfBounds);
		}
		else
		{
			// For loop that checks each register parameter for correct syntax
			for (int j = 1; j < 3; j++)
			{
				// Create a string to hold each parameter for syntax checking
				String parameter = line.get(j);
				
				if ((!(parameter.equalsIgnoreCase("$0"))
								|| !(parameter.equalsIgnoreCase("$1"))
								|| !(parameter.equalsIgnoreCase("$2"))
								|| !(parameter.equalsIgnoreCase("$3"))
								|| !(parameter.equalsIgnoreCase("$4"))
								|| !(parameter.equalsIgnoreCase("$5"))
								|| !(parameter.equalsIgnoreCase("$6"))
								|| !(parameter.equalsIgnoreCase("$7"))))
				{
					//Create an error regarding invalid register syntax.
					ErrorData invalidRegisterSyntax = new ErrorData();
					invalidRegisterSyntax.add(lineCounter, 25, "Invalid register syntax. Correct format is \"$X\", where X is a number from [0-7]");
					
					//Add it to the ErrorOut table.
					errorsFound.add(invalidRegisterSyntax);
				}
			}
		}
		
	}
	
	private void parseSubs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseMuls(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}
	
	private void parseDivs(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter) {
		
		parseAdds(line, errorsFound, symbolsFound, errorIn, instructIn, directIn, lineCounter, locationCounter);
		
	}

}
