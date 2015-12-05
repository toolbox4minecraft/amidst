package amidst.mojangapi.versionlist;

import amidst.mojangapi.ReleaseType;

public class VersionListEntry {
	private static final String REMOTE_PREFIX = "https://s3.amazonaws.com/Minecraft.Download/versions/";
	private static final String MIDDLE_SERVER = "/minecraft_server.";
	private static final String MIDDLE_CLIENT = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final String JSON_FILE_EXTENSION = ".json";

	private String id;
	private ReleaseType type;

	public VersionListEntry() {
		// no-argument constructor for gson
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}

	private String getServerLocation(String prefix, String fileExtension) {
		return prefix + id + MIDDLE_SERVER + id + fileExtension;
	}

	private String getClientLocation(String prefix, String fileExtension) {
		return prefix + id + MIDDLE_CLIENT + id + fileExtension;
	}

	public String getServerJarLocation(String prefix) {
		return getServerLocation(prefix, JAR_FILE_EXTENSION);
	}

	public String getClientJarLocation(String prefix) {
		return getClientLocation(prefix, JAR_FILE_EXTENSION);
	}

	public String getClientJsonLocation(String prefix) {
		return getClientLocation(prefix, JSON_FILE_EXTENSION);
	}

	public String getRemoteServerJarLocation() {
		return getServerLocation(REMOTE_PREFIX, JAR_FILE_EXTENSION);
	}

	public String getRemoteClientJarLocation() {
		return getClientLocation(REMOTE_PREFIX, JAR_FILE_EXTENSION);
	}

	public String getRemoteClientJsonLocation() {
		return getClientLocation(REMOTE_PREFIX, JSON_FILE_EXTENSION);
	}
}
