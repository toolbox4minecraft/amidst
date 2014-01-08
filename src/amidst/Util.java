package amidst;

import javax.swing.*;

import amidst.logging.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Util {
	/** Shows an error message for an exception
	 * @param e the exception for which the stachtrace is to be shown
	 */
	public static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
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
		
		JOptionPane.showMessageDialog(
			null,
			trace,
			e.toString(),
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
			Log.w("Unable to set Minecraft directory 	 to: " + minecraftDirectory + " as that location does not exist or is not a folder.");
		}
		File mcDir = null;
		File homeDirectory = new File(System.getProperty("user.home", "."));
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory())
				mcDir = new File(appData, ".minecraft");
		} else if (os.contains("mac")) {
			mcDir = new File(homeDirectory, "Library/Application Support/minecraft");
		}
		minecraftDirectory = (mcDir != null) ? mcDir : new File(homeDirectory, ".minecraft");
	}
	
	
	public static int makeColor(int r, int g, int b) {
		int color = 0xFF000000;
		color |= 0xFF0000 & (r << 16);
		color |= 0xFF00 & (g << 8);
		color |= 0xFF & b;
		return color;
	}
	public static int mcColor(int color) {
		return 0xFF000000 | color;
	}
	
	private static final int TEMP_DIR_ATTEMPTS = 1000;
	
	/** Guava's method, moved here to avoid a huge dependency
	 * TODO: maybe switch to JDK 7 to use its java.nio.file.Files#createTempDirectory()
	 */
	public static File createTempDir() {
		return getTempDir(System.currentTimeMillis() + "");
	}
	
	public static File getTempDir(String name) {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = name + "-";
		for (int counter=0; counter<TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.isDirectory() || tempDir.mkdir())
				return tempDir;
		}
		
		throw new IllegalStateException("Failed to create directory within "
			+ TEMP_DIR_ATTEMPTS + " attempts (tried "
			+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}


	public static <T> T readObject(BufferedReader reader, final Class<T> clazz) throws JsonIOException, JsonSyntaxException {
		return Amidst.gson.fromJson(reader, clazz);
	}
	
	public static <T> T readObject(File path, final Class<T> clazz) throws IOException, JsonIOException, JsonSyntaxException {
		final BufferedReader reader = new BufferedReader(new FileReader(path));
		T object = Amidst.gson.fromJson(reader, clazz);
		reader.close();
		return object;
	}
	
	public static <T> T readObject(String path, final Class<T> clazz) throws IOException {
		return readObject(new File(path), clazz);
	}
	
	public static int deselectColor(int color) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);
		
		int average = (r + g + b);
		r = (r + average) / 30;
		g = (g + average) / 30;
		b = (b + average) / 30;
		return makeColor(r, g, b);
	}
	
	public static int lightenColor(int color, int brightness) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);
		
		r += brightness;
		g += brightness;
		b += brightness;
		
		if (r > 0xFF) r = 0xFF;
		if (g > 0xFF) g = 0xFF;
		if (b > 0xFF) b = 0xFF;
		
		return makeColor(r, g, b);
	}

	public static int greyScale(int color) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);
		int average = (r + g + b) / 3;
		return makeColor(average, average, average);
	}
}
