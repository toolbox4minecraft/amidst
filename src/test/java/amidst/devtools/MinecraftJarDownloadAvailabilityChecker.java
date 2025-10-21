package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.RemoteVersion;
import amidst.mojangapi.file.Version;
import amidst.parsing.FormatException;

import java.io.IOException;
import java.util.List;

public class MinecraftJarDownloadAvailabilityChecker {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private List<Version> versionList;

	public MinecraftJarDownloadAvailabilityChecker(List<Version> versionList) {
		this.versionList = versionList;
	}

	public void run() {
		for (Version version : versionList) {
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
