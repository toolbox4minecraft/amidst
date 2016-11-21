package amidst.mojangapi.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.util.OperatingSystemDetector;

@Immutable
public enum LibraryFinder {
	;

	@NotNull
	public static List<URL> getLibraryUrls(File librariesDirectory, List<LibraryJson> libraries) {
		List<URL> result = new ArrayList<>();
		for (LibraryJson library : libraries) {
			File libraryFile = getLibraryFile(librariesDirectory, library);
			if (libraryFile != null) {
				try {
					result.add(libraryFile.toURI().toURL());
					AmidstLogger.info("Found library: " + libraryFile);
				} catch (MalformedURLException e) {
					AmidstLogger.warn(e, "Unable to convert library file to URL: " + libraryFile);
				}
			} else {
				AmidstLogger.info("Skipping library: " + library.getName());
			}
		}
		return result;
	}

	private static File getLibraryFile(File librariesDirectory, LibraryJson library) {
		try {
			if (library.isActive(getOs(), OperatingSystemDetector.getVersion())) {
				return getLibraryFile(getLibrarySearchPath(librariesDirectory, library.getName()));
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	private static String getOs() {
		if (OperatingSystemDetector.isWindows()) {
			return "windows";
		} else if (OperatingSystemDetector.isMac()) {
			return "osx";
		} else {
			return "linux";
		}
	}

	private static File getLibrarySearchPath(File librariesDirectory, String libraryName) {
		String result = librariesDirectory.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (String element : split) {
			result += element + "/";
		}
		return new File(result);
	}

	private static File getLibraryFile(File librarySearchPath) {
		if (librarySearchPath.exists()) {
			File result = getFirstFileWithExtension(librarySearchPath.listFiles(), "jar");
			if (result != null && result.exists()) {
				return result;
			} else {
				AmidstLogger.warn(
						"Attempted to search for file at path: " + librarySearchPath + " but found nothing. Skipping.");
				return null;
			}
		} else {
			AmidstLogger.warn("Failed attempt to load library at: " + librarySearchPath);
			return null;
		}
	}

	private static File getFirstFileWithExtension(File[] files, String extension) {
		for (File libraryFile : files) {
			if (getFileExtension(libraryFile.getName()).equals(extension)) {
				return libraryFile;
			}
		}
		return null;
	}

	private static String getFileExtension(String fileName) {
		String extension = "";
		int q = fileName.lastIndexOf('.');
		if (q > 0) {
			extension = fileName.substring(q + 1);
		}
		return extension;
	}
}
