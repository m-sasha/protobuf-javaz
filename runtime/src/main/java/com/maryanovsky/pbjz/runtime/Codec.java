package com.maryanovsky.pbjz.runtime;


import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;

public abstract class Codec<T>{

	public abstract void encode(T value, CodedOutputStream out) throws IOException;

	public abstract T decode(CodedInputStream in) throws IOException;

}
