package com.maryanovsky.pbjz.gen;


import com.google.common.base.CaseFormat;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import static com.google.protobuf.DescriptorProtos.DescriptorProto;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import static com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import static com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import static com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;



/**
 * The main class of the protobuf-javaz protoc plugin.
 *
 * @author Alexander Maryanovsky
 */
public class ProtobufJavaZero{



	/**
	 * The name of the singleton instance field for each {@link Codec} we generate.
	 */
	private static final String CODEC_SINGLETON_INSTANCE_FIELD_NAME = "INSTANCE";



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that write them.
	 */
	@NotNull
	private static final Map<Type, String> WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "writeDoubleField");
		methodNames.put(Type.TYPE_FLOAT, "writeFloatField");
		methodNames.put(Type.TYPE_INT32, "writeInt32Field");
		methodNames.put(Type.TYPE_INT64, "writeInt64Field");
		methodNames.put(Type.TYPE_UINT32, "writeUInt32Field");
		methodNames.put(Type.TYPE_UINT64, "writeUInt64Field");
		methodNames.put(Type.TYPE_SINT32, "writeSInt32Field");
		methodNames.put(Type.TYPE_SINT64, "writeSInt64Field");
		methodNames.put(Type.TYPE_FIXED32, "writeFixed32Field");
		methodNames.put(Type.TYPE_FIXED64, "writeFixed64Field");
		methodNames.put(Type.TYPE_SFIXED32, "writeSFixed32Field");
		methodNames.put(Type.TYPE_SFIXED64, "writeSFixed64Field");
		methodNames.put(Type.TYPE_BOOL, "writeBoolField");
		methodNames.put(Type.TYPE_STRING, "writeStringField");
		methodNames.put(Type.TYPE_BYTES, "writeBytesField");

		WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that
	 * compute their serialized sizes.
	 */
	@NotNull
	private static final Map<Type, String> COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "computeDoubleFieldSize");
		methodNames.put(Type.TYPE_FLOAT, "computeFloatFieldSize");
		methodNames.put(Type.TYPE_INT32, "computeInt32FieldSize");
		methodNames.put(Type.TYPE_INT64, "computeInt64FieldSize");
		methodNames.put(Type.TYPE_UINT32, "computeUInt32FieldSize");
		methodNames.put(Type.TYPE_UINT64, "computeUInt64FieldSize");
		methodNames.put(Type.TYPE_SINT32, "computeSInt32FieldSize");
		methodNames.put(Type.TYPE_SINT64, "computeSInt64FieldSize");
		methodNames.put(Type.TYPE_FIXED32, "computeFixed32FieldSize");
		methodNames.put(Type.TYPE_FIXED64, "computeFixed64FieldSize");
		methodNames.put(Type.TYPE_SFIXED32, "computeSFixed32FieldSize");
		methodNames.put(Type.TYPE_SFIXED64, "computeSFixed64FieldSize");
		methodNames.put(Type.TYPE_BOOL, "computeBoolFieldSize");
		methodNames.put(Type.TYPE_STRING, "computeStringFieldSize");
		methodNames.put(Type.TYPE_BYTES, "computeBytesFieldSize");

		COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}


	/**
	 * The main method, invoked by protoc.
	 */
	public static void main(String[] args) throws IOException{
		CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

		CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();

		for (FileDescriptorProto fileDescriptor : request.getProtoFileList()){
			String javaPackageName = fileDescriptor.getPackage();

			for (DescriptorProto descriptor : fileDescriptor.getMessageTypeList()){
				TypeSpec codec = generateCodec(javaPackageName, null, descriptor);

				JavaFile javaFile = JavaFile.builder(javaPackageName, codec)
						.build();

				String dirName = javaPackageName.replace('.', '/');
				response.addFile(CodeGeneratorResponse.File.newBuilder()
						.setName(dirName + "/" + codec.name + ".java")
						.setContent(javaFile.toString())
						.build());
			}
		}

		response.build().writeTo(System.out);
	}



	/**
	 * Generates the {@link Codec} for a single message type, as described by the given descriptor.
	 */
	@NotNull
	private static TypeSpec generateCodec(@NotNull String targetTypeJavaPackageName,
										  @Nullable ClassName targetTypeOuterClassName,
										  @NotNull DescriptorProto message){
		String targetClassName = message.getName();

		ClassName targetTypeName = (targetTypeOuterClassName == null) ?
				ClassName.get(targetTypeJavaPackageName, targetClassName) :
				targetTypeOuterClassName.nestedClass(targetClassName);

		ClassName codecClassName = ClassName.get("", codecSimpleName(targetClassName));

		TypeSpec.Builder builder = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(ParameterizedTypeName.get(ClassName.get(Codec.class), targetTypeName))
				.addField(generateSingletonInstanceField(codecClassName))
				.addMethod(generatePrivateConstructor())
				.addMethod(generateEncodeMethod(targetTypeName, message))
				.addMethod(generateDecodeMethod(targetTypeName, message))
				.addMethod(generateSerializedSizeComputerMethod(targetTypeName, message));

		if (targetTypeOuterClassName != null) // Nested types must be static
			builder.addModifiers(Modifier.STATIC);

		for (DescriptorProto descriptor : message.getNestedTypeList())
			builder.addType(generateCodec(targetTypeJavaPackageName, targetTypeName, descriptor));

		return builder.build();
	}



	/**
	 * Generates a method that encodes objects of a user-defined type into messages described by the
	 * given descriptor.
	 */
	@NotNull
	private static MethodSpec generateEncodeMethod(@NotNull TypeName target, @NotNull DescriptorProto message){
		ParameterSpec output = notNull(CodedOutputStream.class, "output");
		ParameterSpec value = notNull(target, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(output)
				.addParameter(value)
				.addException(IOException.class);

		for (FieldDescriptorProto field : message.getFieldList()){
			Type fieldType = field.getType();
			String getterName = fieldGetterName(field.getName(), fieldType);
			String primitiveWriterMethodName = WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (primitiveWriterMethodName != null){ // A primitive type
				methodBuilder.addStatement("$L($N, $L, $N.$N())",
						primitiveWriterMethodName, output, field.getNumber(), value, getterName);
			}
			else if (fieldType == Type.TYPE_MESSAGE){ // A non-enum user-defined type
				String codecInstance = getMessageFieldCodecName(field) + "." + CODEC_SINGLETON_INSTANCE_FIELD_NAME;
				methodBuilder.addStatement("$L.writeMessageField($N, $L, $N.$N())",
						codecInstance, output, field.getNumber(), value, getterName);
			}
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		return methodBuilder.build();
	}



	/**
	 * Generates a method that decodes messages described by the given descriptor into objects of
	 * the user-defined type.
	 */
	@NotNull
	private static MethodSpec generateDecodeMethod(@NotNull TypeName target, @NotNull DescriptorProto message){
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



	/**
	 * Generates a method that computes the serialized size of values of the user-defined type.
	 */
	@NotNull
	private static MethodSpec generateSerializedSizeComputerMethod(@NotNull TypeName target, @NotNull DescriptorProto message){
		ParameterSpec value = notNull(target, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("computeSerializedSize")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		methodBuilder.addStatement("int size = 0");

		for (FieldDescriptorProto field : message.getFieldList()){
			Type fieldType = field.getType();
			String getterName = fieldGetterName(field.getName(), fieldType);
			String primitiveSizeComputerMethodName = COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (primitiveSizeComputerMethodName != null){ // A primitive type
				methodBuilder.addStatement("size += $L($L, $N.$N())",
						primitiveSizeComputerMethodName, field.getNumber(), value, getterName);
			}
			else if (fieldType == Type.TYPE_MESSAGE){ // A non-enum user-defined type
				String codecInstance = getMessageFieldCodecName(field) + "." + CODEC_SINGLETON_INSTANCE_FIELD_NAME;
				methodBuilder.addStatement("size += $L.computeMessageFieldSize($L, $N.$N())",
						codecInstance, field.getNumber(), value, getterName);
			}
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		methodBuilder.addStatement("return size");

		return methodBuilder.build();
	}



	/**
	 * Creates a parameter spec with a {@code NotNull} annotation from the given parameter type and
	 * name.
	 */
	@NotNull
	private static ParameterSpec notNull(@NotNull TypeName typeName, @NotNull String paramName){
		return ParameterSpec.builder(typeName, paramName).addAnnotation(NotNull.class).build();
	}


	/**
	 * Creates a parameter spec with a {@code NotNull} annotation from the given parameter type and
	 * name.
	 */
	@NotNull
	private static ParameterSpec notNull(@NotNull Class<?> clazz, @NotNull String paramName){
		return notNull(ClassName.get(clazz), paramName);
	}


	/**
	 * Returns the name of the getter method we expect to be present in the user-defined type for a
	 * message field of the given name, as defined in the proto file.
	 */
	@NotNull
	private static String fieldGetterName(@NotNull String name, @NotNull Type type){
		// If the field is boolean and already starts with "is_", don't duplicate it.
		// For example: "is_red", should become "isRed", not "isIsRed".
		if ((type == Type.TYPE_BOOL) && name.startsWith("is_"))
			name = name.substring(3);

		return ((type == Type.TYPE_BOOL) ? "is" : "get") +
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
	}



	/**
	 * Returns the name of the {@link Codec} class to use for the given message-type field.
	 */
	@NotNull
	private static String getMessageFieldCodecName(@NotNull FieldDescriptorProto field){
		String typeName = field.getTypeName();
		if (typeName.startsWith(".")){
			ClassName fieldClassName = ClassName.bestGuess(typeName.substring(1));
			return fieldClassName.packageName() + "." +
					fieldClassName.simpleNames()
							.stream()
							.map(ProtobufJavaZero::codecSimpleName)
							.collect(Collectors.joining("."));
		}

		// We can just return the type name, without the types its nested in, because the Java
		// scoping rules are (should be?) the same as in protobuf.
		return codecSimpleName(typeName);
	}



	/**
	 * Returns the simple name of the codec class for the given user type.
	 */
	@NotNull
	private static String codecSimpleName(@NotNull String className){
		return className + "Codec";
	}



	/**
	 * Generates a singleton codec instance field.
	 */
	@NotNull
	private static FieldSpec generateSingletonInstanceField(@NotNull ClassName type){
		return FieldSpec.builder(type, CODEC_SINGLETON_INSTANCE_FIELD_NAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("new $T()", type)
				.build();
	}



	/**
	 * Generates a private constructor for the codec.
	 */
	@NotNull
	private static MethodSpec generatePrivateConstructor(){
		return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
	}



}
