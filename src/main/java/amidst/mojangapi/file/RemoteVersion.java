package amidst.mojangapi.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import amidst.mojangapi.file.json.ReleaseType;
import amidst.mojangapi.file.json.versionlist.RemoteVersionJson;
import amidst.mojangapi.file.service.FilenameService;
import amidst.parsing.FormatException;
import amidst.parsing.URIUtils;
import amidst.parsing.json.JsonReader;

public class RemoteVersion {

	private final FilenameService filenameService;
	private final RemoteVersionJson remoteVersionJson;
	private final byte[] rawJson;

	private RemoteVersion(FilenameService filenameService, RemoteVersionJson remoteVersionJson, byte[] rawJson) {
		this.filenameService = filenameService;
		this.remoteVersionJson = remoteVersionJson;
		this.rawJson = rawJson;
	}

	public static RemoteVersion from(FilenameService filenameService, URL metaUrl)
			throws FormatException, IOException {
		byte[] rawJson = URIUtils.readBytes(metaUrl);
		Reader jsonReader = new InputStreamReader(new ByteArrayInputStream(rawJson));
		RemoteVersionJson remoteVersionJson = JsonReader.read(jsonReader, RemoteVersionJson.class);
		return new RemoteVersion(filenameService, remoteVersionJson, rawJson);
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
		copyToFile(URIUtils.newInputStream(remoteVersionJson.getClientUrl()),
			Paths.get(filenameService.getClientJar(prefix, getId())));
		copyToFile(new ByteArrayInputStream(rawJson),
			Paths.get(filenameService.getClientJson(prefix, getId())));
	}

	public void downloadServer(String prefix) throws IOException {
		copyToFile(URIUtils.newInputStream(remoteVersionJson.getServerUrl()),
			Paths.get(filenameService.getServerJar(prefix, getId())));
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
