package amidst.mojangapi.world;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.local.SymbolicNames;

@Immutable
public enum WorldType {
	// @formatter:off
	DEFAULT			("Default",			SymbolicNames.FIELD_WORLD_TYPE_DEFAULT),
	FLAT			("Flat",			SymbolicNames.FIELD_WORLD_TYPE_FLAT),
	LARGE_BIOMES	("Large Biomes",	SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES),
	AMPLIFIED		("Amplified",		SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED),
	CUSTOMIZED		("Customized",		SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED);
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

	public static WorldType from(String nameOrSymbolicFieldName) {
		WorldType result = findInstance(nameOrSymbolicFieldName);
		if (result != null) {
			return result;
		} else {
			Log.e("Unable to find World Type: " + nameOrSymbolicFieldName
					+ ". Falling back to default world type.");
			return DEFAULT;
		}
	}

	private static WorldType findInstance(String nameOrValue) {
		for (WorldType worldType : values()) {
			if (worldType.name.equalsIgnoreCase(nameOrValue)
					|| worldType.symbolicFieldName
							.equalsIgnoreCase(nameOrValue)) {
				return worldType;
			}
		}
		return null;
	}

	private final String name;
	private final String symbolicFieldName;

	private WorldType(String name, String symbolicFieldName) {
		this.name = name;
		this.symbolicFieldName = symbolicFieldName;
	}

	public String getName() {
		return name;
	}

	public String getSymbolicFieldName() {
		return symbolicFieldName;
	}

	@Override
	public String toString() {
		return name;
	}
}
