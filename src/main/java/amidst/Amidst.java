package amidst;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.crash.CrashWindow;
import amidst.logging.FileLogger;
import amidst.logging.Log;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.settings.Setting;

@NotThreadSafe
public class Amidst {
	@CalledOnlyBy(AmidstThread.STARTUP)
	public static void main(String args[]) {
		initUncaughtExceptionHandler();
		parseCommandLineArgumentsAndRun(args);
	}

	private static void initUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				handleCrash(e, thread);
			}
		});
	}

	private static void parseCommandLineArgumentsAndRun(String[] args) {
		CommandLineParameters parameters = new CommandLineParameters();
		AmidstMetaData metadata = createMetadata();
		CmdLineParser parser = new CmdLineParser(parameters, ParserProperties
				.defaults().withShowDefaults(false).withUsageWidth(120)
				.withOptionSorter(null));
		try {
			parser.parseArgument(args);
			run(parameters, metadata, parser);
		} catch (CmdLineException e) {
			System.out.println(metadata.getVersion().createLongVersionString());
			System.err.println(e.getMessage());
			parser.printUsage(System.out);
			System.exit(2);
		}
	}

	private static AmidstMetaData createMetadata() {
		return AmidstMetaData.from(
				ResourceLoader.getProperties("/amidst/metadata.properties"),
				ResourceLoader.getImage("/amidst/icon/amidst-16x16.png"),
				ResourceLoader.getImage("/amidst/icon/amidst-32x32.png"),
				ResourceLoader.getImage("/amidst/icon/amidst-48x48.png"),
				ResourceLoader.getImage("/amidst/icon/amidst-64x64.png"),
				ResourceLoader.getImage("/amidst/icon/amidst-128x128.png"),
				ResourceLoader.getImage("/amidst/icon/amidst-256x256.png"));
	}

	private static void run(CommandLineParameters parameters,
			AmidstMetaData metadata, CmdLineParser parser) {
		initFileLogger(parameters.logFile);
		String versionString = metadata.getVersion().createLongVersionString();
		if (parameters.printHelp) {
			System.out.println(versionString);
			parser.printUsage(System.out);
		} else if (parameters.printVersion) {
			System.out.println(versionString);
		} else {
			Log.i(versionString);
			logTimeAndProperties();
			AmidstSettings settings = createSettings();
			enableGraphicsAccelerationIfNecessary(settings.graphicsAcceleration);
			startApplication(parameters, metadata, settings);
		}
	}

	private static void initFileLogger(String filename) {
		if (filename != null) {
			Log.i("using log file: '" + filename + "'");
			Log.addListener("file", new FileLogger(new File(filename)));
		}
	}

	private static void logTimeAndProperties() {
		Log.i("Current system time: " + getCurrentTimeStamp());
		Log.i(createPropertyString("os.name"));
		Log.i(createPropertyString("os.version"));
		Log.i(createPropertyString("os.arch"));
		Log.i(createPropertyString("java.version"));
		Log.i(createPropertyString("java.vendor"));
		Log.i(createPropertyString("sun.arch.data.model"));
		Log.i(createPropertyString("sun.java2d.opengl"));
		Log.i(createPropertyString("sun.java2d.accthreshold"));
		Log.i(createPropertyString("sun.java2d.d3d"));
		Log.i(createPropertyString("sun.java2d.ddoffscreen"));
		Log.i(createPropertyString("sun.java2d.noddraw"));
	}

	private static String getCurrentTimeStamp() {
		return new Timestamp(new Date().getTime()).toString();
	}

	private static String createPropertyString(String key) {
		StringBuilder b = new StringBuilder();
		b.append("System.getProperty(\"");
		b.append(key);
		b.append("\") == '");
		b.append(System.getProperty(key));
		b.append("'");
		return b.toString();
	}

	private static AmidstSettings createSettings() {
		return new AmidstSettings(Preferences.userNodeForPackage(Amidst.class));
	}

	/**
	 * WARNING: This method MUST be invoked before setLookAndFeel().
	 * The sun.java2d.* properties have no effect after setLookAndFeel() has 
	 * been called.
	 */
	private static void enableGraphicsAccelerationIfNecessary(
			Setting<Boolean> graphicsAcceleration) {
		if (graphicsAcceleration.get()) {
			Log.i("Graphics Acceleration: enabled");
			if (!isWindows()) {
				// We don't set OpenGL on Windows because Direct3D is better   
				// supported, and the default.
				setAndLogProperty("sun.java2d.opengl", "True");
			}
			setAndLogProperty("sun.java2d.accthreshold", "0");
		} else {
			Log.i("Graphics Acceleration: disabled");
		}
	}
	
	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}	

	private static void setAndLogProperty(String key, String value) {
		System.setProperty(key, value);
		Log.i(createPropertyString(key));
	}

	private static void startApplication(CommandLineParameters parameters,
			AmidstMetaData metadata, AmidstSettings settings) {
		SwingUtilities.invokeLater(() -> doStartApplication(parameters,
				metadata, settings));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void doStartApplication(CommandLineParameters parameters,
			AmidstMetaData metadata, AmidstSettings settings) {
		try {
			new Application(parameters, metadata, settings).run();
		} catch (DotMinecraftDirectoryNotFoundException e) {
			Log.w(e.getMessage());
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Amidst is not able to find your '.minecraft' directory, but it requires a working Minecraft installation.",
							"Please install Minecraft",
							JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			handleCrash(e, Thread.currentThread());
		}
	}

	@CalledByAny
	private static void handleCrash(Throwable e, Thread thread) {
		String message = "Amidst has encounted an uncaught exception on the thread "
				+ thread;
		try {
			Log.crash(e, message);
			displayCrashWindow(message, Log.getAllMessages());
		} catch (Throwable t) {
			System.err.println("Amidst crashed!");
			System.err.println(message);
			e.printStackTrace();
		}
	}

	private static void displayCrashWindow(final String message,
			final String allMessages) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CrashWindow.show(message, allMessages, () -> System.exit(4));
			}
		});
	}
}
