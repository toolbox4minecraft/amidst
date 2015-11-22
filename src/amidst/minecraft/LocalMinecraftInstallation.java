package amidst.minecraft;

import java.io.File;

import amidst.logging.Log;

public class LocalMinecraftInstallation {
	private static File minecraftDirectory;
	private static File minecraftLibraries;
	private static File profileDirectory;

	public static void initMinecraftDirectory(String minecraftDirectoryFileName) {
		if (minecraftDirectoryFileName != null) {
			minecraftDirectory = new File(minecraftDirectoryFileName);
			if (minecraftDirectory.exists() && minecraftDirectory.isDirectory()) {
				return;
			}
			Log.w("Unable to set Minecraft directory 	 to: "
					+ minecraftDirectory
					+ " as that location does not exist or is not a folder.");
		}
		File homeDirectory = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();
		minecraftDirectory = getMinecraftDirectory(homeDirectory, os);
		if (minecraftDirectory == null) {
			minecraftDirectory = new File(homeDirectory, ".minecraft");
		}
	}

	private static File getMinecraftDirectory(File homeDirectory, String os) {
		File mcDir = null;
		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory()) {
				mcDir = new File(appData, ".minecraft");
			}
		} else if (os.contains("mac")) {
			mcDir = new File(homeDirectory,
					"Library/Application Support/minecraft");
		}
		return mcDir;
	}

	public static void initMinecraftLibraries(String minecraftLibrariesFileName) {
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
