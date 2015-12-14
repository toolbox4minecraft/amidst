package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class LocalMinecraftInterface implements MinecraftInterface {
	/**
	 * A GenLayer instance, at quarter scale to the final biome layer (i.e. both
	 * axis are divided by 4). Minecraft calculates biomes at
	 * quarter-resolution, then noisily interpolates the biome-map up to 1:1
	 * resolution when needed, this is the biome GenLayer before it is
	 * interpolated.
	 */
	private volatile SymbolicObject quarterResolutionBiomeGenerator;

	/**
	 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
	 * biomes at quarter-resolution, then noisily interpolates the biome-map up
	 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
	 */
	private volatile SymbolicObject fullResolutionBiomeGenerator;

	private final SymbolicClass intCacheClass;
	private final SymbolicClass blockInitClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass worldTypeClass;
	private final RecognisedVersion recognisedVersion;

	LocalMinecraftInterface(SymbolicClass intCacheClass,
			SymbolicClass blockInitClass, SymbolicClass genLayerClass,
			SymbolicClass worldTypeClass, RecognisedVersion recognisedVersion) {
		this.intCacheClass = intCacheClass;
		this.blockInitClass = blockInitClass;
		this.genLayerClass = genLayerClass;
		this.worldTypeClass = worldTypeClass;
		this.recognisedVersion = recognisedVersion;
	}

	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolution) {
		try {
			return doGetBiomeData(x, y, width, height, useQuarterResolution);
		} catch (Exception e) {
			Log.e("unable to get biome data");
			e.printStackTrace();
			return new int[width * height];
		}
	}

	private int[] doGetBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolution) throws IllegalAccessException,
			InvocationTargetException {
		intCacheClass.callStaticMethod("resetIntCache");
		return (int[]) getBiomeGenerator(useQuarterResolution).callMethod(
				"getInts", x, y, width, height);
	}

	private SymbolicObject getBiomeGenerator(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterResolutionBiomeGenerator;
		} else {
			return fullResolutionBiomeGenerator;
		}
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType,
			String generatorOptions) {
		try {
			doCreateWorld(seed, worldType, generatorOptions);
		} catch (Exception e) {
			Log.e("unable to create world");
			e.printStackTrace();
		}
	}

	private void doCreateWorld(long seed, WorldType worldType,
			String generatorOptions) throws IllegalAccessException,
			InvocationTargetException {
		Log.debug("Attempting to create world with seed: " + seed + ", type: "
				+ worldType.getName()
				+ ", and the following generator options:");
		Log.debug(generatorOptions);
		initializeBlock();
		Object[] genLayers = getGenLayers(seed, worldType, generatorOptions);
		quarterResolutionBiomeGenerator = new SymbolicObject(genLayerClass,
				genLayers[0]);
		fullResolutionBiomeGenerator = new SymbolicObject(genLayerClass,
				genLayers[1]);
	}

	/**
	 * Minecraft 1.8 and higher require block initialization to be called before
	 * creating a biome generator.
	 */
	private void initializeBlock() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (blockInitClass != null) {
			blockInitClass.callStaticMethod("initialize");
		}
	}

	private Object[] getGenLayers(long seed, WorldType worldType,
			String generatorOptions) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (worldTypeClass == null) {
			return initializeAllBiomeGenerators(seed);
		} else if (initializeAllBiomeGenerators3Exists()) {
			return initializeAllBiomeGenerators(seed, generatorOptions,
					getWorldType(worldType));
		} else {
			return initializeAllBiomeGenerators(seed, getWorldType(worldType));
		}
	}

	private Object getWorldType(WorldType worldType)
			throws IllegalArgumentException, IllegalAccessException {
		String value = worldType.getValue();
		SymbolicObject object = (SymbolicObject) worldTypeClass
				.getStaticFieldValue(value);
		return object.getObject();
	}

	private boolean initializeAllBiomeGenerators3Exists() {
		return genLayerClass.hasMethod("initializeAllBiomeGenerators3");
	}

	private Object[] initializeAllBiomeGenerators(long seed, Object worldType)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators2", seed, worldType);
	}

	private Object[] initializeAllBiomeGenerators(long seed)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators1", seed);
	}

	private Object[] initializeAllBiomeGenerators(long seed,
			String generatorOptions, Object worldType)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators3", seed, worldType,
				generatorOptions);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}
}
