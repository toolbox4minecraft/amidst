package amidst;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.gson.Gson;

import MoF.Google;
import amidst.gui.OldVersionSelectWindow;
import amidst.gui.version.VersionSelectWindow;
import amidst.json.InstallInformation;
import amidst.logging.FileLogger;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.preferences.BiomeColorProfile;
import amidst.resources.ResourceLoader;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 6;
	public final static String versionOffset = "";
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
		//if (Minecraft.getActiveMinecraft() != null)
		//	return version_major + "." + version_minor + versionOffset + " [Using Minecraft version: " + Minecraft.getActiveMinecraft().version + " | Attempted: " + installInformation.lastVersionId + "]";
		//else
			return version_major + "." + version_minor + versionOffset;
	}
	
}
