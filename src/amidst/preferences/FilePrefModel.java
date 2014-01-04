package amidst.preferences;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

/** Autosaving File model
 */
public class FilePrefModel implements PrefModel<File> {
	private final String key;
	private final Preferences pref;
	
	public FilePrefModel(Preferences pref, String key, File init) {
		super();
		this.pref = pref;
		this.key = key;
		if (pref.get(key, null) == null)
			set(init);
	}

	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public File get() {
		String path = pref.get(key, null);
		assert path != null;
		return new File(path);
	}
	
	@Override
	public void set(File value) {
		try {
			pref.put(key, value.getCanonicalPath());
		} catch (IOException ignored) {
			pref.put(key, value.getPath());
		}
	}
}