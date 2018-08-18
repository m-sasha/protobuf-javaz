package test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;



/**
 * @author Alexander Maryanovsky
 */
public class RepeatedTypesMessage{


//	repeated int32 intValues = 1;
//	repeated double doubleValues = 2;
//	repeated string stringValues = 3;
//	repeated IntMessage intMessages = 4;
//	repeated bool boolValues = 5;


	private final int[] intValues;
	private final double[] doubleValues;
	private final List<String> stringValues;
	private final List<IntMessage> intMessages;
	private final boolean[] boolValues;



	public RepeatedTypesMessage(int[] intValues, double[] doubleValues, List<String> stringValues, List<IntMessage> intMessages, boolean[] boolValues){
		this.intValues = intValues;
		this.doubleValues = doubleValues;
		this.stringValues = stringValues;
		this.intMessages = intMessages;
		this.boolValues = boolValues;
	}



	public int[] getIntValues(){
		return intValues;
	}



	public double[] getDoubleValues(){
		return doubleValues;
	}



	public List<String> getStringValues(){
		return stringValues;
	}



	public List<IntMessage> getIntMessages(){
		return intMessages;
	}



	public boolean[] getBoolValues(){
		return boolValues;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RepeatedTypesMessage that = (RepeatedTypesMessage) o;
		return Arrays.equals(intValues, that.intValues) &&
				Arrays.equals(doubleValues, that.doubleValues) &&
				Objects.equals(stringValues, that.stringValues) &&
				Objects.equals(intMessages, that.intMessages) &&
				Arrays.equals(boolValues, that.boolValues);
	}



	@Override
	public int hashCode(){
		int result = Objects.hash(stringValues, intMessages);
		result = 31 * result + Arrays.hashCode(intValues);
		result = 31 * result + Arrays.hashCode(doubleValues);
		result = 31 * result + Arrays.hashCode(boolValues);
		return result;
	}



}
