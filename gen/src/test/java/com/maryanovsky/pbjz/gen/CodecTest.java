package com.maryanovsky.pbjz.gen;


import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import test.Int2Message;
import test.Int2MessageCodec;
import test.IntMessage;
import test.IntMessageCodec;
import test.OneFieldMessages;
import test.String2Message;
import test.String2MessageCodec;
import test.StringIntMessage;
import test.StringIntMessageCodec;
import test.StringMessage;
import test.StringMessageCodec;
import test.TwoFieldMessages;



/**
 * Runs the test {@link Codec}s, comparing them to the "standard" protobuf encoding/decoding.
 */
public class CodecTest{



	/**
	 * Tests whether the encoding of the given object via the given codec, results in the same
	 * binary output as the encoding of the given protobuf message (which is presumably identical in
	 * contents to the object).
	 */
	private static <T> void testEncodingEquals(@NotNull T obj, Codec<T> codec, com.google.protobuf.GeneratedMessageV3 protoMsg) throws IOException{
		ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
		CodedOutputStream out1 = CodedOutputStream.newInstance(buf1);
		codec.writeMessage(out1, obj);
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
			testEncodingEquals(
					new StringMessage(value), codec,
					OneFieldMessages.StringMessage.newBuilder().setText(value).build());
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
			testEncodingEquals(
					new String2Message(values[i], values[i+1]), codec,
					TwoFieldMessages.String2Message.newBuilder()
							.setText1(values[i])
							.setText2(values[i+1])
							.build());
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
			testEncodingEquals(
					new StringIntMessage(text, number), codec,
					TwoFieldMessages.StringIntMessage.newBuilder()
							.setText(text)
							.setValue(number)
							.build());
		}
	}



	/**
	 * Runs the encoding tests.
	 */
	@Test
	public void testEncoding() throws IOException{
		testIntMessageEncoding(5, 42, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testStringMessageEncoding("Hello, World!", "", "\0");
		testInt2MessageEncoding(-1, 1, 0, 0, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testString2MessageEncoding("", "", "0", "0", "a", "b", "Hello", "Goodbye", "World", "");
		testStringIntMessageEncoding("Hello", 0, "", -1, "foobar", Integer.MAX_VALUE);
	}



}
