package test;

import java.util.Objects;



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



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		StringColorMessage that = (StringColorMessage) o;
		return Objects.equals(text, that.text) &&
				color == that.color;
	}



	@Override
	public int hashCode(){
		return Objects.hash(text, color);
	}



	@Override
	public String toString(){
		return "StringColorMessage{" +
				"text='" + text + '\'' +
				", color=" + color +
				'}';
	}



}
