package amidst;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import amidst.logging.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Util {
	public static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	public static final Gson GSON = new Gson();

	private static String osString;

	public static String getOs() {
		if (osString == null) {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win"))
				osString = "windows";
			else if (os.contains("mac"))
				osString = "osx";
			else
				osString = "linux";
		}
		return osString;
	}

	public static void showError(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		String trace = baos.toString();

		e.printStackTrace();

		JOptionPane.showMessageDialog(null, trace, e.toString(),
				JOptionPane.ERROR_MESSAGE);
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Log.printTraceStack(e);
		}
	}

	public static File minecraftDirectory;

	public static void setMinecraftDirectory() {
		if (Options.instance.minecraftPath != null) {
			minecraftDirectory = new File(Options.instance.minecraftPath);
			if (minecraftDirectory.exists() && minecraftDirectory.isDirectory())
				return;
			Log.w("Unable to set Minecraft directory 	 to: "
					+ minecraftDirectory
					+ " as that location does not exist or is not a folder.");
		}
		File mcDir = null;
		File homeDirectory = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory())
				mcDir = new File(appData, ".minecraft");
		} else if (os.contains("mac")) {
			mcDir = new File(homeDirectory,
					"Library/Application Support/minecraft");
		}
		minecraftDirectory = (mcDir != null) ? mcDir : new File(homeDirectory,
				".minecraft");
	}

	public static File minecraftLibraries;

	public static void setMinecraftLibraries() {
		minecraftLibraries = (Options.instance.minecraftLibraries == null) ? new File(
				minecraftDirectory, "libraries") : new File(
				Options.instance.minecraftLibraries);
	}

	public static File profileDirectory;

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

	private static final int TEMP_DIR_ATTEMPTS = 1000;

	/**
	 * Guava's method, moved here to avoid a huge dependency TODO: maybe switch
	 * to JDK 7 to use its java.nio.file.Files#createTempDirectory()
	 */
	public static File createTempDir() {
		return getTempDir(System.currentTimeMillis() + "");
	}

	public static File getTempDir(String name) {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = name + "-";
		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.isDirectory() || tempDir.mkdir())
				return tempDir;
		}

		throw new IllegalStateException("Failed to create directory within "
				+ TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to "
				+ baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
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

	public static <T> T readObject(String path, final Class<T> clazz)
			throws IOException {
		return readObject(new File(path), clazz);
	}

	public static File getSavesDirectory() {
		if (profileDirectory != null) {
			return new File(profileDirectory, "saves");
		} else {
			return new File(minecraftDirectory, "saves");
		}
	}
}
