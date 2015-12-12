package amidst.preferences;

import java.util.prefs.Preferences;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public abstract class PrefModelBase<T> implements PrefModel<T> {
	protected final Preferences preferences;
	protected final String key;
	private volatile T value;

	public PrefModelBase(Preferences preferences, String key, T defaultValue) {
		this.preferences = preferences;
		this.key = key;
		restore(defaultValue);
	}

	private void restore(T defaultValue) {
		set(getInitialValue(defaultValue));
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public synchronized void set(T value) {
		this.value = value;
		update(value);
	}

	protected abstract T getInitialValue(T defaultValue);

	protected abstract void update(T value);
}
