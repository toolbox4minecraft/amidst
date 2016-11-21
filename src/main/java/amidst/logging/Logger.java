package amidst.logging;

public interface Logger {
	public void debug(String messages);

	public void info(String messages);

	public void warning(String messages);

	public void error(String messages);

	public void crash(Throwable e, String exceptionText, String message);
}
