package amidst.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class Log {
	public static interface CrashHandler {
		void handle(Throwable e, String exceptionText, String message,
				String allLogMessages);
	}

	private static final ConsoleLogger CONSOLE_LOGGER = new ConsoleLogger();
	private static final InMemoryLogger IN_MEMORY_LOGGER = new InMemoryLogger();

	private static final Object LOG_LOCK = new Object();
	private static final boolean IS_USING_ALERTS = true;
	private static final boolean IS_SHOWING_DEBUG = true;

	private static final Map<String, Logger> LOGGER = new HashMap<String, Logger>();

	private static volatile CrashHandler crashHandler;

	static {
		addListener("console", CONSOLE_LOGGER);
		addListener("master", IN_MEMORY_LOGGER);
	}

	public static void addListener(String name, Logger l) {
		synchronized (LOG_LOCK) {
			LOGGER.put(name, l);
		}
	}

	public static void removeListener(String name) {
		synchronized (LOG_LOCK) {
			LOGGER.remove(name);
		}
	}

	public static void setCrashHandler(CrashHandler handler) {
		crashHandler = handler;
	}

	public static void i(Object... messages) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
				listener.info(messages);
			}
		}
	}

	public static void debug(Object... messages) {
		if (IS_SHOWING_DEBUG) {
			synchronized (LOG_LOCK) {
				for (Logger listener : LOGGER.values()) {
					listener.debug(messages);
				}
			}
		}
	}

	public static void w(Object... messages) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
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
			for (Logger listener : LOGGER.values()) {
				listener.error(messages);
			}
		}
	}

	public static void crash(Throwable e, String message) {
		synchronized (LOG_LOCK) {
			String exceptionText = getExceptionText(e);
			for (Logger listener : LOGGER.values()) {
				listener.crash(e, exceptionText, message);
			}
			if (crashHandler != null) {
				crashHandler.handle(e, exceptionText, message,
						IN_MEMORY_LOGGER.getContents());
			}
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
