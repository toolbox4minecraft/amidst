package amidst.mojangapi.minecraftinterface.local;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.real.RealClass;
import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
	INSTANCE;

	private final ClassTranslator classTranslator = createClassTranslator();

	public ClassTranslator get() {
		return classTranslator;
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
					.ifDetect(c ->
						c.searchForLong(1000L)
						&& c.searchForLong(2001L)
						&& c.searchForLong(2000L)
					)
					.thenDeclareRequired(CLASS_LAYER_UTIL)
						.requiredMethod(METHOD_LAYER_UTIL_INITIALIZE_ALL, "a").real("long").symbolic(CLASS_WORLD_TYPE).symbolic(CLASS_GEN_SETTINGS).end()
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
							&& !c.searchForStringContaining("textures")
						    && c.hasMethodWithRealArguments()
						    && !c.hasMethodWithRealArguments(new String[]{ null })
						    && !c.hasMethodWithRealArguments(null, null)
						    && !c.hasMethodWithRealArguments(null, null, null)
						    && !c.hasMethodWithRealArguments(null, null, null, null)
						)
					).thenDeclareRequired(CLASS_GEN_SETTINGS)
						.requiredConstructor(CONSTRUCTOR_GEN_SETTINGS).end()
				.next()
					.ifDetect(c ->
						!c.getRealClassName().contains("$")
						&& c.getRealSuperClassName().equals("java/lang/Object")
						&& c.getNumberOfConstructors() == 1
						&& (c.getNumberOfMethods() >= 1 && c.getNumberOfMethods() <= 4)
						&& (c.getNumberOfFields() == 1 || c.getNumberOfFields() == 2)
						&& c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
						&& (c.hasMethodWithRealArguments("int", "int", "int", "int", null)
						    || c.hasMethodWithRealArguments("int", "int", "int", "int"))
					)
					.thenDeclareRequired(CLASS_LAYER) // the class Layer doesn't exist before 18w47b, but it still recognizes it instead as GenLayer for compatability
						.optionalMethod(METHOD_GEN_LAYER_GET_BIOME_DATA, "a").real("int").real("int").real("int").real("int").symbolic(CLASS_BIOME).end()
						.optionalMethod(METHOD_LAYER_GET_BIOME_DATA, "a").real("int").real("int").real("int").real("int").end() //changed in 18w47b
						.optionalField(FIELD_LAYER_LAZY_AREA, "b") // doesn't exist before 18w47b
				.next()
					.ifDetect(c -> // only use after 18w47b, slower in previous versions
					(c.getNumberOfMethods() == 2 || c.getNumberOfMethods() == 3)
					&& (c.getNumberOfFields() == 3 || c.getNumberOfFields() == 4)
					&& (c.getCpSize() == 63 || c.getCpSize() == 73)
					&& c.getRealSuperClassName().equals("java/lang/Object")
					&& c.getNumberOfConstructors() == 1
					&& c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.getField(2).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
					&& c.hasMethodWithRealArguments("int", "int")
					&& c.hasMethodWithRealArguments()
					&& c.isFinal()
					)
					.thenDeclareOptional(CLASS_LAZY_AREA)
						.requiredMethod(METHOD_LAZY_AREA_GET_VALUE, "a").real("int").real("int").end()
				.next()
					.ifDetect(c ->
						c.getNumberOfConstructors() == 1
						&& c.getNumberOfFields() > 0
						&& c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
						&& c.searchForFloat(0.62222224F)
					)
					.thenDeclareRequired(CLASS_BIOME)
						.optionalMethod(METHOD_BIOME_GET_ID, "a").symbolic(CLASS_BIOME).end()
				.next()
					.ifDetect(c ->
							c.getNumberOfConstructors() == 3
							&& c.getNumberOfFields() == 3
							&& c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.STATIC | AccessFlags.FINAL)
							&& c.searchForUtf8EqualTo("argument.id.invalid")
							&& c.searchForUtf8EqualTo("minecraft")
					)
					.thenDeclareOptional(CLASS_REGISTRY_KEY)
						.requiredConstructor(CONSTRUCTOR_REGISTRY_KEY).real("java.lang.String").end()
				.next()
					.ifDetect(c -> c.getNumberOfConstructors() <= 1
						&& c.getNumberOfFields() > 15
						&& c.searchForUtf8EqualTo("block")
						&& c.searchForUtf8EqualTo("potion")
						&& c.searchForUtf8EqualTo("biome")
						&& c.searchForUtf8EqualTo("item")
					)
					.thenDeclareOptional(CLASS_REGISTRY)
						.requiredField(FIELD_REGISTRY_META_REGISTRY, "f")
						.requiredMethod(METHOD_REGISTRY_GET_ID, "a").real("java.lang.Object").end()
						.optionalMethod(METHOD_REGISTRY_GET_BY_KEY, "b").symbolic(CLASS_REGISTRY_KEY).end()
						.optionalMethod(METHOD_REGISTRY_GET_BY_KEY2, "a").symbolic(CLASS_REGISTRY_KEY).end()  // the name changed in 18w43c

				.construct();
		}
		// @formatter:on
	private boolean extractRealClass(RealClass r) {
		System.err.println(r.getNumberOfMethods());
		return true;
	}
}
