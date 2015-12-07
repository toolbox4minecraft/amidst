package amidst.minecraft;

import java.io.File;

import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;

// TODO: make this non-static
@Deprecated
public class LocalMinecraftInstallation {
	private static File profileDirectory;
	private static DotMinecraftDirectory dotMinecraftDirectory;

	public static void set(DotMinecraftDirectory dotMinecraftDirectory) {
		LocalMinecraftInstallation.dotMinecraftDirectory = dotMinecraftDirectory;

	}

	public static void initProfileDirectory(String gameDirectory) {
		profileDirectory = getProfileDirectory(gameDirectory);
	}

	private static File getProfileDirectory(String gameDirectory) {
		if (gameDirectory != null) {
			File result = new File(gameDirectory);
			if (result.isDirectory()) {
				return result;
			}
			Log.w("Unable to set Profile directory to: " + result
					+ " as that location does not exist or is not a folder.");
		}
		return null;
	}

	public static File getSavesDirectory() {
		if (profileDirectory != null) {
			return new File(profileDirectory, "saves");
		} else {
			return new File(dotMinecraftDirectory.getRoot(), "saves");
		}
	}

	public static File getMinecraftLibraries() {
		return dotMinecraftDirectory.getLibraries();
	}
}
