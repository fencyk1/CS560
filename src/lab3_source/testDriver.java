package lab3_source;

import java.io.File;
import java.io.IOException;

public class testDriver {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//testing bitches
		
		System.out.println("here we go");
		
		File newFile = new File ("objectFiles/objectFileTest.txt");
		ObjectFileSource objectFile = new ObjectFileSource(newFile);

		System.out.println("everything burns");
	}

}
