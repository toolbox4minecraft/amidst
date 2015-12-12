package amidst.preferences;

import java.util.prefs.Preferences;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class StringPreference {
	private final Preferences preferences;
	private final String key;
	private final String defaultValue;

	public StringPreference(Preferences preferences, String key,
			String defaultValue) {
		this.preferences = preferences;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String get() {
		return preferences.get(key, defaultValue);
	}

	public void set(String value) {
		preferences.put(key, value);
	}
}
