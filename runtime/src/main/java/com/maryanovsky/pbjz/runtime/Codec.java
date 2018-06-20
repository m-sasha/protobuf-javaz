package com.maryanovsky.pbjz.runtime;


import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;



/**
 * The base class for all types that encode and decode user-defined types into and from their
 * protobuf wire representation.
 * The class is parameterized with the user-defined type.
 *
 * @author Alexander Maryanovsky
 */
public abstract class Codec<T>{



	/**
	 * Encodes the given value of the user-defined type into a {@link CodedOutputStream}.
	 */
	protected abstract void encode(@NotNull CodedOutputStream output, @NotNull T value) throws IOException;



	/**
	 * Decodes a single value of the user-defined type from the given {@link CodedInputStream}.
	 */
	@Nullable
	protected abstract T decode(@NotNull CodedInputStream input) throws IOException;



	/**
	 * Writes a {@code double} value at the given field number.
	 */
	protected static void writeDouble(@NotNull CodedOutputStream output, int fieldNumber, double value) throws IOException{
		if (value != 0D){
			output.writeDouble(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code float} value at the given field number.
	 */
	protected static void writeFloat(@NotNull CodedOutputStream output, int fieldNumber, float value) throws IOException{
		if (value != 0F){
			output.writeFloat(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code int32} format.
	 */
	protected static void writeInt32(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code int64} format.
	 */
	protected static void writeInt64(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L) {
			output.writeInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code uint32} format.
	 */
	protected static void writeUInt32(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeUInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code uint64} format.
	 */
	protected static void writeUInt64(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeUInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code sint32} format.
	 */
	protected static void writeSInt32(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeSInt32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code sint64} format.
	 */
	protected static void writeSInt64(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeSInt64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code fixed32} format.
	 */
	protected static void writeFixed32(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeFixed32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code fixed64} format.
	 */
	protected static void writeFixed64(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeFixed64(fieldNumber, value);
		}
	}



	/**
	 * Writes an {@code int} value at the given field number, encoded in {@code sfixed32} format.
	 */
	protected static void writeSFixed32(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeSFixed32(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code long} value at the given field number, encoded in {@code sfixed64} format.
	 */
	protected static void writeSFixed64(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeSFixed64(fieldNumber, value);
		}
	}



	/**
	 * Writes a {@code boolean} value at the given field number.
	 */
	protected static void writeBool(@NotNull CodedOutputStream output, int fieldNumber, boolean value) throws IOException{
		if (value){
			output.writeBool(fieldNumber, true);
		}
	}



	/**
	 * Writes a {@link String} at the given field number. A {@code null} value is treated the same
	 * way as an empty string.
	 */
	protected static void writeString(@NotNull CodedOutputStream output, int fieldNumber, @Nullable String value) throws IOException{
		if ((value != null) && !value.isEmpty()){
			output.writeString(fieldNumber, value);
		}
	}



	/**
	 * Writes a byte array at the given field number. A {@code null} value is treated the same way
	 * as a byte array of zero length.
	 */
	protected static void writeBytes(@NotNull CodedOutputStream output, int fieldNumber, @Nullable byte[] value) throws IOException{
		if ((value != null) && (value.length != 0)){
			output.writeByteArray(fieldNumber, value);
		}
	}



	/**
	 * Writes the given value, encoded as a protobuf message. This is the method to use in order
	 * to write a value of the user-defined type.
	 */
	public final void writeMessage(@NotNull CodedOutputStream output, @Nullable T value) throws IOException{
		if (value != null){
			encode(output, value);
		}
	}



}
