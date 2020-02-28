package amidst.mojangapi.minecraftinterface.legacy;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.local.SymbolicNames;

@Immutable
public enum _1_13SymbolicNames {
	;
	
	public static final String CLASS_BOOTSTRAP = "Bootstrap";
	public static final String METHOD_BOOTSTRAP_REGISTER = "register";
	public static final String METHOD_BOOTSTRAP_REGISTER2 = "register2";
	public static final String METHOD_BOOTSTRAP_REGISTER3 = "register3";
	
	public static final String CLASS_UTIL = "Util";
	public static final String FIELD_UTIL_SERVER_EXECUTOR = "SERVER_EXECUTOR";
	
    public static final String CLASS_WORLD_TYPE = SymbolicNames.CLASS_WORLD_TYPE;
    public static final String FIELD_WORLD_TYPE_DEFAULT = SymbolicNames.FIELD_WORLD_TYPE_DEFAULT;
    public static final String FIELD_WORLD_TYPE_FLAT = SymbolicNames.FIELD_WORLD_TYPE_FLAT;
    public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES;
    public static final String FIELD_WORLD_TYPE_AMPLIFIED = SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED;
    public static final String FIELD_WORLD_TYPE_CUSTOMIZED = SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED;
    
	public static final String CLASS_LAYER_UTIL = "LayerUtil";
	public static final String METHOD_LAYER_UTIL_GET_LAYERS = "getLayers";
	
	public static final String CLASS_GEN_LAYER = "GenLayer";
	public static final String FIELD_GEN_LAYER_LAZY_AREA_FACTORY = "lazyAreaFactory";
	public static final String FIELD_GEN_LAYER_LAZY_AREA = "lazyArea";

	public static final String CLASS_GEN_SETTINGS = "OverworldGenSettings";
	public static final String CONSTRUCTOR_GEN_SETTINGS = "<init>";
	
	public static final String CLASS_LAZY_AREA = "LazyArea";
	public static final String METHOD_LAZY_AREA_GET = "get";
	public static final String FIELD_LAZY_AREA_PIXEL_TRANSFORMER = "pixelTransformer";
	
	public static final String CLASS_AREA_FACTORY = "AreaFactory";
	public static final String METHOD_AREA_FACTORY_MAKE = "make";
	
	public static final String CLASS_AREA_DIMENSION = "AreaDimension";
	public static final String CONSTRUCTOR_AREA_DIMENSION = "<init>";
	
	public static final String CLASS_PIXEL_TRANSFORMER = "PixelTransformer";
	public static final String METHOD_PIXEL_TRANSFORMER_APPLY = "apply";
}
