package amidst;

public class Log {
	private static LogListener listener;
	private static boolean isUsingListener;
	
	public static void i(Object s) {
		System.out.println("[info] " + s);
		if (isUsingListener)
			listener.info(s);
	}
	public static void debug(Object s) {
		System.out.println("[debug] " + s);
		if (isUsingListener)
			listener.debug(s);
	}
	public static void w(Object s) {
		System.err.println("[warning] " + s);
		if (isUsingListener)
			listener.warning(s);
	}
	public static void e(Object s) {
		System.err.println("[error] " + s);
		if (isUsingListener)
			listener.error(s);
	}
	public static void kill(Object s) {
		System.err.println("[kill] " + s);
		if (isUsingListener)
			listener.kill(s);
		System.exit(0);
	}
	
	public static void writeTo(LogListener l) {
		listener = l;
		isUsingListener = (l != null);
	}
}
