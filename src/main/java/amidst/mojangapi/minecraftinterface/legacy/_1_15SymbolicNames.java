package amidst.mojangapi.minecraftinterface.legacy;

import amidst.documentation.Immutable;

@Immutable
public enum _1_15SymbolicNames {
	;

    public static final String CLASS_REGISTRY = "Registry";
    public static final String FIELD_REGISTRY_META_REGISTRY = "metaRegistry";
    public static final String METHOD_REGISTRY_GET_BY_KEY = "getByKey";
    public static final String METHOD_REGISTRY_GET_ID = "getId";

    public static final String CLASS_REGISTRY_KEY = "RegistryKey";
    public static final String CONSTRUCTOR_REGISTRY_KEY = "<init>";

	public static final String CLASS_UTIL = "Util";

	// TODO: correctly manage world types; remove duplication with legacy SymbolicNames
	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_WORLD_DATA = "WorldData";
	public static final String CONSTRUCTOR_WORLD_DATA = "<init>";
	public static final String CONSTRUCTOR_WORLD_DATA2 = "<init2>";

	public static final String CLASS_WORLD_SETTINGS = "WorldSettings";
	public static final String CONSTRUCTOR_WORLD_SETTINGS = "<init>";

	public static final String CLASS_GAME_TYPE = "GameType";

	public static final String CLASS_NOISE_BIOME_PROVIDER = "NoiseBiomeProvider";
	public static final String CLASS_NETHER_BIOME_PROVIDER = "NetherBiomeProvider";
	public static final String CONSTRUCTOR_NETHER_BIOME_PROVIDER = "<init>";
	public static final String METHOD_NOISE_BIOME_PROVIDER_GET_BIOME = "getBiome";

	public static final String CLASS_NETHER_BIOME_SETTINGS = "NetherBiomeSettings";
	public static final String CONSTRUCTOR_NETHER_BIOME_SETTINGS = "<init>";
	public static final String METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES1 = "setBiomes1";
	public static final String METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES2 = "setBiomes2";

	public static final String CLASS_OVERWORLD_BIOME_ZOOMER = "OverworldBiomeZoomer";
	public static final String METHOD_BIOME_ZOOMER_GET_BIOME = "getBiome";

    public static final String CLASS_BIOME = "Biome";
}
