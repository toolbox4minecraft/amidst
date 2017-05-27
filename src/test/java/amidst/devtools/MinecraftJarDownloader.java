package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.DownloadService;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

public class MinecraftJarDownloader {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private String prefix;
	private VersionListJson versionList;

	public MinecraftJarDownloader(String prefix, VersionListJson versionList) {
		this.prefix = prefix;
		this.versionList = versionList;
	}

	public void run() {
		DownloadService downloadService = new DownloadService();
		for (VersionListEntryJson version : versionList.getVersions()) {
			boolean hasServer = downloadService.tryDownloadServer(prefix, version);
			boolean hasClient = downloadService.tryDownloadClient(prefix, version);
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
