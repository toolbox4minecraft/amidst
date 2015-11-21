package amidst.minecraft.world;

import java.util.Arrays;
import java.util.List;

import amidst.logging.Log;

public enum WorldType {
	// @formatter:off
	DEFAULT			("Default",			"default"),
	FLAT			("Flat",			"flat"),
	LARGE_BIOMES	("Large Biomes",	"largeBiomes"),
	AMPLIFIED		("Amplified",		"amplified"),
	CUSTOMIZED		("Customized",		"customized");
	// @formatter:on

	// @formatter:off
	private static final List<WorldType> SELECTABLE_WORLD_TYPES = Arrays.asList(
			WorldType.DEFAULT,
			WorldType.FLAT,
			WorldType.LARGE_BIOMES,
			WorldType.AMPLIFIED
	);
	// @formatter:on

	public static List<WorldType> getSelectable() {
		return SELECTABLE_WORLD_TYPES;
	}

	public static WorldType from(String nameOrValue) {
		WorldType result = findInstance(nameOrValue);
		if (result == null) {
			Log.crash("Unable to find World Type: " + nameOrValue);
		}
		return result;
	}

	private static WorldType findInstance(String nameOrValue) {
		for (WorldType worldType : values()) {
			if (worldType.name.equalsIgnoreCase(nameOrValue)
					|| worldType.value.equalsIgnoreCase(nameOrValue)) {
				return worldType;
			}
		}
		return null;
	}

	private String name;
	private String value;

	private WorldType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name;
	}
}
