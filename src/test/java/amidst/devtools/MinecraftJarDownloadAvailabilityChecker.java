package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.file.service.DownloadService;

public class MinecraftJarDownloadAvailabilityChecker {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private VersionListJson versionList;

	public MinecraftJarDownloadAvailabilityChecker(VersionListJson versionList) {
		this.versionList = versionList;
	}

	public void run() {
		DownloadService downloadService = new DownloadService();
		for (VersionListEntryJson version : versionList.getVersions()) {
			boolean hasServer = downloadService.hasServer(version);
			boolean hasClient = downloadService.hasClient(version);
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
