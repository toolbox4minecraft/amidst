package amidst.settings;

import java.util.function.Supplier;

import amidst.documentation.NotNull;

public interface Setting<T> extends Supplier<T> {
	@Override
	@NotNull
	T get();

	void set(@NotNull T value);
}
