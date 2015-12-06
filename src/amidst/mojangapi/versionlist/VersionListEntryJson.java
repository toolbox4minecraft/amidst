package amidst.mojangapi.versionlist;

import amidst.mojangapi.FilenameFactory;
import amidst.mojangapi.ReleaseType;

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

	public String getRemoteClientJson() {
		return FilenameFactory.getRemoteClientJson(id);
	}

	public String getClientJson(String prefix) {
		return FilenameFactory.getClientJson(prefix, id);
	}

	public String getRemoteClientJar() {
		return FilenameFactory.getRemoteClientJar(id);
	}

	public String getClientJar(String prefix) {
		return FilenameFactory.getClientJar(prefix, id);
	}

	public String getRemoteServerJar() {
		return FilenameFactory.getRemoteServerJar(id);
	}

	public String getServerJar(String prefix) {
		return FilenameFactory.getServerJar(prefix, id);
	}
}
