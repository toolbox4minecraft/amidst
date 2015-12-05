package amidst.mojangapi.versionlist;

import java.util.List;

public class VersionList {
	private Latest latest;
	private List<VersionListEntry> versions;

	public VersionList() {
		// no-argument constructor for gson
	}

	public Latest getLatest() {
		return latest;
	}

	public List<VersionListEntry> getVersions() {
		return versions;
	}
}
