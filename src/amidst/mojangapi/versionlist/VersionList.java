package amidst.mojangapi.versionlist;

import java.util.List;

public class VersionList {
	private List<VersionListEntry> versions;

	public VersionList() {
		// no-argument constructor for gson
	}

	public List<VersionListEntry> getVersions() {
		return versions;
	}
}
