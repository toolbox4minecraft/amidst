package amidst.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileLogger implements LogListener {
	private ConcurrentLinkedQueue<String> logMessageQueue = new ConcurrentLinkedQueue<String>();
	private File file;
	private boolean enabled;

	public FileLogger(File file) {
		this.file = file;
		this.enabled = ensureFileExists();
		writeWelcomeMessageToFile();
		startThread();
	}

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

	private void disableBecauseFileCreationFailed() {
		Log.w("Unable to create new file at: " + file
				+ " disabling logging to file. (No exception thrown)");
	}

	private void disableBecauseFileCreationThrowsException(IOException e) {
		Log.w("Unable to create new file at: " + file
				+ " disabling logging to file.");
		e.printStackTrace();
	}

	private void disableBecauseFileIsDirectory() {
		Log.w("Unable to log at path: " + file
				+ " because location is a directory.");
	}

	private void writeWelcomeMessageToFile() {
		write("log", "New FileLogger started.");
	}

	private void startThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				doRun();
			}
		}).start();
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
			appendMessageDelimiter(builder, i, messages);
		}
		logMessageQueue.add(builder.toString());
	}

	private void appendMessageDelimiter(StringBuilder builder, int i,
			Object... messages) {
		if (i < messages.length - 1) {
			builder.append(" ");
		} else {
			builder.append("\r\n");
		}
	}

	private void doRun() {
		while (enabled) {
			if (logMessageQueue.size() != 0 && file.exists() && file.isFile()) {
				writeLogMessage(getLogMessage());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private String getLogMessage() {
		StringBuilder builder = new StringBuilder();
		while (logMessageQueue.size() != 0) {
			builder.append(logMessageQueue.poll());
		}
		return builder.toString();
	}

	private void writeLogMessage(String logMessage) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.append(logMessage);
		} catch (IOException e) {
			Log.w("Unable to write to log file.");
			e.printStackTrace();
		} finally {
			closeWriter(writer);
		}
	}

	private void closeWriter(FileWriter writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				Log.w("Unable to close writer for log file.");
				e.printStackTrace();
			}
		}
	}
}
