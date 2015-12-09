package amidst.mojangapi.file.json.launcherprofiles;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

public class LauncherProfileJson {
	private String name;
	private String lastVersionId;
	private String gameDir;
	private List<ReleaseType> allowedReleaseTypes = Arrays
			.asList(ReleaseType.RELEASE);

	@GsonConstructor
	public LauncherProfileJson() {
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

	public ProfileDirectory createValidProfileDirectory() {
		ProfileDirectory result = new ProfileDirectory(new File(gameDir));
		if (result.isValid()) {
			return result;
		} else {
			return null;
		}
	}

	public VersionDirectory createValidVersionDirectory(MojangApi mojangApi) {
		VersionListJson versionList = mojangApi.getVersionList();
		if (hasLastVersionId()) {
			VersionDirectory result = mojangApi
					.createVersionDirectory(lastVersionId);
			if (result.isValid()) {
				return result;
			} else {
				return null;
			}
		} else if (versionList == null) {
			return null;
		} else if (isAllowed(ReleaseType.SNAPSHOT)) {
			return versionList.findFirstValidSnapshot(mojangApi);
		} else {
			return versionList.findFirstValidRelease(mojangApi);
		}
	}
}