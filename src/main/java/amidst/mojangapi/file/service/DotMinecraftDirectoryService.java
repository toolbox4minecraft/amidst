package amidst.mojangapi.file.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.launcherprofiles.ProfileType;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;
import amidst.util.OperatingSystemDetector;

@Immutable
public class DotMinecraftDirectoryService {
	private final FilenameService filenameService = new FilenameService();

	@NotNull
	public DotMinecraftDirectory createCustomDotMinecraftDirectory(
			Path libraries,
			Path saves,
			Path versions,
			Path launcherProfilesJson) throws DotMinecraftDirectoryNotFoundException {
		return validate(
				DotMinecraftDirectory
						.newCustom(getMinecraftDirectory(), libraries, saves, versions, launcherProfilesJson));
	}

	@NotNull
	public DotMinecraftDirectory createDotMinecraftDirectory(Path dotMinecraftDirectory2)
			throws DotMinecraftDirectoryNotFoundException {
		return validate(new DotMinecraftDirectory(findDotMinecraftDirectory(dotMinecraftDirectory2)));
	}

	@NotNull
	private DotMinecraftDirectory validate(DotMinecraftDirectory dotMinecraftDirectory)
			throws DotMinecraftDirectoryNotFoundException {
		if (dotMinecraftDirectory.isValid()) {
			return dotMinecraftDirectory;
		} else {
			throw new DotMinecraftDirectoryNotFoundException(
					"invalid '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "'");
		}
	}

	@NotNull
	private Path findDotMinecraftDirectory(Path preferredDotMinecraftDirectory) {
		if (preferredDotMinecraftDirectory != null) {
			if (Files.isDirectory(preferredDotMinecraftDirectory)) {
				return preferredDotMinecraftDirectory;
			} else {
				AmidstLogger.warn(
					"Unable to set Minecraft directory to: {} as that location does not exist or is not a folder.",
					preferredDotMinecraftDirectory);
				return getMinecraftDirectory();
			}
		} else {
			return getMinecraftDirectory();
		}
	}

	@NotNull
	private Path getMinecraftDirectory() {
		Path home = Paths.get(System.getProperty("user.home", "."));
		if (OperatingSystemDetector.isWindows()) {
			Path appData = Paths.get(System.getenv("APPDATA"));
			if (Files.isDirectory(appData)) {
				return appData.resolve(".minecraft");
			}
		} else if (OperatingSystemDetector.isMac()) {
			return home.resolve("Library/Application Support/minecraft");
		}
		return home.resolve(".minecraft");
	}

	@NotNull
	public LauncherProfilesJson readLauncherProfilesFrom(DotMinecraftDirectory dotMinecraftDirectory)
			throws FormatException,
			IOException {
		return JsonReader.readLocation(dotMinecraftDirectory.getLauncherProfilesJson(), LauncherProfilesJson.class);
	}

	@NotNull
	public ProfileDirectory createValidProfileDirectory(
			LauncherProfileJson launcherProfileJson,
			DotMinecraftDirectory dotMinecraftDirectory) throws FileNotFoundException {
		String gameDir = launcherProfileJson.getGameDir();
		ProfileDirectory result = createProfileDirectory(dotMinecraftDirectory, gameDir);
		if (result.isValid()) {
			return result;
		} else {
			throw new FileNotFoundException(
					"cannot find valid profile directory for launcher profile '" + launcherProfileJson.getName() + "': "
							+ gameDir);
		}
	}

	@NotNull
	private ProfileDirectory createProfileDirectory(DotMinecraftDirectory dotMinecraftDirectory, String gameDir) {
		if (gameDir != null) {
			return new ProfileDirectory(Paths.get(gameDir));
		} else {
			return dotMinecraftDirectory.asProfileDirectory();
		}
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(
			LauncherProfileJson launcherProfileJson,
			VersionList versionList,
			DotMinecraftDirectory dotMinecraftDirectory) throws FileNotFoundException {
		String lastVersionId = launcherProfileJson.getLastVersionId();

		List<ReleaseType> releaseTypesToSearch = null;
		if (lastVersionId == null) {
			releaseTypesToSearch = launcherProfileJson.getAllowedReleaseTypes();
		} else if (lastVersionId.equals("latest-release")) {
			releaseTypesToSearch = ProfileType.LATEST_RELEASE.getAllowedReleaseTypes().get();
		} else if (lastVersionId.equals("latest-snapshot")) {
			releaseTypesToSearch = ProfileType.LATEST_SNAPSHOT.getAllowedReleaseTypes().get();
		}

		if (releaseTypesToSearch == null) {
			VersionDirectory result = createVersionDirectory(dotMinecraftDirectory, lastVersionId);
			if (result.isValid()) {
				return result;
			}
		} else {
			VersionDirectory result = tryFindFirstValidVersionDirectory(
					releaseTypesToSearch,
					versionList,
					dotMinecraftDirectory);
			if (result != null) {
				return result;
			}
		}

		throw new FileNotFoundException(
				"cannot find valid version directory for launcher profile '" + launcherProfileJson.getName() + "'");
	}

	private VersionDirectory tryFindFirstValidVersionDirectory(
			List<ReleaseType> allowedReleaseTypes,
			VersionList versionList,
			DotMinecraftDirectory dotMinecraftDirectory) {
		for (Version version : versionList.getVersions()) {
			if (allowedReleaseTypes.contains(version.getType())) {
				VersionDirectory versionDirectory = createVersionDirectory(dotMinecraftDirectory, version.getId());
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(Path jar, Path json) throws FileNotFoundException {
		VersionDirectory versionDirectory = new VersionDirectory(jar, json);
		if (versionDirectory.isValid()) {
			return versionDirectory;
		} else {
			throw new FileNotFoundException(
					"cannot find valid version directory for jar: '" + jar + "', json: '" + json + "'");
		}
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(DotMinecraftDirectory dotMinecraftDirectory, String versionId)
			throws FileNotFoundException {
		VersionDirectory versionDirectory = createVersionDirectory(dotMinecraftDirectory, versionId);
		if (versionDirectory.isValid()) {
			return versionDirectory;
		} else {
			throw new FileNotFoundException("cannot find valid version directory for version id '" + versionId + "'");
		}
	}

	@NotNull
	private VersionDirectory createVersionDirectory(DotMinecraftDirectory dotMinecraftDirectory, String versionId) {
		Path versions = dotMinecraftDirectory.getVersions();
		Path jar = filenameService.getClientJarFile(versions, versionId);
		Path json = filenameService.getClientJsonFile(versions, versionId);
		return new VersionDirectory(jar, json);
	}

	public List<VersionDirectory> findInstalledValidVersionDirectories(DotMinecraftDirectory dotMinecraftDirectory) {
		return listFiles(dotMinecraftDirectory.getVersions())
				.filter(Files::isDirectory)
				.map(Path::getFileName)
				.map(id -> createVersionDirectory(dotMinecraftDirectory, id.toString()))
				.filter(VersionDirectory::isValid)
				.collect(Collectors.toList());
	}

	private Stream<Path> listFiles(Path directory) {
		if (Files.isDirectory(directory)) {
			try {
				return Files.list(directory);
			} catch (IOException e) {
				AmidstLogger.error(e, "Error while reading directory " + directory);
			}
		}
		return Stream.empty();
	}
}
