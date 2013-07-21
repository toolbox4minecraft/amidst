package amidst;

import amidst.preferences.BooleanPrefModel;
import amidst.preferences.FilePrefModel;

import java.io.File;
import java.util.prefs.Preferences;

/** Currently selected options that change AMIDST’s behavior
 */
public enum Options {
	instance;
	
	//per-run preferences. TODO: store elsewhere?
	public long seed;
	public String seedText;
	
	//permanent preferences
	public final FilePrefModel jar;
	public final BooleanPrefModel showSlimeChunks;
	public final BooleanPrefModel showGrid;
	public final BooleanPrefModel showNetherFortresses;
	public final BooleanPrefModel showIcons;
	
	private Options() {
		seed = 0L;
		seedText = null;
		
		Preferences pref = Preferences.userNodeForPackage(Amidst.class);
		
		jar                  = new FilePrefModel(   pref, "jar", new File(Util.minecraftDirectory, "bin/minecraft.jar"));
		showSlimeChunks      = new BooleanPrefModel(pref, "slimeChunks",      false);
		showGrid             = new BooleanPrefModel(pref, "grid",             false);
		showNetherFortresses = new BooleanPrefModel(pref, "netherFortresses", false);
		showIcons            = new BooleanPrefModel(pref, "icons",            true);
	}
	
	public File getJar() {
		if (Amidst.installInformation.isPre161)
			return jar.get();
		else
			return Amidst.installInformation.getJarFile();
		
	}
	
	public String getSeedMessage() {
		if (seedText == null)
			return "Seed: " + seed;
		else
			return "Seed: \"" + seedText + "\" (" + seed +  ")";
	}
}
