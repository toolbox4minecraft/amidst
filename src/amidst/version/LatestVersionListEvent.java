package amidst.version;

import amidst.version.LatestVersionList.LoadState;

public class LatestVersionListEvent {
	private LatestVersionList source;
	public LatestVersionListEvent(LatestVersionList source) {
		this.source = source;
	}
	public LatestVersionList getSource() {
		return source;
	}
}
