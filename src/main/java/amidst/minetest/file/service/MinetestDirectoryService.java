package amidst.minetest.file.service;

import java.io.File;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.minetest.file.directory.MinetestDirectory;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.util.OperatingSystemDetector;

@Immutable
public class MinetestDirectoryService {

	@NotNull
	public MinetestDirectory createMinetestDirectory(String preferredMinetestDirectory)
			throws DotMinecraftDirectoryNotFoundException {
		return validate(new MinetestDirectory(findMinetestDirectory(preferredMinetestDirectory)));
	}

	@NotNull
	private MinetestDirectory validate(MinetestDirectory minecraftDirectory)
			throws DotMinecraftDirectoryNotFoundException {
		if (minecraftDirectory.isValid()) {
			return minecraftDirectory;
		} else {
			throw new DotMinecraftDirectoryNotFoundException(
					"invalid mintest directory at: '" + minecraftDirectory.getRoot() + "'");
		}
	}

	@NotNull
	private File findMinetestDirectory(String preferredMinetestDirectory) throws DotMinecraftDirectoryNotFoundException {
		
		File result = null;
		
		if (preferredMinetestDirectory != null) {
			result = new File(preferredMinetestDirectory);
			if (!result.isDirectory()) {
				AmidstLogger.warn(
						"Unable to set Minetest directory to: " + result
								+ " as that location does not exist or is not a folder.");
				result = getMinetestDirectory();
			}
		} else {
			result = getMinetestDirectory();
		}
		
		if (result == null) throw new DotMinecraftDirectoryNotFoundException("unable to locate minetest directory");
		return result;
	}

	private File getMinetestDirectory() {
		
		File result = null;
		
		File home = new File(System.getProperty("user.home", "."));
		if (OperatingSystemDetector.isWindows()) {
			result = new File("C:\\minetest");
			if (!result.isDirectory()) result = new File("C:\\games\\minetest");
			if (!result.isDirectory()) result = null;
		
		} else if (OperatingSystemDetector.isMac()) {
			// /home/username/.minetest/
		}
		
		if (result == null)        result = new File(home, ".minetest");
		if (!result.isDirectory()) result = new File(home, "minetest");
		
		if (!result.isDirectory()) result = null;
		
		return result;
	}
}
