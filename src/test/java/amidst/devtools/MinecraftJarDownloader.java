package amidst.devtools;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.RemoteVersion;
import amidst.mojangapi.file.Version;
import amidst.parsing.FormatException;

import java.io.IOException;
import java.util.List;

public class MinecraftJarDownloader {
	private VersionStateRenderer renderer = new VersionStateRenderer();
	private String prefix;
	private List<Version> versionList;

	public MinecraftJarDownloader(String prefix, List<Version> versionList) {
		this.prefix = prefix;
		this.versionList = versionList;
	}

	public void run() {
		for (Version version : versionList) {
			downloadVersion(version);
		}
	}

	private void downloadVersion(Version version) {
		boolean hasServer = false;
		boolean hasClient = false;
		try {
			RemoteVersion remoteVersion = version.fetchRemoteVersion();
			hasServer = tryDownload(remoteVersion, true);
			hasClient = tryDownload(remoteVersion, false);
		} catch (IOException | FormatException e) {
			e.printStackTrace();
		}
		System.out.println(renderer.render(version, hasServer, hasClient));
	}

	private boolean tryDownload(RemoteVersion version, boolean server) {
		try {
			if (server) {
				version.downloadServer(prefix);
			} else {
				version.downloadClient(prefix);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
