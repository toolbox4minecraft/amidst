package amidst.mojangapi.minecraftinterface.local;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_BIOME;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_BIOME_ZOOMER;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_GAME_TYPE;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_NOISE_BIOME_PROVIDER;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_REGISTRY;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_REGISTRY_KEY;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_WORLD_DATA;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_WORLD_SETTINGS;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CLASS_WORLD_TYPE;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CONSTRUCTOR_REGISTRY_KEY;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CONSTRUCTOR_WORLD_DATA;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.CONSTRUCTOR_WORLD_SETTINGS;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_REGISTRY_META_REGISTRY;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_WORLD_TYPE_AMPLIFIED;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_WORLD_TYPE_CUSTOMIZED;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_WORLD_TYPE_DEFAULT;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_WORLD_TYPE_FLAT;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.FIELD_WORLD_TYPE_LARGE_BIOMES;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.METHOD_BIOME_ZOOMER_GET_BIOME;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.METHOD_NOISE_BIOME_PROVIDER_GET_BIOME;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.METHOD_REGISTRY_GET_BY_KEY;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.METHOD_REGISTRY_GET_ID;
import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.METHOD_WORLD_DATA_MAP_SEED;

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
                    .requiredMethod(METHOD_REGISTRY_GET_BY_KEY, "a").symbolic(CLASS_REGISTRY_KEY).end()

            .next()
                .ifDetect(c -> c.searchForStringContaining("default_1_1"))
                .thenDeclareRequired(CLASS_WORLD_TYPE)
                    .requiredField(FIELD_WORLD_TYPE_DEFAULT,      "b")
                    .requiredField(FIELD_WORLD_TYPE_FLAT,         "c")
                    .requiredField(FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
                    .requiredField(FIELD_WORLD_TYPE_AMPLIFIED,    "e")
                    .requiredField(FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
            .next()
                .ifDetect(c -> c.getRealSuperClassName().equals("java/lang/Enum")
                    && c.searchForUtf8EqualTo("gameMode.")
                )
                .thenDeclareRequired(CLASS_GAME_TYPE)
            .next()
                .ifDetect(c -> !c.getRealClassName().contains("$")
                    && c.isFinal()
                    && c.getNumberOfConstructors() == 2
                    && c.getNumberOfFields() >= 5
                    && c.getNumberOfFields() <= 10
                    && c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.FINAL)
                )
                .thenDeclareRequired(CLASS_WORLD_SETTINGS)
                    .requiredConstructor(CONSTRUCTOR_WORLD_SETTINGS).real("long").symbolic(CLASS_GAME_TYPE).real("boolean").real("boolean").symbolic(CLASS_WORLD_TYPE).end()
            .next()
                .ifDetect(c -> c.getNumberOfFields() > 40
                    && c.searchForUtf8EqualTo("SizeOnDisk")
                )
                .thenDeclareRequired(CLASS_WORLD_DATA)
                    .requiredConstructor(CONSTRUCTOR_WORLD_DATA).symbolic(CLASS_WORLD_SETTINGS).real("java.lang.String").end()
                    .requiredMethod(METHOD_WORLD_DATA_MAP_SEED, "c").real("long").end()
            .next()
                .ifDetect(c -> c.getRealClassName().contains("$")
                    && c.isInterface()
                    && c.getNumberOfMethods() == 1
                    && c.hasMethodWithRealArgsReturning("int", "int", "int", null)
                    && !c.hasMethodWithRealArgsReturning("int", "int", "int", "boolean")
                )
                .thenDeclareRequired(CLASS_NOISE_BIOME_PROVIDER)
                    .requiredMethod(METHOD_NOISE_BIOME_PROVIDER_GET_BIOME, "b").real("int").real("int").real("int").end()
            .next()
                .ifDetect(c -> !c.getRealClassName().contains("$")
                    && c.getRealSuperClassName().equals("java/lang/Enum")
                    && c.hasMethodWithRealArgsReturning("long", "int", "int", "int", null, null)
                    && c.getNumberOfMethods() == 4
                )
                .thenDeclareRequired(CLASS_BIOME_ZOOMER)
                    .requiredMethod(METHOD_BIOME_ZOOMER_GET_BIOME, "a").real("long").real("int").real("int").real("int").symbolic(CLASS_NOISE_BIOME_PROVIDER).end()
            .next()
                .ifDetect(c ->
                    c.getNumberOfConstructors() == 1
                    && c.getNumberOfFields() > 0
                    && c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
                    && (c.searchForFloat(0.62222224F) || c.searchForUtf8EqualTo("Feature placement"))
                )
                .thenDeclareRequired(CLASS_BIOME)
            .construct();
	}
	// @formatter:on
}
