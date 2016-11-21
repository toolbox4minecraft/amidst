package amidst.mojangapi.file;

import java.io.File;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.util.OperatingSystemDetector;

@Immutable
public enum DotMinecraftDirectoryFinder {
	;

	@NotNull
	public static File find(String dotMinecraftCMDParameter) {
		if (dotMinecraftCMDParameter != null) {
			File result = new File(dotMinecraftCMDParameter);
			if (result.isDirectory()) {
				return result;
			} else {
				AmidstLogger.warn(
						"Unable to set Minecraft directory to: " + result
								+ " as that location does not exist or is not a folder.");
			}
		}
		return getMinecraftDirectory();
	}

	@NotNull
	private static File getMinecraftDirectory() {
		File home = new File(System.getProperty("user.home", "."));
		if (OperatingSystemDetector.isWindows()) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory()) {
				return new File(appData, ".minecraft");
			}
		} else if (OperatingSystemDetector.isMac()) {
			return new File(home, "Library/Application Support/minecraft");
		}
		return new File(home, ".minecraft");
	}
}
