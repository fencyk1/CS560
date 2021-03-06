import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Aaron D'Amico
 * 
 * Instruction table is a flat text file. The name of this file is stored on the root directory of the code (As it is with all
 * tables or files that need to be imported). This class has the methods to import this file into a data structure.
 *
 */
public interface InstructTableInterface 
{

	/**
	 * 
	 * import the MOT text file into a data structure.
	 * @throws IOException 
	 * @param File tableFileName is the name of the file to be imported
	 */
	void importTable (File tableFileName) throws IOException;
	
	/**
	 * @param instructionName is the name to instruction to look up to see if it exists
	 * @return is a true false to based on if the instruction exists (true) or not(false)
	 * 
	 * 
	 * When given an instruction it returns a boolean if the error code exists in the data structure. Return is true if instruction 
	 * exists. False otherwise.
	 */
	Boolean hasInstruction (String instructionName);
	
	/**
	 * 
	 * @param instructionName is the name to instruction to look up and find its associated instruction type
	 * @return is a string that represents the instruction type based on the instruction name
	 * 
	 * Get the instruction type based on the instruction name. This returns a string. It returns the string "instruction type not present" if the instruction name doesnt exist in the object.
	 */
	String getInstructionType (String instructionName);
	
	/**
	 * 
	 * @param instructionName is the name to instruction to look up and find its associated instruction opcode
	 * @return is a string that represents the instruction opcode based on the instruction name
	 * 
	 * Get the opcode based on the instruction name. This returns a string. It returns the string "opcode not present" if the instruction name doesnt exist in the object.
	 */
	String getInstructionOpcode (String instructionName);
	
	/**
	 * 
	 * @param instructionName is the name to instruction to look up and find its associated function code 
	 * @return is a string that represents the function code based on the instruction name
	 * 
	 * Get the function code based on the instruction name. This returns a string. It returns the string "function code not present" if the instruction name doesnt exist in the object. It 
	 * returns the string "NA" is the code is not applicable.
	 */
	String getFunctionCode (String instructionName);
	
}
