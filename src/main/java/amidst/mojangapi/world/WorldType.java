package amidst.mojangapi.world;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.local.SymbolicNames;

@Immutable
public enum WorldType {
	// @formatter:off
	DEFAULT      ("Default",      "default",      SymbolicNames.FIELD_WORLD_TYPE_DEFAULT),
	FLAT         ("Flat",         "flat",         SymbolicNames.FIELD_WORLD_TYPE_FLAT),
	LARGE_BIOMES ("Large Biomes", "large-biomes", SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES),
	AMPLIFIED    ("Amplified",    "amplified",    SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED),
	CUSTOMIZED   ("Customized",   "customized",   SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED);
	// @formatter:on

	public static final String PROMPT_EACH_TIME = "Prompt each time";

	// @formatter:off
	private static final WorldType[] SELECTABLE_WORLD_TYPES_ARRAY = new WorldType[] {
			WorldType.DEFAULT,
			WorldType.FLAT,
			WorldType.LARGE_BIOMES,
			WorldType.AMPLIFIED
	};
	// @formatter:on

	private static final List<WorldType> SELECTABLE_WORLD_TYPES = Arrays.asList(SELECTABLE_WORLD_TYPES_ARRAY);

	// @formatter:off
	private static final String[] WORLD_TYPE_SETTING_AVAILABLE_VALUES = new String[] {
			PROMPT_EACH_TIME,
			WorldType.DEFAULT.getName(),
			WorldType.FLAT.getName(),
			WorldType.LARGE_BIOMES.getName(),
			WorldType.AMPLIFIED.getName()
	};
	// @formatter:on

	public static List<WorldType> getSelectable() {
		return SELECTABLE_WORLD_TYPES;
	}

	public static WorldType[] getSelectableArray() {
		return SELECTABLE_WORLD_TYPES_ARRAY;
	}

	public static String[] getWorldTypeSettingAvailableValues() {
		return WORLD_TYPE_SETTING_AVAILABLE_VALUES;
	}

	public static WorldType from(String nameOrSymbolicFieldName) {
		WorldType result = findInstance(nameOrSymbolicFieldName);
		if (result != null) {
			return result;
		} else {
			Log.e("Unable to find World Type: " + nameOrSymbolicFieldName + ". Falling back to default world type.");
			return DEFAULT;
		}
	}

	private static WorldType findInstance(String nameOrValue) {
		for (WorldType worldType : values()) {
			if (worldType.name.equalsIgnoreCase(nameOrValue)
					|| worldType.symbolicFieldName.equalsIgnoreCase(nameOrValue)) {
				return worldType;
			}
		}
		return null;
	}

	private final String name;
	private final String filenameText;
	private final String symbolicFieldName;

	private WorldType(String name, String filenameText, String symbolicFieldName) {
		this.name = name;
		this.filenameText = filenameText;
		this.symbolicFieldName = symbolicFieldName;
	}

	public String getName() {
		return name;
	}

	public String getFilenameText() {
		return filenameText;
	}

	public String getSymbolicFieldName() {
		return symbolicFieldName;
	}

	@Override
	public String toString() {
		return name;
	}
}
