package amidst.mojangapi.file.json.versionlist;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.FilenameFactory;
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
}
