package amidst.mojangapi.file.json.player;

import amidst.documentation.GsonObject;

@GsonObject
public class SimplePlayerJson {
	private String id;
	private String name;

	public SimplePlayerJson() {
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
