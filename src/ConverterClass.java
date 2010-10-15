import java.io.UnsupportedEncodingException;

/**
 * OpConverter takes a line of source code in a number format, and converts <br />
 * it into the requested format, that is, binary, hexadecimal, or decimal.
 * 
 * @author Jeff Wolfe
 *
 */
public class ConverterClass implements ConverterInterface {

	public ConverterClass() {
		//No fields, it's a utility class.
	}
	
	@Override
	public String binaryToHex(String binary) {
		//Convert the binary string into a decimal value.
		int decimal = this.binToDec(binary);
		//Convert the decimal value into a hex value.
		String hexOut = Integer.toHexString(decimal);
		
		return hexOut;
	}

	@Override
	public String hexToBinary(String hex) {
		int counter = 0;
		int decimal = 0;
		char digit;
		
		//Convert the entire string to uppercase for conversion purposes.
		hex = hex.toUpperCase();
		
		//Convert each hex digit to decimal one by one and add them up.
		while (hex.length() > counter)
		{
			//Get least significant digit.
			digit = hex.charAt(hex.length()-1-counter);
			//If the digit is 0 we need to do nothing.
			if (digit == '0')
			{
			}
			//Otherwise, we add it to our decimal variable.
			else if (digit == '1')
			{
				decimal = decimal + (1 * (16^counter));
			}
			else if (digit == '2')
			{
				decimal = decimal + (2 * (16^counter));
			}
			else if (digit == '3')
			{
				decimal = decimal + (3 * (16^counter));
			}
			else if (digit == '4')
			{
				decimal = decimal + (4 * (16^counter));
			}
			else if (digit == '5')
			{
				decimal = decimal + (5 * (16^counter));
			}
			else if (digit == '6')
			{
				decimal = decimal + (6 * (16^counter));
			}
			else if (digit == '7')
			{
				decimal = decimal + (7 * (16^counter));
			}
			else if (digit == '8')
			{
				decimal = decimal + (8 * (16^counter));
			}
			else if (digit == '9')
			{
				decimal = decimal + (9 * (16^counter));
			}
			else if (digit == 'A')
			{
				decimal = decimal + (10 * (16^counter));
			}
			else if (digit == 'B')
			{
				decimal = decimal + (11 * (16^counter));
			}
			else if (digit == 'C')
			{
				decimal = decimal + (12 * (16^counter));
			}
			else if (digit == 'D')
			{
				decimal = decimal + (13 * (16^counter));
			}
			else if (digit == 'E')
			{
				decimal = decimal + (14 * (16^counter));
			}
			else
			{
				decimal = decimal + (15 * (16^counter));
			}
			counter++;
		}
		
		//Return the binary string.
		return Integer.toBinaryString(decimal);
	}

	@Override
	public String decimalToHex(String decimal) {
		
		//Convert the string into an integer.
		int dec = Integer.parseInt(decimal);
		//Return the hex string.
		return Integer.toHexString(dec);
	}

	@Override
	public String binaryToDecimal(String binary) {
		
		// Convert the binary string into a decimal integer, then convert
		// the decimal integer into a String and return.
		return Integer.toString(this.binToDec(binary));
	}

	@Override
	public String decimalToBinary(String decimal) {
		
		//Convert the string into an integer.
		int dec = Integer.parseInt(decimal);
		//Return the binary string.
		return Integer.toBinaryString(dec);
	}
	
	public String asciiToBinary(String ascii) {
		
		//Create a new array of bytes, capable of storing 4 bytes,
		//as that is the maximum number of ascii characters we will
		//encounter at any one time, as the SAL560 contains 1-word
		//operations and operands.
		byte[] binary = new byte[4];
		
		//Create a counter variable for iterating through the byte array.
		int counter = 0;
		
		//Create an integer intermediate value to convert into a binary string.
		int rep;
		
		//Create a string for concatenation.
		String currentBin = new String();
		
		//Create a string for holding the total binary value.
		String totalBin = new String();
		
		
		//Use a try catch for syntactical correctness.
		try 
		{
			//Convert the ascii string passed in, into
			//an array of bytes containing their binary
			//representation.
			binary = ascii.getBytes("US-ASCII");
		} 
		//"US-ASCII" is a supported encoding, so this will never
		//throw an error, but is required for syntax measures.
		catch (UnsupportedEncodingException e) 
		{
			//Again, since this will never throw an error, this
			//is here for syntax purposes, but the stack trace
			//would just print out a trace of where the error
			//occurred and halt the program.
			e.printStackTrace();
		}
		
		//Iterate through the array and create the binary 
		//representation
		while (counter < binary.length)
		{
			//Get one ascii value from the byte array and 
			//store it in an integer in base 10.
			rep = binary[counter];
			
			//Turn it into a 7 digit binary string.
			currentBin = Integer.toBinaryString(rep);
			
			//Concatenate it with the total binary string.
			totalBin = totalBin.concat(currentBin);
			
			counter++;
			
		}

		//Return the final string
		return totalBin;
	}

	/**
	 * This private method is called for the intermediate step in some <br />
	 * conversions of converting binary to decimal.
	 * 
	 * @param binary The binary number to be converted into decimal.
	 * @return The converted decimal integer.
	 */
	private int binToDec(String binary) {
		int decimal = 0;
		int counter = 0;
		int conversion = 0;
		
		//Convert each binary digit until you reach the end of the binary number.
		while (binary.length() > counter) 
		{
			//Turn one digit, starting with the least significant one, of the binary string into an integer.
			conversion = Integer.parseInt(binary.substring(binary.length()-counter, binary.length()-counter+1));
			//Multiply that by 2 to the power of whatever position in the string
			//we are in, starting at 0 and ending at binary.length - 1.
			conversion = conversion * 2^counter;
			//Add the newly converted binary digit to the total decimal number.
			decimal = decimal + conversion;
			//Increment the counter.
			counter++;
		}
		
		return decimal;
	}
}
