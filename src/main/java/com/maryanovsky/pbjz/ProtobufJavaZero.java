package com.maryanovsky.pbjz;


import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import static com.google.protobuf.compiler.PluginProtos.*;

import java.io.IOException;

import javax.lang.model.element.Modifier;


public class ProtobufJavaZero{

	public static void main(String[] args) throws IOException{
		CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

		CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();

		
		response.addFile(CodeGeneratorResponse.File.newBuilder()
				.setName("HelloWorld.java")
				.setContent(helloWorldClass())
		);

		response.build().writeTo(System.out);
	}
	
	
	private static String helloWorldClass(){
		MethodSpec main = MethodSpec.methodBuilder("main")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(void.class)
				.addParameter(String[].class, "args")
				.addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
				.build();

		TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addMethod(main)
				.build();

		JavaFile javaFile = JavaFile.builder("", helloWorld)
				.build();

		return javaFile.toString();
	}

}
