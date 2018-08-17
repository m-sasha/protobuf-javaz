package test;

import java.util.Objects;



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



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		StringIntMessage that = (StringIntMessage) o;
		return value == that.value &&
				Objects.equals(text, that.text);
	}



	@Override
	public int hashCode(){
		return Objects.hash(text, value);
	}



	@Override
	public String toString(){
		return "StringIntMessage{" +
				"text='" + text + '\'' +
				", value=" + value +
				'}';
	}



}
