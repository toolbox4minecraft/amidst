package amidst.clazz.real;

import amidst.documentation.Immutable;

@Immutable
public class ClassConstant<T> {
	private final byte type;
	private final T value;

	public ClassConstant(byte type, T value) {
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
