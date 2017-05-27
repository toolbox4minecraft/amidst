package amidst.mojangapi.file.directory;

import java.io.File;

import amidst.documentation.Immutable;

@Immutable
public class VersionDirectory {
	private final String versionId;
	private final File jar;
	private final File json;

	public VersionDirectory(String versionId, File jar, File json) {
		this.versionId = versionId;
		this.jar = jar;
		this.json = json;
	}

	public boolean isValid() {
		return jar.isFile() && json.isFile();
	}

	public String getVersionId() {
		return versionId;
	}

	public File getJar() {
		return jar;
	}

	public File getJson() {
		return json;
	}
}
