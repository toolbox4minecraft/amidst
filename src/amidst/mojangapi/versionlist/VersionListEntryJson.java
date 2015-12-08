package amidst.mojangapi.versionlist;

import java.io.IOException;
import java.net.MalformedURLException;

import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.internal.FilenameFactory;
import amidst.mojangapi.internal.ReleaseType;
import amidst.utilities.URIUtils;

public class VersionListEntryJson {
	private String id;
	private ReleaseType type;

	public VersionListEntryJson() {
		// no-argument constructor for gson
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}

	public boolean isType(ReleaseType type) {
		if (this.type == null) {
			return type == null;
		} else {
			return this.type.equals(type);
		}
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

	public void downloadServer(String prefix) throws MalformedURLException,
			IOException {
		URIUtils.download(getRemoteServerJar(), getServerJar(prefix));
	}

	public void downloadClient(String prefix) throws MalformedURLException,
			IOException {
		URIUtils.download(getRemoteClientJar(), getClientJar(prefix));
		URIUtils.download(getRemoteClientJson(), getClientJson(prefix));
	}

	public boolean tryDownloadServer(String prefix) {
		try {
			downloadServer(prefix);
			return true;
		} catch (Exception e) {
			Log.w("error downloading server: " + id);
			e.printStackTrace();
		}
		return false;
	}

	public boolean tryDownloadClient(String prefix) {
		try {
			downloadClient(prefix);
			return true;
		} catch (Exception e) {
			Log.w("error downloading client: " + id);
			e.printStackTrace();
		}
		return false;
	}
}
