package amidst;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.crash.CrashWindow;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.logging.FileLogger;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.awt.EventQueue;
import java.sql.Timestamp;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * The entry point class to the Amidst application.
 */
@NotThreadSafe
public class Amidst {

	/**
	 * The entry point to the Amidst application.
	 *
	 * @param args command line arguments (see the wiki)
	 */
	@CalledOnlyBy(AmidstThread.STARTUP)
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> handleCrash(e, thread));

		// Parse CLI arguments
		CommandLineParameters parameters = new CommandLineParameters();
		AmidstMetaData metadata = createMetadata();
		CmdLineParser parser = new CmdLineParser(
				parameters,
				ParserProperties.defaults().withShowDefaults(false).withUsageWidth(120).withOptionSorter(null));

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.out.println(metadata.getVersion().createLongVersionString());
			System.err.println(e.getMessage());
			parser.printUsage(System.out);
			System.exit(2);
		}

		// initialize file logging
		if (parameters.logFile != null) {
			AmidstLogger.info("using log file: '" + parameters.logFile + "'");
			AmidstLogger.addListener("file", new FileLogger(parameters.logFile));
		}

		String versionString = metadata.getVersion().createLongVersionString();

		// Printing the help guide prints and exits
		if (parameters.printHelp) {
			System.out.println(versionString);
			parser.printUsage(System.out);
			return;
		}

		// Printing the version prints and exits
		if (parameters.printVersion) {
			System.out.println(versionString);
			return;
		}

		// Log system information
		AmidstLogger.info(versionString);
		AmidstLogger.info("Current system time: " + new Timestamp(new Date().getTime()));
		AmidstLogger.info(createPropertyString("os.name"));
		AmidstLogger.info(createPropertyString("os.version"));
		AmidstLogger.info(createPropertyString("os.arch"));
		AmidstLogger.info(createPropertyString("java.version"));
		AmidstLogger.info(createPropertyString("java.vendor"));
		AmidstLogger.info(createPropertyString("sun.arch.data.model"));

		// Start application
		EventQueue.invokeLater(() -> {
			AmidstSettings settings = new AmidstSettings(Preferences.userNodeForPackage(Amidst.class));
			try {
				settings.lookAndFeel.get().tryApply();
				new PerApplicationInjector(parameters, metadata, settings).getApplication().run();
			} catch (DotMinecraftDirectoryNotFoundException e) {
				AmidstLogger.warn(e);
				AmidstMessageBox.displayError(
						"Please install Minecraft",
						"Amidst is not able to find your '.minecraft' directory, but it requires a working Minecraft installation.");
			} catch (Exception e) {
				handleCrash(e, Thread.currentThread());
			}
		});
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

	private static String createPropertyString(String key) {
		StringBuilder b = new StringBuilder();
		b.append("System.getProperty(\"");
		b.append(key);
		b.append("\") == '");
		b.append(System.getProperty(key));
		b.append("'");
		return b.toString();
	}

	/**
	 * On an uncaught exception, this logs it and shows a new window.
	 *
	 * @param e      the uncaught exception
	 * @param thread the thread Amidst crashed on
	 */
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
