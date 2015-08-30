package amidst.utilties;

import java.io.File;
import java.util.zip.ZipEntry;

import amidst.Util;
import amidst.logging.Log;

public class FileSystemUtils {
	private FileSystemUtils() {
	}

	public static File getLibraryFile(String libraryName) {
		String searchPath = getSearchPath(libraryName);
		File searchPathFile = new File(searchPath);
		if (!searchPathFile.exists()) {
			Log.w("Failed attempt to load library at: " + searchPathFile);
			return null;
		}
		File libraryFile = getLibraryFile(searchPathFile);
		if (libraryFile == null) {
			Log.w("Attempted to search for file at path: " + searchPath
					+ " but found nothing. Skipping.");
		}
		return libraryFile;
	}

	private static File getLibraryFile(File searchPathFile) {
		File[] libraryFiles = searchPathFile.listFiles();
		for (File libraryFile : libraryFiles) {
			String extension = getFileExtension(libraryFile.getName());
			if (extension.equals("jar")) {
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

	private static String getSearchPath(String libraryName) {
		String result = Util.minecraftLibraries.getAbsolutePath() + "/";
		String[] pathSplit = getPathSplit(libraryName);
		for (int i = 0; i < pathSplit.length; i++) {
			result += pathSplit[i] + "/";
		}
		return result;
	}

	private static String[] getPathSplit(String libraryName) {
		String[] result = libraryName.split(":");
		result[0] = result[0].replace('.', '/');
		return result;
	}

	public static String getFileNameWithoutExtension(ZipEntry entry,
			String extension) {
		String[] nameSplit = entry.getName().split("\\.");
		if (!entry.isDirectory() && nameSplit.length == 2
				&& nameSplit[0].indexOf('/') == -1
				&& nameSplit[1].equals(extension)) {
			return nameSplit[0];
		} else {
			return null;
		}
	}
}
