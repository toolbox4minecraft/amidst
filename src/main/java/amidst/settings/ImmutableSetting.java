package amidst.settings;

import amidst.documentation.Immutable;

@Immutable
public class ImmutableSetting<T> implements Setting<T> {
	private final T value;

	public ImmutableSetting(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		throw new UnsupportedOperationException("ImmutableSetting cannot be modified!");
	}
}
