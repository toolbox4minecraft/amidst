package amidst.preferences;

public interface PrefModel<T> {
	String getKey();

	T get();

	void set(T value);
}
