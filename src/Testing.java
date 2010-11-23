import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Testing {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		
		String programLengthHex = new String();
		String programLoadAddress = new String();
		
		String numLinkingRecords = new String();
		String numTextRecords = new String();
		String execStartAddress = new String();
		String version = "Version # 2.0";
		String revision = "Revision # 0";
		String programName = new String();
		
		//convert binary to hex
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd\t|\tHH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
		
	    
	    //lookup program name and hex data
		
		System.out.println("H\t|\t" + programName + "\t|\t" + programLengthHex + "\t|\t" + programLoadAddress + "\t|\t" + 
					format + "\t|\t" + numLinkingRecords + "\t|\t" + numTextRecords + "\t|\t" + execStartAddress + "\t|\t"
					+ "SAL\t|\t" + version + "\t|\t" + revision + "\t|\t" + programName);
		
	    
	    //lookup program name and hex data
		
		//System.out.println("H\t|\tModule Name\t|\t" + programLengthHex + "\t|\t" + programLoadAddress + "\t|\t" + 
					//cal.YEAR + ":" + cal.DAY_OF_YEAR + "\t|\t" + cal.HOUR_OF_DAY + ":" + cal.MINUTE + ":" + cal.SECOND + "\t|");
		
		SymbolInterface A = new Symbol("A", "0", 32);
		SymbolInterface B = new Symbol("b", "0", 32);
		SymbolInterface C = new Symbol("C", "0", 32);
		A.setUsage("Rage");
		B.setUsage("Rage");
		C.setUsage("Rage");
		
		SymbolTableInterface test = new SymbolTable();
		test.defineSymbol(A);
		test.defineSymbol(C);
		test.defineSymbol(B);
		
		File sym = new File("symbolTable.txt");
		test.sort();
		test.outputTable(sym);
		
		
		
		/*ErrorData testA = new ErrorData(5,100, "This is an error message");
		ErrorData testB = new ErrorData(0,101, "This is an error message again");
		ErrorData testC = new ErrorData(7,200, "This is the last error message");
		
		ErrorOutInterface container = new ErrorOut();
		container.add(testA);
		container.add(testB);
		container.add(testC);
		
		System.out.println(container.errorExists(testA));
		System.out.println(container.errorAtLine(6));
		System.out.println(container.errorAtLine(0));
		System.out.println(container.output(testA));
		System.out.println(container.search(5).errorMessage());
		*/
	}

}
