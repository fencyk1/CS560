package lab3_source;

public class GlobalSymbol implements GlobalSymbolInterface {

	private String symbolName;
	private String loadAddress;
	private String computedAddress;
	private String relocatedAddress;
	private String length;
	private String relocationAdjustment;
	private String executionStart;
	
	public GlobalSymbol(String symbolName, String loadAddress, String computedAddress,
			String relocatedAddress, String length, String relocationAdjustment,
			String executionStart)
	{
		this.symbolName = symbolName;
		this.loadAddress = loadAddress;
		this.computedAddress = computedAddress;
		this.relocatedAddress = relocatedAddress;
		this.length = length;
		this.relocationAdjustment = relocationAdjustment;
		this.executionStart = executionStart;
	}
	
	public GlobalSymbol()
	{
		this.symbolName = new String();
		this.loadAddress = new String();
		this.computedAddress = new String();
		this.relocatedAddress = new String();
		this.length = new String();
		this.relocationAdjustment = new String();
		this.executionStart = new String();
	}
	
	@Override
	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;

	}

	@Override
	public void setInitialLoadAddress(String loadAddress) {
		this.loadAddress = loadAddress;

	}

	@Override
	public void setAssemblerComputedAddress(String computedAddress) {
		this.computedAddress = computedAddress;
	}

	@Override
	public void setLoaderRelocatedAddress(String relocatedAddress) {
		this.relocatedAddress = relocatedAddress;

	}

	@Override
	public void setLength(String length) {
		this.length = length;

	}

	@Override
	public void setRelocationAdjustment(String relocationAdjustment) {
		this.relocationAdjustment = relocationAdjustment;
	}

	@Override
	public void setExecutionStartAddress(String executionStart) {
		this.executionStart = executionStart;
	}

	@Override
	public String getSymbolName() {
		return this.symbolName;
	}

	@Override
	public String getInitialLoadAddress() {
		return this.loadAddress;
	}

	@Override
	public String getAssemblerComputedAddress() {
		return this.computedAddress;
	}

	@Override
	public String getLoaderRelocatedAddress() {
		return this.relocatedAddress;
	}

	@Override
	public String getLength() {
		return this.length;
	}

	@Override
	public String getRelocationAdjustment() {
		return this.relocationAdjustment;
	}

	@Override
	public String getExecutionStartAddress() {
		return this.executionStart;
	}

}
