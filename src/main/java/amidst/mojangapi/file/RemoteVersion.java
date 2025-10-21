package amidst.mojangapi.file;

import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.RemoteVersionJson;
import amidst.parsing.FormatException;
import amidst.parsing.URIUtils;
import amidst.parsing.json.JsonReader;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class RemoteVersion {

	private final RemoteVersionJson remoteVersionJson;
	private final byte[] rawJson;

	private RemoteVersion(RemoteVersionJson remoteVersionJson, byte[] rawJson) {
		this.remoteVersionJson = remoteVersionJson;
		this.rawJson = rawJson;
	}

	public static RemoteVersion from(URL metaUrl)
			throws FormatException, IOException {
		byte[] rawJson = URIUtils.readBytes(metaUrl);
		Reader jsonReader = new InputStreamReader(new ByteArrayInputStream(rawJson));
		RemoteVersionJson remoteVersionJson = JsonReader.read(jsonReader, RemoteVersionJson.class);
		return new RemoteVersion(remoteVersionJson, rawJson);
	}

	public String getId() {
		return Objects.requireNonNull(remoteVersionJson.getId());
	}

	public ReleaseType getType() {
		return Objects.requireNonNull(remoteVersionJson.getType());
	}

	public boolean hasClient() {
		return remoteVersionJson.getClientUrl() != null;
	}

	public boolean hasServer() {
		return remoteVersionJson.getServerUrl() != null;
	}

	public void downloadClient(String prefix) throws IOException {
		String versionId1 = getId();
		copyToFile(URIUtils.newInputStream(remoteVersionJson.getClientUrl()),
			Paths.get(prefix + versionId1 + "/" + versionId1 + ".jar"));
		String versionId = getId();
		copyToFile(new ByteArrayInputStream(rawJson),
			Paths.get(prefix + versionId + "/" + versionId + ".json"));
	}

	public void downloadServer(String prefix) throws IOException {
		String versionId = getId();
		copyToFile(URIUtils.newInputStream(remoteVersionJson.getServerUrl()),
			Paths.get(prefix + versionId + "/minecraft_server." + versionId + ".jar"));
	}

	private void copyToFile(InputStream from, Path to) throws IOException {
		Path parent = to.getParent();
		if (parent != null) Files.createDirectories(parent);
		if (Files.exists(to)) {
			return;
		}
		Path part = to.resolveSibling(to.getFileName() + ".part");
		Files.copy(from, part, StandardCopyOption.REPLACE_EXISTING);
		Files.move(part, to, StandardCopyOption.REPLACE_EXISTING);
	}

}
