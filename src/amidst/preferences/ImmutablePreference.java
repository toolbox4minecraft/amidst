package amidst.preferences;

import amidst.documentation.Immutable;

@Immutable
public class ImmutablePreference<T> implements PrefModel<T> {
	private final T value;

	public ImmutablePreference(T value) {
		this.value = value;
	}

	@Override
	public String getKey() {
		throw new UnsupportedOperationException(
				"AlwaysTruePreference has no key!");
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		throw new UnsupportedOperationException(
				"AlwaysTruePreference cannot be set!");
	}
}
