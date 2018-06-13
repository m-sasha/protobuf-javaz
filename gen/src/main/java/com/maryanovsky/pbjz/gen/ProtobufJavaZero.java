package com.maryanovsky.pbjz.gen;


import com.google.common.base.CaseFormat;
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
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.*;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.lang.model.element.Modifier;



/**
 * The main class of the protobuf-javaz protoc plugin.
 */
public class ProtobufJavaZero{



	private static final Map<Type, String> WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "writeDouble");
		methodNames.put(Type.TYPE_FLOAT, "writeFloat");
		methodNames.put(Type.TYPE_INT32, "writeInt32");
		methodNames.put(Type.TYPE_INT64, "writeInt64");
		methodNames.put(Type.TYPE_UINT32, "writeUInt32");
		methodNames.put(Type.TYPE_UINT64, "writeUInt64");
		methodNames.put(Type.TYPE_SINT32, "writeSInt32");
		methodNames.put(Type.TYPE_SINT64, "writeSInt64");
		methodNames.put(Type.TYPE_FIXED32, "writeFixed32");
		methodNames.put(Type.TYPE_FIXED64, "writeFixed64");
		methodNames.put(Type.TYPE_SFIXED32, "writeSFixed32");
		methodNames.put(Type.TYPE_SFIXED64, "writeSFixed64");
		methodNames.put(Type.TYPE_BOOL, "writeBool");
		methodNames.put(Type.TYPE_STRING, "writeString");
		methodNames.put(Type.TYPE_BYTES, "writeBytes");

		WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}


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


	private static MethodSpec generateEncodeMethod(@NotNull TypeName target, DescriptorProto messageDescriptor){
		ParameterSpec output = notNull(CodedOutputStream.class, "output");
		ParameterSpec value = notNull(target, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(output)
				.addParameter(value)
				.addException(IOException.class);

		for (FieldDescriptorProto fieldDescriptor : messageDescriptor.getFieldList()){
			Type fieldType = fieldDescriptor.getType();
			String getterName = fieldGetterName(fieldDescriptor.getName(), fieldType);
			String primitiveWriterMethodName = WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			// A primitive type
			if (primitiveWriterMethodName != null)
				methodBuilder.addStatement("$L($N, $L, $N.$N())",
						primitiveWriterMethodName, output, fieldDescriptor.getNumber(), value, getterName);
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		return methodBuilder.build();
	}


	private static MethodSpec generateDecodeMethod(@NotNull TypeName target, DescriptorProto messageDescriptor){
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


	private static String fieldGetterName(String name, Type type){
		// If the field is boolean and already starts with "is_", don't duplicate it.
		// For example: "is_red", should become "isRed", not "isIsRed".
		if ((type == Type.TYPE_BOOL) && name.startsWith("is_")){
			name = name.substring(3);
		}

		return ((type == Type.TYPE_BOOL) ? "is" : "get") +
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
	}



}
