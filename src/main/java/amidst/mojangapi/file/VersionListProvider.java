package amidst.mojangapi.file;

import java.io.IOException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.parsing.FormatException;

@ThreadSafe
public class VersionListProvider {
	public static VersionListProvider create() throws FormatException, IOException {
		return new VersionListProvider(VersionList.newLocalVersionList());
	}

	private final VersionList local;
	private volatile VersionList remote;

	public VersionListProvider(VersionList local) {
		this.local = local;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	public void startDownload() throws FormatException, IOException {
		remote = VersionList.newRemoteVersionList();
	}

	public VersionList getRemoteOrElseLocal() {
		VersionList remote = this.remote;
		if (remote != null) {
			return remote;
		} else {
			return local;
		}
	}
}
