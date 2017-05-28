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
import amidst.mojangapi.file.URIUtils;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;

@Immutable
public class DownloadService {
	private final FilenameService filenameService = new FilenameService();

	public boolean hasServer(VersionListEntryJson version) {
		return exists(filenameService.getRemoteServerJar(version.getId()));
	}

	public boolean hasClient(VersionListEntryJson version) {
		return exists(filenameService.getRemoteClientJar(version.getId()));
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

	public void downloadServer(String prefix, VersionListEntryJson version) throws IOException {
		download(
				filenameService.getRemoteServerJar(version.getId()),
				filenameService.getServerJar(prefix, version.getId()));
	}

	public void downloadClient(String prefix, VersionListEntryJson version) throws IOException {
		download(
				filenameService.getRemoteClientJar(version.getId()),
				filenameService.getClientJar(prefix, version.getId()));
		download(
				filenameService.getRemoteClientJson(version.getId()),
				filenameService.getClientJson(prefix, version.getId()));
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

	public boolean tryDownloadServer(String prefix, VersionListEntryJson version) {
		try {
			downloadServer(prefix, version);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download server: " + version.getId());
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix, VersionListEntryJson version) {
		try {
			downloadClient(prefix, version);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download client: " + version.getId());
		}
		return false;
	}
}
