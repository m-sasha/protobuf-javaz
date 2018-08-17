package test;

import java.util.Objects;



public class String2Message{

	private final String text1;
	private final String text2;

	public String2Message(String text1, String text2){
		this.text1 = text1;
		this.text2 = text2;
	}

	public String getText1(){
		return text1;
	}

	public String getText2(){
		return text2;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		String2Message that = (String2Message) o;
		return Objects.equals(text1, that.text1) &&
				Objects.equals(text2, that.text2);
	}



	@Override
	public int hashCode(){
		return Objects.hash(text1, text2);
	}



	@Override
	public String toString(){
		return "String2Message{" +
				"text1='" + text1 + '\'' +
				", text2='" + text2 + '\'' +
				'}';
	}



}
