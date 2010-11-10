import java.io.*;
/**
 * IntermediateFileInterface holds the intermediate data output by the parser
 * and outputs it to a file. It holds the data in an arrayList.
 * 
 * @author Aaron D'Amico
 *
 */
public interface IntermediateFileInterface {

	/**
	 * outputIntermediateFile outputs all of the binary data from the program <br />
	 * into an intermediate file on disk.
	 * 
	 * @param intermediateFileName The intermediate file to be output to a file.
	 * @throws IOException 
	 */
	void outputIntermediateFile(File intermediateFileName) throws IOException;
}
