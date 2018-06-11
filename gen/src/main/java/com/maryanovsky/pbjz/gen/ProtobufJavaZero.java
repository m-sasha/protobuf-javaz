package com.maryanovsky.pbjz.gen;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static com.google.protobuf.compiler.PluginProtos.*;
import static com.google.protobuf.DescriptorProtos.*;

import java.io.IOException;

import javax.lang.model.element.Modifier;


public class ProtobufJavaZero{


	public static void main(String[] args) throws IOException{
		CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

		CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();

		for (FileDescriptorProto fileDescriptor : request.getProtoFileList()){
			String javaPackageName = fileDescriptor.getPackage();

			for (DescriptorProto descriptor : fileDescriptor.getMessageTypeList()){
				response.addFile(generateCodec(javaPackageName, descriptor.getName()));
			}
		}

		response.build().writeTo(System.out);
	}


	private static CodeGeneratorResponse.File generateCodec(String javaPackageName, String targetClassName){
		String dirName = javaPackageName.replace('.', '/');
		String codecClassName = targetClassName + "Codec";

		TypeName targetType = ClassName.get(javaPackageName, targetClassName);
		TypeName ioException = ClassName.get("java.io", "IOException");
		TypeName codedOutputStream = ClassName.get("com.google.protobuf", "CodedOutputStream");
		TypeName codedInputStream = ClassName.get("com.google.protobuf", "CodedInputStream");

		MethodSpec encode = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PUBLIC)
				.returns(void.class)
				.addParameter(targetType, "value")
				.addParameter(codedOutputStream, "out")
				.addException(ioException)
				.build();

		MethodSpec decode = MethodSpec.methodBuilder("decode")
				.addModifiers(Modifier.PUBLIC)
				.returns(targetType)
				.addParameter(codedInputStream, "in")
				.addException(ioException)
				.addStatement("return null")
				.build();

		TypeSpec helloWorld = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(ClassName.get("pbjz.runtime", "Codec"))
				.addMethod(encode)
				.addMethod(decode)
				.build();

		JavaFile javaFile = JavaFile.builder("", helloWorld)
				.build();

		return CodeGeneratorResponse.File.newBuilder()
				.setName(dirName + "/" + codecClassName + ".java")
				.setContent(javaFile.toString())
				.build();
	}


}
