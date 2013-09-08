package amidst;

import com.google.gson.Gson;

import MoF.Google;
import amidst.gui.VersionSelectWindow;
import amidst.json.InstallInformation;
import amidst.minecraft.Minecraft;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 1;
	public final static String versionOffset = "";
	public static InstallInformation installInformation = new InstallInformation(true);
	public static final Gson gson = new Gson();
	
	public static void main(String args[]) {
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
		if (Minecraft.getActiveMinecraft() != null)
			return version_major + "." + version_minor + versionOffset + " [Using Minecraft version: " + Minecraft.getActiveMinecraft().version + " | Attempted: " + installInformation.lastVersionId + "]";
		else
			return version_major + "." + version_minor + versionOffset;
	}
}
