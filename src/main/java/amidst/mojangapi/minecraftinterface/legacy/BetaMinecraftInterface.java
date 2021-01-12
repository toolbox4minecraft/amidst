package amidst.mojangapi.minecraftinterface.legacy;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.UnsupportedDimensionException;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.versionfeatures.DefaultBiomes;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BetaMinecraftInterface implements MinecraftInterface {
    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._b1_7_3;
    private final RecognisedVersion recognisedVersion;
    private final SymbolicClass dimensionBaseClass;
    private final SymbolicClass dimensionImplClass;
    private final SymbolicClass worldClass;
    private final SymbolicClass biomeGeneratorClass;
    private final BiomeMapping biomeMapping;

    private BetaMinecraftInterface(SymbolicClass biomeClass, SymbolicClass dimensionBaseClass, SymbolicClass dimensionImplClass, SymbolicClass worldClass, SymbolicClass biomeGeneratorClass, RecognisedVersion recognisedVersion) {
        this.recognisedVersion = recognisedVersion;
        this.dimensionBaseClass = dimensionBaseClass;
        this.dimensionImplClass = dimensionImplClass;
        this.worldClass = worldClass;
        this.biomeGeneratorClass = biomeGeneratorClass;
        try {
            this.biomeMapping = new BiomeMapping(biomeClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("failed to get biomes", e);
        }

    }

    private SymbolicObject constructDimension() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return new SymbolicObject(dimensionImplClass, dimensionImplClass.getClazz().getConstructor().newInstance());
    }

    private SymbolicObject constructWorld(SymbolicObject overworldDimension, WorldOptions worldOptions) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        long seed = worldOptions.getWorldSeed().getLong();
        Object[] params;
        if (RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._b1_3b)) {
            params = new Object[] {"level name", overworldDimension.getObject(), seed};
        } else {
            params = new Object[] {null, "level name", overworldDimension.getObject(), seed};
        }

        try {
            worldClass.callConstructor(BetaSymbolicNames.CONSTRUCTOR_WORLD, params);
        } catch (NullPointerException | InvocationTargetException ex) {
            // Bwahaha, too late! Before the World constructor raised this NullPointerException
            // (from trying to use a chunkLoader returned by NullSaveHandler instance, or by trying to get
            // a chunkLoader from a null SaveHandler, depending on what you passed as the first parameter
            // of the World constructor) it already assigned
            // its own seed field and called dimension.setWorld(this), so we have a reference to
            // a functional-enough World instance despite the constructor failing!
            // Plus, the Dimension instance has already used the partially constructed World instance
            // to create the BiomeGenerator, which is the whole goal of this, so World's work here is done.

            if (ex instanceof InvocationTargetException) {
                // If we pass a null for the SaveHandler parameter (which saves us having to
                // identify the obfuscated ISaveHandle interface and NullSaveHandler class) then
                // for some reason the NullPointerException bubbles up inside a InvocationTargetException,
                // so here I confirm that the exception is still a NullPointerException.
                if (!(((InvocationTargetException) ex).getTargetException() instanceof NullPointerException)) {
                    throw (InvocationTargetException) ex;
                }
            }
        }

        return (SymbolicObject) dimensionBaseClass.getFieldValue(BetaSymbolicNames.FIELD_DIMENSION_WORLD, overworldDimension);
    }

    private SymbolicObject constructBiomeGenerator(SymbolicObject world) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        // If the world was constructed with the correct dimension then we can read the biome
        // generator straight from that, but as of 1.6 we can't match the Overworld dimension so
        // I'm instead matching the End dimension, which would give us the wrong biome generator, so
        // this method constructs the correct biome generator separately (until I can find a way to
        // get the right dimension).
        return biomeGeneratorClass.callConstructor(BetaSymbolicNames.CONSTRUCTOR_BIOMEGENERATOR, world.getObject());
    }

    public BetaMinecraftInterface(Map<String, SymbolicClass> stringSymbolicClassMap, RecognisedVersion recognisedVersion) {
        this(
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_BIOME),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_DIMENSION_BASE),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_DIMENSION_CONCRETE),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_WORLD),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_BIOMEGENERATOR),
                recognisedVersion);
    }

    @Override
    public WorldAccessor createWorldAccessor(WorldOptions worldOptions) throws MinecraftInterfaceException {
        try {
            SymbolicObject overworld = constructDimension();
            SymbolicObject world = constructWorld(overworld, worldOptions);
            SymbolicObject biomeGenerator = constructBiomeGenerator(world);
            return new WorldAccessor(biomeGenerator);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new MinecraftInterfaceException("Failed to create world accessor", e);
        }
    }

    @Override
    public RecognisedVersion getRecognisedVersion() {
        return recognisedVersion;
    }

    private class WorldAccessor implements MinecraftInterface.WorldAccessor {
        private final SymbolicObject biomeGenerator;

        public WorldAccessor(SymbolicObject biomeGenerator) {
            this.biomeGenerator = biomeGenerator;
        }

        @Override
        public <T> T getBiomeData(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution, Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
            if (dimension != Dimension.OVERWORLD)
                throw new UnsupportedDimensionException(dimension);
            int[] result = new int[width * height];
            int shift = useQuarterResolution ? 2 : 0;
            try {
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        result[j * width + i] = biomeMapping.getBiomeInt(getBiomeAt((x + i) << shift, (y + j) << shift));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new MinecraftInterfaceException("unable to get biome data", e);
            }
            return biomeDataMapper.apply(result);
        }

        private Object getBiomeAt(int x, int y) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return ((SymbolicObject) biomeGenerator.callMethod(BetaSymbolicNames.METHOD_BIOMEGENERATOR_GET_BIOME, x, y)).getObject();
        }

        @Override
        public Set<Dimension> supportedDimensions() {
            return Collections.singleton(Dimension.OVERWORLD);
        }
    }

    private static final class BiomeMapping {
        private final Object tundra;
        private final Object taiga;
        private final Object savanna;
        private final Object shrubland;
        private final Object swampland;
        private final Object forest;
        private final Object desert;
        private final Object plains;
        private final Object seasonalForest;
        private final Object rainforest;

        public BiomeMapping(SymbolicClass biomeClass) throws InvocationTargetException, IllegalAccessException {
            this.tundra = getBiomeFrom(biomeClass, 0.0, 0.0);
            this.taiga = getBiomeFrom(biomeClass, 0.4, 1.0);
            this.savanna = getBiomeFrom(biomeClass, 0.6, 0.0);
            this.shrubland = getBiomeFrom(biomeClass, 0.6, 0.5);
            this.swampland = getBiomeFrom(biomeClass, 0.6, 1.0);
            this.forest = getBiomeFrom(biomeClass, 0.7, 1.0);
            this.desert = getBiomeFrom(biomeClass, 1.0, 0.0);
            this.plains = getBiomeFrom(biomeClass, 1.0, 0.3);
            this.seasonalForest = getBiomeFrom(biomeClass, 1.0, 0.6);
            this.rainforest = getBiomeFrom(biomeClass, 1.0, 1.0);

        }

        private static Object getBiomeFrom(SymbolicClass biomeClass, double temperature, double rainfall) throws InvocationTargetException, IllegalAccessException {
            return ((SymbolicObject) biomeClass.callStaticMethod(BetaSymbolicNames.METHOD_BIOME_FROM_TUPLE, (float) temperature, (float) rainfall)).getObject();
        }

        public int getBiomeInt(Object biome) {
            if (biome == tundra)
                return DefaultBiomes.icePlains;
            if (biome == taiga)
                return DefaultBiomes.taiga;
            if (biome == savanna)
                return DefaultBiomes.savanna;
            if (biome == shrubland)
                return DefaultBiomes.savannaM;
            if (biome == swampland)
                return DefaultBiomes.swampland;
            if (biome == forest)
                return DefaultBiomes.forest;
            if (biome == desert)
                return DefaultBiomes.desert;
            if (biome == plains)
                return DefaultBiomes.plains;
            if (biome == seasonalForest)
                return DefaultBiomes.forestHills;
            if (biome == rainforest)
                return DefaultBiomes.jungle;

            return DefaultBiomes.theEnd;
        }

    }
}
