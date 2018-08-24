package com.maryanovsky.pbjz.gen;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;

import static com.maryanovsky.pbjz.gen.Utils.COMPUTE_SIZE_METHOD_NAMES_BY_PRIMITIVE_TYPE;
import static com.maryanovsky.pbjz.gen.Utils.codecInstanceExpr;
import static com.maryanovsky.pbjz.gen.Utils.fieldGetterName;
import static com.maryanovsky.pbjz.gen.Utils.isRepeated;
import static com.maryanovsky.pbjz.gen.Utils.notNull;



/**
 * Responsible for generating the
 * {@link com.maryanovsky.pbjz.runtime.Codec#computeSerializedSize(Object)} method, which computes
 * the size of an object of the user-defined type, when it's encoded into the the protobuf wire
 * format.
 *
 * @author Alexander Maryanovsky
 */
public class SizeComputeGenerator{



	/**
	 * Generates a method that computes the serialized size of values of the user-defined type - an
	 * implementation of {@link Codec#computeSerializedSize(Object)}.
	 */
	@NotNull
	public static MethodSpec genSizeComputerMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
		ParameterSpec value = notNull(userTypeName, "value");

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("computeSerializedSize")
				.addModifiers(Modifier.PROTECTED)
				.addAnnotation(Override.class)
				.returns(int.class)
				.addParameter(value);

		methodBuilder.addStatement("int size = 0");

		for (FieldDescriptorProto field : descriptor.getFieldList()){
			FieldDescriptorProto.Type fieldType = field.getType();
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
			else if ((fieldType == FieldDescriptorProto.Type.TYPE_MESSAGE) || (fieldType == FieldDescriptorProto.Type.TYPE_ENUM)){ // A user-defined type, with a codec
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

	
	
}
