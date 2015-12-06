package amidst.minecraft;

import java.io.File;

import amidst.logging.Log;
import amidst.mojangapi.DotMinecraftDirectoryFinder;

// TODO: make this non-static
@Deprecated
public class LocalMinecraftInstallation {
	private static File minecraftDirectory;
	private static File minecraftLibraries;
	private static File profileDirectory;

	public static void init(String minecraftDirectoryFileName,
			String minecraftLibrariesFileName) {
		minecraftDirectory = DotMinecraftDirectoryFinder
				.find(minecraftDirectoryFileName);
		if (minecraftLibrariesFileName == null) {
			minecraftLibraries = new File(minecraftDirectory, "libraries");
		} else {
			minecraftLibraries = new File(minecraftLibrariesFileName);
		}
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
			return new File(minecraftDirectory, "saves");
		}
	}

	public static File getMinecraftDirectory() {
		return minecraftDirectory;
	}

	public static File getMinecraftLibraries() {
		return minecraftLibraries;
	}
}
