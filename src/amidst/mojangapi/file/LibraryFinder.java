package amidst.mojangapi.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.utilities.FileSystemUtils;

// TODO: create class LibrariesDirectory?
public enum LibraryFinder {
	;

	public static List<URL> getLibraryUrls(
			DotMinecraftDirectory dotMinecraftDirectory,
			List<LibraryJson> libraries) {
		File librariesDirectory = dotMinecraftDirectory.getLibraries();
		List<URL> result = new ArrayList<URL>();
		for (LibraryJson library : libraries) {
			File libraryFile = getLibraryFile(librariesDirectory, library);
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

	private static File getLibraryFile(File librariesFile, LibraryJson library) {
		if (library.isActive()) {
			return getLibraryFile(librariesFile, library.getName());
		} else {
			return null;
		}
	}

	private static File getLibraryFile(File librariesFile, String libraryName) {
		File searchPath = getLibrarySearchPath(librariesFile, libraryName);
		if (searchPath.exists()) {
			File result = FileSystemUtils.getFirstFileWithExtension(
					searchPath.listFiles(), "jar");
			if (result != null && result.exists()) {
				return result;
			} else {
				Log.w("Attempted to search for file at path: " + searchPath
						+ " but found nothing. Skipping.");
				return null;
			}
		} else {
			Log.w("Failed attempt to load library at: " + searchPath);
			return null;
		}
	}

	private static File getLibrarySearchPath(File librariesFile,
			String libraryName) {
		String result = librariesFile.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (int i = 0; i < split.length; i++) {
			result += split[i] + "/";
		}
		return new File(result);
	}
}
