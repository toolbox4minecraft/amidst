package amidst.mojangapi.minecraftinterface.legacy;

import static amidst.mojangapi.minecraftinterface.legacy.LegacySymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.real.RealClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;

@Immutable
public enum LegacyClassTranslator {
	INSTANCE;

	private static final int WILDCARD = RealClass.CLASS_DATA_WILDCARD;

	private final ClassTranslator classTranslator = createClassTranslator();

	public static ClassTranslator get() {
		return INSTANCE.classTranslator;
	}

	// @formatter:off
	private ClassTranslator createClassTranslator() {
		return ClassTranslator
			.builder()
				.ifDetect(c ->
					c.isClassDataWildcardMatching(createIntCacheWildcardBytes())
					|| c.searchForStringContaining(", tcache: ")
				)
				.thenDeclareRequired(CLASS_INT_CACHE)
					.requiredMethod(METHOD_INT_CACHE_RESET_INT_CACHE, "a").end()
			.next()
				.ifDetect(c -> c.searchForStringContaining("default_1_1"))
				.thenDeclareOptional(CLASS_WORLD_TYPE)
					.requiredField(FIELD_WORLD_TYPE_DEFAULT,      "b")
					.requiredField(FIELD_WORLD_TYPE_FLAT,         "c")
					.requiredField(FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
					.requiredField(FIELD_WORLD_TYPE_AMPLIFIED,    "e")
					.requiredField(FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
			.next()
				.ifDetect(c ->
					c.searchForLong(1000L)
					&& c.searchForLong(2001L)
					&& c.searchForLong(2000L)
				)
				.thenDeclareRequired(LegacySymbolicNames.CLASS_GEN_LAYER)
					// one if the initializeAllBiomeGenerators-methods is required!
					.optionalMethod(METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1, "a").real("long").end()
					.optionalMethod(METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2, "a").real("long").symbolic(CLASS_WORLD_TYPE).end()
					.optionalMethod(METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3, "a").real("long").symbolic(CLASS_WORLD_TYPE).real("String").end()
					.optionalMethod(METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_4, "a").real("long").symbolic(CLASS_WORLD_TYPE).symbolic(CLASS_GEN_OPTIONS).end()
					.requiredMethod(METHOD_GEN_LAYER_GET_INTS,                          "a").real("int") .real("int")                             .real("int")   .real("int").end()
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
				.thenDeclareOptional(CLASS_BLOCK_INIT)
					.requiredMethod(METHOD_BLOCK_INIT_INITIALIZE, "c").end()
			.next()
				.ifDetect(c ->
					// some leeway in case Mojang adds or removes fields in the future
					c.getNumberOfFields() > 70 && c.getNumberOfFields() < 100
					&& c.getNumberOfMethods() == 0
				)
				.thenDeclareOptional(CLASS_GEN_OPTIONS)
			.next()
				.ifDetect(c ->
					// some leeway in case Mojang adds or removes fields in the future
					c.getNumberOfFields() > 70 && c.getNumberOfFields() < 100
					&& c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
					&& c.getField(1).hasFlags(AccessFlags.PUBLIC)
					&& (!c.getField(1).hasFlags(AccessFlags.STATIC))
				)
				.thenDeclareOptional(CLASS_GEN_OPTIONS_FACTORY)
					.requiredMethod(METHOD_GEN_OPTIONS_FACTORY_BUILD, "b").end()
					.requiredMethod(METHOD_GEN_OPTIONS_FACTORY_JSON_TO_FACTORY, "a").real("java.lang.String").end()
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
