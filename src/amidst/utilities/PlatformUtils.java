package amidst.utilities;

import amidst.documentation.Immutable;

@Immutable
public enum PlatformUtils {
	;

	public static String getOs() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return "windows";
		} else if (osName.contains("mac")) {
			return "osx";
		} else {
			return "linux";
		}
	}
}
