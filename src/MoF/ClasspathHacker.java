package MoF;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/** Useful class for dynamically changing the classpath, adding classes during runtime. 
 * @author unknown
 */
public class ClasspathHacker {
	/** Adds a file to the classpath
	 * @param f the file to be added
	 * @throws IOException
	 */
	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}
	
	/** Adds the content pointed by the URL to the classpath.
	 * @param u the URL pointing to the content to be added
	 * @throws IOException
	 */
	public static void addURL(URL u) throws IOException {
		try {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(ClassLoader.getSystemClassLoader(), u); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}