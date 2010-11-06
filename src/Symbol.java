/**
 * Contains all necessary data about a symbol for the symbol table, as represented
 * by a label, a location, a length, and a usage type.
 * 
 * NOTE: DO NOT CONFUSE THE SYMBOL'S NAME IN JAVA WITH ITS LABEL IN SAL560
 * 
 * @author Jeff W
 *
 */
public class Symbol implements SymbolInterface {
	private String label;
	private String location;
	private int length;
	private String usage;
	private String value;
	
	//Default constructor initializing values.
	public Symbol() {
		this.label = new String();
		this.location = "0";
		this.length = 32;
		this.usage = new String();
		this.value = "N/A";
	}
	
	//Constructor taking necessary/always available parameters.
	public Symbol(String name, String loc, int length) {
		this.label = name;
		this.location = loc;
		this.length = length;
		this.usage = new String();
		this.value = "N/A";
	}
	
	@Override
	public void setLabel(String name) {
		//Set the label representation equal to the string 'name'.
		this.label = name;

	}

	@Override
	public void setLocation(String loc) {
		//Set the location representation equal to the value of loc.
		this.location = loc;

	}

	@Override
	public void setUsage(String use) {
		//Sets how the symbol has been used.
		this.usage = use;
	}

	@Override
	public void setLength(int length) {
		//Sets the length of the symbol, usually one word or 32 bits.
		this.length = length;
	}
	
	
	@Override
	public String getLabel() {
		//Return the label for the symbol, that is what ever it goes by in the
		//program.
		return this.label;
	}

	@Override
	public String getLocation() {
		//Return the location of the symbol.
		return this.location;
	}

	@Override
	public String getUsage() {
		//Return whether or not the symbol has been used.
		return this.usage;
	}

	@Override
	public int getLength() {
		//Return the length of the symbol.
		return this.length;
	}
	
	@Override
	public String getValue() {
		//Return the equated value.
		return this.value;
	}
	
	@Override
	public void setValue(String value) {
		//Sets the value
		this.value = value;
	}

}
