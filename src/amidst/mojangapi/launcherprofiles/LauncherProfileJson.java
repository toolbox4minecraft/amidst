package amidst.mojangapi.launcherprofiles;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import amidst.mojangapi.ReleaseType;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.versionlist.VersionListJson;

public class LauncherProfileJson {
	private String name;
	private String lastVersionId;
	private String gameDir;
	private List<ReleaseType> allowedReleaseTypes = Arrays
			.asList(ReleaseType.RELEASE);

	public LauncherProfileJson() {
		// no-argument constructor for gson
	}

	public String getName() {
		return name;
	}

	public String getLastVersionId() {
		return lastVersionId;
	}

	public boolean hasLastVersionId() {
		return lastVersionId != null;
	}

	public String getGameDir() {
		return gameDir;
	}

	public boolean isAllowed(ReleaseType releaseType) {
		return allowedReleaseTypes.contains(releaseType);
	}

	public ProfileDirectory createProfileDirectory() {
		return new ProfileDirectory(new File(gameDir));
	}

	public VersionDirectory createVersionDirectory(
			DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		if (hasLastVersionId()) {
			VersionDirectory result = dotMinecraftDirectory
					.createVersionDirectory(lastVersionId);
			if (result.isValid()) {
				return result;
			} else {
				return null;
			}
		} else if (versionList == null) {
			return null;
		} else if (isAllowed(ReleaseType.SNAPSHOT)) {
			return versionList.findFirstValidSnapshot(dotMinecraftDirectory);
		} else {
			return versionList.findFirstValidRelease(dotMinecraftDirectory);
		}
	}
}