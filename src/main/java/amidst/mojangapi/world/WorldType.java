package amidst.mojangapi.world;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.local.SymbolicNames;

@Immutable
public enum WorldType {
	// @formatter:off
	DEFAULT      ("Default",      "default",      SymbolicNames.FIELD_WORLD_TYPE_DEFAULT),
	FLAT         ("Flat",         "flat",         SymbolicNames.FIELD_WORLD_TYPE_FLAT),
	LARGE_BIOMES ("Large Biomes", "large-biomes", SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES),
	AMPLIFIED    ("Amplified",    "amplified",    SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED),
	CUSTOMIZED   ("Customized",   "customized",   SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED),
	
	// Minetest world types
	V5           ("v5",           "v5",         null),
	V6           ("v6",           "v6",         null),
	V7           ("v7",           "v7",         null),
	// FLAT - can reuse the minecraft FLAT
	FRACTAL      ("Fractal",      "fractal",    null),
	CARPATHIAN   ("Carpathian",   "carpathian", null);
	// @formatter:on

	public static final String PROMPT_EACH_TIME = "Prompt each time";

	public static WorldType from(String nameOrSymbolicFieldName) {
		WorldType result = findInstance(nameOrSymbolicFieldName);
		if (result != null) {
			return result;
		} else {
			String message = "Unable to find World Type: " + nameOrSymbolicFieldName
					+ ". Falling back to default world type.";
			AmidstLogger.error(message);
			AmidstMessageBox.displayError("Error", message);
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
