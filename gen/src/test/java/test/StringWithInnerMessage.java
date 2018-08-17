package test;

import java.util.Objects;



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



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		StringWithInnerMessage that = (StringWithInnerMessage) o;
		return Objects.equals(text, that.text) &&
				Objects.equals(stringMsg, that.stringMsg);
	}



	@Override
	public int hashCode(){
		return Objects.hash(text, stringMsg);
	}



	@Override
	public String toString(){
		return "StringWithInnerMessage{" +
				"text='" + text + '\'' +
				", stringMsg=" + stringMsg +
				'}';
	}



}
