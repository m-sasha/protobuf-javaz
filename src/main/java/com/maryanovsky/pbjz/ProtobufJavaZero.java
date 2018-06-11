package com.maryanovsky.pbjz;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static com.google.protobuf.compiler.PluginProtos.*;
import static com.google.protobuf.DescriptorProtos.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

		MethodSpec encode = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PUBLIC)
				.returns(void.class)
				.addParameter(targetType, "obj")
				.addParameter(OutputStream.class, "out")
				.addException(ClassName.get("java.io", "IOException"))
				.build();

		MethodSpec decode = MethodSpec.methodBuilder("decode")
				.addModifiers(Modifier.PUBLIC)
				.returns(targetType)
				.addParameter(InputStream.class, "in")
				.addStatement("return null")
				.build();

		TypeSpec helloWorld = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
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
