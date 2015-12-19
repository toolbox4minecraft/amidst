package amidst.logging;

public interface Logger {
	public void debug(Object... messages);

	public void info(Object... messages);

	public void warning(Object... messages);

	public void error(Object... messages);

	public void crash(Throwable e, String exceptionText, String message);
}
