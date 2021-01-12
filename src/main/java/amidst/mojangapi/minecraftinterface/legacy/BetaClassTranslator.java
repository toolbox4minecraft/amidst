package amidst.mojangapi.minecraftinterface.legacy;

import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

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
                        .requiredConstructor(BetaSymbolicNames.CONSTRUCTOR_BIOMEGENERATOR).symbolic(BetaSymbolicNames.CLASS_WORLD).end()
                        .requiredMethod(BetaSymbolicNames.METHOD_BIOMEGENERATOR_GET_BIOME, "a").real("int").real("int").end()
                .next()
                    .ifDetect(c -> c.searchForFloat(0.7529412f) && c.searchForFloat(0.84705883f))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_DIMENSION_BASE)
                        .requiredField(BetaSymbolicNames.FIELD_DIMENSION_WORLD, "a")
                .next()
                    .ifDetect(c -> {
                        if (RecognisedVersion.isOlder(version, RecognisedVersion._b1_6_6))
                            return c.searchForInt(8421536) || (c.searchForFloat(0.7529412f) && c.searchForFloat(0.84705883f));
                        else
                            return c.searchForInt(8421536);
                    })
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_DIMENSION_CONCRETE)
                .next()
                    .ifDetect(c -> {
                        int nFields = RecognisedVersion.isOlder(version, RecognisedVersion._b1_6_6) ? 5 : 6;
                        return c.getNumberOfFields() == 0 && c.getNumberOfConstructors() == 0 && c.getNumberOfMethods() == nFields && c.isInterface() && c.hasMethodWithRealArgsReturning("void");
                    })
                    .thenDeclareRequired(BetaSymbolicNames.INTERFACE_SOMETHING)
                .next()
                    .ifDetect(c -> c.searchForInt(1013904223))
                    .thenDeclareRequired(BetaSymbolicNames.CLASS_WORLD)
                        .optionalConstructor(BetaSymbolicNames.CONSTRUCTOR_WORLD).real("String").symbolic(BetaSymbolicNames.CLASS_DIMENSION_BASE).real("long").end()
                        .optionalConstructor(BetaSymbolicNames.CONSTRUCTOR_WORLD).symbolic(BetaSymbolicNames.INTERFACE_SOMETHING).real("String").symbolic(BetaSymbolicNames.CLASS_DIMENSION_BASE).real("long").end()
                .construct();
    }
    // @formatter:on
}
