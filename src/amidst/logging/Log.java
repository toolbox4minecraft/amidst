package amidst.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.swing.JOptionPane;

import MoF.FinderWindow;
import amidst.gui.CrashDialog;

public class Log {
	private static Object logLock = new Object();
	private static HashMap<String, LogListener> listeners = new HashMap<String, LogListener>();
	public static boolean isUsingAlerts = true;
	public static boolean isShowingDebug = true;
	
	static {
		addListener("master", new LogRecorder());
	}
	
	public static void addListener(String name, LogListener listener) {
		synchronized (logLock) {
			listeners.put(name,  listener);
		}
	}
	public static void removeListener(String name) {
		synchronized (logLock) {
			listeners.remove(name);
		}
	}
	public static void printTraceStack(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String exceptionText = stringWriter.toString();
		w(exceptionText);
	}
	
	public static void i(Object... s) {
		synchronized (logLock) {
			printWithTag("info", s);
			if (listeners.size() != 0)
				for (LogListener listener : listeners.values())
					listener.info(s);
			}
	}
	public static void debug(Object... s) {
		if (!isShowingDebug)
			return;
		synchronized (logLock) {
			printWithTag("debug", s);
			if (listeners.size() != 0)
				for (LogListener listener : listeners.values())
					listener.debug(s);
		}
	}
	public static void w(Object... s) {
		synchronized (logLock) {
			printWithTag("warning", s);
			if (listeners.size() != 0)
				for (LogListener listener : listeners.values())
					listener.warning(s);
		}
	}
	
	public static void e(Object... s) {
		synchronized (logLock) {
			printWithTag("error", s);
			if (isUsingAlerts)
				JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
			if (listeners.size() != 0)
				for (LogListener listener : listeners.values())
					listener.error(s);
		}
	}
	
	public static void crash(String message) {
		crash(null, message);
	}
	public static void crash(Throwable e, String message) {
		synchronized (logLock) {
			printWithTag("crash", message);
			String exceptionText = "";
			if (e != null) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				exceptionText = stringWriter.toString();
				printWithTag("crash", exceptionText);
			}
			
			if (listeners.size() != 0)
				for (LogListener listener : listeners.values())
					listener.crash(e, exceptionText, message);
			
			
			new CrashDialog(message);
			if (FinderWindow.instance != null)
				FinderWindow.instance.dispose();
			//System.exit(0);
		}
	}
	
	private static void printWithTag(String tag, Object... msgs) {
		System.out.print("[" + tag + "] ");
		for (int i = 0; i < msgs.length; i++) {
			System.out.print(msgs[i]);
			System.out.print((i < msgs.length - 1) ? " " : "\n");
		}
	}
}
