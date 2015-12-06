package amidst.mojangapi.versionlist;

import java.util.List;

import amidst.mojangapi.ReleaseType;

public class VersionListJson {
	private LatestJson latest;
	private List<VersionListEntryJson> versions;

	public VersionListJson() {
		// no-argument constructor for gson
	}

	public LatestJson getLatest() {
		return latest;
	}

	public List<VersionListEntryJson> getVersions() {
		return versions;
	}

	public VersionListEntryJson getFirst(ReleaseType releaseType) {
		for (VersionListEntryJson version : versions) {
			if (version.isType(releaseType)) {
				return version;
			}
		}
		return null;
	}
}
