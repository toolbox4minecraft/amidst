package amidst.mojangapi.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.util.OperatingSystemDetector;

@Immutable
public class DotMinecraftDirectoryService {
	private final FilenameService filenameService = new FilenameService();

	@NotNull
	public DotMinecraftDirectory createDotMinecraftDirectory(
			String preferredDotMinecraftDirectory,
			String preferredLibrariesDirectory) {
		File dotMinecraftDirectory = findDotMinecraftDirectory(preferredDotMinecraftDirectory);
		if (preferredLibrariesDirectory != null) {
			return new DotMinecraftDirectory(dotMinecraftDirectory, new File(preferredLibrariesDirectory));
		} else {
			return new DotMinecraftDirectory(dotMinecraftDirectory);
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
			throws MojangApiParsingException,
			IOException {
		return JsonReader.readLocation(dotMinecraftDirectory.getLauncherProfilesJson(), LauncherProfilesJson.class);
	}

	@NotNull
	public ProfileDirectory createValidProfileDirectory(
			LauncherProfileJson launcherProfileJson,
			DotMinecraftDirectory dotMinecraftDirectory) throws FileNotFoundException {
		String gameDir = launcherProfileJson.getGameDir();
		if (gameDir != null) {
			ProfileDirectory result = new ProfileDirectory(new File(gameDir));
			if (result.isValid()) {
				return result;
			} else {
				throw new FileNotFoundException(
						"cannot find valid profile directory for launcher profile '" + launcherProfileJson.getName()
								+ "': " + gameDir);
			}
		} else {
			return new ProfileDirectory(dotMinecraftDirectory.getRoot());
		}
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(
			LauncherProfileJson launcherProfileJson,
			VersionListJson versionList,
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
			VersionListJson versionList,
			DotMinecraftDirectory dotMinecraftDirectory) {
		for (VersionListEntryJson version : versionList.getVersions()) {
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
	public VersionDirectory createVersionDirectory(DotMinecraftDirectory dotMinecraftDirectory, String versionId) {
		File versions = dotMinecraftDirectory.getVersions();
		File jar = filenameService.getClientJarFile(versions, versionId);
		File json = filenameService.getClientJsonFile(versions, versionId);
		return new VersionDirectory(versionId, jar, json);
	}

	@NotNull
	public VersionDirectory createVersionDirectoryWithUnknownVersionId(File jar, File json) {
		return new VersionDirectory(VersionDirectory.UNKNOWN_VERSION_ID, jar, json);
	}
}
