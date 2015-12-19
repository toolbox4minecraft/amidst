package amidst.settings;

public interface Setting<T> {
	String getKey();

	T get();

	void set(T value);
}
