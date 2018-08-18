package test;

import java.util.Arrays;



/**
 * @author Alexander Maryanovsky
 */
public class RepeatedIntMessage{


	private final int[] values;



	public RepeatedIntMessage(int[] values){
		this.values = values;
	}



	public int[] getValues(){
		return values;
	}



	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RepeatedIntMessage that = (RepeatedIntMessage) o;
		return Arrays.equals(values, that.values);
	}



	@Override
	public int hashCode(){
		return Arrays.hashCode(values);
	}


}
