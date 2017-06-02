package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;

public class MinecraftJarDownloader {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private String prefix;
	private VersionList versionList;

	public MinecraftJarDownloader(String prefix, VersionList versionList) {
		this.prefix = prefix;
		this.versionList = versionList;
	}

	public void run() {
		for (Version version : versionList.getVersions()) {
			boolean hasServer = version.tryDownloadServer(prefix);
			boolean hasClient = version.tryDownloadClient(prefix);
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
