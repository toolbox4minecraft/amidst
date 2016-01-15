package amidst;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
		AmidstMetaData metadata = createMetadata();
		CommandLineParameters parameters = new CommandLineParameters();
		CmdLineParser parser = new CmdLineParser(parameters, ParserProperties
				.defaults().withShowDefaults(false).withUsageWidth(120)
				.withOptionSorter(null));
		try {
			parser.parseArgument(args);
			run(metadata, parameters, parser);
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

	private static void run(AmidstMetaData metadata,
			CommandLineParameters parameters, CmdLineParser parser) {
		initFileLogger(parameters.logFile);
		String versionString = metadata.getVersion().createLongVersionString();
		if (parameters.printHelp) {
			System.out.println(versionString);
			parser.printUsage(System.out);
		} else if (parameters.printVersion) {
			System.out.println(versionString);
		} else {
			Log.i(versionString);
			startApplication(parameters, metadata);
		}
	}

	private static void initFileLogger(String filename) {
		if (filename != null) {
			Log.i("using log file: '" + filename + "'");
			Log.addListener("file", new FileLogger(new File(filename)));
		}
	}

	private static void startApplication(CommandLineParameters parameters,
			AmidstMetaData metadata) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initGui();
				doStartApplication(parameters, metadata);
			}
		});
	}

	private static void initGui() {
		initLookAndFeel();
		setJava2DEnvironmentVariables();
	}

	private static void initLookAndFeel() {
		if (isWindows()) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e) {
				Log.printTraceStack(e);
			}
		}
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").contains("win");
	}

	private static void setJava2DEnvironmentVariables() {
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("sun.java2d.accthreshold", "0");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void doStartApplication(CommandLineParameters parameters,
			AmidstMetaData metadata) {
		try {
			new Application(parameters, metadata).run();
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
				new CrashWindow(message, allMessages, new Runnable() {
					@Override
					public void run() {
						System.exit(4);
					}
				});
			}
		});
	}
}
