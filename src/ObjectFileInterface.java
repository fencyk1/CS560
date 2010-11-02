
/**
 * 
 * @author Aaron D'Amico
 * Collects the data for object file from the parser object
 * its main goal is to output the object file with this data taken into consideration
 * there will he statments in here that and pretty print statements 
 * ie System.out.println " Header || header stuff" etc
 *
 */
public interface ObjectFileInterface {

	/**
	 * outputs and creates the file
	 * @param sourceCodeFileName
	 */
	void outputObjectFile (IntermediateFile intermediateFile, SymbolTable symbolTable, int locationCounter);
}
