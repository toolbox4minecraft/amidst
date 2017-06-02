package amidst.mojangapi.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;
import amidst.util.OperatingSystemDetector;

@Immutable
public class DotMinecraftDirectoryService {
	private final FilenameService filenameService = new FilenameService();

	@NotNull
	public DotMinecraftDirectory createCustomDotMinecraftDirectory(
			File libraries,
			File saves,
			File versions,
			File launcherProfilesJson) throws DotMinecraftDirectoryNotFoundException {
		return validate(
				DotMinecraftDirectory
						.newCustom(getMinecraftDirectory(), libraries, saves, versions, launcherProfilesJson));
	}

	@NotNull
	public DotMinecraftDirectory createDotMinecraftDirectory(String preferredDotMinecraftDirectory)
			throws DotMinecraftDirectoryNotFoundException {
		return validate(new DotMinecraftDirectory(findDotMinecraftDirectory(preferredDotMinecraftDirectory)));
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
	private File findDotMinecraftDirectory(String preferredDotMinecraftDirectory) {
		if (preferredDotMinecraftDirectory != null) {
			File result = new File(preferredDotMinecraftDirectory);
			if (result.isDirectory()) {
				return result;
			} else {
				AmidstLogger.warn(
						"Unable to set Minecraft directory to: " + result
								+ " as that location does not exist or is not a folder.");
				return getMinecraftDirectory();
			}
		} else {
			return getMinecraftDirectory();
		}
	}

	@NotNull
	private File getMinecraftDirectory() {
		File home = new File(System.getProperty("user.home", "."));
		if (OperatingSystemDetector.isWindows()) {
			File appData = new File(System.getenv("APPDATA"));
			if (appData.isDirectory()) {
				return new File(appData, ".minecraft");
			}
		} else if (OperatingSystemDetector.isMac()) {
			return new File(home, "Library/Application Support/minecraft");
		}
		return new File(home, ".minecraft");
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
			return new ProfileDirectory(new File(gameDir));
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
		if (lastVersionId != null) {
			VersionDirectory result = createVersionDirectory(dotMinecraftDirectory, lastVersionId);
			if (result.isValid()) {
				return result;
			} else {
				// error
			}
		} else {
			VersionDirectory result = tryFindFirstValidVersionDirectory(
					launcherProfileJson.getAllowedReleaseTypes(),
					versionList,
					dotMinecraftDirectory);
			if (result != null) {
				return result;
			} else {
				// error
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
	public VersionDirectory createValidVersionDirectory(File jar, File json) throws FileNotFoundException {
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
		File versions = dotMinecraftDirectory.getVersions();
		File jar = filenameService.getClientJarFile(versions, versionId);
		File json = filenameService.getClientJsonFile(versions, versionId);
		return new VersionDirectory(jar, json);
	}

	public List<VersionDirectory> findInstalledValidVersionDirectories(DotMinecraftDirectory dotMinecraftDirectory) {
		return listFiles(dotMinecraftDirectory.getVersions())
				.stream()
				.filter(File::isDirectory)
				.map(File::getName)
				.map(id -> createVersionDirectory(dotMinecraftDirectory, id))
				.filter(VersionDirectory::isValid)
				.collect(Collectors.toList());
	}

	private List<File> listFiles(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			return Arrays.asList(files);
		} else {
			return Collections.emptyList();
		}
	}
}
