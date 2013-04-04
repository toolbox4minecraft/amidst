package amidst;

import MoF.FinderWindow;
import MoF.Google;

import java.io.IOException;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 0;
	
	public static void main(String args[]) throws IOException {
		Util.setLookAndFeel();
		Google.startTracking();
		Google.track("Run");
		//TODO: load options
		new FinderWindow();
	}
	
	public static String version() {
		return version_major + "." + version_minor;
	}
}
