package test;

import java.util.List;
import java.util.Objects;



/**
 * @author Alexander Maryanovsky
 */
public class RepeatedColorMessage{



	private final List<Color> colors;



	public RepeatedColorMessage(List<Color> colors){
		this.colors = colors;
	}



	public List<Color> getColors(){
		return colors;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RepeatedColorMessage that = (RepeatedColorMessage) o;
		return Objects.equals(colors, that.colors);
	}



	@Override
	public int hashCode(){
		return Objects.hash(colors);
	}



}
