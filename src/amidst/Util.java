package amidst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import amidst.logging.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Util {
	public static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	public static final Gson GSON = new Gson();

	public static String getOs() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "windows";
		} else if (os.contains("mac")) {
			return "osx";
		} else {
			return "linux";
		}
	}

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

	public static void setProfileDirectory(String gameDir) {
		if (gameDir != null && !gameDir.isEmpty()) {
			profileDirectory = new File(gameDir);
			if (profileDirectory.exists() && profileDirectory.isDirectory())
				return;
			Log.w("Unable to set Profile directory 	 to: " + profileDirectory
					+ " as that location does not exist or is not a folder.");
		}
		profileDirectory = null;
	}

	public static <T> T readObject(BufferedReader reader, final Class<T> clazz)
			throws JsonIOException, JsonSyntaxException {
		return GSON.fromJson(reader, clazz);
	}

	public static <T> T readObject(File path, final Class<T> clazz)
			throws IOException, JsonIOException, JsonSyntaxException {
		final BufferedReader reader = new BufferedReader(new FileReader(path));
		T object = GSON.fromJson(reader, clazz);
		reader.close();
		return object;
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
