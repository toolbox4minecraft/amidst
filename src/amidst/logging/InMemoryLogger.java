package amidst.logging;

public class InMemoryLogger implements Logger {
	private StringBuffer buffer = new StringBuffer();

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
		buffer.append("[").append(tag).append("] ");
		for (int i = 0; i < messages.length; i++) {
			buffer.append(messages[i]);
			buffer.append(getMessageDelimiter(i, messages));
		}
	}

	private String getMessageDelimiter(int i, Object... messages) {
		if (i < messages.length - 1) {
			return " ";
		} else {
			return "\n";
		}
	}

	public String getContents() {
		return buffer.toString();
	}
}
