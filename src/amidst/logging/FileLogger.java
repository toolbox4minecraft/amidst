package amidst.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileLogger extends Thread implements LogListener {
	private File file;
	private boolean enabled = true;
	private ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<String>();
	
	public FileLogger(File file) {
		this.file = file;
		if (!file.exists()) {
			try {
				enabled = file.createNewFile();
				if (!enabled)
					Log.w("Unable to create new file at: " + file + " disabling logging to file. (No exception thrown)");
			} catch (IOException e) {
				Log.w("Unable to create new file at: " + file + " disabling logging to file.");
				e.printStackTrace();
				enabled = false;
			}
		} else if (file.isDirectory()) {
			Log.w("Unable to log at path: " + file + " because location is a directory.");
			enabled = false;
		}
		write("log", "New FileLogger started.");
		start();
	}
	@Override
	public void debug(Object... o) {
		write("debug", o);
	}

	@Override
	public void info(Object... o) {
		write("info", o);
	}

	@Override
	public void warning(Object... o) {
		write("warning", o);
	}

	@Override
	public void error(Object... o) {
		write("error", o);
	}


	@Override
	public void crash(Throwable e, String exceptionText, String message) {
		write("crash", message);
		if (exceptionText.length() > 0)
			write("crash", exceptionText);
	}
	
	
	private void write(String tag, Object... msgs) {
		StringBuilder stringBuilder = new StringBuilder(new Timestamp(new Date().getTime()).toString()).append(" [").append(tag).append("] ");
		for (int i = 0; i < msgs.length; i++) {
			stringBuilder.append(msgs[i]);
			stringBuilder.append((i < msgs.length - 1) ? " " : "\r\n");
		}
		logQueue.add(stringBuilder.toString());
	}
	
	@Override
	public void run() {
		while (enabled) {
			if (logQueue.size() != 0) {
				StringBuilder stringBuilder = new StringBuilder();
				while (logQueue.size() != 0)
					stringBuilder.append(logQueue.poll());
				
				if (file.exists() && file.isFile()) {
					FileWriter writer = null;
					try {
						writer = new FileWriter(file, true);
						writer.append(stringBuilder.toString());
					} catch (IOException e) {
						Log.w("Unable to write to log file.");
						e.printStackTrace();
					} finally {
						try {
							if (writer != null)
								writer.close();
						} catch (IOException e) {
							Log.w("Unable to close writer for log file.");
							e.printStackTrace();
						}
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
