package amidst.util;

import amidst.documentation.Immutable;

@Immutable
public enum OperatingSystemDetector {
	;

	private static String OS_NAME = System.getProperty("os.name").toLowerCase();
	private static String OS_VERSION = System.getProperty("os.version");

	public static boolean isWindows() {
		return OS_NAME.indexOf("win") >= 0;
	}

	public static boolean isMac() {
		return OS_NAME.indexOf("mac") >= 0;
	}

	public static boolean isUnix() {
		return OS_NAME.indexOf("nix") >= 0 || OS_NAME.indexOf("nux") >= 0 || OS_NAME.indexOf("aix") > 0;
	}

	public static boolean isSolaris() {
		return OS_NAME.indexOf("sunos") >= 0;
	}

	public static String getVersion() {
		return OS_VERSION;
	}
}
