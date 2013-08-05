package amidst;

import MoF.FinderWindow;
import MoF.Google;
import MoF.Project;
import MoF.SaveLoader;

import java.io.File;
import java.io.IOException;

import amidst.gui.VersionSelectWindow;
import amidst.json.InstallInformation;

public class Amidst {
<<<<<<< HEAD
	public final static int version_major = 2;
	public final static int version_minor = 1;
=======
	public final static int version_major = 3;
	public final static int version_minor = 0;
	public static InstallInformation installInformation = new InstallInformation(false);
>>>>>>> Map-Overhaul
	
	public static void main(String args[]) throws IOException {
		Util.setLookAndFeel();
		Google.startTracking();
		Google.track("Run");
		new VersionSelectWindow();
		
		/*FinderWindow w = new FinderWindow(); //as long as we design it well, we won't need a reference to it ;)
		//TODO: redesign, move to optipns
		if (args.length > 0) {
			File dat = new File(args[0]);
			if (dat.isFile())
				w.setProject(new Project(new SaveLoader(dat)));
			else
				w.setProject(new Project(args[0]));
		}*/
	}
	
	public static String version() {
		return version_major + "." + version_minor;
	}
}
