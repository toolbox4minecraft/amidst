package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

    public static final String CLASS_REGISTRY = "Registry";
    public static final String FIELD_REGISTRY_META_REGISTRY = "metaRegistry";
    public static final String FIELD_REGISTRY_META_REGISTRY2 = "metaRegistry2";
    public static final String METHOD_REGISTRY_CREATE_KEY = "createRegistryKey";
    public static final String METHOD_REGISTRY_GET_BY_KEY = "getByKey";
    public static final String METHOD_REGISTRY_GET_ID = "getId";

    public static final String CLASS_RESOURCE_KEY = "ResourceKey";
    public static final String CONSTRUCTOR_RESOURCE_KEY = "<init>";

	public static final String CLASS_UTIL = "Util";
	public static final String FIELD_UTIL_SERVER_EXECUTOR = "SERVER_EXECUTOR";

	public static final String CLASS_WORLD_GEN_SETTINGS = "WorldGenSettings";
	public static final String METHOD_WORLD_GEN_SETTINGS_CREATE = "create";
	public static final String METHOD_WORLD_GEN_SETTINGS_OVERWORLD = "overworld";
	public static final String METHOD_WORLD_GEN_SETTINGS_OVERWORLD2 = "overworld2";

	public static final String CLASS_NOISE_BIOME_PROVIDER = "NoiseBiomeProvider";
	public static final String METHOD_NOISE_BIOME_PROVIDER_GET_BIOME = "getBiome";

	public static final String CLASS_OVERWORLD_BIOME_ZOOMER = "OverworldBiomeZoomer";
	public static final String METHOD_BIOME_ZOOMER_GET_BIOME = "getBiome";

    public static final String CLASS_BIOME = "Biome";
}
