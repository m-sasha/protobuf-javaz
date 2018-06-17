package com.maryanovsky.pbjz.gen;


import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import test.IntMessage;
import test.IntMessageCodec;
import test.OneFieldMessages;
import test.StringMessage;
import test.StringMessageCodec;



public class CodecTest{


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


	private static void testIntMessageEncoding(int... values) throws IOException{
		for (int value : values){
			testEncodingEquals(
					new IntMessage(value), new IntMessageCodec(),
					OneFieldMessages.IntMessage.newBuilder().setValue(value).build());
		}
	}


	private static void testStringMessageEncoding(String... values) throws IOException{
		for (String value : values){
			testEncodingEquals(
					new StringMessage(value), new StringMessageCodec(),
					OneFieldMessages.StringMessage.newBuilder().setText(value).build());
		}
	}


	@Test
	public void testEncoding() throws IOException{
		testIntMessageEncoding(5, 42, 0xffffffff, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		testStringMessageEncoding("Hello, World!");
	}


}
