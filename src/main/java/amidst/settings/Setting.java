package amidst.settings;

import java.util.function.Supplier;

public interface Setting<T> extends Supplier<T> {
	T get();

	void set(T value);
}
