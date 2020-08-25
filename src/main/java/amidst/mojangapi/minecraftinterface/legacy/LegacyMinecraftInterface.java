package amidst.mojangapi.minecraftinterface.legacy;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.UnsupportedDimensionException;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
/**
 * This is the MinecraftInterface used for versions older than 18w06a, before the 1.13 refactoring
 */
public class LegacyMinecraftInterface implements MinecraftInterface {
    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._18w05a;

	private final SymbolicClass intCacheClass;
	private final SymbolicClass blockInitClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass genOptionsFactoryClass;
	private final RecognisedVersion recognisedVersion;

	private boolean isIntCacheInUse = false;

	LegacyMinecraftInterface(
			SymbolicClass intCacheClass,
			SymbolicClass blockInitClass,
			SymbolicClass genLayerClass,
			SymbolicClass worldTypeClass,
			SymbolicClass genOptionsFactoryClass,
			RecognisedVersion recognisedVersion) {
		this.intCacheClass = intCacheClass;
		this.blockInitClass = blockInitClass;
		this.genLayerClass = genLayerClass;
		this.worldTypeClass = worldTypeClass;
		this.genOptionsFactoryClass = genOptionsFactoryClass;
		this.recognisedVersion = recognisedVersion;
	}

	public LegacyMinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this(
			symbolicClassMap.get(LegacySymbolicNames.CLASS_INT_CACHE),
			symbolicClassMap.get(LegacySymbolicNames.CLASS_BLOCK_INIT),
			symbolicClassMap.get(LegacySymbolicNames.CLASS_GEN_LAYER),
			symbolicClassMap.get(LegacySymbolicNames.CLASS_WORLD_TYPE),
			symbolicClassMap.get(LegacySymbolicNames.CLASS_GEN_OPTIONS_FACTORY),
			recognisedVersion);
	}

	// Only one thread can manipulate the Minecraft int cache at a time
	private synchronized int[] getBiomeData(int x, int y, int width, int height, SymbolicObject biomeGenerator)
			throws MinecraftInterfaceException {
		if (isIntCacheInUse) {
			throw new IllegalStateException("This thread is already generating biome data");
		}
		isIntCacheInUse = true;
		try {
			intCacheClass.callStaticMethod(LegacySymbolicNames.METHOD_INT_CACHE_RESET_INT_CACHE);
			return (int[]) biomeGenerator
				.callMethod(LegacySymbolicNames.METHOD_GEN_LAYER_GET_INTS, x, y, width, height);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		} finally {
			isIntCacheInUse = false;
		}
	}

	@Override
	public World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		try {
			initializeBlock();
			Object[] genLayers = getGenLayers(seed, worldType, generatorOptions);
			SymbolicObject quarterResolutionGen = new SymbolicObject(genLayerClass, genLayers[0]);
			SymbolicObject fullResolutionGen = new SymbolicObject(genLayerClass, genLayers[1]);
			return new World(quarterResolutionGen, fullResolutionGen);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	/**
	 * Minecraft 1.8 and higher require block initialization to be called before
	 * creating a biome generator.
	 */
	private synchronized void initializeBlock() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (blockInitClass != null) {
			blockInitClass.callStaticMethod(LegacySymbolicNames.METHOD_BLOCK_INIT_INITIALIZE);
		}
	}

	private Object[] getGenLayers(long seed, WorldType worldType, String generatorOptions)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		if (worldTypeClass == null) {
			return (Object[]) genLayerClass
					.callStaticMethod(LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1, seed);
		} else if (genLayerClass.hasMethod(LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_4)) {
			return (Object[]) genLayerClass.callStaticMethod(
					LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_4,
					seed,
					getWorldType(worldType).getObject(),
					getGeneratorOptions(generatorOptions).getObject());
		} else if (genLayerClass.hasMethod(LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3)) {
			return (Object[]) genLayerClass.callStaticMethod(
					LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3,
					seed,
					getWorldType(worldType).getObject(),
					generatorOptions);
		} else {
			return (Object[]) genLayerClass.callStaticMethod(
					LegacySymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2,
					seed,
					getWorldType(worldType).getObject());
		}
	}

	private SymbolicObject getGeneratorOptions(String generatorOptions)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		SymbolicObject factory = (SymbolicObject) genOptionsFactoryClass
				.callStaticMethod(LegacySymbolicNames.METHOD_GEN_OPTIONS_FACTORY_JSON_TO_FACTORY, generatorOptions);
		return (SymbolicObject) factory.callMethod(LegacySymbolicNames.METHOD_GEN_OPTIONS_FACTORY_BUILD);
	}

	private SymbolicObject getWorldType(WorldType worldType) throws IllegalArgumentException, IllegalAccessException {
		return (SymbolicObject) worldTypeClass.getStaticFieldValue(worldType.getSymbolicFieldName());
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	private class World implements MinecraftInterface.World {
		private final SymbolicObject quarterResolutionBiomeGenerator;
		private final SymbolicObject fullResolutionBiomeGenerator;

		private World(SymbolicObject quarterResolutionGen, SymbolicObject fullResolutionGen) {
			this.quarterResolutionBiomeGenerator = quarterResolutionGen;
			this.fullResolutionBiomeGenerator = fullResolutionGen;
		}

		@Override
		public<T> T getBiomeData(Dimension dimension,
				int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper)
				throws MinecraftInterfaceException {
			if (dimension != Dimension.OVERWORLD)
				throw new UnsupportedDimensionException(dimension);

			SymbolicObject biomeGenerator = useQuarterResolution ? quarterResolutionBiomeGenerator : fullResolutionBiomeGenerator;
			int[] data = LegacyMinecraftInterface.this.getBiomeData(x, y, width, height, biomeGenerator);
			return biomeDataMapper.apply(data);
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return Collections.singleton(Dimension.OVERWORLD);
		}
	}
}
