package com.maryanovsky.pbjz.gen;


import com.google.common.base.CaseFormat;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.maryanovsky.pbjz.runtime.Codec;
import com.maryanovsky.pbjz.runtime.EnumCodec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
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
import static com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import static com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
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
			String javaPackage = fileDescriptor.getPackage();

			// Generate codecs for message types
			for (DescriptorProto descriptor : fileDescriptor.getMessageTypeList()){
				TypeSpec codec = genMessageCodec(javaPackage, null,	descriptor);
				response.addFile(genCodecFile(javaPackage, codec));
			}

			// Generate codecs for enum types
			for (EnumDescriptorProto descriptor : fileDescriptor.getEnumTypeList()){
				TypeSpec codec = genEnumCodec(javaPackage, null, descriptor);
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

		builder.addMethod(genEncodeMethod(userTypeName, descriptor))
				.addMethod(genDecodeMethod(userTypeName, descriptor))
				.addMethod(genSizeComputerMethod(userTypeName, descriptor));


		// Generate nested codecs for message types
		for (DescriptorProto nestedMessageDescriptor : descriptor.getNestedTypeList()){
			builder.addType(genMessageCodec(userTypeJavaPackage, userTypeName, nestedMessageDescriptor));
		}

		// Generate nested codecs for enum types
		for (EnumDescriptorProto nestedEnumDescriptor : descriptor.getEnumTypeList()){
			builder.addType(genEnumCodec(userTypeJavaPackage, userTypeName,	nestedEnumDescriptor));
		}

		return builder.build();
	}



	/**
	 * Generates a method that encodes objects of a user-defined type into messages described by the
	 * given descriptor - an implementation of {@link Codec#encode(CodedOutputStream, Object)}.
	 */
	@NotNull
	private static MethodSpec genEncodeMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
		ParameterSpec outputParam = notNull(CodedOutputStream.class, "output");
		ParameterSpec valueParam = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(outputParam)
				.addParameter(valueParam)
				.addException(IOException.class);


		for (FieldDescriptorProto field : descriptor.getFieldList()){
			Type fieldType = field.getType();
			String getterName = fieldGetterName(field.getName(), fieldType);
			String primitiveWriterMethodName = WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (primitiveWriterMethodName != null){ // A primitive type
				// e.g. writeFloatField(output, 2, value.getSecondField())
				methodBuilder.addStatement("$L($N, $L, $N.$N())",
						primitiveWriterMethodName, outputParam, field.getNumber(), valueParam, getterName);
			}
			else if ((fieldType == Type.TYPE_MESSAGE) || (fieldType == Type.TYPE_ENUM)){ // A user-defined type, with a codec
				// e.g. TypeCodec.INSTANCE.writeField(output, 3, value.getThirdField())
				methodBuilder.addStatement("$L.writeField($N, $L, $N.$N())",
						codecInstanceExpr(field), outputParam, field.getNumber(), valueParam, getterName);
			}
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		return methodBuilder.build();
	}



	/**
	 * Generates a method that decodes messages described by the given descriptor into objects of
	 * the user-defined type - an implementation of {@link Codec#decode(CodedInputStream)}.
	 */
	@NotNull
	private static MethodSpec genDecodeMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
		ParameterSpec inputParam = notNull(CodedInputStream.class, "input");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("decode")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addAnnotation(Nullable.class)
				.returns(userTypeName)
				.addParameter(inputParam)
				.addException(IOException.class);

		String tagVar = "tag";
		methodBuilder.addStatement("int $L = $L.readTag()", tagVar, inputParam.name);
		CodeBlock.Builder switchBuilder = CodeBlock.builder().beginControlFlow("switch($L)", tagVar);
		for (FieldDescriptorProto field : descriptor.getFieldList()){
			int tagValue = WireFormatProxy.makeTag(field.getNumber(), field.getType());
			switchBuilder.add("case $L: return null; // $L\n", tagValue, field.getName());
			// TODO: Actually decode the value
		}
		switchBuilder.add("default: return null;\n");
		switchBuilder.endControlFlow();

		methodBuilder.addCode(switchBuilder.build());

		return methodBuilder.build();
	}



	/**
	 * Generates a method that computes the serialized size of values of the user-defined type - an
	 * implementation of {@link Codec#computeSerializedSize(Object)}.
	 */
	@NotNull
	private static MethodSpec genSizeComputerMethod(@NotNull TypeName userTypeName,	@NotNull DescriptorProto descriptor){
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("computeSerializedSize")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		methodBuilder.addStatement("int size = 0");

		for (FieldDescriptorProto field : descriptor.getFieldList()){
			Type fieldType = field.getType();
			String getterName = fieldGetterName(field.getName(), fieldType);
			String primitiveSizeComputerMethodName = COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (primitiveSizeComputerMethodName != null){ // A primitive type
				// e.g. size += computeFloatFieldSize(2, value.getSecondField())
				methodBuilder.addStatement("size += $L($L, $N.$N())",
						primitiveSizeComputerMethodName, field.getNumber(), value, getterName);
			}
			else if ((fieldType == Type.TYPE_MESSAGE) || (fieldType == Type.TYPE_ENUM)){ // A user-defined type, with a codec
				// e.g. size += TypeCodec.INSTANCE.computeSerializedSize(3, value.getThirdField())
				methodBuilder.addStatement("size += $L.computeSerializedSize($L, $N.$N())",
						codecInstanceExpr(field), field.getNumber(), value, getterName);
			}
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		methodBuilder.addStatement("return size");

		return methodBuilder.build();
	}



	/**
	 * Generates the {@link Codec} class for a single user-defined type, as described by the given
	 * message descriptor.
	 */
	@NotNull
	private static TypeSpec genEnumCodec(@NotNull String userTypeJavaPackage,
										 @Nullable ClassName userTypeOuterClassName,
										 @NotNull EnumDescriptorProto descriptor){
		String protoTypeName = descriptor.getName();

		ClassName userTypeName = (userTypeOuterClassName == null) ?
				ClassName.get(userTypeJavaPackage, protoTypeName) :
				userTypeOuterClassName.nestedClass(protoTypeName);

		ClassName codecClassName = ClassName.get("", codecSimpleName(protoTypeName)); // package is declared in the file

		TypeSpec.Builder builder = TypeSpec.classBuilder(codecClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.superclass(ParameterizedTypeName.get(ClassName.get(EnumCodec.class), userTypeName))
				.addField(genSingletonInstanceField(codecClassName))
				.addMethod(genPrivateConstructor());

		builder.addMethod(genToEncodedEnumValueMethod(userTypeName, descriptor))
				.addMethod(genFromEncodedEnumValueMethod(userTypeName, descriptor))
				.build();

		return builder.build();
	}



	/**
	 * Generates the method that returns the wire representation of a given user-defined enum-type
	 * value - an implementation of {@link EnumCodec#toEncodedValue(Enum)}.
	 */
	@NotNull
	private static MethodSpec genToEncodedEnumValueMethod(@NotNull TypeName userTypeName, @NotNull EnumDescriptorProto descriptor){
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("toEncodedValue")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		methodBuilder.beginControlFlow("switch($N)", value);
		for (EnumValueDescriptorProto enumValue : descriptor.getValueList())
			methodBuilder.addStatement("case $N: return $L", enumValue.getName(), enumValue.getNumber());
		methodBuilder.addStatement("default: throw new IllegalArgumentException($N + \" has no corresponding proto value\")", value);
		methodBuilder.endControlFlow();

		return methodBuilder.build();
	}



	/**
	 * Generates the method that returns the user-defined enum-type value represented by the given
	 * integer value on the wire - an implementation of {@link EnumCodec#fromEncodedValue(int)}.
	 */
	@NotNull
	private static MethodSpec genFromEncodedEnumValueMethod(@NotNull TypeName userTypeName, @NotNull EnumDescriptorProto descriptor){
		ParameterSpec encoded = ParameterSpec.builder(int.class, "encoded").build();

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("fromEncodedValue")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.addAnnotation(Nullable.class)
				.returns(userTypeName)
				.addParameter(encoded);

		methodBuilder.addCode("return null;\n");

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
	 * Returns the expression of the codec instance to use for the given field.
	 */
	@NotNull
	private static String codecInstanceExpr(@NotNull FieldDescriptorProto field){
		String typeName = field.getTypeName();
		String codecTypeName;
		if (typeName.startsWith(".")){ // Fully qualified name (sans the '.')
			// Convert each inner type name to the name of its codec
			ClassName fieldClassName = ClassName.bestGuess(typeName.substring(1));
			codecTypeName = fieldClassName.packageName() + "." +
					fieldClassName.simpleNames()
							.stream()
							.map(ProtobufJavaZero::codecSimpleName)
							.collect(Collectors.joining("."));
		}
		else{
			// We can just return the type name, without the types its nested in, because the Java
			// scoping rules are (I think) the same as in protobuf.
			codecTypeName = codecSimpleName(typeName);
		}

		return codecTypeName + "." + CODEC_SINGLETON_INSTANCE_FIELD_NAME;
	}



	/**
	 * Returns the simple name of the codec class for the given user type.
	 */
	@NotNull
	private static String codecSimpleName(@NotNull String className){
		return className + "Codec";
	}



	/**
	 * Generates a singleton instance field for the given class.
	 */
	@NotNull
	private static FieldSpec genSingletonInstanceField(@NotNull ClassName type){
		return FieldSpec.builder(type, CODEC_SINGLETON_INSTANCE_FIELD_NAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("new $T()", type)
				.build();
	}



	/**
	 * Generates a private constructor.
	 */
	@NotNull
	private static MethodSpec genPrivateConstructor(){
		return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
	}



}
