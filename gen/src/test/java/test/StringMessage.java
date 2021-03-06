package test;

import java.util.Objects;



public class StringMessage{

	private final String text;

	public StringMessage(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}



	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		StringMessage that = (StringMessage) o;
		return Objects.equals(text, that.text);
	}



	@Override
	public int hashCode(){
		return Objects.hash(text);
	}



	@Override
	public String toString(){
		return "StringMessage{" +
				"text='" + text + '\'' +
				'}';
	}



}
