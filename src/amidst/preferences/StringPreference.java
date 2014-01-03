package amidst.preferences;

import java.util.prefs.Preferences;

public class StringPreference {
	private Preferences preferences;
	private String key;
	private String value;
	
	public StringPreference(Preferences preferences, String key, String defaultValue) {
		this.preferences = preferences;
		this.key = key;
		value = preferences.get(key,  defaultValue);
	}
	
	public String get() {
		return value;
	}
	
	public void set(String value) {
		preferences.put(key,  value);
	}
}
