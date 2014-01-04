package amidst;

import java.awt.Image;
import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.gson.Gson;

import MoF.Google;
import amidst.gui.version.VersionSelectWindow;
import amidst.logging.FileLogger;
import amidst.logging.Log;
import amidst.minecraft.MinecraftUtil;
import amidst.preferences.BiomeColorProfile;
import amidst.resources.ResourceLoader;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 7;
	public final static String versionOffset = " beta 1";
	public static Image icon = ResourceLoader.getImage("icon.png");
	public static final Gson gson = new Gson();
	
	
	public static void main(String args[]) {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.crash(e, "Amidst has encounted an uncaught exception on thread: " + thread);
			}
		});
		CmdLineParser parser = new CmdLineParser(Options.instance); 
		Util.setMinecraftDirectory();
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			Log.w("There was an issue parsing command line options.");
			e.printStackTrace();
		}
		
		if (Options.instance.logPath != null)
			Log.addListener("file", new FileLogger(new File(Options.instance.logPath)));
		
		
		if (!isOSX()) { Util.setLookAndFeel(); }
		Google.startTracking();
		Google.track("Run");
		System.setProperty("sun.java2d.opengl","True");
		System.setProperty("sun.java2d.accthreshold", "0");
		BiomeColorProfile.scan();
		
		new VersionSelectWindow();
	}
	
	public static boolean isOSX() {
		String osName = System.getProperty("os.name");
		return osName.contains("OS X");
	}
	
	public static String version() {
		if (MinecraftUtil.hasInterface())
			return version_major + "." + version_minor + versionOffset + " [Using Minecraft version: " + MinecraftUtil.getVersion() + "]";
		return version_major + "." + version_minor + versionOffset;
	}
	
}
