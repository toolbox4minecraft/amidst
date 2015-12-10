package amidst;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledBy;
import amidst.logging.FileLogger;
import amidst.logging.Log;
import amidst.logging.Log.CrashHandler;

public class Amidst {
	private static final String UNCAUGHT_EXCEPTION_ERROR_MESSAGE = "Amidst has encounted an uncaught exception on thread: ";
	private static final String COMMAND_LINE_PARSING_ERROR_MESSAGE = "There was an issue parsing command line options.";
	private static volatile Application application;
	private static volatile CommandLineParameters parameters = new CommandLineParameters();

	@CalledBy(AmidstThread.STARTUP)
	public static void main(String args[]) {
		initUncaughtExceptionHandler();
		initCrashHandler();
		parseCommandLineArguments(args);
		initLogger();
		initLookAndFeel();
		setJava2DEnvironmentVariables();
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

	private static void initCrashHandler() {
		Log.setCrashHandler(new CrashHandler() {
			@Override
			public void handle(Throwable e, String exceptionText,
					String message, String allLogMessages) {
				if (application != null) {
					application
							.crash(e, exceptionText, message, allLogMessages);
				} else {
					System.err.println("Amidst crashed!");
					System.err.println(message);
					e.printStackTrace();
				}
			}
		});
	}

	private static void parseCommandLineArguments(String[] args) {
		try {
			new CmdLineParser(parameters).parseArgument(args);
		} catch (CmdLineException e) {
			Log.w(COMMAND_LINE_PARSING_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static void initLogger() {
		if (parameters.logPath != null) {
			Log.addListener("file",
					new FileLogger(new File(parameters.logPath)));
		}
	}

	private static void initLookAndFeel() {
		if (!isOSX()) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				Log.printTraceStack(e);
			}
		}
	}

	private static boolean isOSX() {
		return System.getProperty("os.name").contains("OS X");
	}

	private static void setJava2DEnvironmentVariables() {
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("sun.java2d.accthreshold", "0");
	}

	private static void startApplication() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				doStartApplication();
			}
		});
	}

	@CalledBy(AmidstThread.EDT)
	private static void doStartApplication() {
		application = new Application(parameters);
		application.run();
	}
}
