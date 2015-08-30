package amidst.utilties;

import java.io.File;

public class FileSystemUtils {
	private FileSystemUtils() {
	}

	public static File getFirstFileWithExtension(File[] files, String extension) {
		for (File libraryFile : files) {
			if (getFileExtension(libraryFile.getName()).equals(extension)) {
				return libraryFile;
			}
		}
		return null;
	}

	public static String getFileExtension(String fileName) {
		String extension = "";
		int q = fileName.lastIndexOf('.');
		if (q > 0) {
			extension = fileName.substring(q + 1);
		}
		return extension;
	}

	public static String getFileNameWithoutExtension(String fileName,
			String extension) {
		String[] split = fileName.split("\\.");
		if (split.length == 2 && split[0].indexOf('/') == -1
				&& split[1].equals(extension)) {
			return split[0];
		} else {
			return null;
		}
	}
}
