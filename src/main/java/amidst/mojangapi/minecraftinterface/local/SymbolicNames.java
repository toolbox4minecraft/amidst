package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

	public static final String CLASS_REGISTRY = "Registry";
	public static final String FIELD_REGISTRY_BIOME = "biomeRegistry";

	public static final String CLASS_MAPPED_REGISTRY = "MappedRegistry";
	public static final String METHOD_MAPPED_REGISTRY_GET_ID = "getId";
	public static final String METHOD_MAPPED_REGISTRY_BY_ID = "byId";

	public static final String CLASS_UTIL = "Util";
	public static final String FIELD_UTIL_SERVER_EXECUTOR = "SERVER_EXECUTOR";

	// TODO: correctly manage world types; remove duplication with legacy SymbolicNames
	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_LEVEL_DATA = "LevelData";
	public static final String METHOD_LEVEL_DATA_MAP_SEED = "mapSeed";

	// LazyArea quarter resolution data retreival
	public static final String CLASS_LAYERS = "Layers";
	public static final String METHOD_LAYERS_GET_DEFAULT_LAYER = "getDefaultLayer";

	public static final String CLASS_GEN_SETTINGS = "OverworldGenSettings";
	public static final String CONSTRUCTOR_GEN_SETTINGS = "<init>";

	public static final String CLASS_LAZY_AREA = "LazyArea";
	public static final String METHOD_LAZY_AREA_GET = "get";
	public static final String FIELD_LAZY_AREA_PIXEL_TRANSFORMER = "pixelTransformer";

	public static final String CLASS_LAZY_AREA_CONTEXT = "LazyAreaContext";
	public static final String CONSTRUCTOR_LAZY_AREA_CONTEXT = "<init>";

	public static final String CLASS_AREA_FACTORY = "AreaFactory";
	public static final String METHOD_AREA_FACTORY_MAKE = "make";

	public static final String CLASS_PIXEL_TRANSFORMER = "PixelTransformer";
	public static final String METHOD_PIXEL_TRANSFORMER_APPLY = "apply";

	// BiomeZoomer full resolution converter
	public static final String CLASS_NOISE_BIOME_SOURCE = "NoiseBiomeSource";

	public static final String CLASS_FUZZY_OFFSET_CONSTANT_COLUMN_BIOME_ZOOMER = "FuzzyOffsetConstantColumnBiomeZoomer";
	public static final String METHOD_FUZZY_OFFSET_CONSTANT_COLUMN_BIOME_ZOOMER_GET_BIOME = "getBiome";
}
