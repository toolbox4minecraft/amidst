package amidst.mojangapi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.mojangapi.version.LibraryJson;
import amidst.utilities.FileSystemUtils;

public enum LibraryFinder {
	;

	public static List<URL> getLibraryUrls(List<LibraryJson> libraries) {
		List<URL> result = new ArrayList<URL>();
		for (LibraryJson library : libraries) {
			File libraryFile = getLibraryFile(library);
			if (libraryFile != null) {
				try {
					result.add(libraryFile.toURI().toURL());
					Log.i("Found library: " + libraryFile);
				} catch (MalformedURLException e) {
					Log.w("Unable to convert library file to URL with path: "
							+ libraryFile);
					e.printStackTrace();
				}
			} else {
				Log.i("Skipping library: " + library.getName());
			}
		}
		return result;
	}

	private static File getLibraryFile(LibraryJson library) {
		if (library.isActive()) {
			File result = getLibraryFile(library.getName());
			if (result != null && result.exists()) {
				return result;
			}
		}
		return null;
	}

	private static File getLibraryFile(String libraryName) {
		String searchPath = getLibrarySearchPath(libraryName);
		File searchPathFile = new File(searchPath);
		if (!searchPathFile.exists()) {
			Log.w("Failed attempt to load library at: " + searchPathFile);
			return null;
		}
		File result = FileSystemUtils.getFirstFileWithExtension(
				searchPathFile.listFiles(), "jar");
		if (result == null) {
			Log.w("Attempted to search for file at path: " + searchPath
					+ " but found nothing. Skipping.");
		}
		return result;
	}

	private static String getLibrarySearchPath(String libraryName) {
		String result = LocalMinecraftInstallation.getMinecraftLibraries()
				.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (int i = 0; i < split.length; i++) {
			result += split[i] + "/";
		}
		return result;
	}
}
