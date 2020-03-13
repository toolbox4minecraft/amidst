package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

	public static final String CLASS_REGISTRY = "Registry";
	public static final String FIELD_REGISTRY_BIOME = "biomeRegistry";

	public static final String CLASS_MAPPED_REGISTRY = "MappedRegistry";
	public static final String METHOD_MAPPED_REGISTRY_GET_ID = "getId";

	public static final String CLASS_UTIL = "Util";
	public static final String FIELD_UTIL_SERVER_EXECUTOR = "SERVER_EXECUTOR";

	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_WORLD_DATA = "WorldData";
	public static final String METHOD_WORLD_DATA_MAP_SEED = "mapSeed";
	public static final String CONSTRUCTOR_WORLD_DATA = "<init>";

	public static final String CLASS_WORLD_SETTINGS = "WorldSettings";
	public static final String CONSTRUCTOR_WORLD_SETTINGS = "<init>";

	public static final String CLASS_GAME_TYPE = "GameType";

	public static final String CLASS_BIOME_PROVIDER_SETTINGS = "OverworldBiomeProviderSettings";
	public static final String CONSTRUCTOR_BIOME_PROVIDER_SETTINGS = "<init>";

	public static final String CLASS_NOISE_BIOME_PROVIDER = "NoiseBiomeProvider";
	public static final String METHOD_NOISE_BIOME_PROVIDER_GET_BIOME = "getBiome";
	
	public static final String CLASS_OVERWORLD_BIOME_PROVIDER = "OverworldBiomeProvider";
	public static final String CONSTRUCTOR_OVERWORLD_BIOME_PROVIDER = "<init>";

	public static final String CLASS_BIOME_ZOOMER = "BiomeZoomer";
	public static final String METHOD_BIOME_ZOOMER_GET_BIOME = "getBiome";
}
