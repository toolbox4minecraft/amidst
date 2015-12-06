package amidst.mojangapi.versionlist;

import java.util.List;

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
}
