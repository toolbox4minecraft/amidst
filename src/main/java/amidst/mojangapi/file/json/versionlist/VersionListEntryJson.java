package amidst.mojangapi.file.json.versionlist;

import java.net.URL;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;

@Immutable
public class VersionListEntryJson {
	private volatile String id;
	private volatile ReleaseType type;
	private volatile URL url;

	@GsonConstructor
	public VersionListEntryJson() {
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}

	public URL getMetaUrl() {
		return url;
	}
}
