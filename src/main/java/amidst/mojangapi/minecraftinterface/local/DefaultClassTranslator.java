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
            	.ifDetect(c -> c.getNumberOfFields() == 3
            			&& c.searchForUtf8EqualTo("Unable to load registries")
            			&& c.getField(1).hasFlags(AccessFlags.STATIC)
            	)
            	.thenDeclareRequired(CLASS_BOOTSTRAP)
            		.requiredField(FIELD_BOOTSTRAP_IS_BOOTSTRAPPED, "b")
            .next()
            	.ifDetect(c -> c.searchForUtf8EqualTo("Game version not set"))
            	.thenDeclareOptional(CLASS_SHARED_CONSTANTS)
            		.requiredMethod(METHOD_SHARED_CONSTANTS_DETECT_VERSION, "a").end()
            .next()
                .ifDetect(c -> c.getNumberOfConstructors() == 3
                        && (c.getNumberOfFields() >= 3 && c.getNumberOfFields() <= 7)
                        && c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
                        && c.searchForUtf8EqualTo("minecraft")
                        && c.searchForUtf8EqualTo("argument.id.invalid")
                )
                .thenDeclareRequired(CLASS_RESOURCE_KEY)
                    .requiredConstructor(CONSTRUCTOR_RESOURCE_KEY).real("java.lang.String").end()
            .next()
            	.ifDetect(c -> c.searchForStringContaining("ResourceKey["))
            	.thenDeclareOptional(CLASS_REGISTRY_ACCESS_KEY) // since 20w21a
            .next()
                .ifDetect(c -> c.getNumberOfConstructors() <= 1
                    && c.getNumberOfFields() > 15
                    && c.searchForUtf8EqualTo("block")
                    && c.searchForUtf8EqualTo("potion")
                    && (c.searchForUtf8EqualTo("biome") || c.searchForUtf8EqualTo("worldgen/biome"))
                    && c.searchForUtf8EqualTo("item")
                )
                .thenDeclareRequired(CLASS_REGISTRY)
                    .requiredField(FIELD_REGISTRY_META_REGISTRY, "f")
                    .requiredField(FIELD_REGISTRY_META_REGISTRY2, "i")
                    .requiredField(FIELD_REGISTRY_META_REGISTRY3, "h")
                	.optionalMethod(METHOD_REGISTRY_CREATE_KEY, "a").real("java.lang.String").end()
                    .requiredMethod(METHOD_REGISTRY_GET_ID, "a").real("java.lang.Object").end()
                    .requiredMethod(METHOD_REGISTRY_GET_BY_KEY, "a").symbolic(CLASS_RESOURCE_KEY).end()
            .next()
                .ifDetect(c -> c.searchForStringContaining("Missing registry: ")) // since 20w28a
                .thenDeclareOptional(CLASS_REGISTRY_ACCESS)
                    .optionalMethod(METHOD_REGISTRY_ACCESS_BUILTIN, "b").end()
                    .optionalMethod(METHOD_REGISTRY_ACCESS_BUILTIN2, "a").end() // since 20w51a
                    .requiredMethod(METHOD_REGISTRY_ACCESS_GET_REGISTRY, "b").symbolic(CLASS_REGISTRY_ACCESS_KEY).end()
            .next()
                .ifDetect(c -> c.searchForUtf8EqualTo("level-seed")
                	&& c.searchForUtf8EqualTo("generator-settings")
                )
                .thenDeclareRequired(CLASS_WORLD_GEN_SETTINGS)
                    .optionalMethod(METHOD_WORLD_GEN_SETTINGS_CREATE, "a").real("java.util.Properties").end()
                    .optionalMethod(METHOD_WORLD_GEN_SETTINGS_CREATE2, "a").symbolic(CLASS_REGISTRY_ACCESS).real("java.util.Properties").end()
            .next()
                .ifDetect(c -> c.getNumberOfFields() == 7
                		&& c.searchForUtf8EqualTo("overworld")
                		&& c.searchForUtf8EqualTo("the_nether")
                		&& c.searchForUtf8EqualTo("the_end")
                		&& c.searchForUtf8EqualTo("generator"))
                .thenDeclareOptional(CLASS_DIMENSION_SETTINGS)
                	.requiredField(FIELD_DIMENSION_SETTINGS_GENERATOR, "g")
            .next()
                .ifDetect(c -> c.getRealClassName().contains("$")
                	&& c.getRealClassName().length() < 10
                    && c.isInterface()
                    && (c.getNumberOfMethods() == 1 || c.getNumberOfMethods() == 2)
                    && c.hasMethodWithRealArgsReturning("int", "int", "int", null)
                    && !c.hasMethodWithRealArgsReturning("int", "int", "int", "boolean")
                )
                .thenDeclareRequired(CLASS_NOISE_BIOME_PROVIDER)
                    .requiredMethod(METHOD_NOISE_BIOME_PROVIDER_GET_BIOME, "b").real("int").real("int").real("int").end()
            .next()
                .ifDetect(c -> !c.getRealClassName().contains("$")
                    && c.getRealSuperClassName().equals("java/lang/Enum")
                    && c.hasMethodWithRealArgsReturning("long", "int", "int", "int", null, null)
                    && !c.hasMethodWithRealArgsReturning("double", "double")
                )
                .thenDeclareRequired(CLASS_BIOME_ZOOMER)
                    .requiredMethod(METHOD_BIOME_ZOOMER_GET_BIOME, "a").real("long").real("int").real("int").real("int").symbolic(CLASS_NOISE_BIOME_PROVIDER).end()
            .next()
                .ifDetect(c ->
                    (c.getNumberOfConstructors() == 1 || c.getNumberOfConstructors() == 2)
                    && c.getNumberOfFields() > 0
                    && c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
                    && (c.searchForFloat(0.62222224F) || c.searchForUtf8EqualTo("Feature placement"))
                )
                .thenDeclareRequired(CLASS_BIOME)
            .next()
                .ifDetect(c ->
                    (c.searchForStringContaining("Server-Worker-")
                     || c.searchForStringContaining("Worker-"))
                    && c.searchForStringContaining("os.name")
                    && c.searchForLong(1000000L)
                )
                .thenDeclareOptional(CLASS_UTIL)
            .construct();
    }
    // @formatter:on
}
