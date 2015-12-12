package amidst.preferences;

import java.util.prefs.Preferences;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class StringPreference implements PrefModel<String> {
	private final Preferences preferences;
	private final String key;
	private volatile String value;

	public StringPreference(Preferences preferences, String key,
			String defaultValue) {
		this.preferences = preferences;
		this.key = key;
		restore(defaultValue);
	}

	private void restore(String defaultValue) {
		set(preferences.get(key, defaultValue));
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String get() {
		return value;
	}

	@Override
	public synchronized void set(String value) {
		this.value = value;
		this.preferences.put(key, value);
	}
}
