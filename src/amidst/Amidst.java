package amidst;

import MoF.FinderWindow;
import MoF.Google;

import java.io.IOException;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 0;
	private static FinderWindow mainWindow;
	
	public static void main(String args[]) throws IOException {
		Util.setLookAndFeel();
		Google.startTracking();
		Google.track("Run");
		//TODO: load options
		mainWindow = new FinderWindow();
	}
	
	public static String version() {
		return version_major + "." + version_minor;
	}
	
	public static FinderWindow getActiveWindow() {
		return mainWindow;
	}
	
	public static String getPath() {
		return ClassLoader.getSystemClassLoader().getResource(".").getPath();
	}
}
