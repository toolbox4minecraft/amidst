package amidst.mojangapi.dotminecraft;

import java.io.File;

import amidst.mojangapi.version.VersionJson;

public class VersionDirectory {
	private final File jar;
	private final VersionJson json;

	public VersionDirectory(File jar, VersionJson json) {
		this.jar = jar;
		this.json = json;
	}

	public File getJar() {
		return jar;
	}

	public VersionJson getJson() {
		return json;
	}
}
