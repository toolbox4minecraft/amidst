package amidst.mojangapi.file.directory;

import java.io.File;

import amidst.documentation.Immutable;

@Immutable
public class VersionDirectory {
	private final File jar;
	private final File json;

	public VersionDirectory(File jar, File json) {
		this.jar = jar;
		this.json = json;
	}

	public boolean isValid() {
		return jar.isFile() && json.isFile();
	}

	public File getJar() {
		return jar;
	}

	public File getJson() {
		return json;
	}
}
