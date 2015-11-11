package amidst.devtools.mojangapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import amidst.utilties.URIUtils;

public class Version {
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

	private static final String DOWNLOAD_URL_PREFIX = "https://s3.amazonaws.com/Minecraft.Download/versions/";
	private static final String DOWNLOAD_URL_MIDDLE_SERVER = "/minecraft_server.";
	private static final String DOWNLOAD_URL_MIDDLE_CLIENT = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final String JSON_FILE_EXTENSION = ".json";

	private String id;
	private String type;

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public char getTypeChar() {
		return StatelessResources.INSTANCE.getTypeChar(type);
	}

	private String getServerLocation(String fileExtension) {
		return DOWNLOAD_URL_PREFIX + id + DOWNLOAD_URL_MIDDLE_SERVER + id
				+ fileExtension;
	}

	private String getClientLocation(String fileExtension) {
		return DOWNLOAD_URL_PREFIX + id + DOWNLOAD_URL_MIDDLE_CLIENT + id
				+ fileExtension;
	}

	public String getServerJarLocation() {
		return getServerLocation(JAR_FILE_EXTENSION);
	}

	public String getClientJarLocation() {
		return getClientLocation(JAR_FILE_EXTENSION);
	}

	public URI getServerJarURI() {
		return URIUtils.newURI(getServerJarLocation());
	}

	public URI getClientJarURI() {
		return URIUtils.newURI(getClientJarLocation());
	}

	public URL getServerJarURL() throws MalformedURLException {
		return URIUtils.newURL(getServerJarLocation());
	}

	public URL getClientJarURL() throws MalformedURLException {
		return URIUtils.newURL(getClientJarLocation());
	}

	public String getClientJsonLocation() {
		return getClientLocation(JSON_FILE_EXTENSION);
	}

	public URI getClientJsonURI() {
		return URIUtils.newURI(getClientJsonLocation());
	}

	public URL getClientJsonURL() throws MalformedURLException {
		return URIUtils.newURL(getClientJsonLocation());
	}

	public boolean hasServer() {
		return URIUtils.exists(getServerJarLocation());
	}

	public boolean hasClient() {
		return URIUtils.exists(getClientJarLocation());
	}

	public void downloadServer(String basePath) throws MalformedURLException,
			IOException {
		URIUtils.download(getServerJarURL(), getLocalServerJarPath(basePath));
	}

	public void downloadClient(String basePath) throws MalformedURLException,
			IOException {
		URIUtils.download(getClientJarURL(), getLocalClientJarPath(basePath));
		URIUtils.download(getClientJsonURL(), getLocalClientJsonPath(basePath));
	}

	public boolean tryDownloadServer(String basePath) {
		try {
			downloadServer(basePath);
			return true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return false;
	}

	public boolean tryDownloadClient(String basePath) {
		try {
			downloadClient(basePath);
			return true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return false;
	}

	public Path getLocalServerJarPath(String basePath) {
		return Paths.get(basePath, "server", id + JAR_FILE_EXTENSION);
	}

	public Path getLocalClientJarPath(String basePath) {
		return Paths.get(basePath, "client", id + JAR_FILE_EXTENSION);
	}

	public Path getLocalClientJsonPath(String basePath) {
		return Paths.get(basePath, "client", id + JSON_FILE_EXTENSION);
	}
}
