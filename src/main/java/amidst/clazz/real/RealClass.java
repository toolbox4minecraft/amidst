package amidst.clazz.real;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import amidst.documentation.Immutable;

@Immutable
public class RealClass {
	private static Map<Character, String> createPrimitiveTypeConversionMap() {
		Map<Character, String> result = new HashMap<>();
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
	private static final Pattern ARG_PATTERN = Pattern.compile("([\\[]+)?([BCDFIJSZ]|L[^;]+)");
	private static final Pattern OBJECT_PATTERN = Pattern.compile("^([\\[]+)?[LBCDFIJSZ]");
	public static final int CLASS_DATA_WILDCARD = -1;

	private final String realClassName;
	private final byte[] classData;

	private final int minorVersion;
	private final int majorVersion;

	private final int cpSize;
	@SuppressWarnings("unused")
	private final int[] constantTypes;
	private final RealClassConstant<?>[] constants;

	private final List<String> utf8Constants;
	private final List<Float> floatConstants;
	private final List<Long> longConstants;
	private final List<Integer> stringIndices;
	private final List<ReferenceIndex> methodIndices;

	private final int accessFlags;
	private final RealClassField[] fields;

	private final int numberOfConstructors;
	private final int numberOfMethods;

	RealClass(
			String realClassName,
			byte[] classData,
			int minorVersion,
			int majorVersion,
			int cpSize,
			int[] constantTypes,
			RealClassConstant<?>[] constants,
			List<String> utf8Constants,
			List<Float> floatConstants,
			List<Long> longConstants,
			List<Integer> stringIndices,
			List<ReferenceIndex> methodIndices,
			int accessFlags,
			RealClassField[] fields,
			int numberOfConstructors,
			int numberOfMethods) {
		this.realClassName = realClassName;
		this.classData = classData;
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.cpSize = cpSize;
		this.constantTypes = constantTypes;
		this.constants = constants;
		this.utf8Constants = utf8Constants;
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

	public boolean isClassDataWildcardMatching(int[] bytes) {
		int loopLimit = classData.length + 1 - bytes.length;
		for (int startIndex = 0; startIndex < loopLimit; startIndex++) {
			if (isClassDataWildcardMatchingAt(startIndex, bytes)) {
				return true;
			}
		}
		return false;
	}

	private boolean isClassDataWildcardMatchingAt(int startIndex, int[] bytes) {
		for (int offset = 0; offset < bytes.length; offset++) {
			if (bytes[offset] != CLASS_DATA_WILDCARD && classData[startIndex + offset] != (byte) bytes[offset]) {
				return false;
			}
		}
		return true;
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

	public boolean searchForUtf8EqualTo(String required) {
		for (String entry : utf8Constants) {
			if (entry.equals(required)) {
				return true;
			}
		}
		return false;
	}

	public boolean searchForFloat(float required) {
		for (Float entry : floatConstants) {
			if (entry.floatValue() == required) {
				return true;
			}
		}
		return false;
	}

	public boolean searchForLong(long required) {
		for (Long entry : longConstants) {
			if (entry.longValue() == required) {
				return true;
			}
		}
		return false;
	}

	public boolean searchForStringContaining(String requiredValue) {
		for (Integer entry : stringIndices) {
			String entryValue = getStringValueOfConstant(entry);
			if (entryValue.contains(requiredValue)) {
				return true;
			}
		}
		return false;
	}

	public String searchByReturnType(String required) {
		String requiredType = "L" + required + ";";
		for (ReferenceIndex entry : methodIndices) {
			String value = getStringValueOfConstant(entry.getValue2());
			String entryType = value.substring(value.indexOf(')') + 1);
			if (entryType.equals(requiredType)) {
				return getStringValueOfConstant(entry.getValue1());
			}
		}
		return null;
	}

	public RealClassField getField(int index) {
		return fields[index];
	}

	public int getAccessFlags() {
		return accessFlags;
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

	public boolean isInterface() {
		return AccessFlags.hasFlags(accessFlags, AccessFlags.INTERFACE);
	}

	public boolean isFinal() {
		return AccessFlags.hasFlags(accessFlags, AccessFlags.FINAL);
	}

	public String getArgumentsForConstructor(int constructorId) {
		int i = 0;
		for (ReferenceIndex entry : methodIndices) {
			if (getStringValueOfConstant(entry.getValue1()).equals("<init>")) {
				if (i == constructorId) {
					String arguments = getStringValueOfConstant(entry.getValue2());
					return toArgumentString(readArguments(arguments));
				}
				i++;
			}
		}
		return "";
	}

	private String getStringValueOfConstant(int value) {
		return (String) constants[value - 1].getValue();
	}

	private String[] readArguments(String arguments) {
		List<String> result = new ArrayList<>();
		String args = arguments.substring(1).split("\\)")[0];
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
		return arg.substring(0, Math.max(0, matcherEnd - 1)) + getPrimitiveType(arg.charAt(matcherEnd - 1))
				+ arg.substring(Math.min(matcherEnd, arg.length()));
	}

	private String getPrimitiveType(char typeChar) {
		if (PRIMITIV_TYPE_CONVERSION_MAP.containsKey(typeChar)) {
			return PRIMITIV_TYPE_CONVERSION_MAP.get(typeChar);
		} else {
			return "";
		}
	}

	private String toArgumentString(String[] arguments) {
		StringBuilder result = new StringBuilder();
		result.append("(");
		if (arguments.length > 0) {
			result.append(arguments[0]);
			for (int i = 1; i < arguments.length; i++) {
				result.append(",").append(arguments[i]);
			}
		}
		result.append(")");
		return result.toString();
	}

	@Override
	public String toString() {
		return "[RealClass " + realClassName + "]";
	}
}
