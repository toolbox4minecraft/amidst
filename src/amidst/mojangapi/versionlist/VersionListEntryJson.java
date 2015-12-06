package amidst.mojangapi.versionlist;

import amidst.mojangapi.ReleaseType;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;

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

	public VersionDirectory createVersionDirectory(
			DotMinecraftDirectory dotMinecraftDirectory) {
		return dotMinecraftDirectory.createVersionDirectory(id);
	}
}
