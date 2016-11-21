package amidst.mojangapi.file.json.versionlist;

import java.io.IOException;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.FilenameFactory;
import amidst.mojangapi.file.URIUtils;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.ReleaseType;

@Immutable
public class VersionListEntryJson {
	private volatile String id;
	private volatile ReleaseType type;

	@GsonConstructor
	public VersionListEntryJson() {
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}

	public VersionDirectory createVersionDirectory(MojangApi mojangApi) {
		return mojangApi.createVersionDirectory(id);
	}

	public String getClientJar(String prefix) {
		return FilenameFactory.getClientJar(prefix, id);
	}

	public String getClientJson(String prefix) {
		return FilenameFactory.getClientJson(prefix, id);
	}

	public String getServerJar(String prefix) {
		return FilenameFactory.getServerJar(prefix, id);
	}

	public String getRemoteClientJar() {
		return FilenameFactory.getRemoteClientJar(id);
	}

	public String getRemoteClientJson() {
		return FilenameFactory.getRemoteClientJson(id);
	}

	public String getRemoteServerJar() {
		return FilenameFactory.getRemoteServerJar(id);
	}

	public boolean hasServer() {
		return URIUtils.exists(getRemoteServerJar());
	}

	public boolean hasClient() {
		return URIUtils.exists(getRemoteClientJar());
	}

	public void downloadServer(String prefix) throws IOException {
		URIUtils.download(getRemoteServerJar(), getServerJar(prefix));
	}

	public void downloadClient(String prefix) throws IOException {
		URIUtils.download(getRemoteClientJar(), getClientJar(prefix));
		URIUtils.download(getRemoteClientJson(), getClientJson(prefix));
	}

	public boolean tryDownloadServer(String prefix) {
		try {
			downloadServer(prefix);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download server: " + id);
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix) {
		try {
			downloadClient(prefix);
			return true;
		} catch (IOException e) {
			AmidstLogger.warn(e, "unable to download client: " + id);
		}
		return false;
	}
}
