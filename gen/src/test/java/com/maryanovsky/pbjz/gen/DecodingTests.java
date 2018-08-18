package com.maryanovsky.pbjz.gen;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import test.AllTypesMessage;
import test.AllTypesMessageCodec;
import test.Color;
import test.ColorMessage;
import test.ColorMessageCodec;
import test.Int2Message;
import test.Int2MessageCodec;
import test.IntMessage;
import test.IntMessageCodec;
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



/**
 * Tests the decoding of {@link Codec}s, by encoding and decoding values and then comparing them to
 * the originals.
 *
 * @author Alexander Maryanovsky
 */
public class DecodingTests{



	/**
	 * Tests whether encoding and decoding the given object results in an equal object.
	 */
	private static <T> void testEncDecEquals(@NotNull T obj, Codec<T> codec) throws IOException{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		CodedOutputStream out = CodedOutputStream.newInstance(buf);
		codec.write(out, obj);
		out.flush();

		CodedInputStream in = CodedInputStream.newInstance(buf.toByteArray());
		T decodedObj = codec.read(in);

		Assert.assertEquals(obj, decodedObj);
	}



	/**
	 * Tests the decoding of {@link IntMessage} with the given list of values.
	 */
	private static void testIntMessageDecoding(int... values) throws IOException{
		Codec<IntMessage> codec = IntMessageCodec.INSTANCE;
		for (int value : values)
			testEncDecEquals(new IntMessage(value), codec);
	}



	/**
	 * Tests the decoding of {@link StringMessage} with the given list of values.
	 */
	private static void testStringMessageDecoding(String... values) throws IOException{
		Codec<StringMessage> codec = StringMessageCodec.INSTANCE;
		for (String value : values)
			testEncDecEquals(new StringMessage(value), codec);
	}



	/**
	 * Tests the decoding of {@link ColorMessage} with the given list of values.
	 */
	private static void testColorMessageDecoding(Color... values) throws IOException{
		Codec<ColorMessage> codec = ColorMessageCodec.INSTANCE;
		for (Color value : values)
			testEncDecEquals(new ColorMessage(value), codec);
	}



	/**
	 * Tests the decoding of {@link Int2Message} with the given list of values. Each two consecutive
	 * values are converted into objects that are tested.
	 */
	private static void testInt2MessageDecoding(int... values) throws IOException{
		Codec<Int2Message> codec = Int2MessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i)
			testEncDecEquals(new Int2Message(values[i], values[i+1]), codec);
	}



	/**
	 * Tests the decoding of {@link String2Message} with the given list of values. Each two
	 * consecutive values are converted into objects that are tested.
	 */
	private static void testString2MessageDecoding(String... values) throws IOException{
		Codec<String2Message> codec = String2MessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i)
			testEncDecEquals(new String2Message(values[i], values[i+1]), codec);
	}



	/**
	 * Tests the decoding of {@link StringIntMessage} with the given list of values. The values
	 * at even indices are expected to be {@code String}s, and the values at odd indices are
	 * expected to be {@code Integer}s. Each two consecutive values are converted into objects that
	 * are tested.
	 */
	private static void testStringIntMessageDecoding(Object... values) throws IOException{
		Codec<StringIntMessage> codec = StringIntMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			String text = (String)values[i + i%2];
			int number = (Integer)values[i + (i+1)%2];
			testEncDecEquals(new StringIntMessage(text, number), codec);
		}
	}



	/**
	 * Tests the decoding of {@link StringColorMessage} with the given list of values. The values
	 * at even indices are expected to be {@code String}s, and the values at odd indices are
	 * expected to be {@code Color}s. Each two consecutive values are converted into objects that
	 * are tested.
	 */
	private static void testStringColorMessageDecoding(Object... values) throws IOException{
		Codec<StringColorMessage> codec = StringColorMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i){
			String text = (String)values[i + i%2];
			Color color = (Color)values[i + (i+1)%2];
			testEncDecEquals(new StringColorMessage(text, color), codec);
		}
	}



	/**
	 * Tests the decoding of {@link StringWithInnerMessage} with the given list of values.
	 * Each two consecutive values are converted into objects that are tested.
	 */
	private static void testStringWithInnerMessageDecoding(String... values) throws IOException{
		Codec<StringWithInnerMessage> codec = StringWithInnerMessageCodec.INSTANCE;
		for (int i = 0; i < values.length - 1; ++i)
			testEncDecEquals(new StringWithInnerMessage(values[i], new StringMessage(values[i+1])), codec);
	}



	/**
	 * Tests the decoding of {@link AllTypesMessage} with the given values.
	 */
	private static void testAllTypesMessageDecoding(double doubleField, float floatField, int int32Field, long int64Field,
													int uint32Field, long uint64Field, int sint32Field, long sint64Field,
													int fixed32Field, long fixed64Field, int sfixed32Field, long sfixed64Field,
													boolean boolField, String stringField, byte[] bytesField, StringMessage stringMsgField, Color colorField) throws IOException{
		testEncDecEquals(new AllTypesMessage(doubleField, floatField, int32Field, int64Field, uint32Field, uint64Field,
						sint32Field, sint64Field, fixed32Field, fixed64Field, sfixed32Field, sfixed64Field, boolField, stringField,
						bytesField, stringMsgField, colorField), AllTypesMessageCodec.INSTANCE);
	}



	/**
	 * Runs the decoding tests.
	 */
	@Test
	public void testDecoding() throws IOException{
		testIntMessageDecoding(5, 42, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testStringMessageDecoding("Hello, World!", null, "", "\0");
		testColorMessageDecoding(null, Color.BLUE, Color.RED);
		testInt2MessageDecoding(-1, 1, 0, 0, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testString2MessageDecoding("", "", null, "0", "0", "a", "b", "Hello", "Goodbye", "World", null);
		testStringIntMessageDecoding("Hello", 0, null, -1, "foobar", Integer.MAX_VALUE);
		testStringColorMessageDecoding("Peace", Color.RED, "Love", null, "Happiness", Color.BLUE);
		testStringWithInnerMessageDecoding("Hello, World!", "", null, "\0");
		testAllTypesMessageDecoding(1.23, 3.45f, -50, -1234567890240L,
				50, 1234567890240L,50, -1234567890240L,
				50, 1234567890240L, 50, 1234567890240L,
				true, "Hello,", new byte[]{1, -2},
				new StringMessage("World"), Color.RED);
	}



}
