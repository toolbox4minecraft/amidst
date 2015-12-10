package amidst.clazz.real;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.clazz.real.RealClassConstant.RealClassConstantType;

public class RealClassBuilder {
	private final String realClassName;
	private final byte[] classData;
	private final DataInputStream stream;

	public RealClassBuilder(String realClassName, byte[] classData) {
		this.realClassName = realClassName;
		this.classData = classData;
		this.stream = new DataInputStream(new ByteArrayInputStream(classData));
	}

	public RealClass construct() {
		RealClass product = null;
		try {
			if (isValidClass()) {
				int minorVersion = readMinorVersion();
				int majorVersion = readMajorVersion();
				int cpSize = readCpSize();
				int[] constantTypes = new int[cpSize];
				RealClassConstant<?>[] constants = new RealClassConstant<?>[cpSize];
				List<String> utfConstants = new ArrayList<String>();
				List<Float> floatConstants = new ArrayList<Float>();
				List<Long> longConstants = new ArrayList<Long>();
				List<Integer> stringIndices = new ArrayList<Integer>();
				List<ReferenceIndex> methodIndices = new ArrayList<ReferenceIndex>();
				readConstants(cpSize, constantTypes, constants, utfConstants,
						floatConstants, longConstants, stringIndices);
				int accessFlags = readAccessFlags();
				skipThisClass();
				skipSuperClass();
				skipInterfaces();
				RealClassField[] fields = readFields();
				int numberOfMethodsAndConstructors = readNumberOfMethodsAndConstructors();
				int numberOfConstructors = readMethodsAndConstructors(
						numberOfMethodsAndConstructors, constants,
						methodIndices);
				int numberOfMethods = numberOfMethodsAndConstructors
						- numberOfConstructors;
				product = new RealClass(realClassName, classData, minorVersion,
						majorVersion, cpSize, constantTypes, constants,
						utfConstants, floatConstants, longConstants,
						stringIndices, methodIndices, accessFlags, fields,
						numberOfConstructors, numberOfMethods);
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return product;
	}

	private boolean isValidClass() throws IOException {
		return stream.readInt() == 0xCAFEBABE;
	}

	private int readMinorVersion() throws IOException {
		return stream.readUnsignedShort();
	}

	private int readMajorVersion() throws IOException {
		return stream.readUnsignedShort();
	}

	private int readCpSize() throws IOException {
		return stream.readUnsignedShort() - 1;
	}

	private void readConstants(int cpSize, int[] constantTypes,
			RealClassConstant<?>[] constants, List<String> utfConstants,
			List<Float> floatConstants, List<Long> longConstants,
			List<Integer> stringIndices) throws IOException {
		for (int q = 0; q < cpSize; q++) {
			byte type = stream.readByte();
			constantTypes[q] = type;
			constants[q] = readConstant(type, utfConstants, floatConstants,
					longConstants, stringIndices);
			if (RealClassConstantType.Q_INCREASING_TYPES.contains(type)) {
				q++;
			}
		}
	}

	private RealClassConstant<?> readConstant(byte type,
			List<String> utfConstants, List<Float> floatConstants,
			List<Long> longConstants, List<Integer> stringIndices)
			throws IOException {
		switch (type) {
		case RealClassConstantType.STRING:
			return readString(type, utfConstants);
		case RealClassConstantType.INTEGER:
			return readInteger(type);
		case RealClassConstantType.FLOAT:
			return readFloat(type, floatConstants);
		case RealClassConstantType.LONG:
			return readLong(type, longConstants);
		case RealClassConstantType.DOUBLE:
			return readDouble(type);
		case RealClassConstantType.CLASS_REFERENCE:
			return readClassReference(type);
		case RealClassConstantType.STRING_REFERENCE:
			return readStringReference(type, stringIndices);
		case RealClassConstantType.FIELD_REFERENCE:
			return readAnotherReference(type);
		case RealClassConstantType.METHOD_REFERENCE:
			return readAnotherReference(type);
		case RealClassConstantType.INTERFACE_METHOD_REFERENCE:
			return readAnotherReference(type);
		case RealClassConstantType.NAME_AND_TYPE_DESCRIPTOR:
			return readAnotherReference(type);
		default:
			return null;
		}
	}

	private RealClassConstant<String> readString(byte type,
			List<String> utfConstants) throws IOException {
		String value = readStringValue();
		utfConstants.add(value);
		return new RealClassConstant<String>(type, value);
	}

	private String readStringValue() throws IOException {
		char[] result = new char[stream.readUnsignedShort()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (char) stream.readByte();
		}
		return new String(result);
	}

	private RealClassConstant<Integer> readInteger(byte type)
			throws IOException {
		int value = stream.readInt();
		return new RealClassConstant<Integer>(type, value);
	}

	private RealClassConstant<Float> readFloat(byte type,
			List<Float> floatConstants) throws IOException {
		float value = stream.readFloat();
		floatConstants.add(value);
		return new RealClassConstant<Float>(type, value);
	}

	private RealClassConstant<Long> readLong(byte type, List<Long> longConstants)
			throws IOException {
		long value = stream.readLong();
		longConstants.add(value);
		return new RealClassConstant<Long>(type, value);
	}

	private RealClassConstant<Double> readDouble(byte type) throws IOException {
		double value = stream.readDouble();
		return new RealClassConstant<Double>(type, value);
	}

	private RealClassConstant<Integer> readClassReference(byte type)
			throws IOException {
		int value = stream.readUnsignedShort();
		return new RealClassConstant<Integer>(type, value);
	}

	private RealClassConstant<Integer> readStringReference(byte type,
			List<Integer> stringIndices) throws IOException {
		int value = stream.readUnsignedShort();
		stringIndices.add(value);
		return new RealClassConstant<Integer>(type, value);
	}

	private RealClassConstant<ReferenceIndex> readAnotherReference(byte type)
			throws IOException {
		ReferenceIndex value = readReferenceIndex();
		return new RealClassConstant<ReferenceIndex>(type, value);
	}

	private int readAccessFlags() throws IOException {
		return stream.readUnsignedShort();
	}

	private void skipThisClass() throws IOException {
		stream.skip(2);
	}

	private void skipSuperClass() throws IOException {
		stream.skip(2);
	}

	private void skipInterfaces() throws IOException {
		stream.skip(2 * stream.readUnsignedShort());
	}

	private RealClassField[] readFields() throws IOException {
		RealClassField[] fields = new RealClassField[stream.readUnsignedShort()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new RealClassField(stream.readUnsignedShort());
			stream.skip(4);
			skipAttributes();
		}
		return fields;
	}

	private int readNumberOfMethodsAndConstructors() throws IOException {
		return stream.readUnsignedShort();
	}

	private int readMethodsAndConstructors(int numberOfMethodsAndConstructors,
			RealClassConstant<?>[] constants, List<ReferenceIndex> methodIndices)
			throws IOException {
		int numberOfConstructors = 0;
		for (int i = 0; i < numberOfMethodsAndConstructors; i++) {
			stream.skip(2);
			ReferenceIndex referenceIndex = readReferenceIndex();
			methodIndices.add(referenceIndex);
			String constant = (String) constants[referenceIndex.getValue1() - 1]
					.getValue();
			if (constant.contains("<init>")) {
				numberOfConstructors++;
			}
			skipAttributes();
		}
		return numberOfConstructors;
	}

	private ReferenceIndex readReferenceIndex() throws IOException {
		int value1 = stream.readUnsignedShort();
		int value2 = stream.readUnsignedShort();
		return new ReferenceIndex(value1, value2);
	}

	private void skipAttributes() throws IOException {
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
