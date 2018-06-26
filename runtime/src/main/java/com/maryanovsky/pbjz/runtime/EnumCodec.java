package com.maryanovsky.pbjz.runtime;

import com.google.protobuf.CodedOutputStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;



/**
 * The base class for all types that encode and decode a user-defined enum type to and from its
 * protobuf wire representation. Note that the standalone (not as part of a message) writing of enum
 * values is not supported (because it's not supported by the standard protobuf implementation).
 *
 * The class is parameterized with the aforementioned user-defined enum type.
 *
 * @author Alexander Maryanovsky
 */
public abstract class EnumCodec<E extends Enum<E>>{



	/**
	 * Writes the given user-defined enum-type value into a {@link CodedOutputStream} at the given
	 * field number.
	 */
	public final void writeField(@NotNull CodedOutputStream output, int fieldNumber, @Nullable E value) throws IOException{
		if (value != null){
			int encodedValue = toEncodedValue(value);
			if (encodedValue != 0){
				output.writeEnum(fieldNumber, encodedValue);
			}
		}
	}



	/**
	 * Computes and returns the serialized size of the given user-defined enum-type, at the given
	 * field number.
	 */
	public final int computeSerializedSize(int fieldNumber, @Nullable E value){
		if (value == null)
			return 0;

		int encodedValue = toEncodedValue(value);
		if (encodedValue == 0)
			return 0;

		return CodedOutputStream.computeEnumSize(fieldNumber, encodedValue);
	}



	/**
	 * Returns the integer representing the given enum value in the protobuf wire format.
	 */
	protected abstract int toEncodedValue(@NotNull E value);



	/**
	 * Returns the enum value represented by the given integer in the protobuf wire format. Returns
	 * {@code null} if the given integer doesn't map to a known enum value.
	 */
	@Nullable
	protected abstract E fromEncodedValue(int encoded);



}
