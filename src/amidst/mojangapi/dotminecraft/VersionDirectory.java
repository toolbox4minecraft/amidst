package amidst.mojangapi.dotminecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.mojangapi.MojangAPI;
import amidst.mojangapi.version.Version;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class VersionDirectory {
	private final File jar;
	private final Version json;

	public VersionDirectory(File jar, File json) throws JsonSyntaxException,
			JsonIOException, FileNotFoundException, IOException {
		this.jar = jar;
		this.json = MojangAPI.versionFrom(json);
	}

	public File getJar() {
		return jar;
	}

	public Version getJson() {
		return json;
	}
}
