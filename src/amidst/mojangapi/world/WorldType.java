package amidst.mojangapi.world;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
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
		if (result != null) {
			return result;
		} else {
			Log.e("Unable to find World Type: " + nameOrValue
					+ ". Falling back to default world type.");
			return DEFAULT;
		}
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

	private final String name;
	private final String value;

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
