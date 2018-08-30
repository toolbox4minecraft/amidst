package amidst;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.crash.CrashWindow;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.logging.FileLogger;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.util.OperatingSystemDetector;

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
		CmdLineParser parser = new CmdLineParser(
				parameters,
				ParserProperties.defaults().withShowDefaults(false).withUsageWidth(120).withOptionSorter(null));
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

	private static void run(CommandLineParameters parameters, AmidstMetaData metadata, CmdLineParser parser) {
		initFileLogger(parameters.logFile);
		String versionString = metadata.getVersion().createLongVersionString();
		if (parameters.printHelp) {
			System.out.println(versionString);
			parser.printUsage(System.out);
		} else if (parameters.printVersion) {
			System.out.println(versionString);
		} else {
			AmidstLogger.info(versionString);
			logTimeAndProperties();
			enableGraphicsAcceleration();
			startApplication(parameters, metadata, createSettings());
		}
	}

	private static void initFileLogger(String filename) {
		if (filename != null) {
			AmidstLogger.info("using log file: '" + filename + "'");
			AmidstLogger.addListener("file", new FileLogger(new File(filename)));
		}
	}

	private static void logTimeAndProperties() {
		AmidstLogger.info("Current system time: " + getCurrentTimeStamp());
		AmidstLogger.info(createPropertyString("os.name"));
		AmidstLogger.info(createPropertyString("os.version"));
		AmidstLogger.info(createPropertyString("os.arch"));
		AmidstLogger.info(createPropertyString("java.version"));
		AmidstLogger.info(createPropertyString("java.vendor"));
		AmidstLogger.info(createPropertyString("sun.arch.data.model"));
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
		return new AmidstSettings(Preferences.userNodeForPackage(amidstest.Amidstest.class));
	}

	/**
	 * WARNING: This method MUST be invoked before setLookAndFeel(). The
	 * sun.java2d.* properties have no effect after setLookAndFeel() has been
	 * called.
	 * 
	 * Please do not remove this comment in case we decide to use another look
	 * and feel in the future.
	 */
	private static void enableGraphicsAcceleration() {
		enableOpenGLIfNecessary();
		forceGraphicsToVRAM();
	}

	/**
	 * We only use OpenGL on OS X, because it caused lots of bugs on Windows and
	 * performance issues on Linux. Also, this is the behavior of Amidst v3.7.
	 * 
	 * On Windows Direct3D is better supported and the default.
	 * 
	 * Linux has accelerated images without activating OpenGL. The reason for
	 * this is still unknown to the developers of Amidst.
	 * 
	 * https://github.com/toolbox4minecraft/amidst/pull/94
	 */
	private static void enableOpenGLIfNecessary() {
		if (OperatingSystemDetector.isMac()) {
			AmidstLogger.info("Enabling OpenGL.");
			System.setProperty("sun.java2d.opengl", "True");
		} else {
			AmidstLogger.info("Not using OpenGL.");
		}
	}

	private static void forceGraphicsToVRAM() {
		System.setProperty("sun.java2d.accthreshold", "0");
	}

	private static void startApplication(
			CommandLineParameters parameters,
			AmidstMetaData metadata,
			AmidstSettings settings) {
		SwingUtilities.invokeLater(() -> doStartApplication(parameters, metadata, settings));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void doStartApplication(
			CommandLineParameters parameters,
			AmidstMetaData metadata,
			AmidstSettings settings) {
		try {
			new PerApplicationInjector(parameters, metadata, settings).getApplication().run();
		} catch (DotMinecraftDirectoryNotFoundException e) {
			AmidstLogger.warn(e);
			AmidstMessageBox.displayError(
					"Please install Minecraft",
					"Amidst is not able to find your '.minecraft' directory, but it requires a working Minecraft installation.");
		} catch (Exception e) {
			handleCrash(e, Thread.currentThread());
		}
	}

	@CalledByAny
	private static void handleCrash(Throwable e, Thread thread) {
		String message = "Amidst has encounted an uncaught exception on the thread " + thread;
		try {
			// troubleshoot/check for known problems first
			boolean crashHandled = handleKnownCrashes(e);
			
			if (!crashHandled) AmidstLogger.crash(e, message);				
			CrashWindow.showAfterCrash();
		} catch (Throwable t) {
			System.err.println("Amidst crashed!");
			System.err.println(message);
			e.printStackTrace();
		}
	}
	
	@CalledByAny
	private static boolean handleKnownCrashes(Throwable e) {

		boolean result = false;
		
		if (e instanceof java.awt.AWTError && 
			e.getCause() instanceof java.lang.ClassNotFoundException &&
			e.getMessage() != null && e.getMessage().contains("GNOME.Accessibility")) {
			// User is probably running this GUI app with a headless JDK
			// https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=798794#10
			AmidstLogger.crash(e); // Log the exception just in case this diagnoses is wrong
			
			AmidstLogger.crash("WARNING: You might be attempting to run this graphical application using a");
			AmidstLogger.crash("JDK designed for headless systems.");
			AmidstLogger.crash("Several solutions are detailed in the link below:");
			AmidstLogger.crash("https://askubuntu.com/questions/695560/assistive-technology-not-found-awterror");
			result = true;
		}
		
		if (e instanceof java.lang.UnsupportedClassVersionError &&
			e.getMessage() != null && e.getMessage().contains("Unsupported major.minor version 52")) {
			// JRE 8 is required
			AmidstLogger.crash(e); // Log the exception just in case this diagnoses is wrong
			AmidstLogger.crash("WARNING: JRE 8 or later is required");			
			result = true;
		}
		
		return result;
	}
	
}
