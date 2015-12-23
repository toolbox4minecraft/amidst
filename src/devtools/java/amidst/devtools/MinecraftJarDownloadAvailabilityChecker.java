package amidst.devtools;

import java.io.IOException;

import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

public class MinecraftJarDownloadAvailabilityChecker {
	public static void main(String[] args) throws IOException,
			MojangApiParsingException {
		new MinecraftJarDownloadAvailabilityChecker(
				JsonReader.readRemoteVersionList())
				.displayDownloadAvailability();
	}

	private VersionStateRenderer renderer = new VersionStateRenderer();
	private VersionListJson versionList;

	public MinecraftJarDownloadAvailabilityChecker(VersionListJson versionList) {
		this.versionList = versionList;
	}

	public void displayDownloadAvailability() {
		for (VersionListEntryJson version : versionList.getVersions()) {
			boolean hasServer = version.hasServer();
			boolean hasClient = version.hasClient();
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
