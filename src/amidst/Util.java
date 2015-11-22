package amidst;

import java.io.File;

import amidst.logging.Log;

public class Util {
	private static File minecraftDirectory;

	public static void setMinecraftDirectory() {
		if (Options.instance.minecraftPath != null) {
			minecraftDirectory = new File(Options.instance.minecraftPath);
			if (minecraftDirectory.exists() && minecraftDirectory.isDirectory()) {
				return;
			}
			Log.w("Unable to set Minecraft directory 	 to: "
					+ minecraftDirectory
					+ " as that location does not exist or is not a folder.");
		}
		File mcDir = null;
		File homeDirectory = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory()) {
				mcDir = new File(appData, ".minecraft");
			}
		} else if (os.contains("mac")) {
			mcDir = new File(homeDirectory,
					"Library/Application Support/minecraft");
		}
		minecraftDirectory = (mcDir != null) ? mcDir : new File(homeDirectory,
				".minecraft");
	}

	private static File minecraftLibraries;

	public static void setMinecraftLibraries() {
		minecraftLibraries = (Options.instance.minecraftLibraries == null) ? new File(
				minecraftDirectory, "libraries") : new File(
				Options.instance.minecraftLibraries);
	}

	private static File profileDirectory;

	public static void setProfileDirectory(String gameDirectory) {
		profileDirectory = getProfileDirectory(gameDirectory);
	}

	private static File getProfileDirectory(String gameDirectory) {
		if (gameDirectory != null && !gameDirectory.isEmpty()) {
			File profileDirectory = new File(gameDirectory);
			if (profileDirectory.exists() && profileDirectory.isDirectory()) {
				return profileDirectory;
			}
			Log.w("Unable to set Profile directory 	 to: " + profileDirectory
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
