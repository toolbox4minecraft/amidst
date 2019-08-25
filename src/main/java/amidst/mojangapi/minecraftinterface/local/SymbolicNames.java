package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

	// TODO: correctly manage world types; remove duplication with LegacySymbolicNames
	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_BOOTSTRAP = "Bootstrap";
	public static final String METHOD_BOOTSTRAP_REGISTER = "register";
	public static final String METHOD_BOOTSTRAP_REGISTER2 = "register2";
	public static final String METHOD_BOOTSTRAP_REGISTER3 = "register3";

	public static final String CLASS_LAYER_UTIL = "LayerUtil";
	public static final String METHOD_LAYER_UTIL_INITIALIZE_ALL = "initializeAll";

	public static final String CLASS_GEN_SETTINGS = "OverworldGenSettings";
	public static final String CONSTRUCTOR_GEN_SETTINGS = "<init>";

	public static final String CLASS_LAYER = "Layer";	// used to be GenLayer
	public static final String METHOD_LAYER_GET_BIOME_DATA = "getBiomeDataNew";
	public static final String METHOD_GEN_LAYER_GET_BIOME_DATA = "getBiomeDataOld";
	public static final String FIELD_LAYER_LAZY_AREA = "lazyArea";
	
	public static final String CLASS_LAZY_AREA = "LazyArea";
	public static final String METHOD_LAZY_AREA_GET_VALUE = "getValue";
	
	public static final String CLASS_BIOME = "Biome";
	public static final String METHOD_BIOME_GET_ID = "getBiomeId";

	public static final String CLASS_REGISTRY = "Registry";
	public static final String FIELD_REGISTRY_META_REGISTRY = "metaRegistry";
	public static final String METHOD_REGISTRY_GET_BY_KEY = "getByKey";
	public static final String METHOD_REGISTRY_GET_BY_KEY2 = "getByKey2";
	public static final String METHOD_REGISTRY_GET_ID = "getId";

	public static final String CLASS_REGISTRY_KEY = "RegistryKey";
	public static final String CONSTRUCTOR_REGISTRY_KEY = "<init>";
}
