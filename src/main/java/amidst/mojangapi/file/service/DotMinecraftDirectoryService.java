package amidst.mojangapi.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.JsonReader;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.util.OperatingSystemDetector;

@Immutable
public class DotMinecraftDirectoryService {
	@NotNull
	public File createDotMinecraftDirectory(String dotMinecraftCMDParameter) {
		if (dotMinecraftCMDParameter != null) {
			File result = new File(dotMinecraftCMDParameter);
			if (result.isDirectory()) {
				return result;
			} else {
				AmidstLogger.warn(
						"Unable to set Minecraft directory to: " + result
								+ " as that location does not exist or is not a folder.");
			}
		}
		return getMinecraftDirectory();
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
		return JsonReader.readLauncherProfilesFrom(dotMinecraftDirectory.getLauncherProfilesJson());
	}

	@NotNull
	public ProfileDirectory createValidProfileDirectory(LauncherProfileJson launcherProfileJson, MojangApi mojangApi)
			throws FileNotFoundException {
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
			return new ProfileDirectory(mojangApi.getDotMinecraftDirectory().getRoot());
		}
	}

	@NotNull
	public VersionDirectory createValidVersionDirectory(LauncherProfileJson launcherProfileJson, MojangApi mojangApi)
			throws FileNotFoundException {
		String lastVersionId = launcherProfileJson.getLastVersionId();
		if (lastVersionId != null) {
			VersionDirectory result = mojangApi.createVersionDirectory(lastVersionId);
			if (result.isValid()) {
				return result;
			}
		} else {
			VersionDirectory result = tryFindFirstValidVersionDirectory(
					launcherProfileJson.getAllowedReleaseTypes(),
					mojangApi);
			if (result != null) {
				return result;
			}
		}
		throw new FileNotFoundException(
				"cannot find valid version directory for launcher profile '" + launcherProfileJson.getName() + "'");
	}

	private VersionDirectory tryFindFirstValidVersionDirectory(
			List<ReleaseType> allowedReleaseTypes,
			MojangApi mojangApi) throws FileNotFoundException {
		for (VersionListEntryJson version : mojangApi.getVersionList().getVersions()) {
			if (allowedReleaseTypes.contains(version.getType())) {
				VersionDirectory versionDirectory = mojangApi.createVersionDirectory(version.getId());
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}
}
