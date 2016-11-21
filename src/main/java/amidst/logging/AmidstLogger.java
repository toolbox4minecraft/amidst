package amidst.logging;

import java.util.HashMap;
import java.util.Map;

import amidst.documentation.ThreadSafe;

// TODO: switch to standard logging framework like slf4j + log4j?
@ThreadSafe
public class AmidstLogger {
	private static final String DEBUG_TAG = "debug";
	private static final String INFO_TAG = "info";
	private static final String WARNING_TAG = "warning";
	private static final String ERROR_TAG = "error";
	private static final String CRASH_TAG = "crash";

	private static final ConsoleLogger CONSOLE_LOGGER = new ConsoleLogger();
	private static final InMemoryLogger IN_MEMORY_LOGGER = new InMemoryLogger();

	private static final Object LOG_LOCK = new Object();
	private static final boolean IS_USING_ALERTS = true;
	private static final boolean IS_SHOWING_DEBUG = true;

	private static final Map<String, Logger> LOGGER = createLoggerMap();

	private static Map<String, Logger> createLoggerMap() {
		Map<String, Logger> result = new HashMap<>();
		result.put("console", CONSOLE_LOGGER);
		result.put("master", IN_MEMORY_LOGGER);
		return result;
	}

	public static void addListener(String name, Logger logger) {
		synchronized (LOG_LOCK) {
			LOGGER.put(name, logger);
		}
	}

	public static void removeListener(String name) {
		synchronized (LOG_LOCK) {
			LOGGER.remove(name);
		}
	}

	public static void debug(String message) {
		if (IS_SHOWING_DEBUG) {
			synchronized (LOG_LOCK) {
				for (Logger listener : LOGGER.values()) {
					listener.log(DEBUG_TAG, message);
				}
			}
		}
	}

	public static void info(String message) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
				listener.log(INFO_TAG, message);
			}
		}
	}

	public static void warn(String message) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
				listener.log(WARNING_TAG, message);
			}
		}
	}

	public static void warn(Throwable e) {
		warn(MessageFormatter.format(e));
	}

	public static void error(String message) {
		synchronized (LOG_LOCK) {
			if (IS_USING_ALERTS) {
				AmidstMessageBox.displayError("Error", message);
			}
			for (Logger listener : LOGGER.values()) {
				listener.log(ERROR_TAG, message);
			}
		}
	}

	public static void crash(Throwable e, String message) {
		synchronized (LOG_LOCK) {
			String exceptionText = MessageFormatter.format(e);
			for (Logger listener : LOGGER.values()) {
				listener.log(CRASH_TAG, message);
				if (e != null) {
					listener.log(CRASH_TAG, exceptionText);
				}
			}
		}
	}

	public static String getAllMessages() {
		synchronized (LOG_LOCK) {
			return IN_MEMORY_LOGGER.getContents();
		}
	}
}
