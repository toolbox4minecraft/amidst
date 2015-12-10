package amidst.clazz.real;

import amidst.documentation.Immutable;

@Immutable
public class ClassConstant<T> {
	private final byte type;
	private final long offset;
	private final T value;

	public ClassConstant(byte type, long offset, T value) {
		this.type = type;
		this.offset = offset;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public long getOffset() {
		return offset;
	}

	public T getValue() {
		return value;
	}
}
