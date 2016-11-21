package amidst.logging;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class InMemoryLogger implements Logger {
	private StringBuffer buffer = new StringBuffer();

	@Override
	public void debug(String message) {
		write("debug", message);
	}

	@Override
	public void info(String message) {
		write("info", message);
	}

	@Override
	public void warning(String message) {
		write("warning", message);
	}

	@Override
	public void error(String message) {
		write("error", message);
	}

	@Override
	public void crash(String message) {
		write("crash", message);
	}

	private void write(String tag, String message) {
		buffer.append("[").append(tag).append("] ").append(message).append("\n");
	}

	public String getContents() {
		return buffer.toString();
	}
}
