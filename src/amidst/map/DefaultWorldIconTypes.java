package amidst.map;

import java.awt.image.BufferedImage;

import amidst.resources.ResourceLoader;

public enum DefaultWorldIconTypes {
	// @formatter:off
	NETHER_FORTRESS("Nether Fortress"),
	PLAYER("Player"),
	STRONGHOLD("Stronghold"),
	JUNGLE("Jungle Temple"),
	DESERT("Desert Temple"),
	VILLAGE("Village"),
	SPAWN("Default World Spawn"),
	WITCH("Witch Hut"),
	OCEAN_MONUMENT("Ocean Monument");
	// @formatter:on

	private final String name;
	private final BufferedImage image;

	private DefaultWorldIconTypes(String name) {
		this.name = name;
		this.image = ResourceLoader.getImage(getFilename());
	}

	private String getFilename() {
		return this.toString().toLowerCase() + ".png";
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}
}
