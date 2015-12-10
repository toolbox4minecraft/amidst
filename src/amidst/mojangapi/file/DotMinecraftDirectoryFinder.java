package amidst.mojangapi.file;

import java.io.File;

import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public enum DotMinecraftDirectoryFinder {
	;

	public static File find(String dotMinecraftCMDParameter) {
		if (dotMinecraftCMDParameter != null) {
			File result = new File(dotMinecraftCMDParameter);
			if (result.exists() && result.isDirectory()) {
				return result;
			} else {
				Log.w("Unable to set Minecraft directory to: "
						+ result
						+ " as that location does not exist or is not a folder.");
			}
		}
		return getMinecraftDirectory();
	}

	private static File getMinecraftDirectory() {
		File home = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory()) {
				return new File(appData, ".minecraft");
			}
		} else if (os.contains("mac")) {
			return new File(home, "Library/Application Support/minecraft");
		}
		return new File(home, ".minecraft");
	}
}
