package amidst.clazz.real;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteClass {
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

	public static class Field {
		private int accessFlags;

		public boolean hasFlags(int flags) {
			return ByteClass.hasFlags(accessFlags, flags);
		}
	}

	private static class Builder {
		private DataInputStream stream;
		private ByteClass product;
		private long offset;

		private Builder(ByteClassFactory factory, String realClassName,
				byte[] classData) {
			product = new ByteClass(realClassName, classData);
			product.primitiveTypeConversionMap = factory.primitiveTypeConversionMap;
			product.argRegex = factory.argRegex;
			product.objectRegex = factory.objectRegex;
			try {
				stream = createStream();
				if (isValidClass()) {
					readVersion();
					readConstants();
					readAccessFlag();
					skipThisClass();
					skipSuperClass();
					skipInterfaces();
					readFields();
					readMethods();
					readAttributes();
				}
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		public ByteClass get() {
			return product;
		}

		private DataInputStream createStream() {
			return new DataInputStream(new ByteArrayInputStream(
					product.classData));
		}

		private boolean isValidClass() throws IOException {
			return stream.readInt() == 0xCAFEBABE;
		}

		private void readAttributes() {
		}

		private void readMethods() throws IOException {
			product.methodAndConstructorCount = stream.readUnsignedShort();
			for (int i = 0; i < product.methodAndConstructorCount; i++) {
				stream.skip(2);
				int nameIndex = stream.readUnsignedShort();
				product.methodIndices.add(new ReferenceIndex(nameIndex, stream
						.readUnsignedShort()));

				if (((String) product.constants[nameIndex - 1].getValue())
						.contains("<init>")) {
					product.constructorCount++;
				}

				int attributeInfoCount = stream.readUnsignedShort();
				for (int q = 0; q < attributeInfoCount; q++) {
					stream.skip(2);
					int attributeCount = stream.readInt();
					for (int z = 0; z < attributeCount; z++)
						stream.skip(1);
				}
			}
		}

		private void readFields() throws IOException {
			product.fields = new Field[stream.readUnsignedShort()];
			for (int i = 0; i < product.fields.length; i++) {
				product.fields[i] = new Field();
				product.fields[i].accessFlags = stream.readUnsignedShort();
				stream.skip(4);
				int attributeInfoCount = stream.readUnsignedShort();
				for (int q = 0; q < attributeInfoCount; q++) {
					stream.skip(2);
					int attributeCount = stream.readInt();
					for (int z = 0; z < attributeCount; z++)
						stream.skip(1);
				}
			}
		}

		private void skipInterfaces() throws IOException {
			int iCount = stream.readUnsignedShort();
			stream.skip(iCount * 2);
		}

		private void skipSuperClass() throws IOException {
			stream.skip(2);
		}

		private void skipThisClass() throws IOException {
			stream.skip(2);
		}

		private void readAccessFlag() throws IOException {
			product.accessFlags = stream.readUnsignedShort();
		}

		private void readVersion() throws IOException {
			product.minorVersion = stream.readUnsignedShort();
			product.majorVersion = stream.readUnsignedShort();
		}

		private void readConstants() throws IOException {
			product.cpSize = stream.readUnsignedShort() - 1;
			product.constants = new ClassConstant<?>[product.cpSize];
			product.constantTypes = new int[product.cpSize];
			offset = 10;
			for (int q = 0; q < product.cpSize; q++) {
				q = readConstant(q);
			}
		}

		private int readConstant(int q) throws IOException {
			byte tag = stream.readByte();
			offset++;
			product.constantTypes[q] = tag;
			switch (tag) {
			case 1:
				readString(q, tag);
				break;
			case 3:
				readInteger(q, tag);
				break;
			case 4:
				readFloat(q, tag);
				break;
			case 5:
				readLong(q, tag);
				q++;
				break;
			case 6:
				readDouble(q, tag);
				q++;
				break;
			case 7:
				readClassReference(q, tag);
				break;
			case 8:
				readStringReference(q, tag);
				break;
			case 9: // Field reference
				readAnotherReference(q, tag);
				break;
			case 10: // Method reference
				readAnotherReference(q, tag);
				break;
			case 11: // Interface method reference
				readAnotherReference(q, tag);
				break;
			case 12: // Name and type descriptor
				readAnotherReference(q, tag);
				break;
			}
			return q;
		}

		private void readAnotherReference(int q, byte tag) throws IOException {
			ReferenceIndex referenceIndex = new ReferenceIndex(
					stream.readUnsignedShort(), stream.readUnsignedShort());
			product.constants[q] = new ClassConstant<ReferenceIndex>(tag,
					offset, referenceIndex);
			offset += 4;
		}

		private void readStringReference(int q, byte tag) throws IOException {
			ClassConstant<Integer> strRef = new ClassConstant<Integer>(tag,
					offset, stream.readUnsignedShort());
			product.constants[q] = strRef;
			product.stringIndices.add(strRef);
			offset += 2;
		}

		private void readClassReference(int q, byte tag) throws IOException {
			product.constants[q] = new ClassConstant<Integer>(tag, offset,
					stream.readUnsignedShort());
			offset += 2;
		}

		private void readDouble(int q, byte tag) throws IOException {
			product.constants[q] = new ClassConstant<Double>(tag, offset,
					stream.readDouble());
			offset += 8;
		}

		private void readLong(int q, byte tag) throws IOException {
			long cLong = stream.readLong();
			product.constants[q] = new ClassConstant<Long>(tag, offset, cLong);
			product.longConstants.add(cLong);
			offset += 8;
		}

		private void readFloat(int q, byte tag) throws IOException {
			float cFloat = stream.readFloat();
			product.constants[q] = new ClassConstant<Float>(tag, offset, cFloat);
			product.floatConstants.add(cFloat);
			offset += 4;
		}

		private void readInteger(int q, byte tag) throws IOException {
			product.constants[q] = new ClassConstant<Integer>(tag, offset,
					stream.readInt());
			offset += 4;
		}

		private void readString(int q, byte tag) throws IOException {
			int len = stream.readUnsignedShort();
			String stringValue = readStringValue(len);
			product.constants[q] = new ClassConstant<String>(tag, offset,
					stringValue);
			product.utfConstants.add(stringValue);
			offset += 2 + len;
		}

		private String readStringValue(int len) throws IOException {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < len; i++) {
				stringBuilder.append((char) stream.readByte());
			}
			return stringBuilder.toString();
		}
	}

	public static enum ByteClassFactory {
		INSTANCE;

		private Map<Character, String> primitiveTypeConversionMap = new HashMap<Character, String>();
		private Pattern argRegex = Pattern
				.compile("([\\[]+)?([BCDFIJSZ]|L[^;]+)");
		private Pattern objectRegex = Pattern.compile("^([\\[]+)?[LBCDFIJSZ]");

		private ByteClassFactory() {
			primitiveTypeConversionMap.put('B', "byte");
			primitiveTypeConversionMap.put('C', "char");
			primitiveTypeConversionMap.put('D', "double");
			primitiveTypeConversionMap.put('F', "float");
			primitiveTypeConversionMap.put('I', "int");
			primitiveTypeConversionMap.put('J', "long");
			primitiveTypeConversionMap.put('S', "short");
			primitiveTypeConversionMap.put('Z', "boolean");
		}

		public ByteClass create(String realClassName, byte[] classData) {
			return new Builder(this, realClassName, classData).get();
		}
	}

	public static ByteClass newInstance(String realClassName, byte[] classData) {
		return ByteClassFactory.INSTANCE.create(realClassName, classData);
	}

	private static boolean hasFlags(int accessFlags, int flags) {
		return (accessFlags & flags) == flags;
	}

	private byte[] classData;
	private int cpSize;
	private int[] constantTypes;
	private int minorVersion;
	private int majorVersion;
	private ClassConstant<?>[] constants;
	private String realClassName;
	private int accessFlags;

	private List<ClassConstant<Integer>> stringIndices = new ArrayList<ClassConstant<Integer>>();
	private List<ReferenceIndex> methodIndices = new ArrayList<ReferenceIndex>();
	private List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	private List<PropertyDeclaration> properties = new ArrayList<PropertyDeclaration>();
	private List<Float> floatConstants = new ArrayList<Float>();
	private List<Long> longConstants = new ArrayList<Long>();
	private List<String> utfConstants = new ArrayList<String>();

	private Field[] fields;
	private int methodAndConstructorCount;
	private int constructorCount;

	private Map<Character, String> primitiveTypeConversionMap;
	private Pattern argRegex;
	private Pattern objectRegex;

	private ByteClass(String realClassName, byte[] classData) {
		this.realClassName = realClassName;
		this.classData = classData;
	}

	public int getCpSize() {
		return cpSize;
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

	public int getMethodAndConstructorCount() {
		return methodAndConstructorCount;
	}

	public int getConstructorCount() {
		return constructorCount;
	}

	public byte[] getClassData() {
		return classData;
	}

	public String getRealClassName() {
		return realClassName;
	}

	public List<ConstructorDeclaration> getConstructors() {
		return constructors;
	}

	public List<MethodDeclaration> getMethods() {
		return methods;
	}

	public List<PropertyDeclaration> getProperties() {
		return properties;
	}

	public void addConstructor(ConstructorDeclaration constructor) {
		constructors.add(constructor);
	}

	public void addMethod(MethodDeclaration method) {
		methods.add(method);
	}

	public void addProperty(PropertyDeclaration property) {
		properties.add(property);
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

	public String getArguementsForConstructor(int ID) {
		int i = 0;
		for (ReferenceIndex ref : methodIndices) {
			String name = (String) constants[ref.getVal1() - 1].getValue();
			if (name.equals("<init>")) {
				if (i == ID) {
					String args = (String) constants[ref.getVal2() - 1]
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
		Matcher matcher = argRegex.matcher(args);
		while (matcher.find()) {
			String arg = args.substring(matcher.start(), matcher.end());
			Matcher objectMatcher = objectRegex.matcher(arg);
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
		if (primitiveTypeConversionMap.containsKey(typeChar)) {
			return primitiveTypeConversionMap.get(typeChar);
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
		for (ClassConstant<Integer> i : stringIndices) {
			if (((String) constants[i.getValue() - 1].getValue()).contains(str))
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
			String refType = (String) constants[ref.getVal2() - 1].getValue();
			if (("L" + type + ";").equals(refType.substring(refType
					.indexOf(')') + 1)))
				return (String) constants[ref.getVal1() - 1].getValue();
		}
		return null;
	}

	@Override
	public String toString() {
		return "[ByteClass " + realClassName + "]";
	}
}
