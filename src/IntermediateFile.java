import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class IntermediateFile implements IntermediateFileInterface {
	public ArrayList<String> binCode;
	
	public IntermediateFile() 
	{
		this.binCode = new ArrayList<String>();
	}
	
	@Override
	public void outputIntermediateFile(File intermediateFileName) throws IOException 
	{
		System.out.println(">>>>>>>>>>>>> 		Outputting the intermediate file.");
		
		//get input from file, normally that file will be directives.tbl and be located in the src directory of the code
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter("output/" + intermediateFileName)));
		
		int i = 0;
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		while (this.binCode.size() > i)
		{

			out.println(this.binCode.get(i));
			i++;
		}
		
		//close the input
		out.close();

	}

}
