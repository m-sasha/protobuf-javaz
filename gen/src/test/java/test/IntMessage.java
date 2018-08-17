package test;

import java.util.Objects;



public class IntMessage{

	private final int value;

	public IntMessage(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}



	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		IntMessage that = (IntMessage) o;
		return value == that.value;
	}



	@Override
	public int hashCode(){
		return Objects.hash(value);
	}



	@Override
	public String toString(){
		return "IntMessage{" +
				"value=" + value +
				'}';
	}



}
