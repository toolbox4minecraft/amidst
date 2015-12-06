package amidst.mojangapi.versionlist;

public class LatestJson {
	private String snapshot;
	private String release;

	public LatestJson() {
		// no-argument constructor for gson
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getRelease() {
		return release;
	}
}
