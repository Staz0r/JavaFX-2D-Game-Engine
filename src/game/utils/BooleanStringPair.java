package game.utils;

public class BooleanStringPair {
	private final boolean flag;
	private final String string;
	
	public BooleanStringPair(String string, boolean flag) {
		
		this.string = string;
		this.flag = flag;
	}

	// Overloaded constructor to create a new instance of BooleanStringPair with a single string
	public BooleanStringPair(String string) {

		this.flag = false;
		this.string = string;
	}
	
	public boolean getFlag() {

		return flag;
	}
	
	public String getString() {

		return string;
	}
}
