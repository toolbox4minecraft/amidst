package amidst.devtools;

import java.io.IOException;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.RemoteVersion;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.parsing.FormatException;

public class MinecraftJarDownloadAvailabilityChecker {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private VersionList versionList;

	public MinecraftJarDownloadAvailabilityChecker(VersionList versionList) {
		this.versionList = versionList;
	}

	public void run() {
		for (Version version : versionList.getVersions()) {
			checkVersion(version);
		}
	}

	private void checkVersion(Version version) {
		boolean hasServer = false;
		boolean hasClient = false;
		try {
			RemoteVersion remoteVersion = version.fetchRemoteVersion();
			hasServer = remoteVersion.hasServer();
			hasClient = remoteVersion.hasClient();
		} catch (IOException | FormatException e) {
			e.printStackTrace();
		}
		System.out.println(renderer.render(version, hasServer, hasClient));
	}
}
