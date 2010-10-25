
import java.util.ArrayList;



/**
 * 
 * The parser almost acts like a main. This is because it interacts with almost all the other classes we have created. It takes in all the reference tables, the source code, tokenized source
 *  and output classes to create the input for pass two (most, if not all of these classes should be passed as arguments because we want the objects to pass by reference. 
 *  This way after the parser is done all the objects that it outputs will already be in main (assuming it is main that calls the parser) for use in the final stages of the pass.
 * 
 * It parses by going through a array of Strings (each string being a token. Note that this isnt currently a requirement of the tokenizer class. It is only an idea and I am open to other options.
 * Infact Im sure there are much better ones but I could think of any at the time). As it goes through the tokens is will check all the tables (ie the Symbol, directive, error, instruction, MOT)
 * it does this to check for syntax errors and to properly compile the source code in the first pass. At the first pass, the parser should have produced a symbol table, error out object, User report
 * object (I might have missed one or two but this should be close).
 * 
 * @author Mike Fencyk and Aaron D'Amico
 *
 * 
 */
public interface ParserV2Interface {

	/**
	 * This method will check the line for syntax and errors. If it contains
	 * an error it will be added to the error table. If a symbol is undefined it will be added to an internal list
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 *  
	 * @return returns a boolean value of true if and only if it parses the .end directive
	 */
	Boolean parse(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This will return the collection of undefined variables
	 * 
	 * @return ArrayList<String> of undefined variables
	 */
	ArrayList<String> getUndefinedVariables();
	
	/**
	 * This will return an object containing all the symbols the
	 * parser found along with their usage.
	 * 
	 * @return completed symbol table of type Symbol
	 */
	SymbolTable getSymbols();
	
	/**
	 * This will return an InfoHolder collection of the code converted into binary.
	 * 
	 * @return InfoHolder containing line numbers and binary data
	 */
	InfoHolder getBinaryData();
	
	/**
	 * This method will parse the .start directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseStartDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the .end directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseEndDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the .data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the .text directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseTextDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the Int.data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseIntDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the str.data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseStrDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the hex.data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseHexDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the bin.data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseBinDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the Adr.data directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseAdrDataDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the adr.exp directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseAdrExpDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the Ent directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseEntDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the ext directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseExtDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the NOP directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseNopDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the exec.start directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseExecStartDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the mem.skip directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseMemSkipDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the equ directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseEquDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the Equ.exp directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseEquExpDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the reset.lc directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseResetLCDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the debug directive
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseDebugDirective(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the s type commands
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseSTypeCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the r type commands
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseRTypeCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the j type commands
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseJTypeCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the i type commands
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseITypeCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the io type commands
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseIOTypeCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the LUI command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseLUICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the addi command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseADDICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the addiu command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseADDIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the subi command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseSUBICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the subiu command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseSUBIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the muli command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseMULICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the muliu command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseMULIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the divi command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseDIVICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the diviu command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseDIVIUCommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the ori command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the xori command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseXORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);

	/**
	 * This method will parse the nori command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseNORICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	/**
	 * This method will parse the andi command
	 * 
	 * @param line is the line of code after it has been tokenized. It should be an arraylist
	 *  of strings with each string being one token.
	 *  
	 * @param lineNumber is the number of the line being parsed in int form
	 * 
	 * @param errorsFound is the current list of all errors found in the file in an ErrorOut object
	 */
	void parseANDICommand(ArrayList<String> line, int lineNumber, ErrorOut errorsFound);
	
	
	
	
}
