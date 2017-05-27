package amidst.mojangapi.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;

@Immutable
public class LauncherProfileService {
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

	@NotNull
	public LauncherProfilesJson readLauncherProfilesFrom(DotMinecraftDirectory dotMinecraftDirectory)
			throws MojangApiParsingException,
			IOException {
		return JsonReader.readLauncherProfilesFrom(dotMinecraftDirectory.getLauncherProfilesJson());
	}
}
