package com.maryanovsky.pbjz.gen;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.maryanovsky.pbjz.runtime.Codec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import static com.maryanovsky.pbjz.gen.Utils.READ_METHOD_NAMES_BY_PRIMITIVE_TYPE;
import static com.maryanovsky.pbjz.gen.Utils.arrayListOf;
import static com.maryanovsky.pbjz.gen.Utils.codecInstanceExpr;
import static com.maryanovsky.pbjz.gen.Utils.defaultJavaValue;
import static com.maryanovsky.pbjz.gen.Utils.isPacked;
import static com.maryanovsky.pbjz.gen.Utils.isRepeated;
import static com.maryanovsky.pbjz.gen.Utils.javaTypeName;
import static com.maryanovsky.pbjz.gen.Utils.notNull;



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
		methodBuilder.addCode(genDeclareLocalVariablesForFields(descriptor));


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
		whileNotDoneBuilder.addStatement("int $L = $N.readTag()", tagVar, inputParam);
		CodeBlock.Builder switchBuilder = CodeBlock.builder().beginControlFlow("switch($L)", tagVar);
		switchBuilder.add("case 0: $L = true; break;\n", doneVar);
		for (FieldDescriptorProto field : descriptor.getFieldList()){
			FieldDescriptorProto.Type fieldType = field.getType();
			String fieldName = localVarName(field);
			int tagValue = WireFormatProxy.makeTag(field.getNumber(), fieldType);
			String primitiveReaderMethodName = READ_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(fieldType);

			if (isRepeated(field)){ // Repeated field
				if (isPacked(fieldType)){
					switchBuilder.add("case $L:", tagValue)
							.beginControlFlow("")
							.add(genPackedRepeatedFieldReader(field, inputParam))
							.endControlFlow()
							.add("break;");
				}
				else{
					// TODO: Read non-packed repeated types
				}
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
				.map(ReadGenerator::localVarName)
				.collect(Collectors.joining(", "));

		CodeBlock.Builder returnStatement = CodeBlock.builder()
				.add("return new $T(", userTypeName)
				.add(newInstanceArgs)
				.add(")");

		methodBuilder.addStatement(returnStatement.build());

		return methodBuilder.build();
	}



	/**
	 * Generates code that declares a local variable for each field in the type described by the
	 * given descriptor.
	 */
	private static CodeBlock genDeclareLocalVariablesForFields(@NotNull DescriptorProto descriptor){
		CodeBlock.Builder code = CodeBlock.builder();

		for (FieldDescriptorProto field : descriptor.getFieldList()){
			String javaTypeName = javaTypeName(field);
			if (javaTypeName == null){
				System.err.println("Field type " + field.getType() + " is not supported yet");
				break;
			}

			String localVarName = localVarName(field);
			String defaultValue = defaultJavaValue(field);

			if (isRepeated(field))
				code.addStatement("$T $L = $L", arrayListOf(javaTypeName), localVarName, defaultValue); // e.g. ArrayList<UserType> _field = null;
			else
				code.addStatement("$L $L = $L", javaTypeName, localVarName, defaultValue); // e.g. int _field = 0;
		}

		return code.build();
	}



	/**
	 * Generates code that reads a packed repeated field into a local variable.
	 */
	private static CodeBlock genPackedRepeatedFieldReader(@NotNull FieldDescriptorProto field, @NotNull ParameterSpec inputParam){
		// Generates code like this:
		// int length = input.readRawVarint32();
		// int limit = input.pushLimit(length);
		// values_ = new java.util.ArrayList<java.lang.Integer>();
		// while (input.getBytesUntilLimit() > 0) {
		//     values_.add(input.readInt32());
		// }
		// input.popLimit(limit);

		CodeBlock.Builder code = CodeBlock.builder();

		String localVarName = localVarName(field);
		String readMethodName = READ_METHOD_NAMES_BY_PRIMITIVE_TYPE.get(field.getType());

		code.addStatement("int length = $N.readRawVarint32()", inputParam);
		code.addStatement("int limit = $N.pushLimit(length)", inputParam);
		code.addStatement("$L = new $T<>()", localVarName, ArrayList.class);
		code.beginControlFlow("while($N.getBytesUntilLimit() > 0)", inputParam)
			.addStatement("$L.add($N.$L())", localVarName, inputParam, readMethodName)
			.endControlFlow();
		code.addStatement("$N.popLimit(limit)", inputParam);

		return code.build();
	}



	/**
	 * Returns the name of the local variable that should be used for storing the value of the given
	 * field.
	 */
	private static String localVarName(@NotNull FieldDescriptorProto field){
		return "_" + field.getName();
	}



}
