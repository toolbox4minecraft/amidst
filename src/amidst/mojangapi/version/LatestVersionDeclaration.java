package amidst.mojangapi.version;

public class LatestVersionDeclaration {
	private String snapshot;
	private String release;

	public LatestVersionDeclaration() {
		// no-argument constructor for gson
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getRelease() {
		return release;
	}
}
