package amidst.logging;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ConsoleLogger implements Logger {
	@Override
	public void debug(String message) {
		printWithTag("debug", message);
	}

	@Override
	public void info(String message) {
		printWithTag("info", message);
	}

	@Override
	public void warning(String message) {
		printWithTag("warning", message);
	}

	@Override
	public void error(String message) {
		printWithTag("error", message);
	}

	@Override
	public void crash(String message) {
		printWithTag("crash", message);
	}

	private void printWithTag(String tag, String message) {
		System.out.println("[" + tag + "] " + message);
	}
}
