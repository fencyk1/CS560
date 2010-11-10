import java.util.ArrayList;

/**
 * holds all the methods to encode the different instruction types and produce the intermediate file
 * @author Aaron D'Amico, Jeff Wolfe, and Austin Lohr
 *
 */
public class Encoder implements EncoderInterface {

	
	private String encodeIntData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {
		
		//Get the last thing in the array of line
		String toEncode = line.get(line.size()-1);
		
		//Encode that into binary (it will be an integer in base 10)
		Converter converter = new Converter();
		
		String encoded = converter.decimalToBinary(toEncode);
		
		//Extend the string to 32 bits
		while (encoded.length() < 32)
		{
			encoded = "0" + encoded;
		}
		
		//Return that string
		return encoded;

	}

	private String encodeStrData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {
		
		//Get the last thing in the array of the line
		String toEncode = line.get(line.size()-1);
		
		//remove the ' on the ends first.
		toEncode = toEncode.substring(1, toEncode.length()-1);
		
		
		Converter converter = new Converter();
		
		//Encode that into binary from ascii
		String encoded = converter.asciiToBinary(toEncode);
	
		//Extend the string to 32 bits
		while (encoded.length() < 32)
		{
			//Add spaces to the end of the string if it is not large enough
			encoded = encoded + "00100000" ;
		}
		
		//Return that string
		return encoded;
	}
	
	private String encodeDebugOn(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName)
	{
		return "DEBUG1";
	}
	
	private String encodeDebugOff(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName)
	{
		return "DEBUG0";
	}
	
	private String encodeHexData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Get the last thing in the array of line
		String toEncode = line.get(line.size()-1);
		
		//Take off the single quotes
		toEncode = toEncode.substring(1, toEncode.length()-1);
		
		//Convert it into binary
		Converter converter = new Converter();
		
		String encoded = converter.hexToBinary(toEncode);
		
		//Extend the string to 32 bits
		while (encoded.length() < 32)
		{
			encoded = "0" + encoded;
		}
		
		//Return that string
		return encoded;
	}
	
	private String encodeBinData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Get the last thing in the array of the line
		String encoded = line.get(line.size()-1);
		
		//Take off the single quotes
		encoded = encoded.substring(1, encoded.length()-1);
		
		//Extend the string to 32 bits
		while (encoded.length() < 32)
		{
			encoded = "0" + encoded;
		}
		
		//Return that string
		return encoded;
	}

	private String encodeAdrDotData(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {
		
		//Get the last thing in the array of the line
		String toEncode = line.get(line.size()-1);
		
		//Put it in brackets
		String encoded = "[" + toEncode + "]";
		
		//Return the string to be encoded after we get the label's value
		
		return encoded;
	}
	
	private String encodeAdrDotExp(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Get the last thing in the array of the line
		String toEncode = line.get(line.size()-1);
		
		//Put it in brackets
		String encoded = "[" + toEncode + "]";
		
		//Return the string to be encoded after we get the label's value
		
		return encoded;
		// NOTE: Move the nested expression value into the final encoder
	}

	private String encodeNOP(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Return the NOP encoding
		
		return "00000010000000000000000000000000";
	}

	private String encodeMemDotSkip(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Get the last thing in the array of the line
		String toEncode = line.get(line.size()-1);
		
		//Skip that many lines in the object file (54 means fill 54 lines with 8 zeroes)
		int skip = Integer.parseInt(toEncode);
		
		int i = 0;
		
		String encoded = new String();
		
		while (i < skip)
		{
			encoded = encoded + "00000000000000000000000000000000";
			i++;
		}
		//return that string
		
		return encoded;
	}
	
	private String encodeResetDotLC(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Get the last thing in the array of the line
		String toEncode = line.get(line.size()-1);
		
		//Encode that into binary
		Converter converter = new Converter();
		
		String encoded = converter.decimalToBinary(toEncode);
		
		//Return that string
		return encoded;
	}

	private String encodeRType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Encode the opCode in the first 6 bits, 00 for unused, 3 bits for R1, R2, R3 each, 6 bits if it's a shift, 3 unused,
		//6 bits for the function code
		
		String encoded = new String();
		
		//Create a new converter to convert the amt to shift into binary
		Converter converter = new Converter();
		
		//Get the opCode
		String opCode = converter.hexToBinary(instructIn.getInstructionOpcode(opName.toLowerCase()));
		//Extend the opCode
		while (opCode.length() < 6)
		{
			opCode = "0" + opCode;
		}
		//Get the function code
		String fnCode = converter.hexToBinary(instructIn.getFunctionCode(opName.toLowerCase()));
		//Extend the function code
		while (fnCode.length() < 6)
		{
			fnCode = "0" + fnCode;
		}
		
		//If it's a shift, encode it accordingly (reg/reg/amt)
		//TODO: differentiate between labels and immediates
		if(opName.equalsIgnoreCase("sll") || opName.equalsIgnoreCase("srl") || opName.equalsIgnoreCase("sra"))
		{	
			boolean number = true;
			
			//Check if the amt field is a number or label
			try 
			{
				Integer.parseInt(line.get(3));
			}
			catch (NumberFormatException e)
			{
				number = false;
			}
			
			
			//Get the first register's value
			String reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			//Get the second register's value
			String reg2 = converter.decimalToBinary(line.get(2).substring(1));
			//Extend register two to three digits
			while (reg2.length() < 3)
			{
				reg2 = "0" + reg2;
			}
			
			String shift = new String();
			
			//If the amt field is a number, convert to binary
			if(number)
			{
				//Get the shift amount
				shift = converter.decimalToBinary(line.get(3));
				//Extend the shift amount
				while(shift.length() < 6)
				{
					shift = "0" + shift;
				}
			}
			//Otherwise add brackets to the label, and put it in the encoded field.
			else
			{
				shift = "[" +line.get(3) + "]";
			}
			
			//Set the third register to unused
			String reg3 = "000";
			
			encoded = opCode + "00" + reg1 + reg2 + reg3 + shift + "000" + fnCode;
		}
		//Otherwise check if it's a jump register
		else if (opName.equalsIgnoreCase("Jr"))
		{
			//Get the first register's value
			String reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			
			encoded = opCode + "00" + reg1 + "000" + "000" + "000000" + "000" + fnCode;
		}
		//Otherwise encode it as if it has three registers
		else
		{
			//Get the first register's value
			String reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			//Get the second register's value
			String reg2 = converter.decimalToBinary(line.get(2).substring(1));
			//Extend register two to three digits
			while (reg2.length() < 3)
			{
				reg2 = "0" + reg2;
			}
			//Get the third register's value
			String reg3 = converter.decimalToBinary(line.get(3).substring(1));
			//Extend register three to three digits
			while (reg3.length() < 3)
			{
				reg3 = "0" + reg3;
			}
			String shift = "000000";
			
			encoded = opCode + "00" + reg1 + reg2 + reg3 + shift + "000" + fnCode;
		}
		
		return encoded;

	}

	private String encodeJType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//Halt is the only J type
		//6 bit op code, 2 bit addr code, 8 bits unused, 16 bits destination
		
		//Get the destination field
		Converter converter = new Converter();
		
		String toEncode = converter.decimalToBinary(line.get(1));
		
		//Extend the destination
		while(toEncode.length() < 16)
		{
			toEncode = "0" + toEncode;
		}
		
		return "0010000000000000" + toEncode; 

	}

	private String encodeIType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			String opName) {

		//6 bit op code, 2 bits unused, 3 bits for R1, R2, 2 unused bits, 16 bits for the Immediate
		
		Converter converter = new Converter();
		
		//Create strings to hold binary encodings in.
		String reg1 = new String();
		String reg2 = new String();
		String immediate = new String();
		
		//Get the opCode
		String opCode = converter.hexToBinary(instructIn.getInstructionOpcode(opName.toLowerCase()));
		//Extend the opCode
		while (opCode.length() < 6)
		{
			opCode = "0" + opCode;
		}
		
		//If the length is 4, parse as reg/reg/imm
		if (line.size() == 4)
		{
			//Get the first register's value
			reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			//Get the second register's value
			reg2 = converter.decimalToBinary(line.get(2).substring(1));
			//Extend register two to three digits
			while (reg2.length() < 3)
			{
				reg2 = "0" + reg2;
			}
			//Get the immediate's value
			immediate = converter.decimalToBinary(line.get(3));
			//Extend the immediate
			while (immediate.length() < 16)
			{
				immediate = "0" + immediate;
			}
		}
		//Or if it's lwi/lui
		else
		{
			//Get the first register's value
			reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			//Get the immediate's value
			immediate = converter.decimalToBinary(line.get(2));
			//Extend the immediate
			while (immediate.length() < 16)
			{
				immediate = "0" + immediate;
			}
			reg2 = "000";
		}

		String encode = opCode + "00" + reg1 + reg2 + "00" + immediate;
		
		return encode;

	}

	private String encodeSType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, int locationCounter,
			String opName) {

		//6 bit op code, 2 bit addr code, 3 bits for R1, R2, 2 unused bits, 16 bits for memory reference
		
		//TODO: addr code
		
		Converter converter = new Converter();
		
		//Get the opCode
		String opCode = converter.hexToBinary(instructIn.getInstructionOpcode(opName.toLowerCase()));
		//Extend the opCode
		while (opCode.length() < 6)
		{
			opCode = "0" + opCode;
		}
		
		//Lay out encoding storage variables
		String reg1 = new String();
		String reg2 = "000";
		String addr = "00";
		String memoryRef = new String();
		
		//Create an operand variable for the addr(register) case
		String operand = new String();
		

		//If the last thing in the array is one of our special flags, remove it.
		if (line.get(line.size()-1) == "*" || line.get(line.size()-1) == "paren"
			|| line.get(line.size()-1) == "label or num")
		{
			//Remove the last thing in the line arraylist, it was an extra element to determine the format of the IO type
			operand = line.remove(line.size()-1);
		}
		
		
		//If it's 4 tokens long, encode as a reg/reg/addr
		if (line.size() == 4)
		{
			boolean number = true;
			
			//Check if the amt field is a number or label
			try 
			{
				Integer.parseInt(line.get(3));
			}
			catch (NumberFormatException e)
			{
				number = false;
			}
			
			if (symbolsFound.GetUsage(line.get(3)).equalsIgnoreCase("ext"))
			{
				addr = "11";
			}
			
			//Get the first register's value
			reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			//Get the second register's value
			reg2 = converter.decimalToBinary(line.get(2).substring(1));
			//Extend register two to three digits
			while (reg2.length() < 3)
			{
				reg2 = "0" + reg2;
			}
			//If it's a number, encode it appropriately
			if (number)
			{
				//Get the shift amount
				memoryRef = converter.decimalToBinary(line.get(3));
				//Extend the shift amount
				while(memoryRef.length() < 6)
				{
					memoryRef = "0" + memoryRef;
				}
			}
			//Otherwise add brackets to the label, and put it in the encoded field.
			else
			{
				memoryRef = "[" +line.get(3) + "]";
			}
		}
		//If it's 3 tokens long, encode as a reg/addr(r2)
		else if (line.size() == 3)
		{
			//Get the first register's value
			reg1 = converter.decimalToBinary(line.get(1).substring(1));
			//Extend register one to three digits
			while (reg1.length() < 3)
			{
				reg1 = "0" + reg1;
			}
			
			//If it's star notation
			if (operand == "*")
			{
				int relocation = 0;
				//If the star notation has a relocation value (+ or - a number)
				if (line.get(2).length() > 2)
				{
					//Get that relocation value
					relocation = Integer.parseInt(line.get(2).substring(2, line.get(2).length()-1));
					//If it's negative, account for it
					if (line.get(2).charAt(1) == '-')
					{
						relocation = relocation * -1;
					}
				}
				
				//put the current location in memory into the memoryRef
				memoryRef = Integer.toString((locationCounter + relocation));
				//Change it into binary
				memoryRef = converter.decimalToBinary(memoryRef);
				//Extend it.
				while (memoryRef.length() < 16)
				{
					memoryRef = "0" + memoryRef;
				}
			}
			//If it has a (r2)
			else if (operand == "paren")
			{
				//Get the register information if available
				reg2 = line.get(2).substring(line.get(2).length()-2, line.get(2).length() -1);
				reg2 = converter.decimalToBinary(reg2);
				
				//Extend register two to three digits
				while (reg2.length() < 3)
				{
					reg2 = "0" + reg2;
				}
				
				//Check if there is a label or number preceeding the parenthesis
				if(line.get(2).length() > 4)
				{
					//Define a boolean to check for integer-ness
					boolean integer = true;
					
					//be able to store the value of the address should it be an integer.
					int address = 0;
					
					//Check if it's an integer
					try
					{
						address = Integer.parseInt(line.get(2).substring(0, line.get(2).indexOf('(')));
					}
					catch (NumberFormatException e)
					{
						integer = false;
					}
					
					//If it's an integer, store the int value in memoryRef
					if (integer)
					{
						memoryRef = Integer.toString(address);
						memoryRef = converter.decimalToBinary(memoryRef);
					}
					//Otherwise, put the label in that bitch.
					else
					{
						memoryRef = line.get(2).substring(0, line.get(2).indexOf('('));
						memoryRef = "[" + memoryRef + "]";
					}
				}
			}
			//If it's just a label or number
			else
			{
				if (symbolsFound.GetUsage(line.get(2)).equalsIgnoreCase("ext"))
				{
					addr = "11";
				}
				
				//Define a boolean to check for integer-ness
				boolean integer = true;
				
				//be able to store the value of the address should it be an integer.
				int address = 0;
				
				//Check if it's an integer
				try
				{
					address = Integer.parseInt(line.get(2));
				}
				catch (NumberFormatException e)
				{
					integer = false;
				}
				
				//If it's an integer, store the int value in memoryRef
				if (integer)
				{
					memoryRef = Integer.toString(address);
					memoryRef = converter.decimalToBinary(memoryRef);
				}
				//Otherwise, put the label in that bitch.
				else
				{
					memoryRef = line.get(2);
					memoryRef = "[" + memoryRef + "]";
				}
			}
		}
		//If it's 2 tokens long, encode as a addr(r1)
		else
		{
			//If it's star notation
			if (operand == "*")
			{
				int relocation = 0;
				//If the star notation has a relocation value (+ or - a number)
				if (line.get(1).length() > 2)
				{
					//Get that relocation value
					relocation = Integer.parseInt(line.get(1).substring(2, line.get(1).length()-1));
					//If it's negative, account for it
					if (line.get(2).charAt(1) == '-')
					{
						relocation = relocation * -1;
					}
				}
				
				//put the current location in memory into the memoryRef
				memoryRef = Integer.toString((locationCounter + relocation));
				//Change it into binary
				memoryRef = converter.decimalToBinary(memoryRef);
				//Extend it.
				while (memoryRef.length() < 16)
				{
					memoryRef = "0" + memoryRef;
				}
			}
			//If it has a (r1)
			else if (operand == "paren")
			{
				//Get the register information if available
				reg1 = line.get(1).substring(line.get(1).length()-2, line.get(1).length() -2);
				reg1 = converter.decimalToBinary(reg1);
				
				//Extend register two to three digits
				while (reg2.length() < 3)
				{
					reg1 = "0" + reg1;
				}
				
				//Check if there is a label or number preceeding the parenthesis
				if(line.get(1).length() > 4)
				{
					//Define a boolean to check for integer-ness
					boolean integer = true;
					
					//be able to store the value of the address should it be an integer.
					int address = 0;
					
					//Check if it's an integer
					try
					{
						address = Integer.parseInt(line.get(1).substring(0, line.get(1).indexOf('(')));
					}
					catch (NumberFormatException e)
					{
						integer = false;
					}
					
					//If it's an integer, store the int value in memoryRef
					if (integer)
					{
						memoryRef = Integer.toString(address);
						memoryRef = converter.decimalToBinary(memoryRef);
					}
					//Otherwise, put the label in that bitch.
					else
					{
						memoryRef = line.get(1).substring(0, line.get(1).indexOf('('));
						memoryRef = "[" + memoryRef + "]";
					}
				}
			}
			//If it's just a label or number
			else
			{
				if (symbolsFound.GetUsage(line.get(1)).equalsIgnoreCase("ext"))
				{
					addr = "11";
				}
				
				//Define a boolean to check for integer-ness
				boolean integer = true;
				
				//be able to store the value of the address should it be an integer.
				int address = 0;
				
				//Check if it's an integer
				try
				{
					address = Integer.parseInt(line.get(1));
				}
				catch (NumberFormatException e)
				{
					integer = false;
				}
				
				//If it's an integer, store the int value in memoryRef
				if (integer)
				{
					memoryRef = Integer.toString(address);
					memoryRef = converter.decimalToBinary(memoryRef);
				}
				//Otherwise, put the label in that bitch.
				else
				{
					memoryRef = line.get(1);
					memoryRef = "[" + memoryRef + "]";
				}
			}
		}

		String encode = opCode + addr + reg1 + reg2 + "00" + memoryRef;
		
		return encode;
		
	}

	private String encodeIOType(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter, 
			int locationCounter, String opName) {

		//6 bit op code, 2 bit addr code, 3 bit R1, 5 bit quantity in words, 16 bits memory reference

		//TODO: addr code, how does it work?
		
		Converter converter = new Converter();
		
		//Create strings to hold binary encodings in.
		String reg1 = "000";
		String quantity = new String();
		String memoryRef = new String();
		String addr = "00";
		
		
		//Get the opCode
		String opCode = converter.hexToBinary(instructIn.getInstructionOpcode(opName));
		//Extend the opCode
		while (opCode.length() < 6)
		{
			opCode = "0" + opCode;
		}
		//Get the quantity
		quantity = converter.decimalToBinary(line.get(1));
		//Extend the quantity
		while (quantity.length() < 5)
		{
			quantity = "0" + quantity;
		}
		//Remove the last thing in the line arraylist, it was an extra element to determine the format of the IO type
		String operand = line.remove(line.size()-1);
		//If it's star notation
		if (operand == "*")
		{
			int relocation = 0;
			//If the star notation has a relocation value (+ or - a number)
			if (line.get(2).length() > 2)
			{
				//Get that relocation value
				relocation = Integer.parseInt(line.get(2).substring(2, line.get(2).length()-1));
				//If it's negative, account for it
				if (line.get(2).charAt(1) == '-')
				{
					relocation = relocation * -1;
				}
			}
			
			//put the current location in memory into the memoryRef
			memoryRef = Integer.toString((locationCounter + relocation));
			//Change it into binary
			memoryRef = converter.decimalToBinary(memoryRef);
			//Extend it.
			while (memoryRef.length() < 16)
			{
				memoryRef = "0" + memoryRef;
			}
		}
		//If it has a (r1)
		else if (operand == "paren")
		{
			//Get the register information if available
			reg1 = line.get(2).substring(line.get(2).length()-2, line.get(2).length() -2);
			reg1 = converter.decimalToBinary(reg1);
			
			//Check if there is a label or number
			if(line.get(2).length() > 4)
			{
				//Define a boolean to check for integer-ness
				boolean integer = true;
				
				//be able to store the value of the address should it be an integer.
				int address = 0;
				
				//Check if it's an integer
				try
				{
					address = Integer.parseInt(line.get(2).substring(0, line.get(2).indexOf('(')));
				}
				catch (NumberFormatException e)
				{
					integer = false;
				}
				
				//If it's an integer, store the int value in memoryRef
				if (integer)
				{
					memoryRef = Integer.toString(address);
					memoryRef = converter.decimalToBinary(memoryRef);
				}
				//Otherwise, put the label in that bitch.
				else
				{
					memoryRef = line.get(2).substring(0, line.get(2).indexOf('('));
					memoryRef = "[" + memoryRef + "]";
				}
			}
		}
		//If it's just a label or number
		else
		{
			if (symbolsFound.GetUsage(line.get(2)).equalsIgnoreCase("ext"))
			{
				addr = "11";
			}
			
			//Define a boolean to check for integer-ness
			boolean integer = true;
			
			//be able to store the value of the address should it be an integer.
			int address = 0;
			
			//Check if it's an integer
			try
			{
				address = Integer.parseInt(line.get(2));
			}
			catch (NumberFormatException e)
			{
				integer = false;
			}
			
			//If it's an integer, store the int value in memoryRef
			if (integer)
			{
				memoryRef = Integer.toString(address);
				memoryRef = converter.decimalToBinary(memoryRef);
			}
			//Otherwise, put the label in that bitch.
			else
			{
				memoryRef = line.get(2);
				memoryRef = "[" + memoryRef + "]";
			}
		}
		
		String encode = opCode + addr + reg1 + quantity + memoryRef;
		return encode;

	}
///////////////////////////////////////////////////////////////////////////////////////
//*******************Public method to encode//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	@Override
	public void encodeLine(ArrayList<String> line, ErrorOut errorsFound,
			SymbolTable symbolsFound, ErrorTable errorIn,
			InstructTable instructIn, DirectiveTable directIn, int lineCounter,
			int locationCounter, IntermediateFile intermediateFile,
			String opName) 
	{
		
		System.out.println("Encoding into binary for the intermediate file.");
		
		//String to hold the binary representation of the data
		String encodedLineBin = new String();
		
		//if in the instruction table
		if (instructIn.hasInstruction(opName.toLowerCase()))
		{
			//if there then look up the type
			String type = instructIn.getInstructionType(opName.toLowerCase());
			
			//call the encode method for that type
			if (type.equalsIgnoreCase("R"))
			{
				encodedLineBin = encodeRType(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
			}
			else if (type.equalsIgnoreCase("S"))
			{
				encodedLineBin = encodeSType(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter, opName);
			}
			else if (type.equalsIgnoreCase("I"))
			{
				encodedLineBin = encodeIType(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
			}
			else if (type.equalsIgnoreCase("IO"))	
			{
				encodedLineBin = encodeIOType(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, locationCounter, opName);
			}
			else if (type.equalsIgnoreCase("J"))	
			{
				encodedLineBin = encodeJType(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
			}		
			
			//add to intermediateFile
			intermediateFile.binCode.add(encodedLineBin);
		}
		//if in the directives table
		else if (directIn.hasDirective(opName.toLowerCase()))
		{
			
			//if it isnt an instruction then it is a directive and if it is being encoded then it impacts memory
			if (opName.equalsIgnoreCase("int.data"))
			{
				encodedLineBin = encodeIntData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}
			else if (opName.equalsIgnoreCase("str.data"))
			{
				encodedLineBin = encodeStrData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				String encodedPartTwo = new String();
				
				//Break the string into as many pieces as necessary
				while (encodedLineBin.length() > 32)
				{
					encodedPartTwo = encodedLineBin.substring(0, 32);
					encodedLineBin = encodedLineBin.substring(32, encodedLineBin.length());
					
					//add to intermediateFile
					intermediateFile.binCode.add(encodedPartTwo);
				}
				
				while (encodedLineBin.length() < 32)
				{
					//Extend the string to 32 bits
					//Add spaces to the end of the string if it is not large enough
					encodedLineBin = encodedLineBin + "00100000" ;
				}
				
				intermediateFile.binCode.add(encodedLineBin);
		
			}
			else if (opName.equalsIgnoreCase("hex.data"))
			{
				encodedLineBin = encodeHexData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				String encodedPartTwo = new String();
				
				//Break the string into as many pieces as necessary
				while (encodedLineBin.length() > 32)
				{
					encodedPartTwo = encodedLineBin.substring(0, 32);
					encodedLineBin = encodedLineBin.substring(32, encodedLineBin.length());
					
					//add to intermediateFile
					intermediateFile.binCode.add(encodedPartTwo);
				}
				
				while (encodedLineBin.length() < 32)
				{
					//Extend the string to 32 bits
					//Add spaces to the end of the string if it is not large enough
					encodedLineBin = "0" + encodedLineBin;
				}
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}	
			else if (opName.equalsIgnoreCase("bin.data"))
			{
				encodedLineBin = encodeBinData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				String encodedPartTwo = new String();
				
				//Break the string into as many pieces as necessary
				while (encodedLineBin.length() > 32)
				{
					encodedPartTwo = encodedLineBin.substring(0, 32);
					encodedLineBin = encodedLineBin.substring(32, encodedLineBin.length());
					
					//add to intermediateFile
					intermediateFile.binCode.add(encodedPartTwo);
				}
				
				while (encodedLineBin.length() < 32)
				{
					//Extend the string to 32 bits
					//Add spaces to the end of the string if it is not large enough
					encodedLineBin = "0" + encodedLineBin;
				}
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}	
			else if (opName.equalsIgnoreCase("adr.data"))
			{
				encodedLineBin = encodeAdrDotData(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}	
			else if (opName.equalsIgnoreCase("adr.exp"))
			{
				encodedLineBin = encodeAdrDotExp(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}	
			else if (opName.equalsIgnoreCase("nop"))
			{
				encodedLineBin = encodeNOP(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}	
			else if (opName.equalsIgnoreCase("mem.skip"))
			{
				encodedLineBin = encodeMemDotSkip(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				String encodedPartTwo = new String();
				
				//Break the string into as many pieces as necessary
				while (encodedLineBin.length() > 32)
				{
					encodedPartTwo = encodedLineBin.substring(0, 32);
					encodedLineBin = encodedLineBin.substring(32, encodedLineBin.length());
					
					//add to intermediateFile
					intermediateFile.binCode.add(encodedPartTwo);
				}
				
			}
			else if (opName.equalsIgnoreCase("reset.lc"))
			{
				encodedLineBin = encodeResetDotLC(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}
			else if (opName.equalsIgnoreCase("debug1"))
			{
				encodedLineBin = encodeDebugOn(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}
			else if (opName.equalsIgnoreCase("debug0"))
			{
				encodedLineBin = encodeDebugOff(line, errorsFound, symbolsFound, errorIn, instructIn, 
						directIn, lineCounter, opName);
				
				//add to intermediateFile
				intermediateFile.binCode.add(encodedLineBin);
			}
			
		}	

	}
	
}
