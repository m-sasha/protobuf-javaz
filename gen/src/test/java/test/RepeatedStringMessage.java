package test;

import java.util.List;
import java.util.Objects;



/**
 * @author Alexander Maryanovsky
 */
public class RepeatedStringMessage{


	private final List<String> texts;



	public RepeatedStringMessage(List<String> texts){
		this.texts = texts;
	}



	public List<String> getTexts(){
		return texts;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RepeatedStringMessage that = (RepeatedStringMessage) o;
		return Objects.equals(texts, that.texts);
	}



	@Override
	public int hashCode(){
		return Objects.hash(texts);
	}


}
