package amidst.settings;

import java.util.prefs.Preferences;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public abstract class SettingBase<T> implements Setting<T> {
	protected final Preferences preferences;
	protected final String key;
	private volatile T value;

	public SettingBase(Preferences preferences, String key) {
		this.preferences = preferences;
		this.key = key;
	}

	protected void restore(T defaultValue) {
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
