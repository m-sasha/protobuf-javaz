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



}
