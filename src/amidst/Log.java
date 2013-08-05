package amidst;

public class Log {
	private static LogListener listener;
	private static boolean isUsingListener;
	
	public static void i(Object... s) {
		printwithTag("info", s);
		if (isUsingListener)
			listener.info(s);
	}
	public static void debug(Object... s) {
		printwithTag("debug", s);
		if (isUsingListener)
			listener.debug(s);
	}
	public static void w(Object... s) {
		printwithTag("warning", s);
		if (isUsingListener)
			listener.warning(s);
	}
	public static void e(Object... s) {
		printwithTag("error", s);
		if (isUsingListener)
			listener.error(s);
	}
	public static void kill(Object... s) {
		printwithTag("kill", s);
		if (isUsingListener)
			listener.kill(s);
		System.exit(0);
	}
	
	public static void writeTo(LogListener l) {
		listener = l;
		isUsingListener = (l != null);
	}
	
	private static void printwithTag(String tag, Object... msgs) {
		System.out.print("[" + tag + "] ");
		for (int i=0; i<msgs.length; i++) {
			System.out.print(msgs[i]);
			System.out.print((i < msgs.length - 1) ? " " : "\n");
		}
	}
}
