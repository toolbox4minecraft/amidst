package amidst.mojangapi.file.directory;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.ProfileType;
import amidst.util.OperatingSystemDetector;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * API for interacting with the {@code .minecraft} user directory.
 */
@Immutable
public class DotMinecraftDirectory {

	private final Path root;
	private final Path libraries;
	private final Path versions;
	private final Path launcherProfilesJson;

	public DotMinecraftDirectory(Path root) {
		this.root = root;
		this.libraries = root.resolve("libraries");
		this.versions = root.resolve("versions");
		this.launcherProfilesJson = root.resolve("launcher_profiles.json");
	}

	public DotMinecraftDirectory(@NotNull Path root, Path libraries, Path versions, Path launcherProfilesJson) {
		this.root = root;
		this.libraries = libraries == null ? root.resolve("libraries") : libraries;
		this.versions = versions == null ? root.resolve("versions") : versions;
		this.launcherProfilesJson = launcherProfilesJson == null ? root.resolve("launcher_profiles.json") : launcherProfilesJson;
	}

	/**
	 * Checks if the paths in this {@code .minecraft} directory are 'valid'.
	 * <p>
	 * Valid means that {@code root}, {@code libraries}, and {@code versions}
	 * are {@link Files#isDirectory(Path, LinkOption...) directories} and
	 * {@code launcherProfilesJson} is a {@link Files#isRegularFile(Path, LinkOption...) regular file}.
	 *
	 * @return {@code true} for valid directories, {@code false} otherwise
	 */
	public boolean isValid() {
		return Files.isDirectory(root)
				&& Files.isDirectory(libraries)
				&& Files.isDirectory(versions)
				&& Files.isRegularFile(launcherProfilesJson);
	}

	public ProfileDirectory asProfileDirectory() {
		return new ProfileDirectory(root);
	}

	public Path getRoot() {
		return root;
	}

	public Path getLibraries() {
		return libraries;
	}

	public Path getVersions() {
		return versions;
	}

	public Path getLauncherProfilesJson() {
		return launcherProfilesJson;
	}

	@NotNull
	public static DotMinecraftDirectory createDotMinecraftDirectory(Path overrideDirectory) {
		if (overrideDirectory != null) {
			if (Files.isDirectory(overrideDirectory)) {
				return new DotMinecraftDirectory(overrideDirectory);
			}

			AmidstLogger.warn(
					"Unable to set Minecraft directory to: {} as that location does not exist or is not a folder.",
					overrideDirectory);
		}

		return new DotMinecraftDirectory(getMinecraftDirectory());
	}

	@NotNull
	public static Path getMinecraftDirectory() {
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
	public static ProfileDirectory createValidProfileDirectory(
			LauncherProfileJson launcherProfileJson,
			DotMinecraftDirectory dotMinecraftDirectory) throws FileNotFoundException {
		String gameDir = launcherProfileJson.getGameDir();
		ProfileDirectory result = gameDir != null ? new ProfileDirectory(Paths.get(gameDir)) : dotMinecraftDirectory.asProfileDirectory();
		if (result.isValid()) {
			return result;
		} else {
			throw new FileNotFoundException(
					"cannot find valid profile directory for launcher profile '" + launcherProfileJson.getName() + "': "
							+ gameDir);
		}
	}

	@NotNull
	public static VersionDirectory createValidVersionDirectory(
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
			VersionDirectory result = null;
			for (Version version : versionList.getVersions()) {
				if (releaseTypesToSearch.contains(version.getType())) {
					VersionDirectory versionDirectory = createVersionDirectory(dotMinecraftDirectory, version.getId());
					if (versionDirectory.isValid()) {
						result = versionDirectory;
						break;
					}
				}
			}
			if (result != null) {
				return result;
			}
		}

		throw new FileNotFoundException(
				"cannot find valid version directory for launcher profile '" + launcherProfileJson.getName() + "'");
	}

	@NotNull
	public static VersionDirectory createValidVersionDirectory(DotMinecraftDirectory dotMinecraftDirectory, String versionId)
			throws FileNotFoundException {
		VersionDirectory versionDirectory = createVersionDirectory(dotMinecraftDirectory, versionId);
		if (versionDirectory.isValid()) {
			return versionDirectory;
		} else {
			throw new FileNotFoundException("cannot find valid version directory for version id '" + versionId + "'");
		}
	}

	@NotNull
	public static VersionDirectory createVersionDirectory(DotMinecraftDirectory dotMinecraftDirectory, String versionId) {
		Path versions = dotMinecraftDirectory.getVersions();
		Path jar = versions.resolve(versionId + "/" + versionId + ".jar");
		Path json = versions.resolve(versionId + "/" + versionId + ".json");
		return new VersionDirectory(jar, json);
	}
}
