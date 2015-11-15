package amidst;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import amidst.logging.FileLogger;
import amidst.logging.Log;
import amidst.preferences.BiomeColorProfile;
import amidst.utilities.Google;

public class Amidst {
	private static final String UNCAUGHT_EXCEPTION_ERROR_MESSAGE = "Amidst has encounted an uncaught exception on thread: ";
	private static final String COMMAND_LINE_PARSING_ERROR_MESSAGE = "There was an issue parsing command line options.";

	public static void main(String args[]) {
		initUncaughtExceptionHandler();
		parseCommandLineArguments(args);
		initUtil();
		initLogger();
		initLookAndFeel();
		trackRunning();
		setEnvironmentVariables();
		scanForBiomeColorProfiles();
		startApplication();
	}

	private static void initUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.crash(e, UNCAUGHT_EXCEPTION_ERROR_MESSAGE + thread);
			}
		});
	}

	private static void parseCommandLineArguments(String[] args) {
		CmdLineParser parser = new CmdLineParser(Options.instance);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			Log.w(COMMAND_LINE_PARSING_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static void initUtil() {
		Util.setMinecraftDirectory();
		Util.setMinecraftLibraries();
	}

	private static void initLogger() {
		if (Options.instance.logPath != null) {
			Log.addListener("file", new FileLogger(new File(
					Options.instance.logPath)));
		}
	}

	private static void initLookAndFeel() {
		if (!isOSX()) {
			Util.setLookAndFeel();
		}
	}

	private static boolean isOSX() {
		return System.getProperty("os.name").contains("OS X");
	}

	private static void trackRunning() {
		Google.track("Run");
	}

	private static void setEnvironmentVariables() {
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("sun.java2d.accthreshold", "0");
	}

	private static void scanForBiomeColorProfiles() {
		BiomeColorProfile.scan();
	}

	private static void startApplication() {
		if (Options.instance.minecraftJar != null) {
			new Application().displayMapWindow(Options.instance.minecraftJar,
					Options.instance.minecraftPath);
		} else {
			new Application().displayVersionSelectWindow();
		}
	}
}
