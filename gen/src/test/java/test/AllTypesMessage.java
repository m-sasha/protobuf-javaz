package test;

import java.util.Arrays;
import java.util.Objects;



public class AllTypesMessage{

	private double doubleField;
	private float floatField;
	private int int32Field;
	private long int64Field;
	private int uint32Field;
	private long uint64Field;
	private int sint32Field;
	private long sint64Field;
	private int fixed32Field;
	private long fixed64Field;
	private int sfixed32Field;
	private long sfixed64Field;
	private boolean boolField;
	private String stringField;
	private byte[] bytesField;
	private StringMessage stringMsgField;
	private Color colorField;

	public AllTypesMessage(){

	}

	public AllTypesMessage(double doubleField, float floatField, int int32Field, long int64Field, int uint32Field, long uint64Field, int sint32Field, long sint64Field, int fixed32Field, long fixed64Field, int sfixed32Field, long sfixed64Field, boolean boolField, String stringField, byte[] bytesField, StringMessage stringMsgField, Color colorField){
		this.doubleField = doubleField;
		this.floatField = floatField;
		this.int32Field = int32Field;
		this.int64Field = int64Field;
		this.uint32Field = uint32Field;
		this.uint64Field = uint64Field;
		this.sint32Field = sint32Field;
		this.sint64Field = sint64Field;
		this.fixed32Field = fixed32Field;
		this.fixed64Field = fixed64Field;
		this.sfixed32Field = sfixed32Field;
		this.sfixed64Field = sfixed64Field;
		this.boolField = boolField;
		this.stringField = stringField;
		this.bytesField = bytesField;
		this.stringMsgField = stringMsgField;
		this.colorField = colorField;
	}

	public double getDoubleField(){
		return doubleField;
	}

	public float getFloatField(){
		return floatField;
	}

	public int getInt32Field(){
		return int32Field;
	}

	public long getInt64Field(){
		return int64Field;
	}

	public int getUint32Field(){
		return uint32Field;
	}

	public long getUint64Field(){
		return uint64Field;
	}

	public int getSint32Field(){
		return sint32Field;
	}

	public long getSint64Field(){
		return sint64Field;
	}

	public int getFixed32Field(){
		return fixed32Field;
	}

	public long getFixed64Field(){
		return fixed64Field;
	}

	public int getSfixed32Field(){
		return sfixed32Field;
	}

	public long getSfixed64Field(){
		return sfixed64Field;
	}

	public boolean isBoolField(){
		return boolField;
	}

	public String getStringField(){
		return stringField;
	}

	public byte[] getBytesField(){
		return bytesField;
	}

	public StringMessage getStringMsgField(){
		return stringMsgField;
	}

	public Color getColorField(){
		return colorField;
	}



	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		AllTypesMessage that = (AllTypesMessage) o;
		return Double.compare(that.doubleField, doubleField) == 0 &&
				Float.compare(that.floatField, floatField) == 0 &&
				int32Field == that.int32Field &&
				int64Field == that.int64Field &&
				uint32Field == that.uint32Field &&
				uint64Field == that.uint64Field &&
				sint32Field == that.sint32Field &&
				sint64Field == that.sint64Field &&
				fixed32Field == that.fixed32Field &&
				fixed64Field == that.fixed64Field &&
				sfixed32Field == that.sfixed32Field &&
				sfixed64Field == that.sfixed64Field &&
				boolField == that.boolField &&
				Objects.equals(stringField, that.stringField) &&
				Arrays.equals(bytesField, that.bytesField) &&
				Objects.equals(stringMsgField, that.stringMsgField) &&
				colorField == that.colorField;
	}



	@Override
	public int hashCode(){
		int result = Objects.hash(doubleField, floatField, int32Field, int64Field, uint32Field,
				uint64Field, sint32Field, sint64Field, fixed32Field, fixed64Field, sfixed32Field,
				sfixed64Field, boolField, stringField, stringMsgField, colorField);
		result = 31 * result + Arrays.hashCode(bytesField);
		return result;
	}



	@Override
	public String toString(){
		return "AllTypesMessage{" +
				"doubleField=" + doubleField +
				", floatField=" + floatField +
				", int32Field=" + int32Field +
				", int64Field=" + int64Field +
				", uint32Field=" + uint32Field +
				", uint64Field=" + uint64Field +
				", sint32Field=" + sint32Field +
				", sint64Field=" + sint64Field +
				", fixed32Field=" + fixed32Field +
				", fixed64Field=" + fixed64Field +
				", sfixed32Field=" + sfixed32Field +
				", sfixed64Field=" + sfixed64Field +
				", boolField=" + boolField +
				", stringField='" + stringField + '\'' +
				", bytesField=" + Arrays.toString(bytesField) +
				", stringMsgField=" + stringMsgField +
				", colorField=" + colorField +
				'}';
	}



	public static class InnerMessage{

		private final int field;

		public InnerMessage(int field){
			this.field = field;
		}

		public int getField(){
			return field;
		}

	}


	public static class Inner2Message{

		private final InnerMessage field;

		public Inner2Message(InnerMessage field){
			this.field = field;
		}

		public InnerMessage getField(){
			return field;
		}

	}

}
