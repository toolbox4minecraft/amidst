package amidst.mojangapi.file;

import amidst.documentation.Immutable;

@Immutable
public enum NetworkPathUtils {
	;

	/*-
	 * UNC path:                \\server\share\foo
	 * Absolute path:           C:\foo
	 * Relative path:           foo
	 * Directory_relative path: \foo
	 * Drive_relative path:     C:foo
	 */
	/**
	 * @return true if the root directory is a UNC path (Uniform Naming
	 *         Convention, sometimes called a "network path")
	 */
	public static boolean isUNC(String path) {
		return path.startsWith("\\\\");
	}
}
