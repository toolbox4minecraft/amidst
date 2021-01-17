package amidst.mojangapi.minecraftinterface.legacy;

import amidst.clazz.real.RealClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

import java.util.Objects;

public enum BetaClassTranslator {
    INSTANCE;

    // @formatter:off
    public static ClassTranslator get(RecognisedVersion version) {
        return ClassTranslator
                .builder()
                    .ifDetect(c -> c.searchForInt(5169201))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_BIOME)
                        .requiredMethod(BetaSymbolicNames.METHOD_BIOME_FROM_TUPLE, "a").real("float").real("float").end()
                .next()
                    .ifDetect(c -> c.searchForLong(9871L) && c.searchForLong(39811L) && c.searchForLong(543321L))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_BIOMEGENERATOR)
                        .requiredMethod(BetaSymbolicNames.METHOD_BIOMEGENERATOR_GET_BIOME, "a").symbolicArray(BetaSymbolicNames.CLASS_BIOME, 1).real("int").real("int").real("int").real("int").end()
                        .requiredField(BetaSymbolicNames.FIELD_BIOMEGENERATOR_TEMPERATURE, "a")
                        .requiredField(BetaSymbolicNames.FIELD_BIOMEGENERATOR_RAINFALL, "b")
                .next()
                    .ifDetect(BetaClassTranslator::isDimensionBase)
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_DIMENSION_BASE)
                        .requiredField(BetaSymbolicNames.FIELD_DIMENSION_WORLD, "a")
                        .requiredField(BetaSymbolicNames.FIELD_DIMENSION_BIOMEGENERATOR, "b")
                .next()
                    .ifDetect((c, mappedNames) -> RecognisedVersion.isOlder(version, RecognisedVersion._b1_6_6)
                            ? isDimensionBase(c)
                            : c.getNumberOfFields() == 0
                                && c.getNumberOfMethods() == 0
                                && mappedNames.get(BetaSymbolicNames.CLASS_DIMENSION_BASE).equals(c.getRealSuperClassName()))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_DIMENSION_OVERWORLD)
                .next()
                    .ifDetect(c -> {
                        int nMethods = RecognisedVersion.isOlder(version, RecognisedVersion._b1_6_6) ? 5 : 6;
                        return c.getNumberOfFields() == 0 && c.getNumberOfConstructors() == 0 && c.getNumberOfMethods() == nMethods && c.isInterface() && c.hasMethodWithRealArgsReturning("void");
                    })
                    .thenDeclareRequired(BetaSymbolicNames.INTERFACE_SOMETHING)
                .next()
                    .ifDetect(c -> c.searchForInt(1013904223))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_WORLD)
                        .optionalConstructor(BetaSymbolicNames.CONSTRUCTOR_WORLD).real("String").symbolic(BetaSymbolicNames.CLASS_DIMENSION_BASE).real("long").end()
                        .optionalConstructor(BetaSymbolicNames.CONSTRUCTOR_WORLD).symbolic(BetaSymbolicNames.INTERFACE_SOMETHING).real("String").symbolic(BetaSymbolicNames.CLASS_DIMENSION_BASE).real("long").end()
                .next()
                    .ifDetect(c -> c.searchForDouble(10.0)
                        && c.hasConstructorWithRealArgs("java/util/Random", "int")
                        && c.hasMethodWithRealArgsReturning("double", "double", "double")
                        && c.hasMethodWithRealArgsReturning(null, "double", "double", "double", "int", "int", "int", "double", "double", "double", null)
                        && c.getNumberOfConstructors() == 1
                        && c.getNumberOfMethods() == 3
                        && c.getNumberOfFields() == 2
                    )
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_PERLIN_OCTAVE_NOISE)
                        .requiredMethod(BetaSymbolicNames.METHOD_PERLIN_OCTAVE_NOISE_SAMPLE_3D, "a").realArray("double", 1).real("double").real("double").real("double").real("int").real("int").real("int").real("double").real("double").real("double").end()
                        .requiredMethod(BetaSymbolicNames.METHOD_PERLIN_OCTAVE_NOISE_SAMPLE_2D, "a").realArray("double", 1).real("int").real("int").real("int").real("int").real("double").real("double").real("double").end()
                        .requiredField(BetaSymbolicNames.FIELD_PERLIN_OCTAVE_NOISE_OCTAVES, "a")
                .next()
                    .ifDetect(c -> c.searchForDouble(109.0134))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_OVERWORLD_LEVEL_SOURCE)
                        .requiredConstructor(BetaSymbolicNames.CONSTRUCTOR_OVERWORLD_LEVEL_SOURCE).symbolic(BetaSymbolicNames.CLASS_WORLD).real("long").end()
                        .requiredMethod(BetaSymbolicNames.METHOD_OVERWORLD_LEVEL_SOURCE_SHAPE_CHUNK, "a").real("int").real("int").realArray("byte", 1).symbolicArray(BetaSymbolicNames.CLASS_BIOME, 1).realArray("double", 1).end()
                        .requiredField(BetaSymbolicNames.FIELD_UPPER_INTERPOLATION_NOISE, "k")
                        .requiredField(BetaSymbolicNames.FIELD_LOWER_INTERPOLATION_NOISE, "l")
                        .requiredField(BetaSymbolicNames.FIELD_INTERPOLATION_NOISE, "m")
                        .requiredField(BetaSymbolicNames.FIELD_BIOME_NOISE, "a")
                        .requiredField(BetaSymbolicNames.FIELD_DEPTH_NOISE, "b")
                .next()
                    .ifDetect(c -> c.searchForDouble(6.0) && c.searchForDouble(15.0)
                        && c.getNumberOfFields() == 4
                        && c.getNumberOfConstructors() == 2
                        && c.hasMethodWithRealArgsReturning("[double", "double", "double", "double", "int", "int", "int", "double", "double", "double", "double", null))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_PERLIN_NOISE)
                        .requiredField(BetaSymbolicNames.FIELD_PERLIN_NOISE_PERMUTATIONS, "d")
                        .requiredField(BetaSymbolicNames.FIELD_PERLIN_NOISE_X_OFFSET, "a")
                        .requiredField(BetaSymbolicNames.FIELD_PERLIN_NOISE_Y_OFFSET, "b")
                        .requiredField(BetaSymbolicNames.FIELD_PERLIN_NOISE_Z_OFFSET, "c")
                .construct();
    }

    private static boolean isDimensionBase(RealClass c) {
        return c.searchForFloat(0.7529412f) && c.searchForFloat(0.84705883f);
    }
    // @formatter:on
}
