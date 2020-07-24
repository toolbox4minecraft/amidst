package amidst;

import java.nio.file.Path;
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

	public static AmidstMetaData createMetadata() {
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

	private static void initFileLogger(Path file) {
		if (file != null) {
			AmidstLogger.info("using log file: '" + file + "'");
			AmidstLogger.addListener("file", new FileLogger(file));
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
		return new AmidstSettings(Preferences.userNodeForPackage(Amidst.class));
	}

	/**
	 * WARNING: This method MUST be invoked before applyLookAndFeel(). The
	 * sun.java2d.* properties have no effect after applyLookAndFeel() has
	 * been called.
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

	private static void applyLookAndFeel(AmidstSettings settings) {
    	settings.lookAndFeel.get().tryApply();
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
			applyLookAndFeel(settings);
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
			AmidstLogger.crash(e, message);
			CrashWindow.showAfterCrash();
		} catch (Throwable t) {
			System.err.println("Amidst crashed!");
			System.err.println(message);
			e.printStackTrace();
		}
	}
}
