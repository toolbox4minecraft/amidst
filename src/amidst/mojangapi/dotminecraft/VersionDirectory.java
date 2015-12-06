package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.mojangapi.FilenameFactory;
import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.version.VersionJson;

public class VersionDirectory {
	private final String versionId;
	private final File jar;
	private final File json;

	public VersionDirectory(String versionId, File jar, File json) {
		this.versionId = versionId;
		this.jar = jar;
		this.json = json;
	}

	public VersionDirectory(File versions, String versionId) {
		this.versionId = versionId;
		this.jar = FilenameFactory.getClientJarFile(versions, versionId);
		this.json = FilenameFactory.getClientJsonFile(versions, versionId);
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

	public VersionJson readVersionJson() throws FileNotFoundException,
			IOException {
		return MojangAPI.versionFrom(json);
	}
}
