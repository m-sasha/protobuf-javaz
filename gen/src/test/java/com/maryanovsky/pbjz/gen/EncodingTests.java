package com.maryanovsky.pbjz.gen;


import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import test.AllTypesMessage;
import test.AllTypesMessageCodec;
import test.AllTypesMessageOuterClass;
import test.Color;
import test.ColorMessage;
import test.ColorMessageCodec;
import test.Int2Message;
import test.Int2MessageCodec;
import test.IntMessage;
import test.IntMessageCodec;
import test.OneFieldMessages;
import test.String2Message;
import test.String2MessageCodec;
import test.StringColorMessage;
import test.StringColorMessageCodec;
import test.StringIntMessage;
import test.StringIntMessageCodec;
import test.StringMessage;
import test.StringMessageCodec;
import test.StringWithInnerMessage;
import test.StringWithInnerMessageCodec;
import test.TwoFieldMessages;



/**
 * Tests the encoding of {@link Codec}s, comparing them to the "standard" protobuf encoding.
 *
 * @author Alexander Maryanovsky
 */
public class EncodingTests{



	/**
	 * Tests whether the encoding of the given object via the given codec, results in the same
	 * binary output as the encoding of the given protobuf message (which is presumably identical in
	 * contents to the object).
	 */
	private static <T> void testEncodingEquals(@NotNull T obj, Codec<T> codec, com.google.protobuf.GeneratedMessageV3 protoMsg) throws IOException{
		ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
		CodedOutputStream out1 = CodedOutputStream.newInstance(buf1);
		codec.write(out1, obj);
		out1.flush();

		ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
		CodedOutputStream out2 = CodedOutputStream.newInstance(buf2);
		protoMsg.writeTo(out2);
		out2.flush();

		Assert.assertArrayEquals(buf1.toByteArray(), buf2.toByteArray());
	}



	/**
	 * Tests the encoding of {@link IntMessage} with the given list of values.
	 */
	private static void testIntMessageEncoding(int... values) throws IOException{
		Codec<IntMessage> codec = IntMessageCodec.INSTANCE;
		for (int value : values){
			testEncodingEquals(
					new IntMessage(value), codec,
					OneFieldMessages.IntMessage.newBuilder().setValue(value).build());
		}
	}



	/**
	 * Tests the encoding of {@link StringMessage} with the given list of values.
	 */
	private static void testStringMessageEncoding(String... values) throws IOException{
		Codec<StringMessage> codec = StringMessageCodec.INSTANCE;
		for (String value : values){
			OneFieldMessages.StringMessage.Builder builder = OneFieldMessages.StringMessage.newBuilder();
			if (value != null)
				builder.setText(value);
			testEncodingEquals(new StringMessage(value), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link ColorMessage} with the given list of values.
	 */
	private static void testColorMessageEncoding(Color... values) throws IOException{
		Codec<ColorMessage> codec = ColorMessageCodec.INSTANCE;
		for (Color value : values){
			OneFieldMessages.ColorMessage.Builder builder = OneFieldMessages.ColorMessage.newBuilder();
			if (value != null)
				builder.setColorValue(value.ordinal() + 1); // +1 because the protobuf enum has another value - the default one
			testEncodingEquals(new ColorMessage(value), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link Int2Message} with the given list of values. Each two consecutive
	 * values are converted into objects that are tested.
	 */
	private static void testInt2MessageEncoding(int... values) throws IOException{
		Codec<Int2Message> codec = Int2MessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			testEncodingEquals(
					new Int2Message(values[i], values[i+1]), codec,
					TwoFieldMessages.Int2Message.newBuilder()
							.setValue1(values[i])
							.setValue2(values[i+1])
							.build());
		}
	}



	/**
	 * Tests the encoding of {@link String2Message} with the given list of values. Each two
	 * consecutive values are converted into objects that are tested.
	 */
	private static void testString2MessageEncoding(String... values) throws IOException{
		Codec<String2Message> codec = String2MessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			TwoFieldMessages.String2Message.Builder builder = TwoFieldMessages.String2Message.newBuilder();
			if (values[i] != null)
				builder.setText1(values[i]);
			if (values[i+1] != null)
				builder.setText2(values[i+1]);
			testEncodingEquals(
					new String2Message(values[i], values[i+1]), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link StringIntMessage} with the given list of values. The values
	 * at even indices are expected to be {@code String}s, and the values at odd indices are
	 * expected to be {@code Integer}s. Each two consecutive values are converted into objects that
	 * are tested.
	 */
	private static void testStringIntMessageEncoding(Object... values) throws IOException{
		Codec<StringIntMessage> codec = StringIntMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			String text = (String)values[i + i%2];
			int number = (Integer)values[i + (i+1)%2];
			TwoFieldMessages.StringIntMessage.Builder builder = TwoFieldMessages.StringIntMessage.newBuilder();
			if (text != null)
				builder.setText(text);
			builder.setValue(number);
			testEncodingEquals(
					new StringIntMessage(text, number), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link StringColorMessage} with the given list of values. The values
	 * at even indices are expected to be {@code String}s, and the values at odd indices are
	 * expected to be {@code Color}s. Each two consecutive values are converted into objects that
	 * are tested.
	 */
	private static void testStringColorMessageEncoding(Object... values) throws IOException{
		Codec<StringColorMessage> codec = StringColorMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			String text = (String)values[i + i%2];
			Color color = (Color)values[i + (i+1)%2];
			TwoFieldMessages.StringColorMessage.Builder builder = TwoFieldMessages.StringColorMessage.newBuilder();
			if (text != null)
				builder.setText(text);
			if (color != null)
				builder.setColorValue(color.ordinal() + 1); // +1 because the protobuf enum has another value - the default one
			testEncodingEquals(
					new StringColorMessage(text, color), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link StringWithInnerMessage} with the given list of values.
	 * Each two consecutive values are converted into objects that are tested.
	 */
	private static void testStringWithInnerMessageEncoding(String... values) throws IOException{
		Codec<StringWithInnerMessage> codec = StringWithInnerMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			OneFieldMessages.StringMessage.Builder innerMessageBuilder = OneFieldMessages.StringMessage.newBuilder();
			if (values[i+1] != null)
				innerMessageBuilder.setText(values[i+1]);
			TwoFieldMessages.StringWithInnerMessage.Builder builder = TwoFieldMessages.StringWithInnerMessage.newBuilder();
			if (values[i] != null)
				builder.setText(values[i]);
			builder.setStringMsg(innerMessageBuilder.build());
			testEncodingEquals(
					new StringWithInnerMessage(values[i], new StringMessage(values[i+1])), codec, builder.build());
		}
	}



	/**
	 * Tests the encoding of {@link AllTypesMessage} with the given values.
	 */
	private static void testAllTypesMessageEncoding(double doubleField, float floatField, int int32Field, long int64Field,
													int uint32Field, long uint64Field, int sint32Field, long sint64Field,
													int fixed32Field, long fixed64Field, int sfixed32Field, long sfixed64Field,
													boolean boolField, String stringField, byte[] bytesField, StringMessage stringMsgField, Color colorField) throws IOException{
		testEncodingEquals(new AllTypesMessage(doubleField, floatField, int32Field, int64Field, uint32Field, uint64Field,
						sint32Field, sint64Field, fixed32Field, fixed64Field, sfixed32Field, sfixed64Field, boolField, stringField,
						bytesField, stringMsgField, colorField), AllTypesMessageCodec.INSTANCE,
				AllTypesMessageOuterClass.AllTypesMessage.newBuilder()
					.setDoubleField(doubleField)
					.setFloatField(floatField)
					.setInt32Field(int32Field)
					.setInt64Field(int64Field)
					.setUint32Field(uint32Field)
					.setUint64Field(uint64Field)
					.setSint32Field(sint32Field)
					.setSint64Field(sint64Field)
					.setFixed32Field(fixed32Field)
					.setFixed64Field(fixed64Field)
					.setSfixed32Field(sfixed32Field)
					.setSfixed64Field(sfixed64Field)
					.setBoolField(boolField)
					.setStringField(stringField)
					.setBytesField(ByteString.copyFrom(bytesField))
					.setStringMsgField(OneFieldMessages.StringMessage.newBuilder().setText(stringMsgField.getText()))
					.setColorFieldValue(colorField.ordinal() + 1)
					.build()
				);
	}



	/**
	 * Runs the encoding tests.
	 */
	@Test
	public void testEncoding() throws IOException{
		// Can't test empty string, because the standard implementation encodes it as null, but
		// we encode null and empty string differently
		testIntMessageEncoding(5, 42, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testStringMessageEncoding("Hello, World!", null, "\0");
		testColorMessageEncoding(null, null);//, Color.BLUE, Color.RED);
		testInt2MessageEncoding(-1, 1, 0, 0, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testString2MessageEncoding(null, null, "0", "0", "a", "b", "Hello", "Goodbye", "World", null);
		testStringIntMessageEncoding("Hello", 0, null, -1, "foobar", Integer.MAX_VALUE);
		testStringColorMessageEncoding("Peace", Color.RED, "Love", null, "Happiness", Color.BLUE);
		testStringWithInnerMessageEncoding("Hello, World!", null, "\0");
		testAllTypesMessageEncoding(1.23, 3.45f, -50, -1234567890240L,
				50, 1234567890240L,50, -1234567890240L,
				50, 1234567890240L, 50, 1234567890240L,
				true, "Hello,", new byte[]{1, -2},
				new StringMessage("World"), Color.RED);
	}



}
