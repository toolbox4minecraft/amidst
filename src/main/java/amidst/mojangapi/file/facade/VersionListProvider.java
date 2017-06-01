package amidst.mojangapi.file.facade;

import java.io.IOException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.MojangApiParsingException;

@ThreadSafe
public class VersionListProvider {
	public static VersionListProvider create() throws MojangApiParsingException, IOException {
		return new VersionListProvider(VersionList.newLocalVersionList());
	}

	private final VersionList local;
	private volatile VersionList remote;

	public VersionListProvider(VersionList local) {
		this.local = local;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	public void startDownload() throws MojangApiParsingException, IOException {
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
