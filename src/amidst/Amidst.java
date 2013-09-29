package amidst;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import com.google.gson.Gson;

import MoF.Google;
import amidst.gui.VersionSelectWindow;
import amidst.json.InstallInformation;
import amidst.minecraft.Minecraft;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 3;
	public final static String versionOffset = "";
	public static InstallInformation installInformation = new InstallInformation(true);
	public static final Gson gson = new Gson();
	
	public static void main(String args[]) {
		Util.setLookAndFeel();
		Google.startTracking();
		Google.track("Run");
		new VersionSelectWindow();
	}
	
	public static String version() {
		if (Minecraft.getActiveMinecraft() != null)
			return version_major + "." + version_minor + versionOffset + " [Using Minecraft version: " + Minecraft.getActiveMinecraft().version + " | Attempted: " + installInformation.lastVersionId + "]";
		else
			return version_major + "." + version_minor + versionOffset;
	}
	
	private static void loadAllLibraries() {
		File libPath = new File(Util.minecraftDirectory + "lib/");
		if (!libPath.exists())
			return;
		
		
	}
}
