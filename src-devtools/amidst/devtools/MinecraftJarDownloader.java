package amidst.devtools;

import java.io.IOException;

import amidst.devtools.mojangapi.Version;
import amidst.devtools.mojangapi.Versions;
import amidst.devtools.settings.DevToolsSettings;
import amidst.devtools.utils.VersionStateRenderer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MinecraftJarDownloader {
	public static void main(String[] args) throws JsonSyntaxException,
			JsonIOException, IOException {
		new MinecraftJarDownloader(
				DevToolsSettings.INSTANCE.getMinecraftVersionsDirectory(),
				Versions.retrieve()).downloadAll();
	}

	private VersionStateRenderer renderer = new VersionStateRenderer();
	private String basePath;
	private Versions versions;

	public MinecraftJarDownloader(String basePath, Versions versions) {
		this.basePath = basePath;
		this.versions = versions;
	}

	public void downloadAll() {
		for (Version version : versions.getVersions()) {
			boolean hasServer = version.tryDownloadServer(basePath);
			boolean hasClient = version.tryDownloadClient(basePath);
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
