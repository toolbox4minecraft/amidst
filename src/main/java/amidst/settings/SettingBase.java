package amidst.settings;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.prefs.Preferences;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;

@ThreadSafe
public abstract class SettingBase<T> implements Setting<T> {
	private final ConcurrentLinkedQueue<Runnable> listeners = new ConcurrentLinkedQueue<Runnable>();
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

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public void removeListener(Runnable listener) {
		listeners.remove(listener);
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(@NotNull T value) {
		Objects.requireNonNull(value);
		synchronized (this) {
			this.value = value;
			doSet(value);
		}
		for (Runnable listener : listeners) {
			listener.run();
		}
	}

	@NotNull
	protected abstract T getInitialValue(@NotNull T defaultValue);

	protected abstract void doSet(@NotNull T value);
}
