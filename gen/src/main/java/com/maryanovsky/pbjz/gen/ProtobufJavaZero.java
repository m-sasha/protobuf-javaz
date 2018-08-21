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
import java.util.ArrayList;
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
	 * Maps protobuf primitive types to the names of the methods in {@link CodedInputStream} that
	 * read them.
	 */
	@NotNull
	private static final Map<Type, String> READ_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "readDouble");
		methodNames.put(Type.TYPE_FLOAT, "readFloat");
		methodNames.put(Type.TYPE_INT32, "readInt32");
		methodNames.put(Type.TYPE_INT64, "readInt64");
		methodNames.put(Type.TYPE_UINT32, "readUInt32");
		methodNames.put(Type.TYPE_UINT64, "readUInt64");
		methodNames.put(Type.TYPE_SINT32, "readSInt32");
		methodNames.put(Type.TYPE_SINT64, "readSInt64");
		methodNames.put(Type.TYPE_FIXED32, "readFixed32");
		methodNames.put(Type.TYPE_FIXED64, "readFixed64");
		methodNames.put(Type.TYPE_SFIXED32, "readSFixed32");
		methodNames.put(Type.TYPE_SFIXED64, "readSFixed64");
		methodNames.put(Type.TYPE_BOOL, "readBool");
		methodNames.put(Type.TYPE_STRING, "readStringRequireUtf8");
		methodNames.put(Type.TYPE_BYTES, "readByteArray");

		READ_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the corresponding Java types.
	 */
	@NotNull
	private static final Map<Type, String> JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "double");
		methodNames.put(Type.TYPE_FLOAT, "float");
		methodNames.put(Type.TYPE_INT32, "int");
		methodNames.put(Type.TYPE_INT64, "long");
		methodNames.put(Type.TYPE_UINT32, "int");
		methodNames.put(Type.TYPE_UINT64, "long");
		methodNames.put(Type.TYPE_SINT32, "int");
		methodNames.put(Type.TYPE_SINT64, "long");
		methodNames.put(Type.TYPE_FIXED32, "int");
		methodNames.put(Type.TYPE_FIXED64, "long");
		methodNames.put(Type.TYPE_SFIXED32, "int");
		methodNames.put(Type.TYPE_SFIXED64, "long");
		methodNames.put(Type.TYPE_BOOL, "boolean");
		methodNames.put(Type.TYPE_STRING, "String");
		methodNames.put(Type.TYPE_BYTES, "byte[]");

		JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that
	 * compute their serialized sizes.
	 */
	@NotNull
	private static final Map<Type, String> COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "doubleFieldSize");
		methodNames.put(Type.TYPE_FLOAT, "floatFieldSize");
		methodNames.put(Type.TYPE_INT32, "int32FieldSize");
		methodNames.put(Type.TYPE_INT64, "int64FieldSize");
		methodNames.put(Type.TYPE_UINT32, "uInt32FieldSize");
		methodNames.put(Type.TYPE_UINT64, "uInt64FieldSize");
		methodNames.put(Type.TYPE_SINT32, "sInt32FieldSize");
		methodNames.put(Type.TYPE_SINT64, "sInt64FieldSize");
		methodNames.put(Type.TYPE_FIXED32, "fixed32FieldSize");
		methodNames.put(Type.TYPE_FIXED64, "fixed64FieldSize");
		methodNames.put(Type.TYPE_SFIXED32, "sFixed32FieldSize");
		methodNames.put(Type.TYPE_SFIXED64, "sFixed64FieldSize");
		methodNames.put(Type.TYPE_BOOL, "boolFieldSize");
		methodNames.put(Type.TYPE_STRING, "stringFieldSize");
		methodNames.put(Type.TYPE_BYTES, "bytesFieldSize");

		COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that return the
	 * sizes of packed repeated fields of this type.
	 */
	@NotNull
	private static final Map<Type, String> PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "packedRepeatedDoubleFieldSize");
		methodNames.put(Type.TYPE_FLOAT, "packedRepeatedFloatFieldSize");
		methodNames.put(Type.TYPE_INT32, "packedRepeatedInt32FieldSize");
		methodNames.put(Type.TYPE_INT64, "packedRepeatedInt64FieldSize");
		methodNames.put(Type.TYPE_UINT32, "packedRepeatedUInt32FieldSize");
		methodNames.put(Type.TYPE_UINT64, "packedRepeatedUInt64FieldSize");
		methodNames.put(Type.TYPE_SINT32, "packedRepeatedSInt32FieldSize");
		methodNames.put(Type.TYPE_SINT64, "packedRepeatedSInt64FieldSize");
		methodNames.put(Type.TYPE_FIXED32, "packedRepeatedFixed32FieldSize");
		methodNames.put(Type.TYPE_FIXED64, "packedRepeatedFixed64FieldSize");
		methodNames.put(Type.TYPE_SFIXED32, "packedRepeatedSFixed32FieldSize");
		methodNames.put(Type.TYPE_SFIXED64, "packedRepeatedSFixed64FieldSize");
		methodNames.put(Type.TYPE_BOOL, "packedRepeatedBoolFieldSize");

		PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link CodedOutputStream} that
	 * write fields of this type without a tag.
	 */
	@NotNull
	private static final Map<Type, String> WRITE_NO_TAG_METHOD_NAMES_BY_TYPE;
	static{
		Map<Type, String> methodNames = new EnumMap<>(Type.class);
		methodNames.put(Type.TYPE_DOUBLE, "writeDoubleNoTag");
		methodNames.put(Type.TYPE_FLOAT, "writeFloatNoTag");
		methodNames.put(Type.TYPE_INT32, "writeInt32NoTag");
		methodNames.put(Type.TYPE_INT64, "writeInt64NoTag");
		methodNames.put(Type.TYPE_UINT32, "writeUInt32NoTag");
		methodNames.put(Type.TYPE_UINT64, "writeUInt64NoTag");
		methodNames.put(Type.TYPE_SINT32, "writeSInt32NoTag");
		methodNames.put(Type.TYPE_SINT64, "writeSInt64NoTag");
		methodNames.put(Type.TYPE_FIXED32, "writeFixed32NoTag");
		methodNames.put(Type.TYPE_FIXED64, "writeFixed64NoTag");
		methodNames.put(Type.TYPE_SFIXED32, "writeSFixed32NoTag");
		methodNames.put(Type.TYPE_SFIXED64, "writeSFixed64NoTag");
		methodNames.put(Type.TYPE_BOOL, "writeBoolNoTag");

		WRITE_NO_TAG_METHOD_NAMES_BY_TYPE = Collections.unmodifiableMap(methodNames);
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

		builder.addMethod(genWriteMethod(userTypeName, descriptor))
				.addMethod(genReadMethod(userTypeName, descriptor))
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
	 * given descriptor - an implementation of {@link Codec#write(CodedOutputStream, Object)}.
	 */
	@NotNull
	private static MethodSpec genWriteMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
		ParameterSpec outputParam = notNull(CodedOutputStream.class, "output");
		ParameterSpec valueParam = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("write")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(outputParam)
				.addParameter(valueParam)
				.addException(IOException.class);


		// TODO: write the fields in the order of their numbers
		for (FieldDescriptorProto field : descriptor.getFieldList()){
			Type fieldType = field.getType();
			int fieldNumber = field.getNumber();
			String getterName = fieldGetterName(field);
			String primitiveWriterMethodName = WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (isRepeated(field)){ // Repeated field

				String computeRepeatedSizeMethodName = PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE.get(fieldType);
				if (computeRepeatedSizeMethodName != null){ // A type that is packed when repeated

					// Generates code like so:
					// int[] _arr = value.getArr();
					// if (arr != null){
					//   output.writeUInt32NoTag(10);
					//   output.writeUInt32NoTagcomputeRepeatedInt32FieldSize(_arr));
					//   for (int i = 0; i < _arr.length; ++i)
					//     output.writeInt32NoTag(_arr[i]);
					// }

					String writeNoTagMethod = WRITE_NO_TAG_METHOD_NAMES_BY_TYPE.get(fieldType);
					String javaTypeName = javaTypeName(field);
					String fieldValueLocalVarName = "_" + field.getName();
					methodBuilder.addStatement("$L[] $L = $N.$N()", javaTypeName, fieldValueLocalVarName, valueParam, getterName); // e.g. int[] _arr = value.getArr()
					methodBuilder.beginControlFlow("if ($L != null)", fieldValueLocalVarName)
							.addStatement("$N.writeUInt32NoTag($L)", outputParam, WireFormatProxy.makeLengthDelimitedTag(fieldNumber))
							.addStatement("$N.writeUInt32NoTag($L($L))", outputParam, computeRepeatedSizeMethodName, fieldValueLocalVarName)
							.addCode(CodeBlock.builder()
									.beginControlFlow("for (int i = 0; i < $L.length; ++i)", fieldValueLocalVarName)
									.addStatement("$N.$L($L[i])", outputParam, writeNoTagMethod, fieldValueLocalVarName)
									.endControlFlow()
									.build())
							.endControlFlow();
				}
				else{
					// TODO: Implemented non-packed repeated types
				}
			}
			else if (primitiveWriterMethodName != null){ // A primitive type
				// e.g. writeFloatField(output, 2, value.getSecondField())
				methodBuilder.addStatement("$L($N, $L, $N.$N())",
						primitiveWriterMethodName, outputParam, fieldNumber, valueParam, getterName);
			}
			else if ((fieldType == Type.TYPE_MESSAGE) || (fieldType == Type.TYPE_ENUM)){ // A user-defined type, with a codec
				// e.g. TypeCodec.INSTANCE.writeField(output, 3, value.getThirdField())
				// This works for enums too, because the method name in EnumCodec just happens to also be writeField
				methodBuilder.addStatement("$L.writeField($N, $L, $N.$N())",
						codecInstanceExpr(field), outputParam, fieldNumber, valueParam, getterName);
			}
			else
				System.err.println("Field type " + fieldType + " not supported yet");
		}

		return methodBuilder.build();
	}



	/**
	 * Generates a method that decodes messages described by the given descriptor into objects of
	 * the user-defined type - an implementation of {@link Codec#read(CodedInputStream)}.
	 */
	@NotNull
	private static MethodSpec genReadMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
		ParameterSpec inputParam = notNull(CodedInputStream.class, "input");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("read")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addAnnotation(NotNull.class)
				.returns(userTypeName)
				.addParameter(inputParam)
				.addException(IOException.class);

		// For each field, create a local variable to hold it
		for (FieldDescriptorProto field : descriptor.getFieldList()){
			String fieldName = field.getName();
			Type fieldType = field.getType();
			String javaTypeName = javaTypeName(field);
			if (javaTypeName == null){
				System.err.println("Field type " + field.getType() + " is not supported yet");
				return methodBuilder.build();
			}

			String defaultValue = defaultJavaValue(field);

			if (!isRepeated(field))
				methodBuilder.addStatement("$L $L = $L", javaTypeName, fieldName, defaultValue); // e.g. int field = 0;
			else if (PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE.containsKey(fieldType))
				methodBuilder.addStatement("$L[] $L = $L", javaTypeName, fieldName, defaultValue); // e.g. int[] field = 0;
			else
				methodBuilder.addStatement("$L $L = $L", arrayListOf(javaTypeName), fieldName, defaultValue); // e.g. int[] field = 0;
		}


		// Generates code like so:
		// boolean done = false;
		// while (!done){
		//     int tag = input.readTag();
		//     switch (tag){
		//         case <tag1>: <var1> = input.readTypeOfVar1(); break;
		//         ...
		//         case 0: done = true; break;
		//         default:
		//           if (!input.skipField(tag))
		//              done = true;
		//           break;
		//     }
		// }
		String doneVar = "done";
		methodBuilder.addStatement("boolean $L = false", doneVar);
		CodeBlock.Builder whileNotDoneBuilder = CodeBlock.builder().beginControlFlow("while (!$L)", doneVar);

		String tagVar = "tag";
		whileNotDoneBuilder.addStatement("int $L = $L.readTag()", tagVar, inputParam.name);
		CodeBlock.Builder switchBuilder = CodeBlock.builder().beginControlFlow("switch($L)", tagVar);
		switchBuilder.add("case 0: $L = true; break;\n", doneVar);
		for (FieldDescriptorProto field : descriptor.getFieldList()){
			Type fieldType = field.getType();
			String fieldName = field.getName();
			int tagValue = WireFormatProxy.makeTag(field.getNumber(), fieldType);
			String primitiveReaderMethodName = READ_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (isRepeated(field)){ // Repeated field
				// TODO: read a repeated field
			}
			else if (primitiveReaderMethodName != null){ // A primitive type
				// e.g. case 81: intField = input.readInt32(); break;
				switchBuilder.add("case $L: $L = $N.$L(); break;\n",
						tagValue, fieldName, inputParam, primitiveReaderMethodName);
			}
			else{ // A user-defined type, with a codec
				// e.g. case 42: myField = TypeCodec.INSTANCE.decode(input);
				switchBuilder.add("case $L: $L = $L.readField($N); break;\n",
						tagValue, fieldName, codecInstanceExpr(field), inputParam);
			}
		}
		switchBuilder.add("default:\n").indent()
				.add("if (!$N.skipField($L))\n", inputParam, tagVar)
				.indent().add("$L = true;\n", doneVar).unindent()
				.add("break;\n").unindent();
		switchBuilder.endControlFlow();

		whileNotDoneBuilder.add(switchBuilder.build());
		whileNotDoneBuilder.endControlFlow();

		methodBuilder.addCode(whileNotDoneBuilder.build());


		// Generates e.g. return new Type(var1, ...);
		String newInstanceArgs = descriptor.getFieldList().stream()
				.map(FieldDescriptorProto::getName)
				.collect(Collectors.joining(", "));

		CodeBlock.Builder returnStatement = CodeBlock.builder()
				.add("return new $T(", userTypeName)
				.add(newInstanceArgs)
				.add(")");

		methodBuilder.addStatement(returnStatement.build());

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
			String getterName = fieldGetterName(field);
			String primitiveSizeComputerMethodName = COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (isRepeated(field)){
				// TODO: compute the size of a repeated field
			}
			else if (primitiveSizeComputerMethodName != null){ // A primitive type
				// e.g. size += floatFieldSize(2, value.getSecondField())
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
		for (EnumValueDescriptorProto enumValue : descriptor.getValueList()){
			if (enumValue.getNumber() == 0) // We interpret 0 as null
				continue;
			methodBuilder.addStatement("case $N: return $L", enumValue.getName(), enumValue.getNumber());
		}
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

		methodBuilder.beginControlFlow("switch($N)", encoded);
		for (EnumValueDescriptorProto enumValue : descriptor.getValueList()){
			if (enumValue.getNumber() == 0)
				methodBuilder.addStatement("case $L: return null", enumValue.getNumber());
			else
				methodBuilder.addStatement("case $L: return $T.$N", enumValue.getNumber(), userTypeName, enumValue.getName());
		}
		methodBuilder.addStatement("default: throw new IllegalArgumentException($N + \" has no corresponding enum value\")", encoded);
		methodBuilder.endControlFlow();

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
	 * Returns the type name of {@link ArrayList} parameterized with the given Java type.
	 */
	private static TypeName arrayListOf(@NotNull String javaType){
		return ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.bestGuess(javaType));
	}



	/**
	 * Returns the name of the getter method we expect to be present in the user-defined type for
	 * the give proto field.
	 */
	@NotNull
	private static String fieldGetterName(@NotNull FieldDescriptorProto field){
		Type type = field.getType();
		String name = field.getName();

		// If the field is boolean and already starts with "is_", don't duplicate it.
		// For example: "is_red", should become "isRed", not "isIsRed".
		boolean isBoolean = (type == Type.TYPE_BOOL) && !isRepeated(field);
		if (isBoolean && name.startsWith("is_"))
			name = name.substring(3);

		return (isBoolean ? "is" : "get") +
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
	 * Returns the name of the Java type for the corresponding field.
	 */
	@Nullable
	private static String javaTypeName(@NotNull FieldDescriptorProto field){
		Type fieldType = field.getType();
		String javaTypeName = JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE.get(field.getType());
		if (javaTypeName != null)
			return javaTypeName;

		if ((fieldType == Type.TYPE_MESSAGE) || (fieldType == Type.TYPE_ENUM)){ // A user-defined type, with a codec
			String typeName = field.getTypeName();
			if (typeName.startsWith(".")) // Fully qualified name (sans the '.')
				return typeName.substring(1);
			else
				return typeName;
		}

		return null; // Not a supported type;
	}



	/**
	 * Returns the expression for the default Java value of the given protobuf type.
	 */
	@NotNull
	private static String defaultJavaValue(@NotNull FieldDescriptorProto field){
		if (isRepeated(field))
			return "null";

		switch (field.getType()){
			case TYPE_BOOL:
				return "false";
			case TYPE_MESSAGE:
			case TYPE_ENUM:
			case TYPE_STRING:
			case TYPE_BYTES:
				return "null";
			default:
				return "0";
		}
	}



	/**
	 * Returns whether the given field has a "repeated" label.
	 */
	private static boolean isRepeated(@NotNull FieldDescriptorProto field){
		return field.hasLabel() && (field.getLabel() == FieldDescriptorProto.Label.LABEL_REPEATED);
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
