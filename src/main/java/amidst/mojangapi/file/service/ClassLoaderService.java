package amidst.mojangapi.file.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
	public URLClassLoader createClassLoader(Path path, List<LibraryJson> libraries, Path path2)
			throws MalformedURLException {
		List<URL> classLoaderUrls = getAllClassLoaderUrls(path, libraries, path2);
		return new URLClassLoader(classLoaderUrls.toArray(new URL[classLoaderUrls.size()]));
	}

	@NotNull
	private List<URL> getAllClassLoaderUrls(Path path, List<LibraryJson> libraries, Path path2)
			throws MalformedURLException {
		List<URL> result = new ArrayList<>(getLibraryUrls(path, libraries));
		result.add(path2.toUri().toURL());
		return Collections.unmodifiableList(result);
	}

	@NotNull
	private List<URL> getLibraryUrls(Path path, List<LibraryJson> libraries) {
		List<URL> result = new ArrayList<>();
		AmidstLogger.info("Loading libraries.");
		for (LibraryJson library : libraries) {
			if (isLibraryActive(library)) {
				Optional<Path> libraryFile = getLibraryFile(path, library);
				if (libraryFile.isPresent()) {
					try {
						URL libraryUrl = libraryFile.get().toUri().toURL();
						result.add(libraryUrl);
						AmidstLogger.info("Found library " + library.getName() + " at " + libraryUrl);
					} catch (MalformedURLException e) {
						AmidstLogger.warn(e, "Skipping erroneous library {}", library.getName());
					}
				} else {
					AmidstLogger.warn("Skipping missing library {}", library.getName());
				}
			} else {
				AmidstLogger.info("Skipping inactive library {}", library.getName());
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

	private Optional<Path> getLibraryFile(Path path, LibraryJson library) {
		return getLibrarySearchFiles(path, library)
				.filter(f -> hasFileExtension(f, "jar"))
				.findFirst()
				.filter(Files::exists);
	}

	private Stream<Path> getLibrarySearchFiles(Path path, LibraryJson library) {
		return getLibrarySearchFiles(getLibrarySearchPath(path, library.getName()));
	}

	private Path getLibrarySearchPath(Path librariesDirectory, String libraryName) {
		Path path = librariesDirectory.toAbsolutePath();
		String separator = path.getFileSystem().getSeparator();
		boolean firstPart = true;
		for (String elem : libraryName.split(":")) {
			path = path.resolve(firstPart ? elem.replace(".", separator) : elem);
			firstPart = false;
		}
		return path;
	}

	private Stream<Path> getLibrarySearchFiles(Path librarySearchPath) {
		if (Files.isDirectory(librarySearchPath)) {
			try {
				return Files.list(librarySearchPath);
			} catch (IOException e) {
				AmidstLogger.error(e, "Error while reading library directory " + librarySearchPath);
			}
		}
		return Stream.empty();
	}

	private boolean hasFileExtension(Path file, String extension) {
		return getFileExtension(file.getFileName().toString()).equals(extension);
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
