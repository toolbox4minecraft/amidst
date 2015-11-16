package amidst.map;

import java.awt.image.BufferedImage;

import amidst.resources.ResourceLoader;

/**
 * Contains information about all possible Map Markers. Map objects use either
 * its or their own icon
 */
// TODO: link to test.amidst.map object class
public enum MapMarkers {
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

	private MapMarkers(String name) {
		this.name = name;
		String fileName = this.toString().toLowerCase() + ".png";
		image = ResourceLoader.getImage(fileName);
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return name;
	}
}
