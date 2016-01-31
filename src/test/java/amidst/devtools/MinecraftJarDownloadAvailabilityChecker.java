package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

public class MinecraftJarDownloadAvailabilityChecker {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private VersionListJson versionList;

	public MinecraftJarDownloadAvailabilityChecker(VersionListJson versionList) {
		this.versionList = versionList;
	}

	public void run() {
		for (VersionListEntryJson version : versionList.getVersions()) {
			boolean hasServer = version.hasServer();
			boolean hasClient = version.hasClient();
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
