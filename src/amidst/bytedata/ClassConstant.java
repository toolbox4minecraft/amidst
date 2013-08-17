package amidst.bytedata;

public class ClassConstant<T> {
	private T value;
	private byte type;
	public ClassConstant(byte type, long offset, T value) {
		this.value = value;
		this.type = type;
	}
	public T get() {
		return value;
	}
	public int getTag() {
		return type;
	}
}
