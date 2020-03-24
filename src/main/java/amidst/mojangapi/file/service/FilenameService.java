package amidst.mojangapi.file.service;

import java.nio.file.Path;

import amidst.documentation.Immutable;

@Immutable
public class FilenameService {
	private static final String MIDDLE_SERVER = "/minecraft_server.";
	private static final String MIDDLE_CLIENT = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final String JSON_FILE_EXTENSION = ".json";

	public Path getClientJsonFile(Path prefix, String versionId) {
		return getClientFile(prefix, versionId, JSON_FILE_EXTENSION);
	}

	public Path getClientJarFile(Path prefix, String versionId) {
		return getClientFile(prefix, versionId, JAR_FILE_EXTENSION);
	}

	private Path getClientFile(Path prefix, String versionId, String fileExtension) {
		return prefix.resolve(getClientLocation("", versionId, fileExtension));
	}

	public String getClientJson(String prefix, String versionId) {
		return getClientLocation(prefix, versionId, JSON_FILE_EXTENSION);
	}
	public String getClientJar(String prefix, String versionId) {
		return getClientLocation(prefix, versionId, JAR_FILE_EXTENSION);
	}

	private String getClientLocation(String prefix, String versionId, String fileExtension) {
		return prefix + versionId + MIDDLE_CLIENT + versionId + fileExtension;
	}

	public String getServerJar(String prefix, String versionId) {
		return getServerLocation(prefix, versionId, JAR_FILE_EXTENSION);
	}

	private String getServerLocation(String prefix, String versionId, String fileExtension) {
		return prefix + versionId + MIDDLE_SERVER + versionId + fileExtension;
	}
}
