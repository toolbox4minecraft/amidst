package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

	public static final String CLASS_INT_CACHE = "IntCache";
	public static final String METHOD_INT_CACHE_RESET_INT_CACHE = "resetIntCache";

	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_GEN_LAYER = "GenLayer";
	public static final String METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1 = "initializeAllBiomeGenerators1";
	public static final String METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2 = "initializeAllBiomeGenerators2";
	public static final String METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3 = "initializeAllBiomeGenerators3";
	public static final String METHOD_GEN_LAYER_GET_INTS = "getInts";

	public static final String CLASS_BLOCK_INIT = "BlockInit";
	public static final String METHOD_BLOCK_INIT_INITIALIZE = "initialize";

	// The early betas used a completely different biome generation system 
	public static final String CLASS_BETA_BIOME                       = "betaBiome";
	public static final String   FIELD_BETA_BIOME_NAME                = "name";	
	
	public static final String CLASS_BETA_BIOMEGENERATOR              = "betaBiomeGenerator";
	public static final String   METHOD_BETA_BIOMEGENERATOR_GET_BIOME = "getBiomes";
	
	public static final String CLASS_BETA_WORLD                       = "betaWorld";

	public static final String CLASS_BETA_DIMENSION_ABSTRACT          = "betaDimension_abstract";
	public static final String CLASS_BETA_DIMENSION_CONCRETE          = "betaDimension_concrete";
	public static final String   FIELD_BETA_DIMENSION_WORLD           = "betaWorldField";
	public static final String   FIELD_BETA_DIMENSION_BIOMEGENERATOR  = "betaBiomeGeneratorField";
}
