package amidst.settings;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class SettingBase<T> implements Setting<T> {
	private final Consumer<T> setter;
	private volatile T value;

	public SettingBase(T defaultValue, UnaryOperator<T> getter, Consumer<T> setter) {
		Objects.requireNonNull(defaultValue);
		this.setter = setter;
		this.set(getter.apply(defaultValue));
	}

	@Override
	public synchronized T get() {
		return value;
	}

	@Override
	public synchronized void set(T value) {
		Objects.requireNonNull(value);
		this.value = value;
		setter.accept(value);
	}
}
