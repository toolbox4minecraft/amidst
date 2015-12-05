package amidst.mojangapi.versionlist;

public class LatestVersion {
	private String snapshot;
	private String release;

	public LatestVersion() {
		// no-argument constructor for gson
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getRelease() {
		return release;
	}
}
