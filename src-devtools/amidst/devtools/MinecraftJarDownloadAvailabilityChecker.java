package amidst.devtools;

import java.io.IOException;

import amidst.devtools.mojangapi.Version;
import amidst.devtools.mojangapi.Versions;
import amidst.devtools.utils.VersionStateRenderer;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MinecraftJarDownloadAvailabilityChecker {
	public static void main(String[] args) throws JsonSyntaxException,
			JsonIOException, IOException {
		new MinecraftJarDownloadAvailabilityChecker(Versions.retrieve())
				.displayDownloadAvailability();
	}

	private VersionStateRenderer renderer = new VersionStateRenderer();
	private Versions versions;

	public MinecraftJarDownloadAvailabilityChecker(Versions versions) {
		this.versions = versions;
	}

	public void displayDownloadAvailability() {
		for (Version version : versions.getVersions()) {
			boolean hasServer = version.hasServer();
			boolean hasClient = version.hasClient();
			System.out.println(renderer.render(version, hasServer, hasClient));
		}
	}
}
