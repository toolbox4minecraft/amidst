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
				.ifDetect(c -> 
					c.isClassDataWildcardMatching(createIntCacheWildcardBytes())
					|| c.searchForStringContaining(", tcache: ") 
				)
				.thenDeclareRequired(SymbolicNames.CLASS_INT_CACHE)
					.requiredMethod(SymbolicNames.METHOD_INT_CACHE_RESET_INT_CACHE, "a").end()
			.next()
				.ifDetect(c -> c.searchForStringContaining("default_1_1"))
				.thenDeclareOptional(SymbolicNames.CLASS_WORLD_TYPE)
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_DEFAULT,      "b")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_FLAT,         "c")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED,    "e")
					.requiredField(SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
			.next()
				.ifDetect(c ->
					c.searchForLong(1000L)
					&& c.searchForLong(2001L)
					&& c.searchForLong(2000L)
				)
				.thenDeclareRequired(SymbolicNames.CLASS_GEN_LAYER)
					// one if the initializeAllBiomeGenerators-methods is required!
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1, "a").real("long").end()
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2, "a").real("long").symbolic(SymbolicNames.CLASS_WORLD_TYPE).end()
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3, "a").real("long").symbolic(SymbolicNames.CLASS_WORLD_TYPE).real("String").end()
					.optionalMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_4, "a").real("long").symbolic(SymbolicNames.CLASS_WORLD_TYPE).symbolic(SymbolicNames.CLASS_GEN_OPTIONS).end()
					.requiredMethod(SymbolicNames.METHOD_GEN_LAYER_GET_INTS,                          "a").real("int") .real("int")                             .real("int")   .real("int").end()
			.next()
				.ifDetect(c -> 
					c.getNumberOfConstructors() == 0
					&& c.getNumberOfMethods() == 6
					&& c.getNumberOfFields() >= 3
					&& c.getNumberOfFields() <= 4
					&& c.getField(0).hasFlags(AccessFlags.STATIC)
					&& c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.STATIC)
					&& c.searchForUtf8EqualTo("isDebugEnabled")
				)
				.thenDeclareOptional(SymbolicNames.CLASS_BLOCK_INIT)
					.requiredMethod(SymbolicNames.METHOD_BLOCK_INIT_INITIALIZE, "c").end()
			.next()
				.ifDetect(c ->
					// some leeway in case Mojang adds or removes fields in the future
					c.getNumberOfFields() > 70 && c.getNumberOfFields() < 100
					&& c.getNumberOfMethods() == 0
				)
				.thenDeclareOptional(SymbolicNames.CLASS_GEN_OPTIONS)
			.next()
				.ifDetect(c ->
					// some leeway in case Mojang adds or removes fields in the future
					c.getNumberOfFields() > 70 && c.getNumberOfFields() < 100
					&& c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
					&& c.getField(1).hasFlags(AccessFlags.PUBLIC)
				)
				.thenDeclareOptional(SymbolicNames.CLASS_GEN_OPTIONS_FACTORY)
					.requiredMethod(SymbolicNames.METHOD_GEN_OPTIONS_FACTORY_BUILD, "b").end()
					.requiredMethod(SymbolicNames.METHOD_GEN_OPTIONS_FACTORY_JSON_TO_FACTORY, "a").real("String").end()
			.construct();
	}

	private int[] createIntCacheWildcardBytes() {
		return new int[] { 0x11, 0x01, 0x00, 0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD,
				0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xBB,
				0x00, WILDCARD, 0x59, 0xB7, 0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xBB, 0x00, WILDCARD, 0x59, 0xB7,
				0x00, WILDCARD, 0xB3, 0x00, WILDCARD, 0xB1 };
	}
	// @formatter:on
}
