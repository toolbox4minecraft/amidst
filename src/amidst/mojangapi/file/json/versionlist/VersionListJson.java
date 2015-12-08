package amidst.mojangapi.file.json.versionlist;

import java.util.Collections;
import java.util.List;

import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;

public class VersionListJson {
	private List<VersionListEntryJson> versions = Collections.emptyList();

	public VersionListJson() {
		// no-argument constructor for gson
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}

	public VersionDirectory findFirstValidSnapshot(MojangApi mojangApi) {
		return findFirstValidVersionDirectory(mojangApi);
	}

	public VersionDirectory findFirstValidRelease(MojangApi mojangApi) {
		return findFirstValidVersionDirectory(mojangApi, ReleaseType.RELEASE);
	}

	public VersionDirectory findFirstValidVersionDirectory(MojangApi mojangApi) {
		for (VersionListEntryJson version : versions) {
			VersionDirectory versionDirectory = version
					.createVersionDirectory(mojangApi);
			if (versionDirectory.isValid()) {
				return versionDirectory;
			}
		}
		return null;
	}

	public VersionDirectory findFirstValidVersionDirectory(MojangApi mojangApi,
			ReleaseType releaseType) {
		for (VersionListEntryJson version : versions) {
			if (version.isType(releaseType)) {
				VersionDirectory versionDirectory = version
						.createVersionDirectory(mojangApi);
				if (versionDirectory.isValid()) {
					return versionDirectory;
				}
			}
		}
		return null;
	}
}
