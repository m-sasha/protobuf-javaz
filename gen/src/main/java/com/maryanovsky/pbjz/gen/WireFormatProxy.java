package com.maryanovsky.pbjz.gen;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.WireFormat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;



/**
 * Wraps {@link com.google.protobuf.WireFormat} providing access, at code generation time, to
 * non-public fields and methods. Additionally, it provides convenience methods.
 *
 * @author Alexander Maryanovsky
 */
public class WireFormatProxy{


	/**
	 * Maps {@link DescriptorProtos.FieldDescriptorProto.Type} to the corresponding
	 * {@link WireFormat.FieldType}
	 */
	private static final Map<DescriptorProtos.FieldDescriptorProto.Type, WireFormat.FieldType> PROTO_TO_FIELD_TYPE;
	static{
		Map<DescriptorProtos.FieldDescriptorProto.Type, WireFormat.FieldType> map = new EnumMap<>(DescriptorProtos.FieldDescriptorProto.Type.class);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, WireFormat.FieldType.DOUBLE);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, WireFormat.FieldType.FLOAT);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, WireFormat.FieldType.INT64);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, WireFormat.FieldType.UINT64);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, WireFormat.FieldType.INT32);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, WireFormat.FieldType.FIXED64);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, WireFormat.FieldType.FIXED32);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, WireFormat.FieldType.BOOL);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, WireFormat.FieldType.STRING);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP, WireFormat.FieldType.GROUP);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE, WireFormat.FieldType.MESSAGE);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES, WireFormat.FieldType.BYTES);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, WireFormat.FieldType.UINT32);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM, WireFormat.FieldType.ENUM);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, WireFormat.FieldType.SFIXED32);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, WireFormat.FieldType.SFIXED64);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, WireFormat.FieldType.SINT32);
		map.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, WireFormat.FieldType.SINT64);

		PROTO_TO_FIELD_TYPE = map;
	}



	/**
	 * The {@link WireFormat#makeTag} method reference.
	 */
	private static final Method MAKE_TAG_METHOD;
	static{
		try{
			MAKE_TAG_METHOD = WireFormat.class.getDeclaredMethod("makeTag", int.class, int.class);
			MAKE_TAG_METHOD.setAccessible(true);
		} catch (NoSuchMethodException e){
			throw new IllegalStateException("Unable to find WireFormat.makeTag(int, int)");
		}
	}



	/**
	 * Invokes the {@link WireFormat#makeTag} and returns the result.
	 */
	private static int makeTag(int fieldNumber, int wireType){
		try{
			return (Integer)MAKE_TAG_METHOD.invoke(null, fieldNumber, wireType);
		} catch (IllegalAccessException | InvocationTargetException e){
			throw new IllegalStateException("Unable to invoke WireFormat.makeTag(int, int)", e);
		}
	}



	/**
	 * Returns the tag value, given the field number and the
	 * {@link DescriptorProtos.FieldDescriptorProto.Type type} of the field.
	 */
	public static int makeTag(int fieldNumber, DescriptorProtos.FieldDescriptorProto.Type fieldType){
		int wireType = PROTO_TO_FIELD_TYPE.get(fieldType).getWireType();
		return makeTag(fieldNumber, wireType);
	}



	/**
	 * Returns the tag value for length delimited fields, given the field number.
	 */
	public static int makeLengthDelimitedTag(int fieldNumber){
		return makeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
	}



}
