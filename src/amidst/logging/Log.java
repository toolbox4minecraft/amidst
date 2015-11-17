package amidst.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import amidst.gui.CrashWindow;
import amidst.gui.MapWindow;

public class Log {
	private static final ConsoleLogger CONSOLE_LOGGER = new ConsoleLogger();
	private static final InMemoryLogger IN_MEMORY_LOGGER = new InMemoryLogger();

	private static final Object LOG_LOCK = new Object();
	private static final boolean IS_USING_ALERTS = true;
	private static final boolean IS_SHOWING_DEBUG = true;

	private static Map<String, Logger> logger = new HashMap<String, Logger>();

	static {
		addListener("console", CONSOLE_LOGGER);
		addListener("master", IN_MEMORY_LOGGER);
	}

	public static void addListener(String name, Logger l) {
		synchronized (LOG_LOCK) {
			logger.put(name, l);
		}
	}

	public static void removeListener(String name) {
		synchronized (LOG_LOCK) {
			logger.remove(name);
		}
	}

	public static void i(Object... messages) {
		synchronized (LOG_LOCK) {
			for (Logger listener : logger.values()) {
				listener.info(messages);
			}
		}
	}

	public static void debug(Object... messages) {
		if (IS_SHOWING_DEBUG) {
			synchronized (LOG_LOCK) {
				for (Logger listener : logger.values()) {
					listener.debug(messages);
				}
			}
		}
	}

	public static void w(Object... messages) {
		synchronized (LOG_LOCK) {
			for (Logger listener : logger.values()) {
				listener.warning(messages);
			}
		}
	}

	public static void e(Object... messages) {
		synchronized (LOG_LOCK) {
			if (IS_USING_ALERTS) {
				JOptionPane.showMessageDialog(null, messages, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			for (Logger listener : logger.values()) {
				listener.error(messages);
			}
		}
	}

	public static void crash(String message) {
		crash(null, message);
	}

	public static void crash(Throwable e, String message) {
		synchronized (LOG_LOCK) {
			String exceptionText = getExceptionText(e);
			for (Logger listener : logger.values()) {
				listener.crash(e, exceptionText, message);
			}
			new CrashWindow(message, IN_MEMORY_LOGGER.getContents());
			if (MapWindow.getInstance() != null)
				MapWindow.getInstance().dispose();
			// System.exit(0);
		}
	}

	private static String getExceptionText(Throwable e) {
		if (e != null) {
			return getStackTraceAsString(e);
		} else {
			return "";
		}
	}

	public static void printTraceStack(Throwable e) {
		w(getStackTraceAsString(e));
	}

	private static String getStackTraceAsString(Throwable e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
}
