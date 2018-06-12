package com.maryanovsky.pbjz.runtime;


import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;



/**
 * The base class for all types that encode and decode user-defined types into and from their
 * protobuf wire representation.
 * The class is parameterized with the user-defined type.
 */
public abstract class Codec<T>{



	/**
	 * Encodes the given value into a {@link CodedOutputStream}.
	 */
	public abstract void encode(T value, CodedOutputStream out) throws IOException;



	/**
	 * Decodes a single value from the given {@link CodedInputStream}.
	 */
	public abstract T decode(CodedInputStream in) throws IOException;



	/**
	 * Writes a {@code double} value at the given field number.
	 */
	protected final void writeDouble(CodedOutputStream out, int fieldNumber, double value) throws IOException{
		if (value != 0D){
			out.writeDouble(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code float} value at the given field number.
	 */
	protected final void writeFloat(CodedOutputStream out, int fieldNumber, float value) throws IOException{
		if (value != 0F){
			out.writeFloat(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code int32} format.
	 */
	protected final void writeInt32(CodedOutputStream out, int fieldNumber, int value) throws IOException{
		if (value != 0){
			out.writeInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code int64} format.
	 */
	protected final void writeInt64(CodedOutputStream out, int fieldNumber, long value) throws IOException{
		if (value != 0L) {
			out.writeInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code uint32} format.
	 */
	protected final void writeUInt32(CodedOutputStream out, int fieldNumber, int value) throws IOException{
		if (value != 0){
			out.writeUInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code uint64} format.
	 */
	protected final void writeUInt64(CodedOutputStream out, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			out.writeUInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code sint32} format.
	 */
	protected final void writeSInt32(CodedOutputStream out, int fieldNumber, int value) throws IOException{
		if (value != 0){
			out.writeSInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code sint64} format.
	 */
	protected final void writeSInt64(CodedOutputStream out, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			out.writeSInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code fixed32} format.
	 */
	protected final void writeFixed32(CodedOutputStream out, int fieldNumber, int value) throws IOException{
		if (value != 0){
			out.writeFixed32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code fixed64} format.
	 */
	protected final void writeFixed64(CodedOutputStream out, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			out.writeFixed64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code sfixed32} format.
	 */
	protected final void writeSFixed32(CodedOutputStream out, int fieldNumber, int value) throws IOException{
		if (value != 0){
			out.writeSFixed32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code sfixed64} format.
	 */
	protected final void writeSFixed64(CodedOutputStream out, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			out.writeSFixed64(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code boolean} value at the given field number.
	 */
	protected final void writeBool(CodedOutputStream out, int fieldNumber, boolean value) throws IOException{
		if (value){
			out.writeBool(fieldNumber, true);
		}
	}



	/**
	 * Writes a {@link String} at the given field number.
	 */
	protected final void writeString(CodedOutputStream out, int fieldNumber, String value) throws IOException{
		if (!value.isEmpty()){
			out.writeString(fieldNumber, value);
		}
	}



	/**
	 * Writes a byte array at the given field number.
	 */
	protected final void writeBytes(CodedOutputStream out, int fieldNumber, byte[] value) throws IOException{
		if (value.length != 0){
			out.writeByteArray(fieldNumber, value);
		}
	}



}
