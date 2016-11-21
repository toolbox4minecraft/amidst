package amidst.util;

import amidst.documentation.Immutable;

@Immutable
public enum FileExtensionChecker {
	;

	/**
	 * Checks whether the given filename has the given file extension.
	 * 
	 * @param filename The filename
	 * @param extension The expected file extension without the leading "."
	 */
	public static boolean hasFileExtension(String filename, String extension) {
		return filename.toLowerCase().endsWith("." + extension);
	}
}
