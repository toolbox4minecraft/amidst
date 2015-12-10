package amidst.clazz.real;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import amidst.documentation.Immutable;

public class RealClass {
	/*-
	 * 	ACC_PUBLIC	 	0x0001	 Declared public; may be accessed from outside its package.
	 *	ACC_PRIVATE	 	0x0002	 Declared private; usable only within the defining class.
	 *	ACC_PROTECTED	0x0004	 Declared protected; may be accessed within subclasses.
	 *	ACC_STATIC	 	0x0008	 Declared static.
	 *	ACC_FINAL	 	0x0010	 Declared final; no further assignment after initialization.
	 *	ACC_VOLATILE	0x0040	 Declared volatile; cannot be cached.
	 *	ACC_TRANSIENT	0x0080	 Declared transient; not written or read by a persistent object manager.
	 *	ACC_INTERFACE	0x0200	 Is an interface, not a class.
	 **/
	@Immutable
	public static class AccessFlags {
		public static final int PUBLIC = 0x01;
		public static final int PRIVATE = 0x02;
		public static final int PROTECTED = 0x04;
		public static final int STATIC = 0x08;
		public static final int FINAL = 0x10;
		public static final int VOLATILE = 0x40;
		public static final int TRANSIENT = 0x80;
		public static final int INTERFACE = 0x0200;
	}

	@Immutable
	private static class ConstantType {
		private static final int STRING = 1;
		private static final int INTEGER = 3;
		private static final int FLOAT = 4;
		private static final int LONG = 5;
		private static final int DOUBLE = 6;
		private static final int CLASS_REFERENCE = 7;
		private static final int STRING_REFERENCE = 8;
		private static final int FIELD_REFERENCE = 9;
		private static final int METHOD_REFERENCE = 10;
		private static final int INTERFACE_METHOD_REFERENCE = 11;
		private static final int NAME_AND_TYPE_DESCRIPTOR = 12;

		private static final List<Integer> Q_INCREASING_TYPES = Arrays.asList(
				LONG, DOUBLE);
	}

	@Immutable
	public static class Field {
		private final int accessFlags;

		public Field(int accessFlags) {
			this.accessFlags = accessFlags;
		}

		public boolean hasFlags(int flags) {
			return RealClass.hasFlags(accessFlags, flags);
		}
	}

	private static class Builder {
		private final String realClassName;
		private final byte[] classData;
		private final DataInputStream stream;

		private Builder(String realClassName, byte[] classData) {
			this.realClassName = realClassName;
			this.classData = classData;
			this.stream = new DataInputStream(new ByteArrayInputStream(
					classData));
		}

		public RealClass construct() {
			RealClass product = null;
			try {
				if (isValidClass()) {
					int minorVersion = readMinorVersion();
					int majorVersion = readMajorVersion();
					int cpSize = readCpSize();
					int[] constantTypes = new int[cpSize];
					ClassConstant<?>[] constants = new ClassConstant<?>[cpSize];
					List<String> utfConstants = new ArrayList<String>();
					List<Float> floatConstants = new ArrayList<Float>();
					List<Long> longConstants = new ArrayList<Long>();
					List<Integer> stringIndices = new ArrayList<Integer>();
					List<ReferenceIndex> methodIndices = new ArrayList<ReferenceIndex>();
					readConstants(cpSize, constantTypes, constants,
							utfConstants, floatConstants, longConstants,
							stringIndices);
					int accessFlags = readAccessFlags();
					skipThisClass();
					skipSuperClass();
					skipInterfaces();
					Field[] fields = readFields();
					int numberOfMethodsAndConstructors = readNumberOfMethodsAndConstructors();
					int numberOfConstructors = readMethodsAndConstructors(
							numberOfMethodsAndConstructors, constants,
							methodIndices);
					int numberOfMethods = numberOfMethodsAndConstructors
							- numberOfConstructors;
					product = new RealClass(realClassName, classData,
							minorVersion, majorVersion, cpSize, constantTypes,
							constants, utfConstants, floatConstants,
							longConstants, stringIndices, methodIndices,
							accessFlags, fields, numberOfConstructors,
							numberOfMethods);
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
				ClassConstant<?>[] constants, List<String> utfConstants,
				List<Float> floatConstants, List<Long> longConstants,
				List<Integer> stringIndices) throws IOException {
			for (int q = 0; q < cpSize; q++) {
				byte type = stream.readByte();
				constantTypes[q] = type;
				constants[q] = readConstant(type, utfConstants, floatConstants,
						longConstants, stringIndices);
				if (ConstantType.Q_INCREASING_TYPES.contains(type)) {
					q++;
				}
			}
		}

		private ClassConstant<?> readConstant(byte type,
				List<String> utfConstants, List<Float> floatConstants,
				List<Long> longConstants, List<Integer> stringIndices)
				throws IOException {
			switch (type) {
			case ConstantType.STRING:
				return readString(type, utfConstants);
			case ConstantType.INTEGER:
				return readInteger(type);
			case ConstantType.FLOAT:
				return readFloat(type, floatConstants);
			case ConstantType.LONG:
				return readLong(type, longConstants);
			case ConstantType.DOUBLE:
				return readDouble(type);
			case ConstantType.CLASS_REFERENCE:
				return readClassReference(type);
			case ConstantType.STRING_REFERENCE:
				return readStringReference(type, stringIndices);
			case ConstantType.FIELD_REFERENCE:
				return readAnotherReference(type);
			case ConstantType.METHOD_REFERENCE:
				return readAnotherReference(type);
			case ConstantType.INTERFACE_METHOD_REFERENCE:
				return readAnotherReference(type);
			case ConstantType.NAME_AND_TYPE_DESCRIPTOR:
				return readAnotherReference(type);
			default:
				return null;
			}
		}

		private ClassConstant<String> readString(byte type,
				List<String> utfConstants) throws IOException {
			String value = readStringValue();
			utfConstants.add(value);
			return new ClassConstant<String>(type, value);
		}

		private String readStringValue() throws IOException {
			char[] result = new char[stream.readUnsignedShort()];
			for (int i = 0; i < result.length; i++) {
				result[i] = (char) stream.readByte();
			}
			return new String(result);
		}

		private ClassConstant<Integer> readInteger(byte type)
				throws IOException {
			int value = stream.readInt();
			return new ClassConstant<Integer>(type, value);
		}

		private ClassConstant<Float> readFloat(byte type,
				List<Float> floatConstants) throws IOException {
			float value = stream.readFloat();
			floatConstants.add(value);
			return new ClassConstant<Float>(type, value);
		}

		private ClassConstant<Long> readLong(byte type, List<Long> longConstants)
				throws IOException {
			long value = stream.readLong();
			longConstants.add(value);
			return new ClassConstant<Long>(type, value);
		}

		private ClassConstant<Double> readDouble(byte type) throws IOException {
			double value = stream.readDouble();
			return new ClassConstant<Double>(type, value);
		}

		private ClassConstant<Integer> readClassReference(byte type)
				throws IOException {
			int value = stream.readUnsignedShort();
			return new ClassConstant<Integer>(type, value);
		}

		private ClassConstant<Integer> readStringReference(byte type,
				List<Integer> stringIndices) throws IOException {
			int value = stream.readUnsignedShort();
			stringIndices.add(value);
			return new ClassConstant<Integer>(type, value);
		}

		private ClassConstant<ReferenceIndex> readAnotherReference(byte type)
				throws IOException {
			ReferenceIndex value = readReferenceIndex();
			return new ClassConstant<ReferenceIndex>(type, value);
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

		private Field[] readFields() throws IOException {
			Field[] fields = new Field[stream.readUnsignedShort()];
			for (int i = 0; i < fields.length; i++) {
				fields[i] = new Field(stream.readUnsignedShort());
				stream.skip(4);
				skipAttributes();
			}
			return fields;
		}

		private int readNumberOfMethodsAndConstructors() throws IOException {
			return stream.readUnsignedShort();
		}

		private int readMethodsAndConstructors(
				int numberOfMethodsAndConstructors,
				ClassConstant<?>[] constants, List<ReferenceIndex> methodIndices)
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

	public static RealClass newInstance(String realClassName, byte[] classData) {
		return new Builder(realClassName, classData).construct();
	}

	private static boolean hasFlags(int accessFlags, int flags) {
		return (accessFlags & flags) == flags;
	}

	private static Map<Character, String> createPrimitiveTypeConversionMap() {
		Map<Character, String> result = new HashMap<Character, String>();
		result.put('B', "byte");
		result.put('C', "char");
		result.put('D', "double");
		result.put('F', "float");
		result.put('I', "int");
		result.put('J', "long");
		result.put('S', "short");
		result.put('Z', "boolean");
		return Collections.unmodifiableMap(result);
	}

	private static final Map<Character, String> PRIMITIV_TYPE_CONVERSION_MAP = createPrimitiveTypeConversionMap();
	private static final Pattern ARG_PATTERN = Pattern
			.compile("([\\[]+)?([BCDFIJSZ]|L[^;]+)");
	private static final Pattern OBJECT_PATTERN = Pattern
			.compile("^([\\[]+)?[LBCDFIJSZ]");

	private final String realClassName;
	private final byte[] classData;

	private final int minorVersion;
	private final int majorVersion;

	private final int cpSize;
	private final int[] constantTypes;
	private final ClassConstant<?>[] constants;

	private final List<String> utfConstants;
	private final List<Float> floatConstants;
	private final List<Long> longConstants;
	private final List<Integer> stringIndices;
	private final List<ReferenceIndex> methodIndices;

	private final int accessFlags;
	private final Field[] fields;

	private final int numberOfConstructors;
	private final int numberOfMethods;

	private RealClass(String realClassName, byte[] classData, int minorVersion,
			int majorVersion, int cpSize, int[] constantTypes,
			ClassConstant<?>[] constants, List<String> utfConstants,
			List<Float> floatConstants, List<Long> longConstants,
			List<Integer> stringIndices, List<ReferenceIndex> methodIndices,
			int accessFlags, Field[] fields, int numberOfConstructors,
			int numberOfMethods) {
		this.realClassName = realClassName;
		this.classData = classData;
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.cpSize = cpSize;
		this.constantTypes = constantTypes;
		this.constants = constants;
		this.utfConstants = utfConstants;
		this.floatConstants = floatConstants;
		this.longConstants = longConstants;
		this.stringIndices = stringIndices;
		this.methodIndices = methodIndices;
		this.accessFlags = accessFlags;
		this.fields = fields;
		this.numberOfConstructors = numberOfConstructors;
		this.numberOfMethods = numberOfMethods;
	}

	public int[] getConstantTypes() {
		return constantTypes;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public ClassConstant<?>[] getConstants() {
		return constants;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public Field[] getFields() {
		return fields;
	}

	public int getNumberOfConstructors() {
		return numberOfConstructors;
	}

	public int getNumberOfMethods() {
		return numberOfMethods;
	}

	public int getNumberOfFields() {
		return fields.length;
	}

	public byte[] getClassData() {
		return classData;
	}

	public String getRealClassName() {
		return realClassName;
	}

	public Field getField(int index) {
		return fields[index];
	}

	public boolean isInterface() {
		return hasFlags(accessFlags, AccessFlags.INTERFACE);
	}

	public boolean isFinal() {
		return hasFlags(accessFlags, AccessFlags.FINAL);
	}

	public String getArgumentsForConstructor(int ID) {
		int i = 0;
		for (ReferenceIndex ref : methodIndices) {
			String name = (String) constants[ref.getValue1() - 1].getValue();
			if (name.equals("<init>")) {
				if (i == ID) {
					String args = (String) constants[ref.getValue2() - 1]
							.getValue();
					return toArgumentString(readArguments(args));
				}
				i++;
			}
		}
		return "";
	}

	private String toArgumentString(String[] args) {
		StringBuilder result = new StringBuilder();
		result.append("(");
		if (args.length > 0) {
			result.append(args[0]);
			for (int i = 1; i < args.length; i++) {
				result.append(",").append(args[i]);
			}
		}
		result.append(")");
		return result.toString();
	}

	private String[] readArguments(String eArgs) {
		List<String> result = new ArrayList<String>();
		String args = eArgs.substring(1).split("\\)")[0];
		Matcher matcher = ARG_PATTERN.matcher(args);
		while (matcher.find()) {
			String arg = args.substring(matcher.start(), matcher.end());
			Matcher objectMatcher = OBJECT_PATTERN.matcher(arg);
			if (objectMatcher.find()) {
				arg = getObjectArg(arg, objectMatcher.end());
			}
			result.add(arg);
		}
		return result.toArray(new String[result.size()]);
	}

	private String getObjectArg(String arg, int matcherEnd) {
		return arg.substring(0, Math.max(0, matcherEnd - 1))
				+ getPrimitiveType(arg.charAt(matcherEnd - 1))
				+ arg.substring(Math.min(matcherEnd, arg.length()));
	}

	private String getPrimitiveType(char typeChar) {
		if (PRIMITIV_TYPE_CONVERSION_MAP.containsKey(typeChar)) {
			return PRIMITIV_TYPE_CONVERSION_MAP.get(typeChar);
		} else {
			return "";
		}
	}

	public boolean searchForFloat(float f) {
		for (Float cFloat : floatConstants) {
			if (cFloat.floatValue() == f) {
				return true;
			}
		}
		return false;
	}

	public boolean searchForLong(long l) {
		for (Long cLong : longConstants) {
			if (cLong.longValue() == l) {
				return true;
			}
		}
		return false;
	}

	public boolean searchForString(String str) {
		for (Integer i : stringIndices) {
			if (((String) constants[i - 1].getValue()).contains(str))
				return true;
		}
		return false;
	}

	public boolean searchForUtf(String str) {
		for (String text : utfConstants) {
			if (text.equals(str))
				return true;
		}
		return false;
	}

	public String searchByReturnType(String type) {
		for (ReferenceIndex ref : methodIndices) {
			String refType = (String) constants[ref.getValue2() - 1].getValue();
			if (("L" + type + ";").equals(refType.substring(refType
					.indexOf(')') + 1)))
				return (String) constants[ref.getValue1() - 1].getValue();
		}
		return null;
	}

	@Override
	public String toString() {
		return "[RealClass " + realClassName + "]";
	}
}
