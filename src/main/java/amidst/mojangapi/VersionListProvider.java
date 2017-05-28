package amidst.mojangapi;

import java.io.IOException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.file.service.VersionListService;

@ThreadSafe
public class VersionListProvider {
	public static VersionListProvider create() throws MojangApiParsingException, IOException {
		VersionListService versionListService = new VersionListService();
		return new VersionListProvider(versionListService, versionListService.readLocalVersionListFromResource());
	}

	private final VersionListService versionListService;
	private final VersionListJson local;
	private volatile VersionListJson remote;

	public VersionListProvider(VersionListService versionListService, VersionListJson local) {
		this.versionListService = versionListService;
		this.local = local;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	public void startDownload() throws MojangApiParsingException, IOException {
		remote = versionListService.readRemoteVersionList();
	}

	public VersionListJson getRemoteOrElseLocal() {
		VersionListJson remote = this.remote;
		if (remote != null) {
			return remote;
		} else {
			return local;
		}
	}
}
