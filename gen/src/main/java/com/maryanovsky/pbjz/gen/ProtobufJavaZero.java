package com.maryanovsky.pbjz.gen;


import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

			for (DescriptorProto descriptor : fileDescriptor.getMessageTypeList())
				response.addFile(generateCodec(javaPackageName, descriptor));
		}

		response.build().writeTo(System.out);
	}


	private static CodeGeneratorResponse.File generateCodec(String javaPackageName, DescriptorProto descriptor){
		String targetClassName = descriptor.getName();
		String dirName = javaPackageName.replace('.', '/');
		String codecClassName = targetClassName + "Codec";

		TypeName targetTypeName = ClassName.get(javaPackageName, targetClassName);

		TypeSpec codec = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(ParameterizedTypeName.get(ClassName.get(Codec.class), targetTypeName))
				.addMethod(generateEncodeMethod(targetTypeName, descriptor))
				.addMethod(generateDecodeMethod(targetTypeName, descriptor))
				.build();

		JavaFile javaFile = JavaFile.builder(javaPackageName, codec)
				.build();

		return CodeGeneratorResponse.File.newBuilder()
				.setName(dirName + "/" + codecClassName + ".java")
				.setContent(javaFile.toString())
				.build();
	}


	private static MethodSpec generateEncodeMethod(@NotNull TypeName target, DescriptorProto descriptor){
		return MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(notNull(CodedOutputStream.class, "output"))
				.addParameter(notNull(target, "value"))
				.addException(IOException.class)
				.build();
	}


	private static MethodSpec generateDecodeMethod(@NotNull TypeName target, DescriptorProto descriptor){
		return MethodSpec.methodBuilder("decode")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addAnnotation(Nullable.class)
				.returns(target)
				.addParameter(notNull(CodedInputStream.class, "input"))
				.addException(IOException.class)
				.addStatement("return null")
				.build();
	}


	private static ParameterSpec notNull(TypeName typeName, String paramName){
		return ParameterSpec.builder(typeName, paramName).addAnnotation(NotNull.class).build();
	}


	private static ParameterSpec notNull(Class<?> clazz, String paramName){
		return notNull(ClassName.get(clazz), paramName);
	}


}
