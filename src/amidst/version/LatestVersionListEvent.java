package amidst.version;

public class LatestVersionListEvent {
	private final LatestVersionList source;

	public LatestVersionListEvent(LatestVersionList source) {
		this.source = source;
	}

	public LatestVersionList getSource() {
		return source;
	}
}
