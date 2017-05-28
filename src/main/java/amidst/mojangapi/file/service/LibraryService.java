package amidst.mojangapi.file.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.mojangapi.file.json.version.LibraryRuleJson;
import amidst.mojangapi.file.json.version.LibraryRuleOsJson;
import amidst.util.OperatingSystemDetector;

@Immutable
public class LibraryService {
	private static final String ACTION_ALLOW = "allow";

	@NotNull
	public List<URL> getLibraryUrls(File librariesDirectory, List<LibraryJson> libraries) {
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

	private File getLibraryFile(File librariesDirectory, LibraryJson library) {
		try {
			if (isLibraryActive(library, getOs(), OperatingSystemDetector.getVersion())) {
				return getLibraryFile(getLibrarySearchPath(librariesDirectory, library.getName()));
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	private String getOs() {
		if (OperatingSystemDetector.isWindows()) {
			return "windows";
		} else if (OperatingSystemDetector.isMac()) {
			return "osx";
		} else {
			return "linux";
		}
	}

	private File getLibrarySearchPath(File librariesDirectory, String libraryName) {
		String result = librariesDirectory.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (String element : split) {
			result += element + "/";
		}
		return new File(result);
	}

	private File getLibraryFile(File librarySearchPath) {
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

	private File getFirstFileWithExtension(File[] files, String extension) {
		for (File libraryFile : files) {
			if (getFileExtension(libraryFile.getName()).equals(extension)) {
				return libraryFile;
			}
		}
		return null;
	}

	private String getFileExtension(String fileName) {
		String extension = "";
		int q = fileName.lastIndexOf('.');
		if (q > 0) {
			extension = fileName.substring(q + 1);
		}
		return extension;
	}

	/**
	 * Note, that multiple rules might be applicable. We take the last
	 * applicable rule. However, this might be wrong so we need to take the most
	 * specific rule? For now this works fine.
	 */
	private boolean isLibraryActive(LibraryJson libraryJson, String os, String version) {
		List<LibraryRuleJson> rules = libraryJson.getRules();
		if (rules.isEmpty()) {
			return true;
		}
		boolean result = false;
		for (LibraryRuleJson rule : rules) {
			if (isApplicable(os, version, rule)) {
				result = isAllowed(rule);
			}
		}
		return result;
	}

	private boolean isApplicable(String os, String version, LibraryRuleJson rule) {
		LibraryRuleOsJson osRule = rule.getOs();
		return osRule == null || isApplicable(os, version, osRule);
	}

	private boolean isApplicable(String os, String version, LibraryRuleOsJson osRule) {
		String nameInJson = osRule.getName();
		String versionInJson = osRule.getVersion();
		return nameInJson.equals(os) && (versionInJson == null || Pattern.matches(versionInJson, version));
	}

	private boolean isAllowed(LibraryRuleJson rule) {
		return rule.getAction().equals(ACTION_ALLOW);
	}
}
