package lab3_source;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Loader implements LoaderInterface {

	public ArrayList<String> loadFile;
	
	//difference between load address of first object file and the length of all previous object files
	public int delta = 0;
	
	
	//constructor
	public Loader() 
	{
		this.loadFile = new ArrayList<String>();
	}
	
	//TODO: void addHeaderToLoadFile();
	
	
	@Override
	public void addObjectToLoadFile() 
	{
		//create header 

	}

	@Override
	public void correctSymbolAddresses() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void output() throws IOException 
	{
		System.out.println(">>>>>>>>>>>>> 		Outputting the intermediate file.");
		
		//get input from file, normally that file will be directives.tbl and be located in the src directory of the code
		PrintWriter out = new PrintWriter (new BufferedWriter(new FileWriter("output/loadFile")));
		
		int i = 0;
		
		//keep getting lines of from the file and add them to the properties objects until the file and been completely traversed
		while (this.loadFile.size() > i)
		{

			out.println(this.loadFile.get(i));
			i++;
		}
		
		//close the input
		out.close();

	}

}
