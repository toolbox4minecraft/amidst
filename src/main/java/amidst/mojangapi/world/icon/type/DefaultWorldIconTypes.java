package amidst.mojangapi.world.icon.type;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.icon.WorldIconImage;

/**
 * This is only a helper enum to have a central place where these constants can
 * be collected. However, it should not be used as a type.
 */
@Immutable
public enum DefaultWorldIconTypes {
	// @formatter:off
	NETHER_FORTRESS     ("nether_fortress",   "Nether Fortress"),
	BASTION_REMNANT     ("bastion_remnant",   "Bastion Remnant"),
	PLAYER              ("player",            "Player"),
	STRONGHOLD          ("stronghold",        "Stronghold"),
	JUNGLE              ("jungle",            "Jungle Temple"),
	DESERT              ("desert",            "Desert Temple"),
	VILLAGE             ("village",           "Village"),
	SPAWN               ("spawn",             "World Spawn"),
	WITCH               ("witch",             "Witch Hut"),
	OCEAN_MONUMENT      ("ocean_monument",    "Ocean Monument"),
	IGLOO               ("igloo",             "Igloo"),
	MINESHAFT           ("mineshaft",         "Mineshaft"),
	WOODLAND_MANSION    ("woodland_mansion",  "Woodland Mansion"),
	END_CITY            ("end_city",          "Likely End City"),
	POSSIBLE_END_CITY   ("possible_end_city", "Possible End City"),
	OCEAN_RUINS         ("ocean_ruins",       "Ocean Ruins"),
	SHIPWRECK           ("shipwreck",         "Shipwreck"),
	BURIED_TREASURE     ("buried_treasure",   "Buried Treasure"),
	PILLAGER_OUTPOST    ("pillager_outpost",  "Pillager Outpost");
	// @formatter:on

	private static final Map<String, DefaultWorldIconTypes> typeMap = createTypeMap();

	private static Map<String, DefaultWorldIconTypes> createTypeMap() {
		Map<String, DefaultWorldIconTypes> result = new HashMap<>();
		for (DefaultWorldIconTypes iconType : EnumSet.allOf(DefaultWorldIconTypes.class)) {
			result.put(iconType.getName(), iconType);
		}
		return Collections.unmodifiableMap(result);
	}

	public static DefaultWorldIconTypes getByName(String name) {
		return typeMap.get(name);
	}

	public static boolean exists(String name) {
		return typeMap.containsKey(name);
	}

	private static String getFilename(String name) {
		return "/amidst/gui/main/icon/" + name + ".png";
	}

	private final String name;
	private final String label;
	private final WorldIconImage image;

	private DefaultWorldIconTypes(String name, String label) {
		this.name = name;
		this.label = label;
		this.image = WorldIconImage.fromPixelTransparency(ResourceLoader.getImage(getFilename(name)));
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public WorldIconImage getImage() {
		return image;
	}
}
