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
	NETHER_FORTRESS,
	PLAYER,
	SLIME,
	STRONGHOLD,
	JUNGLE,
	DESERT,
	VILLAGE,
	SPAWN,
	WITCH,
	OCEAN_MONUMENT;
	// @formatter:on

	private final BufferedImage image;

	private MapMarkers() {
		String fileName = this.toString().toLowerCase() + ".png";
		image = ResourceLoader.getImage(fileName);
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return toString();
	}
}
