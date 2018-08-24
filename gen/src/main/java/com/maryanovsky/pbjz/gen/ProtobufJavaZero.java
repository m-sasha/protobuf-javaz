package com.maryanovsky.pbjz.gen;


import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import static com.google.protobuf.DescriptorProtos.DescriptorProto;
import static com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import static com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import static com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import static com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;

import static com.maryanovsky.pbjz.gen.Utils.*;



/**
 * The main class of the protobuf-javaz protoc plugin.
 *
 * @author Alexander Maryanovsky
 */
public class ProtobufJavaZero{



	/**
	 * The main method, invoked by protoc.
	 */
	public static void main(String[] args) throws IOException{
		CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

		CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();

		for (FileDescriptorProto fileDescriptor : request.getProtoFileList()){
			String javaPackage = fileDescriptor.getPackage();

			// Generate codecs for message types
			for (DescriptorProto descriptor : fileDescriptor.getMessageTypeList()){
				TypeSpec codec = genMessageCodec(javaPackage, null,	descriptor);
				response.addFile(genCodecFile(javaPackage, codec));
			}

			// Generate codecs for enum types
			for (EnumDescriptorProto descriptor : fileDescriptor.getEnumTypeList()){
				TypeSpec codec = EnumCodecGenerator.genEnumCodec(javaPackage, null, descriptor);
				response.addFile(genCodecFile(javaPackage, codec));
			}
		}

		response.build().writeTo(System.out);
	}



	/**
	 * Generates the file for the given top-level codec type.
	 */
	private static CodeGeneratorResponse.File genCodecFile(@NotNull String javaPackage, @NotNull TypeSpec codec){
		String dirName = javaPackage.replace('.', '/');
		JavaFile javaFile = JavaFile.builder(javaPackage, codec).build();

		return CodeGeneratorResponse.File.newBuilder()
				.setName(dirName + "/" + codec.name + ".java")
				.setContent(javaFile.toString())
				.build();
	}



	/**
	 * Generates the {@link Codec} class for a single user-defined type, as described by the given
	 * message descriptor.
	 */
	@NotNull
	private static TypeSpec genMessageCodec(@NotNull String userTypeJavaPackage,
											@Nullable ClassName userTypeOuterClassName,
											@NotNull DescriptorProto descriptor){
		String protoTypeName = descriptor.getName();

		ClassName userTypeName = (userTypeOuterClassName == null) ?
				ClassName.get(userTypeJavaPackage, protoTypeName) :
				userTypeOuterClassName.nestedClass(protoTypeName);

		ClassName codecClassName = ClassName.get("", codecSimpleName(protoTypeName)); // package is declared in the file

		TypeSpec.Builder builder = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(ParameterizedTypeName.get(ClassName.get(Codec.class), userTypeName))
				.addField(genSingletonInstanceField(codecClassName))
				.addMethod(genPrivateConstructor());

		if (userTypeOuterClassName != null) // Nested types must be static
			builder.addModifiers(Modifier.STATIC);

		builder.addMethod(WriteGenerator.genWriteMethod(userTypeName, descriptor))
				.addMethod(ReadGenerator.genReadMethod(userTypeName, descriptor))
				.addMethod(SizeComputeGenerator.genSizeComputerMethod(userTypeName, descriptor));


		// Generate nested codecs for message types
		for (DescriptorProto nestedMessageDescriptor : descriptor.getNestedTypeList()){
			builder.addType(genMessageCodec(userTypeJavaPackage, userTypeName, nestedMessageDescriptor));
		}

		// Generate nested codecs for enum types
		for (EnumDescriptorProto nestedEnumDescriptor : descriptor.getEnumTypeList()){
			builder.addType(EnumCodecGenerator.genEnumCodec(userTypeJavaPackage, userTypeName,	nestedEnumDescriptor));
		}

		return builder.build();
	}



}
