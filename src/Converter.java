import java.io.UnsupportedEncodingException;
import java.lang.Math;

/**
 * OpConverter takes a line of source code in a number format, and converts <br />
 * it into the requested format, that is, binary, hexadecimal, or decimal.
 * 
 * @author Jeff Wolfe
 *
 */
public class Converter implements ConverterInterface {

	public Converter() {
		//No fields, it's a utility class.
	}
	
	@Override
	public String binaryToHex(String binary) {
		//Convert the binary string into a decimal value.		
		long decimal = Long.parseLong(binary,2);
		//Convert the decimal value into a hex value.
		String hexOut = Long.toHexString(decimal);
		return hexOut;
	}

	@Override
	public String hexToBinary(String hex) {
		
		//Convert the hex into a decimal integer.
		int i = Integer.parseInt(hex,16);
		
		//Convert that into binary.
		String bin = Integer.toBinaryString(i);
		
		return bin;
	}

	@Override
	public String decimalToHex(String decimal) {
		
		//Convert the string into an integer.
		long dec = Long.parseLong(decimal);
		//Return the hex string.
		return Long.toHexString(dec);
	}

	@Override
	public String binaryToDecimal(String binary) {
		
		// Convert the binary string into a decimal integer, then convert
		// the decimal integer into a String and return.
		//Convert the binary string into a decimal value.		
		long decimal = Long.parseLong(binary,2);
		return Long.toString(decimal);
	}

	@Override
	public String decimalToBinary(String decimal) {
		
		//Convert the string into an integer.
		long dec = Long.parseLong(decimal);
		//Return the binary string.
		return Long.toBinaryString(dec);
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
			
			//Turn it into a 8 digit binary string.
			currentBin = Integer.toBinaryString(rep);
			while (currentBin.length() < 8)
			{
				currentBin = "0" + currentBin;
			}
			
			//Concatenate it with the total binary string.
			totalBin = totalBin.concat(currentBin);
			
			counter++;
			
		}

		//Return the final string
		return totalBin;
	}
	
	public String twosCompToInteger(String twos)
	{
		long decimal = Long.parseLong(twos,2);
		return Long.toString(decimal);
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
		String binOne = new String();
		
		//Convert each binary digit until you reach the end of the binary number.
		while (binary.length() > counter) 
		{
			//Turn one digit, starting with the least significant one, of the binary string into an integer.
			binOne = binary.substring(binary.length()-(counter+1), binary.length()-counter);			
			conversion = Integer.parseInt(binOne);
			//Multiply that by 2 to the power of whatever position in the string
			//we are in, starting at 0 and ending at binary.length - 1.
			conversion = (int) (conversion * Math.pow(2, counter));
			//Add the newly converted binary digit to the total decimal number.
			decimal = decimal + conversion;
			//Increment the counter.
			counter++;
		}
		
		return decimal;
	}
}
