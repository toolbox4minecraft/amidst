package amidst.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
	private final Path file;
	private final ScheduledExecutorService executor;

	@CalledOnlyBy(AmidstThread.STARTUP)
	public FileLogger(Path file) {
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
		try {
			Files.createFile(file);
		} catch (FileAlreadyExistsException e){
			if (Files.isDirectory(file)) {
				disableBecauseFileIsDirectory();
				return false;
			} else {
				// log file already exists; nothing to do
			}
		} catch (IOException e) {
			disableBecauseFileCreationThrowsException(e);
			return false;
		}
		return true;
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void disableBecauseFileCreationThrowsException(IOException e) {
		AmidstLogger.warn(e, "Unable to create new file at: {} disabling logging to file.", file);
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void disableBecauseFileIsDirectory() {
		AmidstLogger.warn("Unable to log at path: {} because location is a directory.", file);
	}

	@CalledOnlyBy(AmidstThread.STARTUP)
	private void writeWelcomeMessageToFile() {
		log("log", "New FileLogger started.");
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
		if (!logMessageQueue.isEmpty() && Files.isRegularFile(file)) {
			writeLogMessages();
		}
	}

	@CalledOnlyBy(AmidstThread.FILE_LOGGER)
	private void writeLogMessages() {
		try (BufferedWriter writer = Files.newBufferedWriter(file,
				StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
			String msg = null;
			while((msg = logMessageQueue.poll()) != null) {
				writer.write(msg);
			}
		} catch (IOException e) {
			AmidstLogger.warn(e, "Unable to write to log file.");
		}
	}

	@Override
	public void log(String tag, String message) {
		String currentTime = new Timestamp(new Date().getTime()).toString();
		StringBuilder builder = new StringBuilder()
				.append(currentTime)
				.append(" [")
				.append(tag)
				.append("] ")
				.append(message)
				.append("\r\n");
		logMessageQueue.add(builder.toString());
	}
}
