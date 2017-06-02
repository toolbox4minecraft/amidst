package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;

public class MinecraftJarDownloadAvailabilityChecker {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private VersionList versionList;

	public MinecraftJarDownloadAvailabilityChecker(VersionList versionList) {
		this.versionList = versionList;
	}

	public void run() {
		for (Version version : versionList.getVersions()) {
			boolean hasServer = version.hasServer();
			boolean hasClient = version.hasClient();
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
