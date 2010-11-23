package lab3_source;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Loader implements LoaderInterface {

	//the load file to get outputed 
	public ArrayList<String> loadFile;
	
	//difference between load address of first object file and the length of all previous object files
	public int delta = 0;
	
	//the list of indexes that will need to reference the symbol table in pass two
	ArrayList<Integer> passTwoIndexes = new ArrayList<Integer>();
	
	//constructor
	public Loader() 
	{
		this.loadFile = new ArrayList<String>();
	}
	
	public void addHeaderToLoadFile(ObjectFileSource objectFile)
	{
		//Set up the date and time for printing.		
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd|HH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
        
		//Set the version and revision numbers
		String version = "Version # 2.03";
		String revision = "Revision # 3";
        
		String header;
		header = "LH|" + objectFile.getExecutionStartAddressFromHeader() + "|" +
						objectFile.getModuleNameFromHeader() + "|" + objectFile.getProgramLengthInHexFromHeader() + "|" +
							objectFile.getProgramLoadAddressInHexfromHeader() + "|" + format + "|SAL-LINK|" + version + "|" + revision + "|" +
									objectFile.getModuleNameFromHeader();
		
		//add to the array
		loadFile.add(header);
		
		//TODO add all the text records from the first object file without adding the delta
		//steal this from below
		
		
		//correct the delta
		//add load address to the length 
		int loadAddress = Integer.parseInt(objectFile.getProgramLoadAddressInHexfromHeader(),16);
		int programLength = Integer.parseInt(objectFile.getProgramLengthInHexFromHeader(),16);
		delta = loadAddress + programLength;
		
	}
	
	
	@Override
	public void addObjectToLoadFile(ObjectFileSource objectFile) 
	{
		//iterate through the text record array adding all the text arrays to the load file
		//taking note in an int array list of all the indexes with an E or a R that will need to be referenced later
		// this will come into play but going to load file array at the index of the E or R and adjusting the data there
		int i = 0;
		while (objectFile.textRecords.size() > i)
		{
			String linkerText = new String ();;
			
			//TODO compute the proper addresses by adding the delta to the address in hex
			
			linkerText = "LT|" + objectFile.getAddressInHexFromTextAtLine(i)
			
			
			loadFile.add(linkerText);
			i++;
		}
		
		
		//correct the delta
		//add  the length 
		int programLength = Integer.parseInt(objectFile.getProgramLengthInHexFromHeader(),16);
		delta = programLength + delta;
	}

	@Override
	public void correctSymbolAddresses(GlobalSymbolTable globalSymbolTable) 
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
