package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;

	// TODO: correctly manage world types; remove duplication with legacy SymbolicNames
	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";
}
