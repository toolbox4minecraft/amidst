package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonConstructor;

public class SimplePlayerJson {
	private String id;
	private String name;

	@GsonConstructor
	public SimplePlayerJson() {
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
