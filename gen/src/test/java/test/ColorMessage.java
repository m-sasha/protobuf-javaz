package test;

import java.util.Objects;



public class ColorMessage{

	private final Color color;

	public ColorMessage(Color color){
		this.color = color;
	}

	public Color getColor(){
		return color;
	}



	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ColorMessage that = (ColorMessage) o;
		return color == that.color;
	}



	@Override
	public int hashCode(){
		return Objects.hash(color);
	}



	@Override
	public String toString(){
		return "ColorMessage{" +
				"color=" + color +
				'}';
	}



}
