package test;

public class StringIntMessage{

	private final String text;
	private final int value;

	public StringIntMessage(String text, int value){
		this.text = text;
		this.value = value;
	}

	public String getText(){
		return text;
	}

	public int getValue(){
		return value;
	}

}
