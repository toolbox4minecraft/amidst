package amidst.mojangapi.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.utilities.FileSystemUtils;

@Immutable
public enum LibraryFinder {
	;

	public static List<URL> getLibraryUrls(File librariesDirectory,
			List<LibraryJson> libraries) {
		List<URL> result = new ArrayList<URL>();
		for (LibraryJson library : libraries) {
			File libraryFile = getLibraryFile(librariesDirectory, library);
			if (libraryFile != null) {
				try {
					result.add(libraryFile.toURI().toURL());
					Log.i("Found library: " + libraryFile);
				} catch (MalformedURLException e) {
					Log.w("Unable to convert library file to URL: "
							+ libraryFile);
					e.printStackTrace();
				}
			} else {
				Log.i("Skipping library: " + library.getName());
			}
		}
		return result;
	}

	private static File getLibraryFile(File librariesDirectory,
			LibraryJson library) {
		if (library.isActive()) {
			return getLibraryFile(getLibrarySearchPath(librariesDirectory,
					library.getName()));
		} else {
			return null;
		}
	}

	private static File getLibrarySearchPath(File librariesDirectory,
			String libraryName) {
		String result = librariesDirectory.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (int i = 0; i < split.length; i++) {
			result += split[i] + "/";
		}
		return new File(result);
	}

	private static File getLibraryFile(File librarySearchPath) {
		if (librarySearchPath.exists()) {
			File result = FileSystemUtils.getFirstFileWithExtension(
					librarySearchPath.listFiles(), "jar");
			if (result != null && result.exists()) {
				return result;
			} else {
				Log.w("Attempted to search for file at path: "
						+ librarySearchPath + " but found nothing. Skipping.");
				return null;
			}
		} else {
			Log.w("Failed attempt to load library at: " + librarySearchPath);
			return null;
		}
	}
}
