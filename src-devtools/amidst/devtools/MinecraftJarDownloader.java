package amidst.devtools;

import java.io.IOException;

import amidst.devtools.settings.DevToolsSettings;
import amidst.devtools.utils.VersionStateRenderer;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MinecraftJarDownloader {
	public static void main(String[] args) throws JsonSyntaxException,
			JsonIOException, IOException {
		new MinecraftJarDownloader(
				DevToolsSettings.INSTANCE.getMinecraftVersionsDirectory(),
				JsonReader.readRemoteVersionList()).downloadAll();
	}

	private VersionStateRenderer renderer = new VersionStateRenderer();
	private String prefix;
	private VersionListJson versionList;

	public MinecraftJarDownloader(String prefix, VersionListJson versionList) {
		this.prefix = prefix;
		this.versionList = versionList;
	}

	public void downloadAll() {
		for (VersionListEntryJson version : versionList.getVersions()) {
			boolean hasServer = version.tryDownloadServer(prefix);
			boolean hasClient = version.tryDownloadClient(prefix);
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
