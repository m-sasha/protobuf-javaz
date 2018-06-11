package com.maryanovsky.pbjz;


import static com.google.protobuf.compiler.PluginProtos.*;

import java.io.IOException;

public class ProtobufJavaZero{

	public static void main(String[] args) throws IOException{
		CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

		CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();
		response.addFile(CodeGeneratorResponse.File.newBuilder()
				.setName("HelloWorld.java")
				.setContent("public class HelloWorld{\n\tpublic static void main(String[] args){\n\t\tSystem.out.println(\"Hello, World!\");\n\t}\n}")
		);

		response.build().writeTo(System.out);
	}

}
