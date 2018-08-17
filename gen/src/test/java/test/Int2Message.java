package test;

import java.util.Objects;



public class Int2Message{

	private final int value1;
	private final int value2;

	public Int2Message(int value1, int value2){
		this.value1 = value1;
		this.value2 = value2;
	}

	public int getValue1(){
		return value1;
	}

	public int getValue2(){
		return value2;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Int2Message that = (Int2Message) o;
		return value1 == that.value1 &&
				value2 == that.value2;
	}



	@Override
	public int hashCode(){
		return Objects.hash(value1, value2);
	}



	@Override
	public String toString(){
		return "Int2Message{" +
				"value1=" + value1 +
				", value2=" + value2 +
				'}';
	}



}
