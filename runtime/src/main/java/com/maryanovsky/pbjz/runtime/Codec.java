package com.maryanovsky.pbjz.runtime;


import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.google.protobuf.WireFormat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;



/**
 * The base class for all types that encode and decode a user-defined type into and from its
 * protobuf wire representation.
 *
 * The class is parameterized with the aforementioned user-defined type.
 *
 * @author Alexander Maryanovsky
 */
public abstract class Codec<T>{



	/**
	 * Writes the given value of the user-defined type into a {@link CodedOutputStream}.
	 * This is the public method to use in order to write a single value of the user-defined type.
	 */
	public abstract void write(@NotNull CodedOutputStream output, @NotNull T value) throws IOException;



	/**
	 * Reads a single value of the user defined type from the given {@link CodedInputStream}.
	 * This is the public method to use in order to read a single value of the user-defined type.
	 */
	@NotNull
	public abstract T read(@NotNull CodedInputStream input) throws IOException;



	/**
	 * Computes and returns the serialized size of the given value of the user-defined type.
	 */
	protected abstract int computeSerializedSize(@NotNull T value);



	/**
	 * Writes a {@code double} field at the given field number.
	 */
	protected static void writeDoubleField(@NotNull CodedOutputStream output, int fieldNumber, double value) throws IOException{
		if (value != 0D){
			output.writeDouble(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code double} field, at the given field number.
	 */
	protected static int computeDoubleFieldSize(int fieldNumber, double value){
		return (value == 0D) ? 0 : CodedOutputStream.computeDoubleSize(fieldNumber, value);
	}



	/**
	 * Writes a {@code float} field at the given field number.
	 */
	protected static void writeFloatField(@NotNull CodedOutputStream output, int fieldNumber, float value) throws IOException{
		if (value != 0F){
			output.writeFloat(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code float} field, at the given field number.
	 */
	protected static int computeFloatFieldSize(int fieldNumber, float value){
		return (value == 0F) ? 0 : CodedOutputStream.computeFloatSize(fieldNumber, value);
	}



	/**
	 * Writes an {@code int} field at the given field number, encoded in {@code int32} format.
	 */
	protected static void writeInt32Field(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeInt32(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of an {@code int} field, at the given field number, encoded in
	 * {@code int32} format.
	 */
	protected static int computeInt32FieldSize(int fieldNumber, int value){
		return (value == 0) ? 0 : CodedOutputStream.computeInt32Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code long} field at the given field number, encoded in {@code int64} format.
	 */
	protected static void writeInt64Field(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L) {
			output.writeInt64(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code long} field, at the given field number, encoded in
	 * {@code int64} format.
	 */
	protected static int computeInt64FieldSize(int fieldNumber, long value){
		return (value == 0L) ? 0 : CodedOutputStream.computeInt64Size(fieldNumber, value);
	}



	/**
	 * Writes an {@code int} field at the given field number, encoded in {@code uint32} format.
	 */
	protected static void writeUInt32Field(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeUInt32(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of an {@code int} field, at the given field number, encoded in
	 * {@code uint32} format.
	 */
	protected static int computeUInt32FieldSize(int fieldNumber, int value){
		return (value == 0) ? 0 : CodedOutputStream.computeUInt32Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code long} field at the given field number, encoded in {@code uint64} format.
	 */
	protected static void writeUInt64Field(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeUInt64(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code long} field, at the given field number, encoded in
	 * {@code uint64} format.
	 */
	protected static int computeUInt64FieldSize(int fieldNumber, long value){
		return (value == 0L) ? 0 : CodedOutputStream.computeUInt64Size(fieldNumber, value);
	}



	/**
	 * Writes an {@code int} field at the given field number, encoded in {@code sint32} format.
	 */
	protected static void writeSInt32Field(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeSInt32(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of an {@code int} field, at the given field number, encoded in
	 * {@code sint32} format.
	 */
	protected static int computeSInt32FieldSize(int fieldNumber, int value){
		return (value == 0) ? 0 : CodedOutputStream.computeSInt32Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code long} field at the given field number, encoded in {@code sint64} format.
	 */
	protected static void writeSInt64Field(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeSInt64(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code long} field, at the given field number, encoded in
	 * {@code sint64} format.
	 */
	protected static int computeSInt64FieldSize(int fieldNumber, long value){
		return (value == 0L) ? 0 : CodedOutputStream.computeSInt64Size(fieldNumber, value);
	}



	/**
	 * Writes an {@code int} field at the given field number, encoded in {@code fixed32} format.
	 */
	protected static void writeFixed32Field(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeFixed32(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of an {@code int} field, at the given field number, encoded in
	 * {@code fixed32} format.
	 */
	protected static int computeFixed32FieldSize(int fieldNumber, int value){
		return (value == 0) ? 0 : CodedOutputStream.computeFixed32Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code long} field at the given field number, encoded in {@code fixed64} format.
	 */
	protected static void writeFixed64Field(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeFixed64(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code long} field, at the given field number, encoded in
	 * {@code fixed64} format.
	 */
	protected static int computeFixed64FieldSize(int fieldNumber, long value){
		return (value == 0L) ? 0 : CodedOutputStream.computeFixed64Size(fieldNumber, value);
	}



	/**
	 * Writes an {@code int} field at the given field number, encoded in {@code sfixed32} format.
	 */
	protected static void writeSFixed32Field(@NotNull CodedOutputStream output, int fieldNumber, int value) throws IOException{
		if (value != 0){
			output.writeSFixed32(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of an {@code int} field, at the given field number, encoded in
	 * {@code sfixed32} format.
	 */
	protected static int computeSFixed32FieldSize(int fieldNumber, int value){
		return (value == 0) ? 0 : CodedOutputStream.computeSFixed32Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code long} field at the given field number, encoded in {@code sfixed64} format.
	 */
	protected static void writeSFixed64Field(@NotNull CodedOutputStream output, int fieldNumber, long value) throws IOException{
		if (value != 0L){
			output.writeSFixed64(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@code long} field, at the given field number, encoded in
	 * {@code sfixed64} format.
	 */
	protected static int computeSFixed64FieldSize(int fieldNumber, long value){
		return (value == 0L) ? 0 : CodedOutputStream.computeSFixed64Size(fieldNumber, value);
	}



	/**
	 * Writes a {@code boolean} field at the given field number.
	 */
	protected static void writeBoolField(@NotNull CodedOutputStream output, int fieldNumber, boolean value) throws IOException{
		if (value){
			output.writeBool(fieldNumber, true);
		}
	}



	/**
	 * Computes the serialized size of a {@code boolean} field, at the given field number.
	 */
	protected static int computeBoolFieldSize(int fieldNumber, boolean value){
		return !value ? 0 : CodedOutputStream.computeBoolSize(fieldNumber, true);
	}



	/**
	 * Writes a {@link String} field at the given field number. A {@code null} value is treated the
	 * same way as an empty string.
	 */
	protected static void writeStringField(@NotNull CodedOutputStream output, int fieldNumber, @Nullable String value) throws IOException{
		if (value != null){
			output.writeString(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a {@link String} field, at the given field number.
	 */
	protected static int computeStringFieldSize(int fieldNumber, @Nullable String value){
		return (value == null) ? 0 : CodedOutputStream.computeStringSize(fieldNumber, value);
	}



	/**
	 * Writes a byte array field at the given field number. A {@code null} value is treated the same
	 * way as a byte array of zero length.
	 */
	protected static void writeBytesField(@NotNull CodedOutputStream output, int fieldNumber, @Nullable byte[] value) throws IOException{
		if (value != null){
			output.writeByteArray(fieldNumber, value);
		}
	}



	/**
	 * Computes the serialized size of a byte array field, at the given field number.
	 */
	protected static int computeBytesFieldSize(int fieldNumber, @Nullable byte[] value){
		return (value == null) ? 0 : CodedOutputStream.computeByteArraySize(fieldNumber, value);
	}



	/**
	 * Writes the given field of the user-defined type, at the given field number.
	 */
	public final void writeField(@NotNull CodedOutputStream output, int fieldNumber, @Nullable T value) throws IOException{
		if (value != null){
			output.writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
			writeFieldNoTag(output, value);
		}
	}


	/**
	 * Writes the given value of the user-defined type, sans the tag.
	 * This is the equivalent of {@link CodedOutputStream#writeMessageNoTag(MessageLite)}.
	 */
	private void writeFieldNoTag(@NotNull CodedOutputStream output, @NotNull T value) throws IOException{
		output.writeUInt32NoTag(computeSerializedSize(value));
		write(output, value);
	}



	/**
	 * Reads a field of the user-defined type.
	 * This is the equivalent of {@link CodedInputStream#readMessage(Parser, ExtensionRegistryLite)}
	 */
	public T readField(@NotNull CodedInputStream input) throws IOException{
		int length = input.readRawVarint32();
		int oldLimit = input.pushLimit(length);
		T result = read(input);
		input.checkLastTagWas(0);
		input.popLimit(oldLimit);

		return result;
	}



	/**
	 * Computes the serialized size of a field of the user-defined type, at the given field number.
	 */
	public final int computeSerializedSize(int fieldNumber, @Nullable T value){
		if (value == null)
			return 0;

		return CodedOutputStream.computeTagSize(fieldNumber) + computeSerializedSizeNoTag(value);
	}



	/**
	 * Computes the serialized size of a field of the user-defined type, sans the tag.
	 * This is the equivalent of {@link CodedOutputStream#computeMessageSizeNoTag(MessageLite)}.
	 */
	private int computeSerializedSizeNoTag(@NotNull T value){
		int fieldSize = computeSerializedSize(value);
		return CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
	}



}
