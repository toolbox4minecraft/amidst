package amidst.logging;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class InMemoryLogger implements Logger {
	private StringBuffer buffer = new StringBuffer();

	@Override
	public void log(String tag, String message) {
		buffer.append("[").append(tag).append("] ").append(message).append("\n");
	}

	public String getContents() {
		return buffer.toString();
	}
}
