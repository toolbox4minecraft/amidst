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
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class BetaMinecraftInterface implements MinecraftInterface {
    // Values in [0, 16] progressively increase the accuracy of ocean display (16 = real ocean generation)
    // Set to -1 to disable ocean generation entirely.
    public static final int OCEAN_PRECISION = 4;

    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._b1_7_3;
    private final RecognisedVersion recognisedVersion;
    private final SymbolicClass dimensionBaseClass;
    private final SymbolicClass worldClass;
    private final SymbolicClass overworldLevelSourceClass;
    private final BiomeMapping biomeMapping;
    private final Class<?> dimensionImplClass;
    private final Class<?> hackedNoiseClass;
    private final SymbolicClass perlinNoiseClass;

    private BetaMinecraftInterface(
            SymbolicClass biomeClass,
            SymbolicClass dimensionBaseClass,
            SymbolicClass worldClass,
            SymbolicClass overworldLevelSourceClass,
            SymbolicClass perlinOctaveNoiseClass,
            SymbolicClass perlinNoiseClass,
            RecognisedVersion recognisedVersion) {
        this.recognisedVersion = recognisedVersion;
        this.dimensionBaseClass = dimensionBaseClass;
        this.worldClass = worldClass;
        this.overworldLevelSourceClass = overworldLevelSourceClass;
        this.perlinNoiseClass = perlinNoiseClass;
        this.dimensionImplClass = makeInstantiableDimension(dimensionBaseClass);
        this.hackedNoiseClass = makeInterpolationNoiseClass(perlinOctaveNoiseClass);
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
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_WORLD),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_OVERWORLD_LEVEL_SOURCE),
                stringSymbolicClassMap.get(BetaSymbolicNames.CLASS_PERLIN_OCTAVE_NOISE),
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

    private Class<?> makeInstantiableDimension(SymbolicClass dimensionBaseClass) {
        // Since b1.6, the Dimension class is abstract, with OverworldDimension
        // being an empty class inheriting from it. Our ClassTranslator cannot
        // match "empty class that inherits from Overworld", so we just generate
        // an equivalent class at runtime.
        return new ByteBuddy()
                .subclass(dimensionBaseClass.getClazz())
                .make()
                .load(dimensionBaseClass.getClazz().getClassLoader())
                .getLoaded();
    }

    private SymbolicObject constructDimension() throws IllegalAccessException, InstantiationException {
        return new SymbolicObject(dimensionBaseClass, dimensionImplClass.newInstance());
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

    /** Provides both biomes and oceans, at a configurable precision. */
    private class OceanProvidingWorldAccessor implements MinecraftInterface.WorldAccessor {
        private final SymbolicObject biomeGenerator;
        private final SymbolicObject overworldLevelSource;
        private final boolean generateBlocks;
        // instance variable to avoid allocations
        private final byte[] blocks = new byte[32768];

        public OceanProvidingWorldAccessor(SymbolicObject biomeGenerator, SymbolicObject overworldLevelSource, int precision) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
            this.biomeGenerator = biomeGenerator;
            this.overworldLevelSource = overworldLevelSource;
            this.generateBlocks = precision >= 0;

            adjustNoise(overworldLevelSource, precision);
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
                if (generateBlocks) {
                    // Run ShapeChunk (using our own noise generators) to determine where water is
                    double[] temperatures = getTemperatures();
                    fillBlocks(chunkZ, chunkX, temperatures);
                    out = getBlocksAtY63(blocks);
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

        private void fillBlocks(int chunkZ, int chunkX, double[] temperatures) throws IllegalAccessException, InvocationTargetException {
            // This call has ***massive*** overhead. The overhead of Method.invoke is literally 50% of this calls execution time.
            // I tried throwing MethodHandle at it and even using ByteBuddy to generate code for invoking this, but both just made it worse.
            // If you are looking for ways to speed up beta oceans, fix this overhead!
            overworldLevelSource.callMethod(BetaSymbolicNames.METHOD_OVERWORLD_LEVEL_SOURCE_SHAPE_CHUNK, chunkX, chunkZ, blocks, null, temperatures);
        }

        private Object[] getBiomes(int chunkZ, int chunkX) throws IllegalAccessException, InvocationTargetException {
            return (Object[]) biomeGenerator.callMethod(BetaSymbolicNames.METHOD_BIOMEGENERATOR_GET_BIOME, null, chunkX * 16, chunkZ * 16, 16, 16);
        }

        private int[] determineBiomes(int[] out, Object[] biomes) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    int outIdx = x + z * 16;
                    if (out[outIdx] == 9) {
                        out[outIdx] = DefaultBiomes.ocean;
                    } else if (out[outIdx] == 79) {
                        out[outIdx] = DefaultBiomes.coldOcean;
                    } else {
                        out[outIdx] = biomeMapping.getBiomeInt(biomes[z + x * 16]);
                    }
                }
            }
            return out;
        }

        private int[] getBlocksAtY63(byte[] blocks) {
            return ChunkBasedGen.streamY63()
                    .map(i -> blocks[i])
                    .toArray();
        }

        @Override
        public Set<Dimension> supportedDimensions() {
            return Collections.singleton(Dimension.OVERWORLD);
        }
    }

    /** Handles mapping of Minecraft's Biome objects to out biome IDs. */
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

    /**
     * A custom implementation of perlin octave noise to speed up interpolation noises
     * <p>
     * At full precision, over 95% of the time in shapeChunk would be spent generating the 3D interpolation noise. Only
     * 2 of the 16 generated y levels are relevant for oceans, so {@link PerlinNoise} skips calculating the others.
     * Additionally, this class allows us to configure how many octaves to actually use. Each octave contributes only
     * half as much as its successor, so using only 8 or even 4 of the 16 octaves can provide a significant speedup while
     * remaining very accurate.
     * <p>
     * ByteBuddy is used to create a subclass of Minecraft's PerlinOctaveNoise which delegates to
     * {@link TrimmedPerlinOctaveNoise#sample}. Instances of that subclass are then placed into the overworldLevelSource
     * to replace its interpolation noise generators.
     */
    public static class TrimmedPerlinOctaveNoise {
        private final PerlinNoise[] octaves;
        private final int firstOctave;

        public TrimmedPerlinOctaveNoise(PerlinNoise[] octaves, int firstOctave) {
            this.octaves = octaves;
            this.firstOctave = firstOctave;
        }

        public static TrimmedPerlinOctaveNoise fromSymbolic(SymbolicObject perlinOctaveNoise, SymbolicClass perlinNoiseClass, int octaveCount) throws IllegalAccessException {
            Object[] octaveObjects = (Object[]) perlinOctaveNoise.getFieldValue(BetaSymbolicNames.FIELD_PERLIN_OCTAVE_NOISE_OCTAVES);
            PerlinNoise[] octaves = new PerlinNoise[octaveObjects.length];
            for (int i = 0; i < octaves.length; ++i) {
                octaves[i] = PerlinNoise.fromSymbolic(new SymbolicObject(perlinNoiseClass, octaveObjects[i]));
            }

            return new TrimmedPerlinOctaveNoise(octaves, octaves.length - octaveCount);
        }

        // Method gets injected by ByteBuddy
        @SuppressWarnings("unused")
        public double[] sample(double[] resultArr, double x, double y, double z,
                               int resX, int resY, int resZ, double scaleX, double scaleY,
                               double scaleZ) {
            if (resultArr == null) {
                resultArr = new double[resX * resY * resZ];
            } else {
                Arrays.fill(resultArr, 0.0);
            }

            double inverseIntensity = 1.0 / (1 << firstOctave);
            for (int i = firstOctave; i < octaves.length; ++i) {
                octaves[i].sample(resultArr,
                        x, y, z,
                        resX, resY, resZ,
                        scaleX * inverseIntensity, scaleY * inverseIntensity, scaleZ * inverseIntensity,
                        inverseIntensity);
                inverseIntensity /= 2;
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
                    // Range of Y values is hard-coded to avoid generating noise that is irrelevant to ocean gen.
                    for (int yIdx = 7; yIdx < 9; ++yIdx) {
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
                        // This is incorrect, as the lerps depend on yPos, not cubeY, so we need to figure which
                        // yPos minecraft would have used to generate the lerps for this index.
                        int lastY = findLastLerpIndex(y, yScale, yIdx, cubeY);
                        double[] lerps = calcBuggedLerps(y, yScale, xPos, zPos, cubeX, cubeZ, u, lastY);

                        array[xIdx * resZ * resY + zIdx * resY + yIdx] += lerp(w, lerp(v, lerps[0], lerps[1]), lerp(v, lerps[2], lerps[3])) * (1 / scale);
                    }
                }
            }
        }

        private int findLastLerpIndex(double y, double yScale, int yIdx, int cubeY) {
            int searchIdx = yIdx;
            while (true) {
                double searchPos = (y + searchIdx) * yScale + yOffset;
                int searchCube = (int) Math.floor(searchPos) & 255;
                if (searchIdx < 0 || searchCube != cubeY)
                    break;
                --searchIdx;
            }
            return searchIdx + 1;
        }

        private double[] calcBuggedLerps(double y, double yScale, double xPos, double zPos, int cubeX, int cubeZ, double u, int yIdx) {
            double yPos = (y + yIdx) * yScale + yOffset;
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

        private static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private static double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }

    public interface InterpolationNoiseSetter {
        void setNoise(TrimmedPerlinOctaveNoise hacked);
    }

    public static Class<?> makeInterpolationNoiseClass(SymbolicClass perlinOctaveNoiseClass) {
        return new ByteBuddy()
                .subclass(perlinOctaveNoiseClass.getClazz())
                .defineField("hacked", TrimmedPerlinOctaveNoise.class, Visibility.PUBLIC)
                .implement(InterpolationNoiseSetter.class).intercept(FieldAccessor.ofField("hacked"))
                .method(ElementMatchers.is(perlinOctaveNoiseClass.getMethod(BetaSymbolicNames.METHOD_PERLIN_OCTAVE_NOISE_SAMPLE_3D).getRawMethod()))
                .intercept(MethodDelegation.toField("hacked"))
                .make()
                .load(perlinOctaveNoiseClass.getClazz().getClassLoader())
                .getLoaded();
    }

    public Object makeInterpolationNoise(SymbolicObject octaveNoise, int octaveCount) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Create the object that will handle the actual noise generation
        TrimmedPerlinOctaveNoise noise = TrimmedPerlinOctaveNoise.fromSymbolic(octaveNoise, perlinNoiseClass, octaveCount);
        // Instantiate our delegating subclass
        InterpolationNoiseSetter interpolationNoise = (InterpolationNoiseSetter) hackedNoiseClass.getConstructor(Random.class, int.class).newInstance(null, 0);
        // Make the instance delegate to our noise object
        interpolationNoise.setNoise(noise);

        return interpolationNoise;
    }

    public void adjustNoise(SymbolicObject levelSource, int precision) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object levelSourceObj = levelSource.getObject();
        String[] symbolicFields = new String[]{
                BetaSymbolicNames.FIELD_LOWER_INTERPOLATION_NOISE,
                BetaSymbolicNames.FIELD_UPPER_INTERPOLATION_NOISE,
                BetaSymbolicNames.FIELD_INTERPOLATION_NOISE,
        };
        int[] precisionFactor = new int[] {1, 1, 2};

        for (int i = 0; i < symbolicFields.length; ++i) {
            Field field = levelSource.getType().getField(symbolicFields[i]).getRawField();
            int octaveCount = precision / precisionFactor[i];
            Object newOctaveNoise = makeInterpolationNoise((SymbolicObject) levelSource.getFieldValue(symbolicFields[i]), octaveCount);
            field.set(levelSourceObj, newOctaveNoise);
        }
    }
}
