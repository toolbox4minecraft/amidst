package amidst.logging;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import amidst.documentation.ThreadSafe;

// TODO: switch to standard logging framework like slf4j + log4j?
@ThreadSafe
public class AmidstLogger {
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

	public static void info(Object... messages) {
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

	public static void warn(Object... messages) {
		synchronized (LOG_LOCK) {
			for (Logger listener : LOGGER.values()) {
				listener.warning(messages);
			}
		}
	}

	public static void warn(Throwable e) {
		warn(MessageFormatter.format(e));
	}

	public static void error(Object... messages) {
		synchronized (LOG_LOCK) {
			if (IS_USING_ALERTS) {
				JOptionPane.showMessageDialog(null, messages, "Error", JOptionPane.ERROR_MESSAGE);
			}
			for (Logger listener : LOGGER.values()) {
				listener.error(messages);
			}
		}
	}

	public static void crash(Throwable e, String message) {
		synchronized (LOG_LOCK) {
			String exceptionText = MessageFormatter.format(e);
			for (Logger listener : LOGGER.values()) {
				listener.crash(e, exceptionText, message);
			}
		}
	}

	public static String getAllMessages() {
		synchronized (LOG_LOCK) {
			return IN_MEMORY_LOGGER.getContents();
		}
	}
}
