package amidst.mojangapi.minecraftinterface.legacy;

import static amidst.mojangapi.minecraftinterface.legacy._1_13SymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.translator.ClassTranslator;

public enum _1_13ClassTranslator {
	INSTANCE;

	private final ClassTranslator classTranslator = createClassTranslator();

	public static ClassTranslator get() {
		return INSTANCE.classTranslator;
	}

	// @formatter:off
	private ClassTranslator createClassTranslator() {
		return ClassTranslator
			.builder()
				.ifDetect(c -> c.searchForStringContaining("default_1_1"))
				.thenDeclareRequired(CLASS_WORLD_TYPE)
					.requiredField(FIELD_WORLD_TYPE_DEFAULT,      "b")
					.requiredField(FIELD_WORLD_TYPE_FLAT,         "c")
					.requiredField(FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
					.requiredField(FIELD_WORLD_TYPE_AMPLIFIED,    "e")
					.requiredField(FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
			.next()
				.ifDetect(c -> c.getNumberOfConstructors() == 0
					&& c.getNumberOfMethods() >= 6
					&& c.getNumberOfMethods() <= 11
					&& c.getNumberOfFields() >= 3
					&& c.getNumberOfFields() <= 4
					&& c.getField(0).hasFlags(AccessFlags.STATIC)
					&& c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.STATIC)
					&& c.searchForUtf8EqualTo("isDebugEnabled")
				)
				.thenDeclareRequired(CLASS_BOOTSTRAP)
					.optionalMethod(METHOD_BOOTSTRAP_REGISTER, "c").end()
                    .optionalMethod(METHOD_BOOTSTRAP_REGISTER2, "b").end() // the name changed in 18w43c
                    .optionalMethod(METHOD_BOOTSTRAP_REGISTER3, "a").end() // the name changed again in 19w07a
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
					    && c.hasMethodWithRealArgsReturning(new String[]{ null })
					    && !c.hasMethodWithRealArgsReturning(null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null, null)
					    && !c.hasMethodWithRealArgsReturning(null, null, null, null, null)
					)
				).thenDeclareRequired(CLASS_GEN_SETTINGS)
					.requiredConstructor(CONSTRUCTOR_GEN_SETTINGS).end()
			.next()
				.ifDetect(c ->
					c.searchForLong(1000L)
					&& c.searchForLong(2001L)
					&& c.searchForLong(2000L)
				)
				.thenDeclareRequired(CLASS_LAYER_UTIL)
					.requiredMethod(METHOD_LAYER_UTIL_GET_LAYERS, "a").real("long").symbolic(CLASS_WORLD_TYPE).symbolic(CLASS_GEN_SETTINGS).end()
			.next()
				.ifDetect(c ->
					!c.getRealClassName().contains("$")
					&& c.getRealSuperClassName().equals("java/lang/Object")
					&& c.getNumberOfConstructors() == 1
					&& (c.getNumberOfMethods() >= 1 && c.getNumberOfMethods() <= 4)
					&& (c.getNumberOfFields() == 1 || c.getNumberOfFields() == 2)
					&& c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& (c.hasMethodWithRealArgsReturning("int", "int", "int", "int", null, null)
					    || c.hasMethodWithRealArgsReturning("int", "int", "int", "int", null))
				)
				.thenDeclareRequired(CLASS_GEN_LAYER)
					.optionalField(FIELD_GEN_LAYER_LAZY_AREA_FACTORY, "a")
					.optionalField(FIELD_GEN_LAYER_LAZY_AREA, "b")
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
					.optionalMethod(METHOD_LAZY_AREA_GET, "a").real("int").real("int").end()
					.requiredField(FIELD_LAZY_AREA_PIXEL_TRANSFORMER, "a")
			.next()
				.ifDetect(c -> 
					c.getRealClassName().equals("bzg")
				)
				.thenDeclareOptional(CLASS_AREA_DIMENSION)
					.optionalConstructor(CONSTRUCTOR_AREA_DIMENSION).real("int").real("int").real("int").real("int").end()
			.next()
				.ifDetect(c -> 
					c.isInterface()
					&& c.getNumberOfMethods() == 1
					&& c.getNumberOfConstructors() == 0
					&& c.getNumberOfFields() == 0
					&& c.searchForUtf8EqualTo("make")
				)
				.thenDeclareOptional(CLASS_AREA_FACTORY)
					.optionalMethod(METHOD_AREA_FACTORY_MAKE, "make").symbolic(CLASS_AREA_DIMENSION).end()
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
					c.searchForStringContaining("Server-Worker-")
					&& c.searchForStringContaining("os.name")
					&& c.searchForLong(1000000L)
				)
				.thenDeclareOptional(CLASS_UTIL)
					.optionalField(FIELD_UTIL_SERVER_EXECUTOR, "c")
			.construct();
	}
	// @formatter:on
}
