package amidst.mojangapi.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.parsing.URIUtils;

@Immutable
public class DownloadService {
	private final FilenameService filenameService = new FilenameService();

	public boolean hasServer(String versionId) {
		return exists(filenameService.getRemoteServerJar(versionId));
	}

	public boolean hasClient(String versionId) {
		return exists(filenameService.getRemoteClientJar(versionId));
	}

	private static boolean exists(String location) {
		try {
			HttpURLConnection connection = (HttpURLConnection) URIUtils.newURL(location).openConnection();
			connection.setRequestMethod("HEAD");
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean tryDownloadServer(String prefix, String versionId) {
		try {
			downloadServer(prefix, versionId);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download server: " + versionId);
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix, String versionId) {
		try {
			downloadClient(prefix, versionId);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download client: " + versionId);
		}
		return false;
	}

	public void downloadServer(String prefix, String versionId) throws IOException {
		download(filenameService.getRemoteServerJar(versionId), filenameService.getServerJar(prefix, versionId));
	}

	public void downloadClient(String prefix, String versionId) throws IOException {
		download(filenameService.getRemoteClientJar(versionId), filenameService.getClientJar(prefix, versionId));
		download(filenameService.getRemoteClientJson(versionId), filenameService.getClientJson(prefix, versionId));
	}

	private void download(String from, String to) throws IOException {
		download(URIUtils.newURL(from), Paths.get(to));
	}

	private void download(URL from, Path to) throws IOException {
		to.getParent().toFile().mkdirs();
		if (to.toFile().exists()) {
			return;
		}
		Path part = Paths.get(to.toString() + ".part");
		InputStream in = URIUtils.newInputStream(from);
		Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
		Files.move(part, to, StandardCopyOption.REPLACE_EXISTING);
	}
}
