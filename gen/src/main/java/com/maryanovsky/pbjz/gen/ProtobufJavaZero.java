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
import static com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
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
				TypeSpec codec = genCodec(javaPackage, null,
						UserDefinedTypeDescriptor.forMessageType(descriptor));
				response.addFile(genCodecFile(javaPackage, codec));
			}

			// Generate codecs for enum types
			for (EnumDescriptorProto descriptor : fileDescriptor.getEnumTypeList()){
				TypeSpec codec = genCodec(javaPackage, null,
						UserDefinedTypeDescriptor.forEnumType(descriptor));
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
	 * Generates the {@link Codec} for a single user-defined type, as described by the given
	 * descriptor.
	 */
	@NotNull
	private static TypeSpec genCodec(@NotNull String userTypeJavaPackage,
									 @Nullable ClassName userTypeOuterClassName,
									 @NotNull UserDefinedTypeDescriptor descriptor){
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


		if (descriptor.isMessageType()){
			DescriptorProto messageDescriptor = descriptor.messageDescriptor;

			// Generate nested codecs for message types
			for (DescriptorProto nestedMessageDescriptor : messageDescriptor.getNestedTypeList()){
				builder.addType(
						genCodec(userTypeJavaPackage, userTypeName,
								UserDefinedTypeDescriptor.forMessageType(nestedMessageDescriptor)));
			}

			// Generate nested codecs for enum types
			for (EnumDescriptorProto nestedEnumDescriptor : messageDescriptor.getEnumTypeList()){
				builder.addType(
						genCodec(userTypeJavaPackage, userTypeName,
								UserDefinedTypeDescriptor.forEnumType(nestedEnumDescriptor)));
			}
		}

		return builder.build();
	}



	/**
	 * Generates a method that encodes objects of a user-defined type into messages described by the
	 * given descriptor - an implementation of {@link Codec#encode(CodedOutputStream, Object)}.
	 */
	@NotNull
	private static MethodSpec genEncodeMethod(@NotNull TypeName userTypeName, @NotNull UserDefinedTypeDescriptor descriptor){
		ParameterSpec output = notNull(CodedOutputStream.class, "output");
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("encode")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(void.class)
				.addParameter(output)
				.addParameter(value)
				.addException(IOException.class);


		if (descriptor.isMessageType()){
			DescriptorProto messageDescriptor = descriptor.messageDescriptor;

			for (FieldDescriptorProto field : messageDescriptor.getFieldList()){
				Type fieldType = field.getType();
				String getterName = fieldGetterName(field.getName(), fieldType);
				String primitiveWriterMethodName = WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

				if (primitiveWriterMethodName != null){ // A primitive type
					methodBuilder.addStatement("$L($N, $L, $N.$N())",
							primitiveWriterMethodName, output, field.getNumber(), value, getterName);
				}
				else if (fieldType == Type.TYPE_MESSAGE){ // A non-enum user-defined type
					String codecInstance = messageFieldCodecName(field) + "." + CODEC_SINGLETON_INSTANCE_FIELD_NAME;
					methodBuilder.addStatement("$L.writeMessageField($N, $L, $N.$N())",
							codecInstance, output, field.getNumber(), value, getterName);
				}
				else
					System.err.println("Field type " + fieldType + " not supported yet");
			}
		}
		else{
			// TODO: Implement encoding for enum types
		}

		return methodBuilder.build();
	}



	/**
	 * Generates a method that decodes messages described by the given descriptor into objects of
	 * the user-defined type - an implementation of {@link Codec#decode(CodedInputStream)}.
	 */
	@NotNull
	private static MethodSpec genDecodeMethod(@NotNull TypeName userTypeName, @NotNull UserDefinedTypeDescriptor descriptor){
		return MethodSpec.methodBuilder("decode")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addAnnotation(Nullable.class)
				.returns(userTypeName)
				.addParameter(notNull(CodedInputStream.class, "input"))
				.addException(IOException.class)
				.addStatement("return null")
				.build();
	}



	/**
	 * Generates a method that computes the serialized size of values of the user-defined type - an
	 * implementation of {@link Codec#computeSerializedSize(Object)}.
	 */
	@NotNull
	private static MethodSpec genSizeComputerMethod(@NotNull TypeName userTypeName,
													@NotNull UserDefinedTypeDescriptor descriptor){
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("computeSerializedSize")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		if (descriptor.isMessageType()){
			DescriptorProto messageDescriptor = descriptor.messageDescriptor;

			methodBuilder.addStatement("int size = 0");

			for (FieldDescriptorProto field : messageDescriptor.getFieldList()){
				Type fieldType = field.getType();
				String getterName = fieldGetterName(field.getName(), fieldType);
				String primitiveSizeComputerMethodName = COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

				if (primitiveSizeComputerMethodName != null){ // A primitive type
					methodBuilder.addStatement("size += $L($L, $N.$N())",
							primitiveSizeComputerMethodName, field.getNumber(), value, getterName);
				}
				else if (fieldType == Type.TYPE_MESSAGE){ // A non-enum user-defined type
					String codecInstance = messageFieldCodecName(field) + "." + CODEC_SINGLETON_INSTANCE_FIELD_NAME;
					methodBuilder.addStatement("size += $L.computeMessageFieldSize($L, $N.$N())",
							codecInstance, field.getNumber(), value, getterName);
				}
				else
					System.err.println("Field type " + fieldType + " not supported yet");
			}

			methodBuilder.addStatement("return size");
		}
		else{
			// TODO: Implement size computation for enum types
		}

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
	private static String messageFieldCodecName(@NotNull FieldDescriptorProto field){
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



	/**
	 * Represents a user-defined protobuf type, whether it's a message or an enum type.
	 */
	private static class UserDefinedTypeDescriptor{



		/**
		 * The message type descriptor, or {@code null} if an enum type is represented.
		 */
		public final DescriptorProto messageDescriptor;



		/**
		 * The enum type descriptor, or {@code null} if a message type is represented.
		 */
		public final EnumDescriptorProto enumDescriptor;



		/**
		 * Creates a new {@link UserDefinedTypeDescriptor} with the given descriptors, of which
		 * exactly one must be non-{@code null}.
		 */
		private UserDefinedTypeDescriptor(DescriptorProto messageDescriptor, EnumDescriptorProto enumDescriptor){
			this.messageDescriptor = messageDescriptor;
			this.enumDescriptor = enumDescriptor;
		}



		/**
		 * Creates a {@link UserDefinedTypeDescriptor} for a message type.
		 */
		public static UserDefinedTypeDescriptor forMessageType(@NotNull DescriptorProto messageDescriptor){
			return new UserDefinedTypeDescriptor(messageDescriptor, null);
		}



		/**
		 * Creates a {@link UserDefinedTypeDescriptor} for an enum type.
		 */
		public static UserDefinedTypeDescriptor forEnumType(@NotNull EnumDescriptorProto enumDescriptor){
			return new UserDefinedTypeDescriptor(null, enumDescriptor);
		}



		/**
		 * Returns whether a message type is represented.
		 */
		public boolean isMessageType(){
			return messageDescriptor != null;
		}



		/**
		 * Returns the name of the type.
		 */
		public String getName(){
			return isMessageType() ? messageDescriptor.getName() : enumDescriptor.getName();
		}


	}



}
