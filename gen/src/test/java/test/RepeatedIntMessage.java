package test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;



/**
 * @author Alexander Maryanovsky
 */
public class RepeatedIntMessage{


	private final List<Integer> values;



	public RepeatedIntMessage(List<Integer> values){
		this.values = values;
	}



	public List<Integer> getValues(){
		return values;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RepeatedIntMessage that = (RepeatedIntMessage) o;
		return Objects.equals(values, that.values);
	}



	@Override
	public int hashCode(){
		return Objects.hash(values);
	}

	
}
