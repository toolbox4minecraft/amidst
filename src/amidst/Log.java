package amidst;

import javax.swing.JOptionPane;

public class Log {
	private static LogListener listener;
	private static boolean isUsingListener;
	public static boolean isUsingAlerts = true;
	
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
		if (isUsingAlerts)
			JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
		if (isUsingListener)
			listener.error(s);
	}
	public static void kill(Object... s) {
		printwithTag("kill", s);
		if (isUsingListener)
			listener.kill(s);
		if (isUsingAlerts)
			JOptionPane.showMessageDialog(null, s, "Fatal Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	
	public static void writeTo(LogListener l) {
		listener = l;
		isUsingListener = (l != null);
	}
	
	private static void printwithTag(String tag, Object... msgs) {
		System.out.print("[" + tag + "] ");
		for (int i = 0; i < msgs.length; i++) {
			System.out.print(msgs[i]);
			System.out.print((i < msgs.length - 1) ? " " : "\n");
		}
	}
}
