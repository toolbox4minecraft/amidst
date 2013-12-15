package amidst.logging;

public interface LogListener {
	public void debug(Object... o);
	public void info(Object... o);
	public void warning(Object... o);
	public void error(Object... o);
	public void crash(Throwable e, String exceptionText, String message);
}
