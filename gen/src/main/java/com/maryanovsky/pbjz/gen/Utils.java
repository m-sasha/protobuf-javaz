package com.maryanovsky.pbjz.gen;



import com.google.common.base.CaseFormat;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;



/**
 * A collection of utility methods for generating the codec code.
 *
 * @author Alexander Maryanovsky
 */
public class Utils{



	/**
	 * The name of the singleton instance field for each {@link Codec} we generate.
	 */
	private static final String CODEC_SINGLETON_INSTANCE_FIELD_NAME = "INSTANCE";



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that write them.
	 */
	@NotNull
	public static final Map<FieldDescriptorProto.Type, String> WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "writeDoubleField");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "writeFloatField");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "writeInt32Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "writeInt64Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "writeUInt32Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "writeUInt64Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "writeSInt32Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "writeSInt64Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "writeFixed32Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "writeFixed64Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "writeSFixed32Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "writeSFixed64Field");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "writeBoolField");
		methodNames.put(FieldDescriptorProto.Type.TYPE_STRING, "writeStringField");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BYTES, "writeBytesField");

		WRITE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link CodedInputStream} that
	 * read them.
	 */
	@NotNull
	public static final Map<FieldDescriptorProto.Type, String> READ_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "readDouble");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "readFloat");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "readInt32");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "readInt64");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "readUInt32");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "readUInt64");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "readSInt32");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "readSInt64");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "readFixed32");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "readFixed64");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "readSFixed32");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "readSFixed64");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "readBool");
		methodNames.put(FieldDescriptorProto.Type.TYPE_STRING, "readStringRequireUtf8");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BYTES, "readByteArray");

		READ_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the corresponding Java types.
	 */
	@NotNull
	private static final Map<FieldDescriptorProto.Type, String> JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "double");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "float");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "int");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "long");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "int");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "long");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "int");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "long");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "int");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "long");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "int");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "long");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "boolean");
		methodNames.put(FieldDescriptorProto.Type.TYPE_STRING, "String");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BYTES, "byte[]");

		JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that
	 * compute their serialized sizes.
	 */
	@NotNull
	public static final Map<FieldDescriptorProto.Type, String> COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "doubleFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "floatFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "int32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "int64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "uInt32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "uInt64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "sInt32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "sInt64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "fixed32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "fixed64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "sFixed32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "sFixed64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "boolFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_STRING, "stringFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BYTES, "bytesFieldSize");

		COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link Codec} that return the
	 * sizes of packed repeated fields of this type.
	 */
	@NotNull
	public static final Map<FieldDescriptorProto.Type, String> PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "packedRepeatedDoubleFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "packedRepeatedFloatFieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "packedRepeatedInt32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "packedRepeatedInt64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "packedRepeatedUInt32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "packedRepeatedUInt64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "packedRepeatedSInt32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "packedRepeatedSInt64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "packedRepeatedFixed32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "packedRepeatedFixed64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "packedRepeatedSFixed32FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "packedRepeatedSFixed64FieldSize");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "packedRepeatedBoolFieldSize");

		PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Maps protobuf primitive types to the names of the methods in {@link CodedOutputStream} that
	 * write fields of this type without a tag.
	 */
	@NotNull
	public static final Map<FieldDescriptorProto.Type, String> WRITE_NO_TAG_METHOD_NAMES_BY_TYPE;
	static{
		Map<FieldDescriptorProto.Type, String> methodNames = new EnumMap<>(FieldDescriptorProto.Type.class);
		methodNames.put(FieldDescriptorProto.Type.TYPE_DOUBLE, "writeDoubleNoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FLOAT, "writeFloatNoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT32, "writeInt32NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_INT64, "writeInt64NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT32, "writeUInt32NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_UINT64, "writeUInt64NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT32, "writeSInt32NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SINT64, "writeSInt64NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED32, "writeFixed32NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_FIXED64, "writeFixed64NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED32, "writeSFixed32NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_SFIXED64, "writeSFixed64NoTag");
		methodNames.put(FieldDescriptorProto.Type.TYPE_BOOL, "writeBoolNoTag");

		WRITE_NO_TAG_METHOD_NAMES_BY_TYPE = Collections.unmodifiableMap(methodNames);
	}



	/**
	 * Creates a parameter spec with a {@code NotNull} annotation from the given parameter type and
	 * name.
	 */
	@NotNull
	public static ParameterSpec notNull(@NotNull TypeName typeName, @NotNull String paramName){
		return ParameterSpec.builder(typeName, paramName).addAnnotation(NotNull.class).build();
	}


	/**
	 * Creates a parameter spec with a {@code NotNull} annotation from the given parameter type and
	 * name.
	 */
	@NotNull
	public static ParameterSpec notNull(@NotNull Class<?> clazz, @NotNull String paramName){
		return notNull(ClassName.get(clazz), paramName);
	}



	/**
	 * Returns the type name of {@link ArrayList} parameterized with the given Java type.
	 */
	public static TypeName arrayListOf(@NotNull String javaType){
		return ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.bestGuess(javaType));
	}



	/**
	 * Returns the name of the getter method we expect to be present in the user-defined type for
	 * the give proto field.
	 */
	@NotNull
	public static String fieldGetterName(@NotNull FieldDescriptorProto field){
		FieldDescriptorProto.Type type = field.getType();
		String name = field.getName();

		// If the field is boolean and already starts with "is_", don't duplicate it.
		// For example: "is_red", should become "isRed", not "isIsRed".
		boolean isBoolean = (type == FieldDescriptorProto.Type.TYPE_BOOL) && !isRepeated(field);
		if (isBoolean && name.startsWith("is_"))
			name = name.substring(3);

		return (isBoolean ? "is" : "get") +
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
	}



	/**
	 * Returns the expression of the codec instance to use for the given field.
	 */
	@NotNull
	public static String codecInstanceExpr(@NotNull FieldDescriptorProto field){
		String typeName = field.getTypeName();
		String codecTypeName;
		if (typeName.startsWith(".")){ // Fully qualified name (sans the '.')
			// Convert each inner type name to the name of its codec
			ClassName fieldClassName = ClassName.bestGuess(typeName.substring(1));
			codecTypeName = fieldClassName.packageName() + "." +
					fieldClassName.simpleNames()
							.stream()
							.map(Utils::codecSimpleName)
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
	public static String codecSimpleName(@NotNull String className){
		return className + "Codec";
	}



	/**
	 * Returns the name of the Java type for the corresponding field.
	 */
	@Nullable
	public static String javaTypeName(@NotNull FieldDescriptorProto field){
		FieldDescriptorProto.Type fieldType = field.getType();
		String javaTypeName = JAVA_TYPE_NAMES_BY_PRIMITIVE_TYPE.get(field.getType());
		if (javaTypeName != null)
			return javaTypeName;

		if ((fieldType == FieldDescriptorProto.Type.TYPE_MESSAGE) ||
				(fieldType == FieldDescriptorProto.Type.TYPE_ENUM)){ // A user-defined type, with a codec
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
	public static String defaultJavaValue(@NotNull FieldDescriptorProto field){
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
	public static boolean isRepeated(@NotNull FieldDescriptorProto field){
		return field.hasLabel() && (field.getLabel() == FieldDescriptorProto.Label.LABEL_REPEATED);
	}



	/**
	 * Returns whether the given field type is packed when repeated.
	 */
	public static boolean isPacked(@NotNull FieldDescriptorProto.Type type){
		return PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE.containsKey(type);
	}



	/**
	 * Generates a singleton instance field for the given class.
	 */
	@NotNull
	public static FieldSpec genSingletonInstanceField(@NotNull ClassName type){
		return FieldSpec.builder(type, CODEC_SINGLETON_INSTANCE_FIELD_NAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("new $T()", type)
				.build();
	}



	/**
	 * Generates a private constructor.
	 */
	@NotNull
	public static MethodSpec genPrivateConstructor(){
		return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
	}




}
