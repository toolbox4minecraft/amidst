package amidst.mojangapi.world.icon;

import java.awt.image.BufferedImage;

import amidst.documentation.Immutable;
import amidst.resources.ResourceLoader;

/**
 * This is only a helper enum to have a central place where these constants can
 * be collected. However, it should not be used as a type.
 */
@Immutable
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
