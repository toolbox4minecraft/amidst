package amidst.settings;

import java.util.Objects;
import java.util.prefs.Preferences;

import amidst.documentation.NotNull;
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

	protected void restore(@NotNull T defaultValue) {
		Objects.requireNonNull(defaultValue);
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
	public synchronized void set(@NotNull T value) {
		Objects.requireNonNull(value);
		this.value = value;
		update(value);
	}

	@NotNull
	protected abstract T getInitialValue(@NotNull T defaultValue);

	protected abstract void update(@NotNull T value);
}
