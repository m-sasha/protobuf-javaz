package com.maryanovsky.pbjz.gen;

import com.google.protobuf.DescriptorProtos;
import com.maryanovsky.pbjz.runtime.Codec;
import com.maryanovsky.pbjz.runtime.EnumCodec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Modifier;

import static com.maryanovsky.pbjz.gen.Utils.*;



/**
 * @author Alexander Maryanovsky
 */
public class EnumCodecGenerator{



	/**
	 * Generates the {@link Codec} class for a single user-defined type, as described by the given
	 * message descriptor.
	 */
	@NotNull
	public static TypeSpec genEnumCodec(@NotNull String userTypeJavaPackage,
								 @Nullable ClassName userTypeOuterClassName,
								 @NotNull DescriptorProtos.EnumDescriptorProto descriptor){
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
	private static MethodSpec genToEncodedEnumValueMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProtos.EnumDescriptorProto descriptor){
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("toEncodedValue")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		methodBuilder.beginControlFlow("switch($N)", value);
		for (DescriptorProtos.EnumValueDescriptorProto enumValue : descriptor.getValueList()){
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
	private static MethodSpec genFromEncodedEnumValueMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProtos.EnumDescriptorProto descriptor){
		ParameterSpec encoded = ParameterSpec.builder(int.class, "encoded").build();

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("fromEncodedValue")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.addAnnotation(Nullable.class)
				.returns(userTypeName)
				.addParameter(encoded);

		methodBuilder.beginControlFlow("switch($N)", encoded);
		for (DescriptorProtos.EnumValueDescriptorProto enumValue : descriptor.getValueList()){
			if (enumValue.getNumber() == 0)
				methodBuilder.addStatement("case $L: return null", enumValue.getNumber());
			else
				methodBuilder.addStatement("case $L: return $T.$N", enumValue.getNumber(), userTypeName, enumValue.getName());
		}
		methodBuilder.addStatement("default: throw new IllegalArgumentException($N + \" has no corresponding enum value\")", encoded);
		methodBuilder.endControlFlow();

		return methodBuilder.build();
	}



}
