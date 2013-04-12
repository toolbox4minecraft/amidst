package amidst.preferences;

import java.io.IOException;

/** Backed by a Preferences instance, saves and loads its value from and to it.
 * TODO: test the fuck out of this
 */
public interface PrefModel<T> {
	String getKey();
	
	public T get();
	public void set(T value) throws IOException;
}
