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
import amidst.util.ChunkBasedGen;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BetaMinecraftInterface implements MinecraftInterface {
    // Values in [0, 16] progressively increase the accuracy of ocean display, set to -1 to
    // disable ocean generation entirely.
    // Vanilla = 16
    public static final int OCEAN_PRECISION = 4;

    // If set to false, only one layer of noise is generated at Y=64 and used directly, instead of
    // interpolating between two at Y=56 and Y=64. This will introduce inaccuracies, but also
    // half the amount of time spent on 3D noise.
    // Vanilla = true
    public static final boolean INTERPOLATE_NOISE_VERTICALLY = true;

    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._b1_7_3;
    private final RecognisedVersion recognisedVersion;
    private final SymbolicClass dimensionBaseClass;
    private final SymbolicClass worldClass;
    private final SymbolicClass overworldLevelSourceClass;
    private final BiomeMapping biomeMapping;
    private final SymbolicClass dimensionOverworldClass;
    private final SymbolicClass perlinNoiseClass;

    private BetaMinecraftInterface(
            SymbolicClass biomeClass,
            SymbolicClass dimensionBaseClass,
            SymbolicClass dimensionOverworldClass,
            SymbolicClass worldClass,
            SymbolicClass overworldLevelSourceClass,
            SymbolicClass perlinNoiseClass,
            RecognisedVersion recognisedVersion) {
        this.recognisedVersion = recognisedVersion;
        this.dimensionBaseClass = dimensionBaseClass;
        this.worldClass = worldClass;
        this.overworldLevelSourceClass = overworldLevelSourceClass;
        this.perlinNoiseClass = perlinNoiseClass;
        this.dimensionOverworldClass = dimensionOverworldClass;
        try {
            this.biomeMapping = new BiomeMapping(biomeClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("failed to get biomes", e);
        }

    }

    public BetaMinecraftInterface(Map<String, SymbolicClass> stringSymbolicClassMap, RecognisedVersion recognisedVersion) {
        this(
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_BIOME),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_DIMENSION_BASE),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_DIMENSION_OVERWORLD),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_WORLD),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_OVERWORLD_LEVEL_SOURCE),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_PERLIN_NOISE),
                recognisedVersion);
    }

    @Override
    public WorldAccessor createWorldAccessor(WorldOptions worldOptions) throws MinecraftInterfaceException {
        try {
            SymbolicObject overworld = constructDimension();
            SymbolicObject world = constructWorld(overworld, worldOptions);
            SymbolicObject biomeGenerator = getBiomeGenerator(overworld);
            SymbolicObject overworldLevelSource = constructOverworldLevelSource(world, worldOptions);
            return new OceanProvidingWorldAccessor(biomeGenerator, overworldLevelSource, OCEAN_PRECISION);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new MinecraftInterfaceException("Failed to create world accessor", e);
        }
    }

    @Override
    public RecognisedVersion getRecognisedVersion() {
        return recognisedVersion;
    }

    private SymbolicObject constructDimension() throws IllegalAccessException, InstantiationException {
        return new SymbolicObject(dimensionBaseClass, dimensionOverworldClass.getClazz().newInstance());
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

    private SymbolicObject getBiomeGenerator(SymbolicObject overworld) throws IllegalAccessException {
        return (SymbolicObject) overworld.getFieldValue(BetaSymbolicNames.FIELD_DIMENSION_BIOMEGENERATOR);
    }

    private SymbolicObject constructOverworldLevelSource(SymbolicObject world, WorldOptions worldOptions) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return overworldLevelSourceClass.callConstructor(BetaSymbolicNames.CONSTRUCTOR_OVERWORLD_LEVEL_SOURCE, world.getObject(), worldOptions.getWorldSeed().getLong());
    }

    private OceanOracle makeOceanOracle(SymbolicObject levelSource, int precision) throws IllegalAccessException {
        return new OceanOracle(
                (SymbolicObject) levelSource.getFieldValue(BetaSymbolicNames.FIELD_BIOME_NOISE),
                (SymbolicObject) levelSource.getFieldValue(BetaSymbolicNames.FIELD_DEPTH_NOISE),
                makeOctaveNoise((SymbolicObject) levelSource.getFieldValue(BetaSymbolicNames.FIELD_INTERPOLATION_NOISE), precision / 2),
                makeOctaveNoise((SymbolicObject) levelSource.getFieldValue(BetaSymbolicNames.FIELD_UPPER_INTERPOLATION_NOISE), precision),
                makeOctaveNoise((SymbolicObject) levelSource.getFieldValue(BetaSymbolicNames.FIELD_LOWER_INTERPOLATION_NOISE), precision)
        );

    }

    private PerlinOctaveNoise makeOctaveNoise(SymbolicObject fieldValue, int precision) throws IllegalAccessException {
        return PerlinOctaveNoise.fromSymbolic(fieldValue, perlinNoiseClass, precision);
    }

    /** Provides both biomes and oceans, at a configurable precision. */
    private class OceanProvidingWorldAccessor implements MinecraftInterface.WorldAccessor {
        private final SymbolicObject biomeGenerator;
        private final OceanOracle oceanOracle;
        private final boolean generateOceans;

        public OceanProvidingWorldAccessor(SymbolicObject biomeGenerator, SymbolicObject overworldLevelSource, int precision) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
            this.biomeGenerator = biomeGenerator;
            this.oceanOracle = makeOceanOracle(overworldLevelSource, precision);
            this.generateOceans = precision >= 0;
        }

        @Override
        public <T> T getBiomeData(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution, Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
            if (dimension != Dimension.OVERWORLD)
                throw new UnsupportedDimensionException(dimension);
            // The "shapeChunk" method in OverworldLevelSource assumes that its BiomeGenerator was just called for a 16 * 16 area.
            // Thus, we need to generate biomes chunk-based.
            int[] result = ChunkBasedGen.mapChunkBased(x, y, width, height, useQuarterResolution, this::biomesInChunk);
            return biomeDataMapper.apply(result);
        }

        private int[] biomesInChunk(int chunkZ, int chunkX) throws MinecraftInterfaceException {
            try {
                // Generate real biomes using the BiomeGenerator
                Object[] biomes = getBiomes(chunkZ, chunkX);
                int[] out;
                if (generateOceans) {
                    double[] temperatures = getTemperatures();
                    double[] rainfall = getRainfall();
                    out = oceanOracle.determineOceans(chunkX, chunkZ, null, temperatures, rainfall);
                } else {
                    out = new int[256];
                }
                // Combine that info
                return determineBiomes(out, biomes);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MinecraftInterfaceException("Failed to generate chunk!", e);
            }
        }

        private double[] getTemperatures() throws IllegalAccessException {
            return (double[]) biomeGenerator.getFieldValue(BetaSymbolicNames.FIELD_BIOMEGENERATOR_TEMPERATURE);
        }

        private double[] getRainfall() throws IllegalAccessException {
            return (double[]) biomeGenerator.getFieldValue(BetaSymbolicNames.FIELD_BIOMEGENERATOR_RAINFALL);
        }

        private Object[] getBiomes(int chunkZ, int chunkX) throws IllegalAccessException, InvocationTargetException {
            return (Object[]) biomeGenerator.callMethod(BetaSymbolicNames.METHOD_BIOMEGENERATOR_GET_BIOME, null, chunkX * 16, chunkZ * 16, 16, 16);
        }

        private int[] determineBiomes(int[] out, Object[] biomes) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    int outIdx = x + z * 16;
                    if (out[outIdx] == OceanOracle.OCEAN) {
                        out[outIdx] = DefaultBiomes.ocean;
                    } else if (out[outIdx] == OceanOracle.FROZEN_OCEAN) {
                        out[outIdx] = DefaultBiomes.coldOcean;
                    } else {
                        out[outIdx] = biomeMapping.getBiomeInt(biomes[z + x * 16]);
                    }
                }
            }
            return out;
        }

        @Override
        public Set<Dimension> supportedDimensions() {
            return Collections.singleton(Dimension.OVERWORLD);
        }
    }

    /** Handles mapping of Minecraft's Biome objects to out biome IDs. */
    private static class BiomeMapping {
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
            // Matching the static fields containing each biome would be too much work.
            // Instead, just get the biomes by giving known (temperature, rainfall) inputs.
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
            // The game uses biomes like an enum, so we can simply check for identity rather than equality.
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

    private static class OceanOracle {
        // Outputs of shapeChunk
        public static final int OCEAN = -1;
        public static final int FROZEN_OCEAN = -2;
        public static final int LAND = -3;

        // Arbitrary constants to make us generate the same noise
        private static final int NOISE_POSITION_FACTOR = 4;
        private static final double BIOME_NOISE_SCALE = 1.121;
        private static final double DEPTH_NOISE_SCALE = 200.0;
        private static final double MAIN_INTERPOLATION_SCALE_XZ = 8.555150000000001;
        private static final double MAIN_INTERPOLATION_SCALE_Y = 4.277575000000001;
        private static final double OTHER_INTERPOLATION_SCALE = 684.412;

        // Offsets to deal with the fact that vanilla generates noise for all Y values
        private static final double VANILLA_NOISE_HEIGHT = 17;
        private static final double NOISE_HEIGHT_OFFSET = INTERPOLATE_NOISE_VERTICALLY ? 7 : 8;

        // Dimensions of the noise arrays
        private static final int NOISE_WIDTH = 5;
        private static final int NOISE_HEIGHT = INTERPOLATE_NOISE_VERTICALLY ? 2 : 1;
        private static final int NOISE_DEPTH = 5;

        // Various noise sources
        private final SymbolicObject biomeNoise;
        private final SymbolicObject depthNoise;
        private final PerlinOctaveNoise interpolationNoise;
        private final PerlinOctaveNoise upperInterpolationNoise;
        private final PerlinOctaveNoise lowerInterpolationNoise;

        // Arrays that are stored to avoid re-allocating them constantly.
        private double[] biomeNoises = null;
        private double[] depthNoises = null;
        private double[] interpolationNoises = null;
        private double[] upperInterpolationNoises = null;
        private double[] lowerInterpolationNoises = null;
        private double[] noises = null;

        public OceanOracle(SymbolicObject biomeNoise, SymbolicObject depthNoise, PerlinOctaveNoise interpolationNoise, PerlinOctaveNoise upperInterpolationNoise, PerlinOctaveNoise lowerInterpolationNoise) {
            this.biomeNoise = biomeNoise;
            this.depthNoise = depthNoise;
            this.interpolationNoise = interpolationNoise;
            this.upperInterpolationNoise = upperInterpolationNoise;
            this.lowerInterpolationNoise = lowerInterpolationNoise;
        }

        public int[] determineOceans(int chunkX, int chunkZ, int[] oceansIn, double[] temperatureNoises, double[] rainfallNoises) throws InvocationTargetException, IllegalAccessException {
            int[] oceans = (oceansIn != null && oceansIn.length >= 16 * 16) ? oceansIn : new int[16 * 16];

            this.noises = this.calculateNoise(this.noises, chunkX, chunkZ, NOISE_WIDTH, NOISE_HEIGHT, NOISE_DEPTH, temperatureNoises, rainfallNoises);
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    double noiseAtPoint = NOISE_HEIGHT > 1
                            ? interpolateNoise3d(x, 63 % 8, z, noises, NOISE_HEIGHT, NOISE_DEPTH)
                            : interpolateNoise2d(x, z, noises, NOISE_DEPTH);
                    boolean isOcean = noiseAtPoint <= 0;
                    boolean isFrozen = temperatureNoises[x * 16 + z] < 0.5;
                    oceans[z * 16 + x] = isOcean ? (isFrozen ? FROZEN_OCEAN : OCEAN) : LAND;
                }
            }

            return oceans;
        }

        private double[] calculateNoise(double[] noisesIn, int chunkX, int chunkZ, int noiseWidth, int noiseHeight, int noiseDepth, double[] temperatureNoises, double[] rainfallNoises) throws InvocationTargetException, IllegalAccessException {
            int noisesLength = noiseWidth * noiseHeight * noiseDepth;
            double[] noises = (noisesIn != null && noisesIn.length >= noisesLength) ? noisesIn : new double[noisesLength];

            int sampleX = chunkX * NOISE_POSITION_FACTOR;
            int sampleZ = chunkZ * NOISE_POSITION_FACTOR;
            biomeNoises = (double[]) biomeNoise.callMethod(BetaSymbolicNames.METHOD_PERLIN_OCTAVE_NOISE_SAMPLE_2D, biomeNoises, sampleX, sampleZ, noiseWidth, noiseDepth, BIOME_NOISE_SCALE, BIOME_NOISE_SCALE, 0);
            depthNoises = (double[]) depthNoise.callMethod(BetaSymbolicNames.METHOD_PERLIN_OCTAVE_NOISE_SAMPLE_2D, depthNoises, sampleX, sampleZ, noiseWidth, noiseDepth, DEPTH_NOISE_SCALE, DEPTH_NOISE_SCALE, 0);
            interpolationNoises = interpolationNoise.sample3d(interpolationNoises, sampleX, NOISE_HEIGHT_OFFSET, sampleZ, noiseWidth, noiseHeight, noiseDepth, MAIN_INTERPOLATION_SCALE_XZ, MAIN_INTERPOLATION_SCALE_Y, MAIN_INTERPOLATION_SCALE_XZ);
            upperInterpolationNoises = upperInterpolationNoise.sample3d(upperInterpolationNoises, sampleX, NOISE_HEIGHT_OFFSET, sampleZ, noiseWidth, noiseHeight, noiseDepth, OTHER_INTERPOLATION_SCALE, OTHER_INTERPOLATION_SCALE, OTHER_INTERPOLATION_SCALE);
            lowerInterpolationNoises = lowerInterpolationNoise.sample3d(lowerInterpolationNoises, sampleX, NOISE_HEIGHT_OFFSET, sampleZ, noiseWidth, noiseHeight, noiseDepth, OTHER_INTERPOLATION_SCALE, OTHER_INTERPOLATION_SCALE, OTHER_INTERPOLATION_SCALE);

            for(int xNoiseIdx = 0; xNoiseIdx < noiseWidth; ++xNoiseIdx) {
                for(int yNoiseIdx = 0; yNoiseIdx < noiseHeight; ++yNoiseIdx) {
                    for(int zNoiseIdx = 0; zNoiseIdx < noiseDepth; ++zNoiseIdx) {
                        int blockX = (int) Math.floor(16.0 / noiseWidth * (xNoiseIdx + 0.5));
                        int blockZ = (int) Math.floor(16.0 / noiseDepth * (zNoiseIdx + 0.5));
                        int climateIdx = blockX * 16 + blockZ;
                        double temperature = temperatureNoises[climateIdx];
                        double rainfall = rainfallNoises[climateIdx];

                        int noise2dIdx = xNoiseIdx * noiseDepth + zNoiseIdx;
                        double biome = biomeNoises[noise2dIdx];
                        double depth = depthNoises[noise2dIdx];

                        int noise3dIdx = (xNoiseIdx * noiseDepth + zNoiseIdx) * noiseHeight + yNoiseIdx;
                        double upperInter = upperInterpolationNoises[noise3dIdx];
                        double lowerInter = lowerInterpolationNoises[noise3dIdx];
                        double inter = interpolationNoises[noise3dIdx];

                        double mergedNoise = mergeNoises(yNoiseIdx,
                                biome, depth, upperInter, lowerInter, inter, temperature, rainfall);

                        noises[noise3dIdx] = mergedNoise;
                    }
                }
            }

            return noises;
        }

        /** Combines the noise values in just the right way. */
        private static double mergeNoises(double yNoiseIdx, double biomeNoiseV, double depthNoiseV, double upperInterpolationNoiseV, double lowerInterpolationNoiseV, double interpolationNoiseV, double temperatureV, double rainfallV) {
            // This took a lot of trial and error to get right...
            double scaledRainfall = 1.0 - rainfallV * temperatureV;
            double biomeNoiseValue = biomeNoiseV / 512.0 + 0.5;
            double biomeFactor = Math.max(0, Math.min(1, biomeNoiseValue * (1 - Math.pow(scaledRainfall, 4))));

            double upperInter = upperInterpolationNoiseV / 512.0;
            double lowerInter = lowerInterpolationNoiseV / 512.0;
            double mainInter = interpolationNoiseV / 20.0 + 0.5;
            double clampedInterpolated = PerlinNoise.lerp(Math.max(0, Math.min(1, mainInter)), upperInter, lowerInter);

            // Why are there so many conditionals for depth noise???
            double depth1 = depthNoiseV / 8000.0;
            double depth2 = depth1 * (depth1 < 0 ? -0.8999999999999999 : 3.0) - 2.0;
            double depth3 = depth2 < 0 ? Math.max(-2, depth2) / 5.6 : Math.min(1, depth2) / 8.0;
            double depthAdjustedBiomeFactor = depth2 < 0 ? 0.5 : biomeFactor + 0.5;
            double depth4 = (NOISE_HEIGHT_OFFSET + yNoiseIdx - VANILLA_NOISE_HEIGHT * (0.5 + depth3 * 0.25)) * 12.0 / depthAdjustedBiomeFactor;
            double depth5 = depth4 < 0 ? depth4 * 4.0 : depth4;

            return clampedInterpolated - depth5;
        }

        @SuppressWarnings("DuplicateExpressions")
        private double interpolateNoise3d(int blockX, int blockY, int blockZ, double[] noises, int noiseHeight, int noiseDepth) {
            int idxX = blockX / 4;
            int idxY = blockY / 8;
            int idxZ = blockZ / 4;

            // Get the noise values surrounding this coordinate
            // variable names are noise[XYZ corner]
            // @formatter:off
            double noise000 = noises[((idxX    ) * noiseDepth + idxZ    ) * noiseHeight + idxY    ];
            double noise001 = noises[((idxX    ) * noiseDepth + idxZ + 1) * noiseHeight + idxY    ];
            double noise010 = noises[((idxX    ) * noiseDepth + idxZ    ) * noiseHeight + idxY + 1];
            double noise011 = noises[((idxX    ) * noiseDepth + idxZ + 1) * noiseHeight + idxY + 1];
            double noise100 = noises[((idxX + 1) * noiseDepth + idxZ    ) * noiseHeight + idxY    ];
            double noise101 = noises[((idxX + 1) * noiseDepth + idxZ + 1) * noiseHeight + idxY    ];
            double noise110 = noises[((idxX + 1) * noiseDepth + idxZ    ) * noiseHeight + idxY + 1];
            double noise111 = noises[((idxX + 1) * noiseDepth + idxZ + 1) * noiseHeight + idxY + 1];
            // @formatter:on

            double relX = blockX % 4;
            double relY = blockY % 8;
            double relZ = blockZ % 4;

            // interpolate X
            double noiseX00 = PerlinNoise.lerp(relX / 4, noise000, noise100);
            double noiseX01 = PerlinNoise.lerp(relX / 4, noise001, noise101);
            double noiseX10 = PerlinNoise.lerp(relX / 4, noise010, noise110);
            double noiseX11 = PerlinNoise.lerp(relX / 4, noise011, noise111);

            // interpolate Y
            double noiseXY0 = PerlinNoise.lerp(relY / 8, noiseX00, noiseX10);
            double noiseXY1 = PerlinNoise.lerp(relY / 8, noiseX01, noiseX11);

            // interpolate Z
            return PerlinNoise.lerp(relZ / 4, noiseXY0, noiseXY1);
        }

        private double interpolateNoise2d(int blockX, int blockZ, double[] noises, int noiseDepth) {
            int idxX = blockX / 4;
            int idxZ = blockZ / 4;

            double noise00 = noises[(idxX    ) * noiseDepth + idxZ    ];
            double noise01 = noises[(idxX    ) * noiseDepth + idxZ + 1];
            double noise10 = noises[(idxX + 1) * noiseDepth + idxZ    ];
            double noise11 = noises[(idxX + 1) * noiseDepth + idxZ + 1];

            double relX = blockX % 4;
            double relZ = blockZ % 4;

            // interpolate X
            double noiseX0 = PerlinNoise.lerp(relX / 4, noise00, noise10);
            double noiseX1 = PerlinNoise.lerp(relX / 4, noise01, noise11);

            // interpolate Z
            return PerlinNoise.lerp(relZ / 4, noiseX0, noiseX1);
        }
    }

    /**
     * A custom implementation of perlin octave noise to speed up interpolation noises
     * <p>
     * At full precision, over 95% of the time in shapeChunk would be spent generating the 3D interpolation noise. Only
     * 2 of the 16 generated y levels are relevant for oceans, so {@link PerlinNoise} is implemented to allow us to avoid
     * calculating the others. Additionally, this class allows us to configure how many octaves to actually use. Each
     * octave contributes only half as much as its successor, so using only 8 or even 4 of the 16 octaves can provide
     * a significant speedup while remaining very accurate.
     * <p>
     * We sadly are not able to just re-use Minecraft's perlin noise as that implementation is bugged so that
     * selecting a different starting Y value alters the results. Minecraft's perlin octave noise only allows us to
     * skip the n most significant octaves, which is counterproductive, so we had to re-implement that as well.
     */
    private static class PerlinOctaveNoise {
        private final PerlinNoise[] octaves;
        private final int firstOctave;

        public PerlinOctaveNoise(PerlinNoise[] octaves, int firstOctave) {
            this.octaves = octaves;
            this.firstOctave = firstOctave;
        }

        /** Construct an instance by stealing the random state from Minecraft's implementation */
        public static PerlinOctaveNoise fromSymbolic(SymbolicObject perlinOctaveNoise, SymbolicClass perlinNoiseClass, int octaveCount) throws IllegalAccessException {
            Object[] octaveObjects = (Object[]) perlinOctaveNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_OCTAVE_NOISE_OCTAVES);
            PerlinNoise[] octaves = new PerlinNoise[octaveObjects.length];
            for (int i = 0; i < octaves.length; ++i) {
                octaves[i] = PerlinNoise.fromSymbolic(new SymbolicObject(perlinNoiseClass, octaveObjects[i]));
            }

            return new PerlinOctaveNoise(octaves, octaves.length - octaveCount);
        }

        public double[] sample3d(double[] resultArr, double x, double y, double z,
                                 int resX, int resY, int resZ, double scaleX, double scaleY,
                                 double scaleZ) {
            if (resultArr == null) {
                resultArr = new double[resX * resY * resZ];
            } else {
                Arrays.fill(resultArr, 0.0);
            }

            for (int i = firstOctave; i < octaves.length; ++i) {
                double inverseIntensity = 1.0 / (1 << i);
                octaves[i].sample(resultArr,
                        x, y, z,
                        resX, resY, resZ,
                        scaleX * inverseIntensity, scaleY * inverseIntensity, scaleZ * inverseIntensity,
                        inverseIntensity);
            }
            return resultArr;
        }
    }

    private static class PerlinNoise {
        private final int[] permutations;
        private final double xOffset;
        private final double yOffset;
        private final double zOffset;

        public PerlinNoise(int[] permutations, double xOffset, double yOffset, double zOffset) {
            this.permutations = permutations;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }

        public static PerlinNoise fromSymbolic(SymbolicObject perlinNoise) throws IllegalAccessException {
            return new PerlinNoise(
                    (int[]) perlinNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_NOISE_PERMUTATIONS),
                    (double) perlinNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_NOISE_X_OFFSET),
                    (double) perlinNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_NOISE_Y_OFFSET),
                    (double) perlinNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_NOISE_Z_OFFSET)
            );
        }

        public void sample(double[] array, double x, double y, double z, int resX, int resY, int resZ, double xScale, double yScale, double zScale, double scale) {
            for (int xIdx = 0; xIdx < resX; ++xIdx) {
                for (int zIdx = 0; zIdx < resZ; ++zIdx) {
                    for (int yIdx = 0; yIdx < resY; ++yIdx) {
                        double xPos = (x + xIdx) * xScale + xOffset;
                        double yPos = (y + yIdx) * yScale + yOffset;
                        double zPos = (z + zIdx) * zScale + zOffset;

                        // The below is basically just perlinNoise(xPos, yPos, zPos), but some adjustments for Mojank.
                        int cubeX = (int) Math.floor(xPos) & 255;
                        int cubeY = (int) Math.floor(yPos) & 255;
                        int cubeZ = (int) Math.floor(zPos) & 255;
                        xPos -= Math.floor(xPos);
                        yPos -= Math.floor(yPos);
                        zPos -= Math.floor(zPos);
                        double u = fade(xPos);
                        double v = fade(yPos);
                        double w = fade(zPos);

                        // Minecraft re-uses the lerps from the previous y value if cubeY didn't change.
                        // This is incorrect, as the lerps depend on yPos, not cubeY, so we need to figure out
                        // which yPos minecraft would have used to generate the lerps for this index.
                        int lastY = findLastLerpIndex(yScale, (int) Math.round(y + yIdx), cubeY);
                        double[] lerps = calcBuggedLerps(yScale, xPos, zPos, cubeX, cubeZ, u, lastY);

                        array[xIdx * resZ * resY + zIdx * resY + yIdx] += lerp(w, lerp(v, lerps[0], lerps[1]), lerp(v, lerps[2], lerps[3])) * (1 / scale);
                    }
                }
            }
        }

        private int findLastLerpIndex(double yScale, int yIdx, int cubeY) {
            int searchIdx = yIdx;
            while (true) {
                double searchPos = searchIdx * yScale + yOffset;
                int searchCube = (int) Math.floor(searchPos) & 255;
                if (searchIdx < 0 || searchCube != cubeY)
                    break;
                --searchIdx;
            }
            return searchIdx + 1;
        }

        private double[] calcBuggedLerps(double yScale, double xPos, double zPos, int cubeX, int cubeZ, double u, int yIdx) {
            double yPos = yIdx * yScale + yOffset;
            int cubeY = (int) Math.floor(yPos) & 255;
            yPos -= Math.floor(yPos);
            int A = permutations[cubeX] + cubeY;
            int AA = permutations[A] + cubeZ;
            int AB = permutations[A + 1] + cubeZ;
            int B = permutations[cubeX + 1] + cubeY;
            int BA = permutations[B] + cubeZ;
            int BB = permutations[B + 1] + cubeZ;

            double[] lerps = new double[4];
            lerps[0] = lerp(u, grad(permutations[AA], xPos, yPos, zPos),
                    grad(permutations[BA], xPos - 1, yPos, zPos));
            lerps[1] = lerp(u, grad(permutations[AB], xPos, yPos - 1, zPos),
                    grad(permutations[BB], xPos - 1, yPos - 1, zPos));
            lerps[2] = lerp(u, grad(permutations[AA + 1], xPos, yPos, zPos - 1),
                    grad(permutations[BA + 1], xPos - 1, yPos, zPos - 1));
            lerps[3] = lerp(u, grad(permutations[AB + 1], xPos, yPos - 1, zPos - 1),
                    grad(permutations[BB + 1], xPos - 1, yPos - 1, zPos - 1));
            return lerps;
        }

        public static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        public static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        public static double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}
