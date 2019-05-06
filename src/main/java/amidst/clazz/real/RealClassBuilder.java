package amidst.clazz.real;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.clazz.real.RealClassConstant.RealClassConstantType;
import amidst.documentation.Immutable;

/**
 * See https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.8
 */
@Immutable
public class RealClassBuilder {
	public RealClass construct(String realClassName, byte[] classData) throws RealClassCreationException {
		try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(classData))) {
			return doConstruct(realClassName, classData, stream);
		} catch (IOException e) {
			throw new RealClassCreationException("unable to create real class for the class: " + realClassName, e);
		}
	}

	private RealClass doConstruct(String realClassName, byte[] classData, DataInputStream stream) throws IOException {
		if (isValidClass(stream)) {
			int minorVersion = readMinorVersion(stream);
			int majorVersion = readMajorVersion(stream);
			int cpSize = readCpSize(stream);
			int[] constantTypes = new int[cpSize];
			RealClassConstant<?>[] constants = new RealClassConstant<?>[cpSize];
			List<String> utf8Constants = new ArrayList<>();
			List<Float> floatConstants = new ArrayList<>();
			List<Double> doubleConstants = new ArrayList<>();
			List<Long> longConstants = new ArrayList<>();
			List<Integer> stringIndices = new ArrayList<>();
			List<ReferenceIndex> methodIndices = new ArrayList<>();
			readConstants(
					stream,
					cpSize,
					constantTypes,
					constants,
					utf8Constants,
					floatConstants,
					doubleConstants,
					longConstants,
					stringIndices);
			int accessFlags = readAccessFlags(stream);
			skipThisClass(stream);
			String superClassName = readSuperClass(stream, constants);
			skipInterfaces(stream);
			RealClassField[] fields = readFields(stream);
			int numberOfMethodsAndConstructors = readNumberOfMethodsAndConstructors(stream);
			int numberOfConstructors = readMethodsAndConstructors(
					stream,
					numberOfMethodsAndConstructors,
					constants,
					methodIndices);
			int numberOfMethods = numberOfMethodsAndConstructors - numberOfConstructors;
			return new RealClass(
					realClassName,
					superClassName,
					classData,
					minorVersion,
					majorVersion,
					cpSize,
					constantTypes,
					constants,
					utf8Constants,
					floatConstants,
					doubleConstants,
					longConstants,
					stringIndices,
					methodIndices,
					accessFlags,
					fields,
					numberOfConstructors,
					numberOfMethods);
		} else {
			return null;
		}
	}

	private boolean isValidClass(DataInputStream stream) throws IOException {
		return stream.readInt() == 0xCAFEBABE;
	}

	private int readMinorVersion(DataInputStream stream) throws IOException {
		return stream.readUnsignedShort();
	}

	private int readMajorVersion(DataInputStream stream) throws IOException {
		return stream.readUnsignedShort();
	}

	private int readCpSize(DataInputStream stream) throws IOException {
		return stream.readUnsignedShort() - 1;
	}

	private void readConstants(
			DataInputStream stream,
			int cpSize,
			int[] constantTypes,
			RealClassConstant<?>[] constants,
			List<String> utf8Constants,
			List<Float> floatConstants,
			List<Double> doubleConstants,
			List<Long> longConstants,
			List<Integer> stringIndices) throws IOException {
		for (int q = 0; q < cpSize; q++) {
			byte type = stream.readByte();
			constantTypes[q] = type;
			constants[q] = readConstant(stream, type, utf8Constants, floatConstants, doubleConstants, longConstants, stringIndices);
			if (RealClassConstantType.isQIncreasing(type)) {
				q++;
			}
		}
	}

	private RealClassConstant<?> readConstant(
			DataInputStream stream,
			byte type,
			List<String> utf8Constants,
			List<Float> floatConstants,
			List<Double> doubleConstants,
			List<Long> longConstants,
			List<Integer> stringIndices) throws IOException {
		switch (type) {
		case RealClassConstantType.STRING:
			return readString(stream, type, utf8Constants);
		case RealClassConstantType.INTEGER:
			return readInteger(stream, type);
		case RealClassConstantType.FLOAT:
			return readFloat(stream, type, floatConstants);
		case RealClassConstantType.LONG:
			return readLong(stream, type, longConstants);
		case RealClassConstantType.DOUBLE:
			return readDouble(stream, type, doubleConstants);
		case RealClassConstantType.CLASS_REFERENCE:
			return readClassReference(stream, type);
		case RealClassConstantType.STRING_REFERENCE:
			return readStringReference(stream, type, stringIndices);
		case RealClassConstantType.FIELD_REFERENCE:
			return readAnotherReference(stream, type);
		case RealClassConstantType.METHOD_REFERENCE:
			return readAnotherReference(stream, type);
		case RealClassConstantType.INTERFACE_METHOD_REFERENCE:
			return readAnotherReference(stream, type);
		case RealClassConstantType.NAME_AND_TYPE_DESCRIPTOR:
			return readAnotherReference(stream, type);
		case RealClassConstantType.METHOD_HANDLE:
			return readMethodHandle(stream, type);
		case RealClassConstantType.METHOD_TYPE:
			return readMethodType(stream, type);
		case RealClassConstantType.INVOKE_DYNAMIC:
			return readInvokeDynamic(stream, type);
		default:
			throw new IOException("unknown constant type: " + type);
		}
	}

	private RealClassConstant<String> readString(DataInputStream stream, byte type, List<String> utf8Constants)
			throws IOException {
		String value = readStringValue(stream);
		utf8Constants.add(value);
		return new RealClassConstant<>(type, value);
	}

	private String readStringValue(DataInputStream stream) throws IOException {
		char[] result = new char[stream.readUnsignedShort()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (char) stream.readByte();
		}
		return new String(result);
	}

	private RealClassConstant<Integer> readInteger(DataInputStream stream, byte type) throws IOException {
		int value = stream.readInt();
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Float> readFloat(DataInputStream stream, byte type, List<Float> floatConstants)
			throws IOException {
		float value = stream.readFloat();
		floatConstants.add(value);
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Long> readLong(DataInputStream stream, byte type, List<Long> longConstants)
			throws IOException {
		long value = stream.readLong();
		longConstants.add(value);
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Double> readDouble(DataInputStream stream, byte type, List<Double> doubleConstants) throws IOException {
		double value = stream.readDouble();
		doubleConstants.add(value);
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Integer> readClassReference(DataInputStream stream, byte type) throws IOException {
		int value = stream.readUnsignedShort();
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Integer> readStringReference(
			DataInputStream stream,
			byte type,
			List<Integer> stringIndices) throws IOException {
		int value = stream.readUnsignedShort();
		stringIndices.add(value);
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<ReferenceIndex> readAnotherReference(DataInputStream stream, byte type)
			throws IOException {
		ReferenceIndex value = readReferenceIndex(stream);
		return new RealClassConstant<>(type, value);
	}

	private RealClassConstant<Void> readMethodHandle(DataInputStream stream, byte type) throws IOException {
		stream.readByte();
		stream.readUnsignedShort();
		// return a dummy
		return new RealClassConstant<Void>(type, null);
	}

	private RealClassConstant<Void> readMethodType(DataInputStream stream, byte type) throws IOException {
		stream.readUnsignedShort();
		// return a dummy
		return new RealClassConstant<Void>(type, null);
	}

	private RealClassConstant<Void> readInvokeDynamic(DataInputStream stream, byte type) throws IOException {
		stream.readUnsignedShort();
		stream.readUnsignedShort();
		// return a dummy
		return new RealClassConstant<Void>(type, null);
	}

	private int readAccessFlags(DataInputStream stream) throws IOException {
		return stream.readUnsignedShort();
	}

	private void skipThisClass(DataInputStream stream) throws IOException {
		stream.skip(2);
	}

	private String readSuperClass(DataInputStream stream, RealClassConstant<?>[] constants) throws IOException {
		int superClassEntry = stream.readUnsignedShort();
		int superClassName = (Integer) constants[superClassEntry-1].getValue();
		return (String) constants[superClassName-1].getValue();
	}

	private void skipInterfaces(DataInputStream stream) throws IOException {
		stream.skip(2 * stream.readUnsignedShort());
	}

	private RealClassField[] readFields(DataInputStream stream) throws IOException {
		RealClassField[] fields = new RealClassField[stream.readUnsignedShort()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new RealClassField(stream.readUnsignedShort());
			stream.skip(4);
			skipAttributes(stream);
		}
		return fields;
	}

	private int readNumberOfMethodsAndConstructors(DataInputStream stream) throws IOException {
		return stream.readUnsignedShort();
	}

	private int readMethodsAndConstructors(
			DataInputStream stream,
			int numberOfMethodsAndConstructors,
			RealClassConstant<?>[] constants,
			List<ReferenceIndex> methodIndices) throws IOException {
		int numberOfConstructors = 0;
		for (int i = 0; i < numberOfMethodsAndConstructors; i++) {
			stream.skip(2);
			ReferenceIndex referenceIndex = readReferenceIndex(stream);
			methodIndices.add(referenceIndex);
			String constant = (String) constants[referenceIndex.getValue1() - 1].getValue();
			if (constant.contains("<init>")) {
				numberOfConstructors++;
			}
			skipAttributes(stream);
		}
		return numberOfConstructors;
	}

	private ReferenceIndex readReferenceIndex(DataInputStream stream) throws IOException {
		int value1 = stream.readUnsignedShort();
		int value2 = stream.readUnsignedShort();
		return new ReferenceIndex(value1, value2);
	}

	private void skipAttributes(DataInputStream stream) throws IOException {
		int attributeInfoCount = stream.readUnsignedShort();
		for (int q = 0; q < attributeInfoCount; q++) {
			stream.skip(2);
			int attributeCount = stream.readInt();
			for (int z = 0; z < attributeCount; z++) {
				stream.skip(1);
			}
		}
	}
}
