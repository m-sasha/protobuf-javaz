package test;

public class StringWithInnerMessage{

	private final String text;

	private final StringMessage stringMsg;

	public StringWithInnerMessage(String text, StringMessage stringMsg){
		this.text = text;
		this.stringMsg = stringMsg;
	}

	public String getText(){
		return text;
	}

	public StringMessage getStringMsg(){
		return stringMsg;
	}

}
