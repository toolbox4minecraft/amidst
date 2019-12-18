package amidst.mojangapi.minecraftinterface.local;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
	INSTANCE;

	private final ClassTranslator classTranslator = createClassTranslator();

	public static ClassTranslator get() {
		return INSTANCE.classTranslator;
	}

	// @formatter:off
	private ClassTranslator createClassTranslator() {
	    return ClassTranslator
            .builder()
		    .ifDetect(c -> c.getNumberOfConstructors() <= 1
		        && c.getNumberOfFields() > 15
		        && c.searchForUtf8EqualTo("block")
		        && c.searchForUtf8EqualTo("potion")
		        && c.searchForUtf8EqualTo("biome")
		        && c.searchForUtf8EqualTo("item")
		    )
		    .thenDeclareOptional(CLASS_REGISTRY)
		    	.requiredField(FIELD_REGISTRY_META_REGISTRY, "f")
            .next()
                .ifDetect(c -> c.searchForStringContaining("default_1_1"))
                .thenDeclareRequired(CLASS_WORLD_TYPE)
                    .requiredField(FIELD_WORLD_TYPE_DEFAULT,      "b")
                    .requiredField(FIELD_WORLD_TYPE_FLAT,         "c")
                    .requiredField(FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
                    .requiredField(FIELD_WORLD_TYPE_AMPLIFIED,    "e")
                    .requiredField(FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
            .next()
                .ifDetect(c -> c.getNumberOfFields() > 40
                    && c.searchForUtf8EqualTo("SizeOnDisk")
                )
                .thenDeclareRequired(CLASS_LEVEL_DATA)
                    .requiredMethod(METHOD_LEVEL_DATA_MAP_SEED, "c").real("long").end()
			.next()
				.ifDetect(c ->
					c.searchForLong(1000L)
					&& c.searchForLong(2001L)
					&& c.searchForLong(2000L)
				)
				.thenDeclareRequired(CLASS_LAYERS)
					.requiredMethod(METHOD_LAYERS_GET_DEFAULT_LAYER, "a").symbolic(CLASS_WORLD_TYPE).symbolic(CLASS_GEN_SETTINGS).real("java.util.function.LongFunction").end()
			.next()
				.ifDetect(c -> ( // before 18w46a
						c.getNumberOfConstructors() == 1
						&& c.getNumberOfFields() >= 15
						&& c.getNumberOfMethods() >= 19
						&& c.searchForFloat(684.412F)
					) || ( // from 18w46a
						!c.getRealClassName().contains("$")
						&& !c.getRealSuperClassName().equals("java/lang/Object")
						&& c.getNumberOfFields() >= 4
						&& c.getNumberOfMethods() == c.getNumberOfFields()
						&& c.getNumberOfConstructors() >= 1
						&& c.getField(0).hasFlags(AccessFlags.FINAL | AccessFlags.PRIVATE)
						&& !c.getField(0).hasFlags(AccessFlags.STATIC)
                        && c.getField(2).hasFlags(AccessFlags.FINAL | AccessFlags.PRIVATE)
						&& !c.searchForStringContaining("textures")
					    && c.hasMethodWithNoArgs()
					    && !c.hasMethodWithRealArgsReturning(null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null, null, null)
					)
				)
				.thenDeclareRequired(CLASS_GEN_SETTINGS)
					.requiredConstructor(CONSTRUCTOR_GEN_SETTINGS).end()
			.next()
				.ifDetect(c -> 
					(c.getNumberOfMethods() == 2 || c.getNumberOfMethods() == 3)
					&& (c.getNumberOfFields() == 3 || c.getNumberOfFields() == 4)
					&& c.searchForInt(Integer.MIN_VALUE)
					&& c.getRealSuperClassName().equals("java/lang/Object")
					&& c.getNumberOfConstructors() == 1
					&& c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.getField(2).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.hasMethodWithRealArgsReturning("int", "int", "int")
					&& c.hasMethodWithNoArgs()
					&& c.isFinal()
				)
				.thenDeclareRequired(CLASS_LAZY_AREA)
					.requiredMethod(METHOD_LAZY_AREA_GET, "a").real("int").real("int").end()
					.optionalField(FIELD_LAZY_AREA_PIXEL_TRANSFORMER, "a")
			.next()
				.ifDetect(c -> 
				c.searchForFloat(0.25f)
				&& c.searchForInt(Integer.MIN_VALUE)
				&& c.getNumberOfFields() == 5
				)
				.thenDeclareRequired(CLASS_LAZY_AREA_CONTEXT)
					.requiredConstructor(CONSTRUCTOR_LAZY_AREA_CONTEXT).real("int").real("long").real("long").end()
			.next()
				.ifDetect(c -> 
					c.isInterface()
					&& c.hasMethodWithNoArgs()
					&& c.getNumberOfMethods() == 1
					&& c.getNumberOfConstructors() == 0
					&& c.getNumberOfFields() == 0
					&& c.searchForUtf8EqualTo("make")
				)
				.thenDeclareRequired(CLASS_AREA_FACTORY)
					.requiredMethod(METHOD_AREA_FACTORY_MAKE, "make").end()
			.next()
				.ifDetect(c -> 
					c.isInterface()
					&& c.hasMethodWithRealArgsReturning("int", "int", "int")
					&& c.getNumberOfMethods() == 1
					&& c.getNumberOfConstructors() == 0
					&& c.getNumberOfFields() == 0
					&& c.searchForUtf8EqualTo("apply")
				)
				.thenDeclareRequired(CLASS_PIXEL_TRANSFORMER)
					.requiredMethod(METHOD_PIXEL_TRANSFORMER_APPLY, "apply").real("int").real("int").end()
			.next()
				.ifDetect(c -> 
					//c.getRealClassName().equals("blx")
					c.getNumberOfMethods() == 7
					&& c.getNumberOfFields() == 2
					&& c.getNumberOfConstructors() == 1
					&& c.searchForDouble(4.0D)
					&& c.searchForDouble(0.5D)
					&& c.searchForDouble(0.9D)
					&& c.searchForDouble(1024.0D)
					&& c.searchForLong(1024L)
					&& c.hasMethodWithRealArgsReturning("long", "int", "int", "int", "double", "double", "double", null)
				)
				.thenDeclareRequired(CLASS_FUZZY_OFFSET_BIOME_ZOOMER)
					.requiredMethod(METHOD_FUZZY_OFFSET_BIOME_ZOOMER_GET_FIDDLE_DISTANCE, "a").real("long").real("int").real("int").real("int").real("double").real("double").real("double").end()
            .construct();
	}
	// @formatter:on
}
