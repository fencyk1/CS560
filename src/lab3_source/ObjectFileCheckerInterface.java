package lab3_source;

/**
 * Class that is used to check the syntax of the object files given. Checks the
 * Syntax of the hex, checks that the start address is within our bounds of memory,
 * checks that labels have valid syntax, and checks that the amount of text records
 * is less than or equal to the length of the program.
 * @author Austin
 *
 */
public interface ObjectFileCheckerInterface {

	/**
	 * We'll check everything.
	 */
	Boolean checkEverything(ObjectFileSource objectFile);
	
	/**
	 * Checks that the syntax given is valid hex code.
	 * @param lineNumber The number of the line being checked.
	 * @return The result of the check.
	 */
	Boolean checkHexSyntax (String hexData);
	
	/**
	 * Check that the start address in the header is within our memory bounds.
	 * @return The result of the check.
	 */
	Boolean checkStartAddress(String programLoadAddress);
	
	/**
	 * Check that any labels in the line are of correct syntax.
	 * @param lineNumber The number of the line to be checked.
	 * @return The result of the check.
	 */
	Boolean checkLabels(String label);
	
	/**
	 * Check that the amount of text records are less than or equal to the
	 * lenght of the program.
	 * @return The result of the check.
	 */
	Boolean checkRecordLength(ObjectFileSource objectFile);
}