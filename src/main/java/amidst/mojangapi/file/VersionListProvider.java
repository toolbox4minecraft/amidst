package amidst.mojangapi.file;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.parsing.FormatException;

@ThreadSafe
public class VersionListProvider {
	public static VersionListProvider create() throws FormatException, IOException {
		return new VersionListProvider(VersionList.newLocalVersionList());
	}

	@NotNull
	@Deprecated
	public static VersionList getRemoteOrLocalVersionList() throws FileNotFoundException {
		VersionList versionList = VersionListProvider.remoteOrLocalVersionList;
		if (versionList == null) {
			synchronized (VersionListProvider.class) {
				versionList = VersionListProvider.remoteOrLocalVersionList;
				if (versionList == null) {
					versionList = VersionList.newRemoteOrLocalVersionList();
					VersionListProvider.remoteOrLocalVersionList = versionList;
				}
			}
		}
		return versionList;
	}

	@Deprecated
	private static volatile VersionList remoteOrLocalVersionList;

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
