package test;

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

	public AllTypesMessage(){

	}

	public AllTypesMessage(double doubleField, float floatField, int int32Field, long int64Field, int uint32Field, long uint64Field, int sint32Field, long sint64Field, int fixed32Field, long fixed64Field, int sfixed32Field, long sfixed64Field, boolean boolField, String stringField, byte[] bytesField){
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

}
