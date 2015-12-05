package amidst.mojangapi;

public enum FilenameFactory {
	;

	private static final String REMOTE_PREFIX = "https://s3.amazonaws.com/Minecraft.Download/versions/";
	private static final String MIDDLE_SERVER = "/minecraft_server.";
	private static final String MIDDLE_CLIENT = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final String JSON_FILE_EXTENSION = ".json";

	public static String getRemoteClientJson(String id) {
		return getClientJson(REMOTE_PREFIX, id);
	}

	public static String getClientJson(String prefix, String id) {
		return getClientLocation(prefix, id, JSON_FILE_EXTENSION);
	}

	public static String getRemoteClientJar(String id) {
		return getClientJar(REMOTE_PREFIX, id);
	}

	public static String getClientJar(String prefix, String id) {
		return getClientLocation(prefix, id, JAR_FILE_EXTENSION);
	}

	private static String getClientLocation(String prefix, String id,
			String fileExtension) {
		return prefix + id + MIDDLE_CLIENT + id + fileExtension;
	}

	public static String getRemoteServerJar(String id) {
		return getServerJar(REMOTE_PREFIX, id);
	}

	public static String getServerJar(String prefix, String id) {
		return getServerLocation(prefix, id, JAR_FILE_EXTENSION);
	}

	private static String getServerLocation(String prefix, String id,
			String fileExtension) {
		return prefix + id + MIDDLE_SERVER + id + fileExtension;
	}
}
