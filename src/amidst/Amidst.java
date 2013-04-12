package amidst;

import MoF.FinderWindow;
import MoF.Google;
import MoF.Project;
import MoF.SaveLoader;

import java.io.File;
import java.io.IOException;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 0;
	
	public static void main(String args[]) throws IOException {
		Util.setLookAndFeel();
		Google.startTracking();
		Google.track("Run");
		//TODO: load options
		FinderWindow w = new FinderWindow();
		//TODO: redesign, move to optipns
		if (args.length > 0) {
			File dat = new File(args[0]);
			if (dat.isFile())
				w.setProject(new Project(new SaveLoader(dat)));
			else
				w.setProject(new Project(args[0]));
		}
	}
	
	public static String version() {
		return version_major + "." + version_minor;
	}
}
