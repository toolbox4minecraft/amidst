package amidst.mojangapi.version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import amidst.utilities.URIUtils;

public class VersionDeclaration {
	private static enum StatelessResources {
		INSTANCE;

		private char defaultTypeChar = 'N';
		private Map<String, Character> typeCharMap = createTypeCharMap();

		private Map<String, Character> createTypeCharMap() {
			HashMap<String, Character> result = new HashMap<String, Character>();
			result.put("snapshot", 'S');
			result.put("release", 'R');
			result.put("old_beta", 'B');
			result.put("old_alpha", 'A');
			return result;
		}

		private char getTypeChar(String type) {
			if (typeCharMap.containsKey(type)) {
				return typeCharMap.get(type);
			} else {
				return defaultTypeChar;
			}
		}
	}

	private static final String REMOTE_PREFIX = "https://s3.amazonaws.com/Minecraft.Download/versions/";
	private static final String MIDDLE_SERVER = "/minecraft_server.";
	private static final String MIDDLE_CLIENT = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final String JSON_FILE_EXTENSION = ".json";

	private String id;
	private String type;

	public VersionDeclaration() {
		// no-argument constructor for gson
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public char getTypeChar() {
		return StatelessResources.INSTANCE.getTypeChar(type);
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

	public URI getServerJarURI(String prefix) {
		return URIUtils.newURI(getServerJarLocation(prefix));
	}

	public URI getClientJarURI(String prefix) {
		return URIUtils.newURI(getClientJarLocation(prefix));
	}

	public URL getServerJarURL(String prefix) throws MalformedURLException {
		return URIUtils.newURL(getServerJarLocation(prefix));
	}

	public URL getClientJarURL(String prefix) throws MalformedURLException {
		return URIUtils.newURL(getClientJarLocation(prefix));
	}

	public String getClientJsonLocation(String prefix) {
		return getClientLocation(prefix, JSON_FILE_EXTENSION);
	}

	public URI getClientJsonURI(String prefix) {
		return URIUtils.newURI(getClientJsonLocation(prefix));
	}

	public URL getClientJsonURL(String prefix) throws MalformedURLException {
		return URIUtils.newURL(getClientJsonLocation(prefix));
	}

	public boolean hasServer(String prefix) {
		return URIUtils.exists(getServerJarLocation(prefix));
	}

	public boolean hasClient(String prefix) {
		return URIUtils.exists(getClientJarLocation(prefix));
	}

	public void downloadServer(String prefix, String targetPrefix)
			throws MalformedURLException, IOException {
		URIUtils.download(getServerJarURL(prefix),
				getServerJarURI(targetPrefix));
	}

	public void downloadClient(String prefix, String targetPrefix)
			throws MalformedURLException, IOException {
		URIUtils.download(getClientJarURL(prefix),
				getClientJarURI(targetPrefix));
		URIUtils.download(getClientJsonURL(prefix),
				getClientJsonURI(targetPrefix));
	}

	public boolean tryDownloadServer(String prefix, String targetPrefix) {
		try {
			downloadServer(prefix, targetPrefix);
			return true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix, String targetPrefix) {
		try {
			downloadClient(prefix, targetPrefix);
			return true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return false;
	}
}
