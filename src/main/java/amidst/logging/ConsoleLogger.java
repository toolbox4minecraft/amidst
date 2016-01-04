package amidst.logging;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ConsoleLogger implements Logger {
	@Override
	public void debug(Object... messages) {
		printWithTag("debug", messages);
	}

	@Override
	public void info(Object... messages) {
		printWithTag("info", messages);
	}

	@Override
	public void warning(Object... messages) {
		printWithTag("warning", messages);
	}

	@Override
	public void error(Object... messages) {
		printWithTag("error", messages);
	}

	@Override
	public void crash(Throwable e, String exceptionText, String message) {
		printWithTag("crash", message);
		if (!exceptionText.isEmpty()) {
			printWithTag("crash", exceptionText);
		}
	}

	private void printWithTag(String tag, Object... messages) {
		System.out.print("[" + tag + "] ");
		for (int i = 0; i < messages.length; i++) {
			System.out.print(messages[i]);
			System.out.print(getMessageDelimiter(i, messages));
		}
	}

	private String getMessageDelimiter(int i, Object... messages) {
		if (i < messages.length - 1) {
			return " ";
		} else {
			return "\n";
		}
	}
}
