package amidst.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class FileLogger implements Logger {
	private final ConcurrentLinkedQueue<String> logMessageQueue = new ConcurrentLinkedQueue<>();
	private final File file;
	private final ScheduledExecutorService executor;

	@CalledOnlyBy(AmidstThread.STARTUP)
	public FileLogger(File file) {
		this.file = file;
		this.executor = createExecutor();
		if (ensureFileExists()) {
			writeWelcomeMessageToFile();
			start();
		}
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private ScheduledExecutorService createExecutor() {
		return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private boolean ensureFileExists() {
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					disableBecauseFileCreationFailed();
					return false;
				}
			} catch (IOException e) {
				disableBecauseFileCreationThrowsException(e);
				return false;
			}
		} else if (file.isDirectory()) {
			disableBecauseFileIsDirectory();
			return false;
		}
		return true;
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void disableBecauseFileCreationFailed() {
		Log.w("Unable to create new file at: " + file + " disabling logging to file. (No exception thrown)");
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void disableBecauseFileCreationThrowsException(IOException e) {
		Log.w("Unable to create new file at: " + file + " disabling logging to file.");
		e.printStackTrace();
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void disableBecauseFileIsDirectory() {
		Log.w("Unable to log at path: " + file + " because location is a directory.");
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void writeWelcomeMessageToFile() {
		write("log", "New FileLogger started.");
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void start() {
		executor.scheduleWithFixedDelay(new Runnable() {
			@CalledOnlyBy(AmidstThread.FILE_LOGGER)
			@Override
			public void run() {
				processQueue();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
	}

	@CalledOnlyBy(AmidstThread.FILE_LOGGER)
	private void processQueue() {
		if (!logMessageQueue.isEmpty() && file.isFile()) {
			writeLogMessage(getLogMessage());
		}
	}

	@CalledOnlyBy(AmidstThread.FILE_LOGGER)
	private String getLogMessage() {
		StringBuilder builder = new StringBuilder();
		while (!logMessageQueue.isEmpty()) {
			builder.append(logMessageQueue.poll());
		}
		return builder.toString();
	}

	@CalledOnlyBy(AmidstThread.FILE_LOGGER)
	private void writeLogMessage(String logMessage) {
		try (FileWriter writer = new FileWriter(file, true)) {
			writer.append(logMessage);
		} catch (IOException e) {
			Log.w("Unable to write to log file.");
			e.printStackTrace();
		}
	}

	@Override
	public void debug(Object... messages) {
		write("debug", messages);
	}

	@Override
	public void info(Object... messages) {
		write("info", messages);
	}

	@Override
	public void warning(Object... messages) {
		write("warning", messages);
	}

	@Override
	public void error(Object... messages) {
		write("error", messages);
	}

	@Override
	public void crash(Throwable e, String exceptionText, String message) {
		write("crash", message);
		if (!exceptionText.isEmpty()) {
			write("crash", exceptionText);
		}
	}

	private void write(String tag, Object... messages) {
		String currentTime = new Timestamp(new Date().getTime()).toString();
		StringBuilder builder = new StringBuilder(currentTime);
		builder.append(" [").append(tag).append("] ");
		for (int i = 0; i < messages.length; i++) {
			builder.append(messages[i]);
			builder.append(getMessageDelimiter(i, messages));
		}
		logMessageQueue.add(builder.toString());
	}

	private String getMessageDelimiter(int i, Object... messages) {
		if (i < messages.length - 1) {
			return " ";
		} else {
			return "\r\n";
		}
	}
}
