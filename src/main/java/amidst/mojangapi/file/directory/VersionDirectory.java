package amidst.mojangapi.file.directory;

import java.nio.file.Files;
import java.nio.file.Path;

import amidst.documentation.Immutable;

@Immutable
public class VersionDirectory {
	private final Path jar;
	private final Path json;

	public VersionDirectory(Path jar, Path json) {
		this.jar = jar;
		this.json = json;
	}

	public boolean isValid() {
		return Files.isRegularFile(jar) && Files.isRegularFile(json);
	}

	public Path getJar() {
		return jar;
	}

	public Path getJson() {
		return json;
	}
}
