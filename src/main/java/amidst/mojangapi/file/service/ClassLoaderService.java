package amidst.mojangapi.file.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.mojangapi.file.json.version.LibraryRuleJson;
import amidst.mojangapi.file.json.version.LibraryRuleOsJson;
import amidst.util.OperatingSystemDetector;

@Immutable
public class ClassLoaderService {
	private static final String ACTION_ALLOW = "allow";

	@NotNull
	public URLClassLoader createClassLoader(File librariesDirectory, List<LibraryJson> libraries, File versionJarFile)
			throws MalformedURLException {
		List<URL> classLoaderUrls = getAllClassLoaderUrls(librariesDirectory, libraries, versionJarFile);
		return new URLClassLoader(classLoaderUrls.toArray(new URL[classLoaderUrls.size()]));
	}

	@NotNull
	private List<URL> getAllClassLoaderUrls(File librariesDirectory, List<LibraryJson> libraries, File versionJarFile)
			throws MalformedURLException {
		List<URL> result = new LinkedList<>(getLibraryUrls(librariesDirectory, libraries));
		result.add(versionJarFile.toURI().toURL());
		return Collections.unmodifiableList(result);
	}

	@NotNull
	private List<URL> getLibraryUrls(File librariesDirectory, List<LibraryJson> libraries) {
		List<URL> result = new ArrayList<>();
		AmidstLogger.info("Loading libraries.");
		for (LibraryJson library : libraries) {
			if (isLibraryActive(library)) {
				Optional<File> libraryFile = getLibraryFile(librariesDirectory, library);
				if (libraryFile.isPresent()) {
					try {
						URL libraryUrl = libraryFile.get().toURI().toURL();
						result.add(libraryUrl);
						AmidstLogger.info("Found library " + library.getName() + " at " + libraryUrl);
					} catch (MalformedURLException e) {
						AmidstLogger.warn(e, "Skipping erroneous library " + library.getName());
					}
				} else {
					AmidstLogger.warn("Skipping missing library " + library.getName());
				}
			} else {
				AmidstLogger.info("Skipping inactive library " + library.getName());
			}
		}
		AmidstLogger.info("Finished loading libraries.");
		return result;
	}

	private boolean isLibraryActive(LibraryJson library) {
		return isLibraryActive(getOsName(), OperatingSystemDetector.getVersion(), library.getRules());
	}

	private String getOsName() {
		if (OperatingSystemDetector.isWindows()) {
			return "windows";
		} else if (OperatingSystemDetector.isMac()) {
			return "osx";
		} else {
			return "linux";
		}
	}

	/**
	 * Note, that multiple rules might be applicable. We take the last
	 * applicable rule. However, this might be wrong so we need to take the most
	 * specific rule? For now this works fine.
	 */
	private boolean isLibraryActive(String osName, String osVersion, List<LibraryRuleJson> rules) {
		if (rules.isEmpty()) {
			return true;
		}
		boolean result = false;
		for (LibraryRuleJson rule : rules) {
			if (isApplicable(osName, osVersion, rule)) {
				result = isAllowed(rule);
			}
		}
		return result;
	}

	private boolean isApplicable(String osName, String osVersion, LibraryRuleJson rule) {
		LibraryRuleOsJson osRule = rule.getOs();
		return osRule == null || Objects.equals(osRule.getName(), osName)
				&& (osRule.getVersion() == null || Pattern.matches(osRule.getVersion(), osVersion));
	}

	private boolean isAllowed(LibraryRuleJson rule) {
		return Objects.equals(rule.getAction(), ACTION_ALLOW);
	}

	private Optional<File> getLibraryFile(File librariesDirectory, LibraryJson library) {
		return Arrays
				.stream(getLibrarySearchFiles(librariesDirectory, library))
				.filter(f -> hasFileExtension(f, "jar"))
				.findFirst()
				.filter(File::exists);
	}

	private File[] getLibrarySearchFiles(File librariesDirectory, LibraryJson library) {
		return getLibrarySearchFiles(getLibrarySearchPath(librariesDirectory.getAbsolutePath(), library.getName()));
	}

	private File getLibrarySearchPath(String librariesDirectory, String libraryName) {
		String result = librariesDirectory + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (String element : split) {
			result += element + "/";
		}
		return new File(result);
	}

	private File[] getLibrarySearchFiles(File librarySearchPath) {
		if (librarySearchPath.isDirectory()) {
			return librarySearchPath.listFiles();
		} else {
			return new File[0];
		}
	}

	private boolean hasFileExtension(File file, String extension) {
		return getFileExtension(file.getName()).equals(extension);
	}

	private String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			return fileName.substring(index + 1);
		} else {
			return "";
		}
	}
}
