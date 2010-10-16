/**
	 * @author - Kyle Kynard
	 * 
	 * This class is designed to be a holder of information that the parser 
	 * will need in order to produce a proper symbol table. 
	 * 
	 * This information holding object will store each piece needed for the 
	 * symbol table by using three position-aligned ArrayLists.
	 */
public interface InfoHolderInterface {
/**
 * This method takes a value of an LC and searches for the fist (or next) line
 * that has the corresponding LC. 
 * 
 * @param finder The LC with which the line is to be found
 * @param start The line to begin searching the symbol table for the line 
 * 				that has finder as the LC; this value is to be zero if performing 
 * 				the first search 
 * 
 * @return	The line number at which the LC is located; will return a -1 if there
 * 				is no line with the given LC
 */
	int findLineByLC (int finder, int start); 
	/**
	 * This method gives the user the LC that is located at the requested line.
	 * 
	 * @param loc The line number that has the desired LC.
	 * 
	 * @return The LC that is at the line number given by loc
	 */
	int findLCByLine (int loc);
	
	/**
	 * This method searches for the binary encoding collocated with the given LC
	 * 
	 * @param finder The LC with which the binary encoding is to be found
	 * @param start The line to begin searching the symbol table for the binary encoding 
	 * 				that has finder as the LC at the same line; this value is to be zero
	 * 				if performing the first search
	 * @return The binary encoding located at the same line as the LC; will return a null
	 * 			String if no LC 
	 */
	
	
	public String findBinaryByLC( int finder, int start);
	
	/**
	 * Provides the binary encoding located at the given line.
	 * 
	 * @param loc The line with the desired binary string
	 * @return The binary String located at the line given by loc
	 */
	
	String findBinaryByLine (int loc); 
	
	/**
	 * This method will add a new "line" of stored information: an LC, 
	 * the binary encoding, and a new line number to keep track of where
	 * the new info is.
	 * 
	 * @param LC the Location Counter to be added to the line.
	 * @param bin The binary encoding to be added to the line.
	 */
	
	
	void AddLine(int LC, String bin);
}
