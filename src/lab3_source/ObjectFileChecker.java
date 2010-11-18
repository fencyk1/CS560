package lab3_source;

import java.io.UnsupportedEncodingException;

import ErrorData;

public class ObjectFileChecker implements ObjectFileCheckerInterface {

	@Override
	public Boolean checkEverything(ObjectFileSource objectFile)
	{
		Boolean errorsExist = false;
		
		// Check the header record.
		// If the start address is outside of the bounds of our memory,
		// swap errorsExist to be true.
		if (checkStartAddress(objectFile.getProgramLengthInHexFromHeader()))
		{
			errorsExist = true;
		}
		
		// General counter
		int i = 1;
		
		// Check each text record of the object file for valid hex and valid
		// labels and makes errorsExist true if either contains errors.
		while (objectFile.textRecords.size() > i-1)
		{
			// Check the hex syntax and change errorsExist to true if there are
			// any errors found.
			if (checkHexSyntax(objectFile.getDataWordFromTextAtLine(i)))
			{
				errorsExist = true;
			}
			
			// Get the number of adjustments from the object file's line
			String adjustments = objectFile.getAdjustmentsFromTextAtLine(i);
			
			// General counter
			int j = 1;
			
			// Set j to be the int representation of adjustments.
			if (adjustments.equals("2"))
			{
				j = 2;
			}
			else if (adjustments.equals("3"))
			{
				j = 3;
			}
			else if (adjustments.equals("4"))
			{
				j = 4;
			}
			
			// General counter
			int k = 1;
			
			// Check the labels for the number of adjustments in the line.
			while (k <= j)
			{
				// Check the first label
				if (k == 1)
				{
					if (checkLabels(objectFile.getFirstLabelReferenceFromTextAtLine(i)))
					{
						errorsExist = true;
					}
				}
				// Check the second label
				else if (k == 2)
				{
					if (checkLabels(objectFile.getSecondLabelReferenceFromTextAtLine(i)))
					{
						errorsExist = true;
					}
				}
				// Check the third label
				else if (k == 3)
				{
					if (checkLabels(objectFile.getThirdLabelReferenceFromTextAtLine(i)))
					{
						errorsExist = true;
					}
				}
				// Check the fourth label
				else if (k == 4)
				{
					if (checkLabels(objectFile.getFourthLabelReferenceFromTextAtLine(i)))
					{
						errorsExist = true;
					}
				}
				k++;
			}
			
			i++;
		}
		
		// Check that the number of text records is less than or equal to the
		// length of the program.
		if (checkRecordLength(objectFile.getProgramLengthInHexFromHeader(), i))
		{
			errorsExist = true;
		}
		
		return errorsExist;
		
	}
	
	@Override
	public Boolean checkHexSyntax(String hexData) {
		// TODO Auto-generated method stub
		
		int i = 0;
		
		while ((hexData.length() > i))
		{
			
			// Create a 1 character long substring representing the Hex
			// character at the index i.
			String hexChar = hexData.substring(i, i + 1);
			
			// Check the character to make sure it falls within the range of
			// valid hex values (0-F) and if it doesn't, throw an invalid HexValue error.
			if (!(hexChar.equalsIgnoreCase("0")) 
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
				return true;
			}
			i++;
			
		}
		
		
		
		return false;
	}

	@Override
	public Boolean checkStartAddress(String programLoadAddress) {
		// TODO Auto-generated method stub
		
		Boolean errorsExist = false;
		
		int loadAddress = 0;
		
		// Attempt to get the load address
		try
		{
			loadAddress = Integer.parseInt(programLoadAddress);
		}
		catch (NumberFormatException e)
		{
			errorsExist = true;
			// TODO Throw and error.
		}
		
		// Check the bound of the load address
		if ((loadAddress < 0) || (loadAddress > 65535))
		{
			errorsExist = true;
		}
		
		return errorsExist;
		
	}

	@Override
	public Boolean checkLabels(String label) {
		// TODO Auto-generated method stub
		
		Boolean errorsExist = false;
		int counter = 0;
		byte[] binary = new byte[1];
		int ascii = 0;
		String labelChar = "";
		
		while ((label.length() > counter) &&(label.charAt(counter) != '('))
		{
			//Move one character from the label into "label"
			labelChar = label.substring(counter, counter+1);
			
			//Use a try catch for syntactical correctness.
			try 
			{
				//Convert the ascii string passed in, into
				//an array of bytes containing their binary
				//representation.
				binary = labelChar.getBytes("US-ASCII");
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
			
			//Convert from a binary stream into an integer representation
			ascii = binary[0];
			
			// Alphanumeric shizznit
			if (!((ascii >= 48 && ascii <=57) || (ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)))
			{			
				errorsExist = true;
			}
			
			counter++;
		}
		
		
		
		return errorsExist;
	}

	@Override
	public Boolean checkRecordLength(String recordLength, int i) {
		// TODO Auto-generated method stub
		Boolean errorsExist = false;
		
		// Check that the number of text records is less than or equal to the
		// length of the program.
		if (!((i-1) <= Integer.parseInt(recordLength)))
		{
			errorsExist = true;
		}
		
		return errorsExist;
	}

}
