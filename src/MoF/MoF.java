
package MoF;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class MoF {
	public static double version_major = 2.0;
	public static int version_minor = 44;
	public static String biomeName;
	public static String intCacheName;
	public static String chunkName;
	public static String version;
	public static int versionID;
	
	public static boolean NETHER = true;
	public static boolean DEBUG = true;
	public static boolean DISABLE_SAVE = false;
	
	public static String stuff = "";
	
	static {
		stuff += "Hey, if you actually bothered to decompile this code\n";
		stuff += "I just want to say sorry-- it's so messy and ugly :(\n";
		stuff += "I mean... really.. I'm sorry.\n";
		stuff += "....I cry a little bit every time I make an update...\n";
	}
	
	public static String version() {
		return MoF.version_major + "." + MoF.version_minor;
	}
}
