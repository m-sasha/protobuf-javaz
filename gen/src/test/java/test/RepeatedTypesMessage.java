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


	private final List<Integer> intValues;
	private final List<Double> doubleValues;
	private final List<String> stringValues;
	private final List<IntMessage> intMessages;
	private final List<Boolean> boolValues;



	public RepeatedTypesMessage(List<Integer> intValues, List<Double> doubleValues, List<String> stringValues, List<IntMessage> intMessages, List<Boolean> boolValues){
		this.intValues = intValues;
		this.doubleValues = doubleValues;
		this.stringValues = stringValues;
		this.intMessages = intMessages;
		this.boolValues = boolValues;
	}



	public List<Integer> getIntValues(){
		return intValues;
	}



	public List<Double> getDoubleValues(){
		return doubleValues;
	}



	public List<String> getStringValues(){
		return stringValues;
	}



	public List<IntMessage> getIntMessages(){
		return intMessages;
	}



	public List<Boolean> getBoolValues(){
		return boolValues;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RepeatedTypesMessage that = (RepeatedTypesMessage) o;
		return Objects.equals(intValues, that.intValues) &&
				Objects.equals(doubleValues, that.doubleValues) &&
				Objects.equals(stringValues, that.stringValues) &&
				Objects.equals(intMessages, that.intMessages) &&
				Objects.equals(boolValues, that.boolValues);
	}



	@Override
	public int hashCode(){
		return Objects.hash(intValues, doubleValues, stringValues, intMessages, boolValues);
	}



}
