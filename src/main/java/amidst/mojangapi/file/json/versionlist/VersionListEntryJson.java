package amidst.mojangapi.file.json.versionlist;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.MojangApi;
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
}
