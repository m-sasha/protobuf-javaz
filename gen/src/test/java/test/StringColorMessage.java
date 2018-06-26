package test;

public class StringColorMessage{

	private final String text;
	private final Color color;

	public StringColorMessage(String text, Color color){
		this.text = text;
		this.color = color;
	}

	public String getText(){
		return text;
	}

	public Color getColor(){
		return color;
	}

}
