package amidst.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import amidst.documentation.NotNull;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class Lazy<T> {
	public static <T> Lazy<T> fromValue(T value) {
		Objects.requireNonNull(value, VALUE_NULL_ERROR);
		return new Lazy<>(() -> value);
	}

	public static <T> Lazy<T> from(Supplier<T> supplier) {
		Objects.requireNonNull(supplier, SUPPLIER_NULL_ERROR);
		return new Lazy<>(supplier);
	}

	private static final String VALUE_NULL_ERROR = "the value of a lazy cannot be null";
	private static final String SUPPLIER_NULL_ERROR = "the supplier of a lazy cannot be null";

	private final Supplier<T> supplier;
	private T value;

	public Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@NotNull
	public T getOrCreateValue() {
		if (value == null) {
			value = supplier.get();
			Objects.requireNonNull(value, VALUE_NULL_ERROR);
		}
		return value;
	}

	public void replaceWithValue(Function<T, T> replacer) {
		value = replacer.apply(getOrCreateValue());
	}

	public void ifInitialized(Consumer<T> consumer) {
		if (value != null) {
			consumer.accept(value);
		}
	}

	public void setToValue(T value) {
		this.value = value;
	}
}
