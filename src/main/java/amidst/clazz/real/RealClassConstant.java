package amidst.clazz.real;

import amidst.documentation.Immutable;

@Immutable
public class RealClassConstant<T> {
	@Immutable
	public static enum RealClassConstantType {
		;

		public static final int STRING = 1;
		public static final int INTEGER = 3;
		public static final int FLOAT = 4;
		public static final int LONG = 5;
		public static final int DOUBLE = 6;
		public static final int CLASS_REFERENCE = 7;
		public static final int STRING_REFERENCE = 8;
		public static final int FIELD_REFERENCE = 9;
		public static final int METHOD_REFERENCE = 10;
		public static final int INTERFACE_METHOD_REFERENCE = 11;
		public static final int NAME_AND_TYPE_DESCRIPTOR = 12;
		public static final int METHOD_HANDLE = 15;
		public static final int METHOD_TYPE = 16;
		public static final int INVOKE_DYNAMIC = 18;

		public static boolean isQIncreasing(byte type) {
			return type == LONG || type == DOUBLE;
		}
	}

	private final byte type;
	private final T value;

	public RealClassConstant(byte type, T value) {
		this.type = type;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public T getValue() {
		return value;
	}
}
