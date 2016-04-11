package amidst.devtools.utils;

import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;

public class VersionStateRenderer {
	public String render(VersionListEntryJson version, boolean hasServer, boolean hasClient) {
		return toBox(hasServer, 'S') + " " + toBox(hasClient, 'C') + " " + version.getType().getTypeChar() + " "
				+ version.getId();
	}

	private String toBox(boolean value, char c) {
		if (value) {
			return "[" + c + "]";
		} else {
			return "[ ]";
		}
	}
}
