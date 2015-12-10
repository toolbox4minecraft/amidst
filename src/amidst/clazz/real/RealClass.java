package amidst.clazz.real;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealClass {
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
	private final RealClassConstant<?>[] constants;

	private final List<String> utfConstants;
	private final List<Float> floatConstants;
	private final List<Long> longConstants;
	private final List<Integer> stringIndices;
	private final List<ReferenceIndex> methodIndices;

	private final int accessFlags;
	private final RealClassField[] fields;

	private final int numberOfConstructors;
	private final int numberOfMethods;

	RealClass(String realClassName, byte[] classData, int minorVersion,
			int majorVersion, int cpSize, int[] constantTypes,
			RealClassConstant<?>[] constants, List<String> utfConstants,
			List<Float> floatConstants, List<Long> longConstants,
			List<Integer> stringIndices, List<ReferenceIndex> methodIndices,
			int accessFlags, RealClassField[] fields, int numberOfConstructors,
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

	public String getRealClassName() {
		return realClassName;
	}

	public byte[] getClassData() {
		return classData;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getCpSize() {
		return cpSize;
	}

	public int[] getConstantTypes() {
		return constantTypes;
	}

	public RealClassConstant<?>[] getConstants() {
		return constants;
	}

	public boolean searchForUtf(String str) {
		for (String text : utfConstants) {
			if (text.equals(str))
				return true;
		}
		return false;
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

	public String searchByReturnType(String type) {
		for (ReferenceIndex ref : methodIndices) {
			String refType = (String) constants[ref.getValue2() - 1].getValue();
			if (("L" + type + ";").equals(refType.substring(refType
					.indexOf(')') + 1)))
				return (String) constants[ref.getValue1() - 1].getValue();
		}
		return null;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public RealClassField[] getFields() {
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

	public RealClassField getField(int index) {
		return fields[index];
	}

	public boolean isInterface() {
		return AccessFlags.hasFlags(accessFlags, AccessFlags.INTERFACE);
	}

	public boolean isFinal() {
		return AccessFlags.hasFlags(accessFlags, AccessFlags.FINAL);
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

	@Override
	public String toString() {
		return "[RealClass " + realClassName + "]";
	}
}
