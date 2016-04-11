package amidst.mojangapi.world.icon.type;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.icon.WorldIconImage;

/**
 * This is only a helper enum to have a central place where these constants can
 * be collected. However, it should not be used as a type. Note, that the name
 * of the enum elements represent the icon filename at the same time!
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
	SPAWN("World Spawn"),
	WITCH("Witch Hut"),
	OCEAN_MONUMENT("Ocean Monument"),
	IGLOO("Igloo"),
	MINESHAFT("Mineshaft"),
	END_CITY("Likely End City"),
	POSSIBLE_END_CITY("Possible End City");
	// @formatter:on

	private final String name;
	private final WorldIconImage image;

	private DefaultWorldIconTypes(String name) {
		this.name = name;
		this.image = WorldIconImage.fromPixelTransparency(ResourceLoader.getImage(getFilename()));
	}

	private String getFilename() {
		return "/amidst/gui/main/icon/" + toString().toLowerCase() + ".png";
	}

	public String getName() {
		return name;
	}

	public WorldIconImage getImage() {
		return image;
	}
}
