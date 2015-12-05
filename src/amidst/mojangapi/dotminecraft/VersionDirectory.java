package amidst.mojangapi.dotminecraft;

import java.io.File;

public class VersionDirectory {
	private final File jar;
	private final File json;

	public VersionDirectory(File jar, File json) {
		this.jar = jar;
		this.json = json;
	}

	public File getJar() {
		return jar;
	}

	public File getJson() {
		return json;
	}
}
