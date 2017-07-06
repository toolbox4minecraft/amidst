package amidst.mojangapi.file.json.versionlist;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;

@Immutable
@GsonObject(ignoreUnknown=true)
public class VersionListEntryJson {
	private volatile String id;
	private volatile ReleaseType type;

	public VersionListEntryJson() {
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}
}
