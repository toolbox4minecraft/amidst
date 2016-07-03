package amidst.mojangapi.minecraftinterface.local;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.real.RealClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;

@Immutable
public enum DefaultClassTranslator {
	INSTANCE;

	private static final int WILDCARD = RealClass.CLASS_DATA_WILDCARD;

	private final ClassTranslator classTranslator = createClassTranslator();

	public ClassTranslator get() {
		return classTranslator;
	}

	// @formatter:off
	private ClassTranslator createClassTranslator() {
		return ClassTranslator
			.builder()
				.ifDetect()
					.wildcardBytes(createIntCacheWildcardBytes())
					.or()
					.stringContaining(", tcache: ")
				.thenDeclareRequired(SymbolicNames.CLASS_INT_CACHE)
					.requiredMethod(SymbolicNames.METHOD_INT_CACHE_RESET_INT_CACHE, "a").end()
			.next()
				.ifDetect()
					.stringContaining("default_1_1")
				.thenDeclareOptional(SymbolicNames.CLASS_WORLD_TYPE)
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_DEFAULT,      "b")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_FLAT,         "c")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED,    "e")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
			.next()
				.ifDetect()
					.longs(1000L, 2001L, 2000L)
				.thenDeclareRequired(SymbolicNames.CLASS_GEN_LAYER)
					// one if the initializeAllBiomeGenerators-methods is required!
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1, "a").real("long").end()
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2, "a").real("long").symbolic("WorldType").end()
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3, "a").real("long").symbolic("WorldType").real("String").end()
					.requiredMethod(SymbolicNames.METHOD_GEN_LAYER_GET_INTS,                          "a").real("int") .real("int")          .real("int")   .real("int").end()
			.next()
				.ifDetect()
					.numberOfConstructors(0)
					.numberOfMethods(6)
					.numberOfFields(3)
					.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 0, 1, 2)
					.utf8EqualTo("isDebugEnabled")
					.or()
					.numberOfConstructors(0)
					.numberOfMethods(6)
					.numberOfFields(3)
					.fieldFlags(AccessFlags.PUBLIC | AccessFlags.STATIC, 0)
					.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 1, 2)
					.utf8EqualTo("isDebugEnabled")
				.thenDeclareOptional(SymbolicNames.CLASS_BLOCK_INIT)
					.requiredMethod(SymbolicNames.METHOD_BLOCK_INIT_INITIALIZE, "c").end()
			.construct();
	}
	// @formatter:on

	private int[] createIntCacheWildcardBytes() {
		return new int[] { 0x11, 0x01, 0x00, 0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD,
				0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xBB,
				0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7,
				0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xB1 };
	}
}
