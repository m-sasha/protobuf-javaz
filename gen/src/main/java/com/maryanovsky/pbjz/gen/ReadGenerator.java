package com.maryanovsky.pbjz.gen;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import static com.maryanovsky.pbjz.gen.Utils.*;



/**
 * Responsible for generating the
 * {@link com.maryanovsky.pbjz.runtime.Codec#read(CodedInputStream)} method, which decodes the
 * user-defined type from the protobuf wire format.
 *
 * @author Alexander Maryanovsky
 */
public class ReadGenerator{



	/**
	 * Generates a method that decodes messages described by the given descriptor into objects of
	 * the user-defined type - an implementation of {@link Codec#read(CodedInputStream)}.
	 */
	@NotNull
	public static MethodSpec genReadMethod(@NotNull TypeName userTypeName, @NotNull DescriptorProto descriptor){
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
			FieldDescriptorProto.Type fieldType = field.getType();
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
			FieldDescriptorProto.Type fieldType = field.getType();
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



}
