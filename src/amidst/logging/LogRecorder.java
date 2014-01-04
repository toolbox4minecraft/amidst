package amidst.logging;

public class LogRecorder implements LogListener {
	private static StringBuffer buffer = new StringBuffer();
	
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
	
	private static void write(String tag, Object... msgs) {
		buffer.append("[" + tag + "] ");
		for (int i = 0; i < msgs.length; i++)
			buffer.append(msgs[i] + ((i < msgs.length - 1) ? " " : "\n"));
	}
	
	public static String getContents() {
		return buffer.toString();
	}
}
