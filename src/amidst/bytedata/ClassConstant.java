package amidst.bytedata;

public class ClassConstant<T> {
	private byte type;
	private long offset;
	private T value;

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
