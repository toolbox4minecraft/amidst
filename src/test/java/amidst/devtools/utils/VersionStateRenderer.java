package amidst.devtools.utils;

import amidst.mojangapi.file.Version;

public class VersionStateRenderer {
	public String render(Version version, boolean hasServer, boolean hasClient) {
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
