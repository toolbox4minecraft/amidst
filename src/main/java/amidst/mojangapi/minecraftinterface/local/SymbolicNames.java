package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

    public static final String CLASS_REGISTRY = "Registry";
    public static final String FIELD_REGISTRY_META_REGISTRY = "metaRegistry";
    public static final String FIELD_REGISTRY_META_REGISTRY2 = "metaRegistry2";
    public static final String FIELD_REGISTRY_META_REGISTRY3 = "metaRegistry3";
    public static final String METHOD_REGISTRY_CREATE_KEY = "createRegistryKey";
    public static final String METHOD_REGISTRY_GET_BY_KEY = "getByKey";
    public static final String METHOD_REGISTRY_GET_ID = "getId";

    public static final String CLASS_REGISTRY_ACCESS = "RegistryAccess";
    public static final String METHOD_REGISTRY_ACCESS_BUILTIN = "builtin";
    public static final String METHOD_REGISTRY_ACCESS_GET_REGISTRY = "getRegistry";

    public static final String CLASS_RESOURCE_KEY = "ResourceKey";
    public static final String CONSTRUCTOR_RESOURCE_KEY = "<init>";

    public static final String CLASS_REGISTRY_ACCESS_KEY = "RegistryKey";

	public static final String CLASS_UTIL = "Util";

	public static final String CLASS_WORLD_GEN_SETTINGS = "WorldGenSettings";
	public static final String METHOD_WORLD_GEN_SETTINGS_CREATE = "create";
	public static final String METHOD_WORLD_GEN_SETTINGS_CREATE2 = "create2";

	public static final String CLASS_DIMENSION_SETTINGS = "DimensionSettings";
	public static final String FIELD_DIMENSION_SETTINGS_GENERATOR = "generator";

	public static final String CLASS_NOISE_BIOME_PROVIDER = "NoiseBiomeProvider";
	public static final String METHOD_NOISE_BIOME_PROVIDER_GET_BIOME = "getBiome";

	public static final String CLASS_BIOME_ZOOMER = "BiomeZoomer";
	public static final String METHOD_BIOME_ZOOMER_GET_BIOME = "getBiome";

    public static final String CLASS_BIOME = "Biome";
}
