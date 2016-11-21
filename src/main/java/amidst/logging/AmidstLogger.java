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
		log(DEBUG_TAG, message);
	}

	public static void info(String message) {
		log(INFO_TAG, message);
	}

	public static void warn(String message) {
		log(WARNING_TAG, message);
	}

	public static void warn(Throwable e) {
		log(WARNING_TAG, MessageFormatter.format(e));
	}

	public static void error(String message) {
		log(ERROR_TAG, message);
	}

	public static void crash(Throwable e, String message) {
		log(CRASH_TAG, message);
		if (e != null) {
			log(CRASH_TAG, MessageFormatter.format(e));
		}
	}

	private static void log(String tag, String message) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
				listener.log(tag, message);
			}
		}
	}

	public static String getAllMessages() {
		synchronized (LOG_LOCK) {
			return IN_MEMORY_LOGGER.getContents();
		}
	}
}
