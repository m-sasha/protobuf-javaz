package com.maryanovsky.pbjz.gen;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import static com.maryanovsky.pbjz.gen.Utils.*;



/**
 * Responsible for generating the
 * {@link Codec#write(CodedOutputStream, Object)} method, which encodes the user-defined type into
 * the protobuf wire format.
 *
 * @author Alexander Maryanovsky
 */
public class WriteGenerator{



	/**
	 * Generates a method that encodes objects of a user-defined type into messages described by the
	 * given descriptor - an implementation of {@link Codec#write(CodedOutputStream, Object)}.
	 */
	@NotNull
	public static MethodSpec genWriteMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
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

			methodBuilder.addCode("\n");

			if (isRepeated(field)){ // Repeated field
				if (isPacked(fieldType)){
					methodBuilder.addComment("Write $L", field.getName());
					methodBuilder.addCode(genPackedRepeatedFieldWriter(field, valueParam, outputParam));
				}
				else{
					// TODO: Write non-packed repeated types
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
	 * Returns a code block that writes the given field of the given user-defined value, into the
	 * given output ({@link CodedOutputStream}).
	 */
	private static CodeBlock genPackedRepeatedFieldWriter(@NotNull FieldDescriptorProto field,
														  @NotNull ParameterSpec valueParam,
														  @NotNull ParameterSpec outputParam){
		// Generates code like so:
		// int[] _arr = value.getArr();
		// if (arr != null){
		//   output.writeUInt32NoTag(10);
		//   output.writeUInt32NoTag(packedRepeatedInt32FieldSize(_arr));
		//   for (int i = 0; i < _arr.length; ++i)
		//     output.writeInt32NoTag(_arr[i]);
		// }

		CodeBlock.Builder code = CodeBlock.builder();

		String javaTypeName = javaTypeName(field);
		if (javaTypeName == null){ // Not a supported type yet
			System.err.println("Field type " + field.getType() + " is not supported yet");
			return code.build();
		}

		Type fieldType = field.getType();
		String getterName = fieldGetterName(field);
		String computeRepeatedSizeMethodName = PACKED_REPEATED_SIZE_METHOD_NAMES_BY_TYPE.get(fieldType);
		String writeNoTagMethod = WRITE_NO_TAG_METHOD_NAMES_BY_TYPE.get(fieldType);
		TypeName collectionType = collectionOf(javaTypeName);
		String fieldValueLocalVarName = "_" + field.getName();

		code.addStatement("$T $L = $N.$N()", collectionType, fieldValueLocalVarName, valueParam, getterName); // e.g. Collection<Integer> _arr = value.getArr()
		code.beginControlFlow("if ($L != null)", fieldValueLocalVarName)
				.addStatement("$N.writeUInt32NoTag($L)", outputParam, WireFormatProxy.makeLengthDelimitedTag(field.getNumber()))
				.addStatement("$N.writeUInt32NoTag($L($L))", outputParam, computeRepeatedSizeMethodName, fieldValueLocalVarName)
				.add(CodeBlock.builder()
						.beginControlFlow("for ($L item : $L)", javaTypeName, fieldValueLocalVarName)
						.addStatement("$N.$L(item)", outputParam, writeNoTagMethod)
						.endControlFlow()
						.build())
				.endControlFlow();

		return code.build();
	}



}
