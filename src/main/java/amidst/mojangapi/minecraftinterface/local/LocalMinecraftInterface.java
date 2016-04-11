package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
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

	LocalMinecraftInterface(
			SymbolicClass intCacheClass,
			SymbolicClass blockInitClass,
			SymbolicClass genLayerClass,
			SymbolicClass worldTypeClass,
			RecognisedVersion recognisedVersion) {
		this.intCacheClass = intCacheClass;
		this.blockInitClass = blockInitClass;
		this.genLayerClass = genLayerClass;
		this.worldTypeClass = worldTypeClass;
		this.recognisedVersion = recognisedVersion;
	}

	// @formatter:off
	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		try {
			intCacheClass.callStaticMethod(SymbolicNames.METHOD_INT_CACHE_RESET_INT_CACHE);
			return (int[]) getBiomeGenerator(useQuarterResolution).callMethod(SymbolicNames.METHOD_GEN_LAYER_GET_INTS, x, y, width, height);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}

	private SymbolicObject getBiomeGenerator(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterResolutionBiomeGenerator;
		} else {
			return fullResolutionBiomeGenerator;
		}
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		try {
			Log.i("Creating world with seed '" + seed + "' and type '" + worldType.getName() + "'");
			Log.i("Using the following generator options: " + generatorOptions);
			initializeBlock();
			Object[] genLayers = getGenLayers(seed, worldType, generatorOptions);
			quarterResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[0]);
			fullResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[1]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	/**
	 * Minecraft 1.8 and higher require block initialization to be called before
	 * creating a biome generator.
	 */
	private void initializeBlock()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (blockInitClass != null) {
			blockInitClass.callStaticMethod(SymbolicNames.METHOD_BLOCK_INIT_INITIALIZE);
		}
	}

	private Object[] getGenLayers(long seed, WorldType worldType, String generatorOptions)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (worldTypeClass == null) {
			return (Object[]) genLayerClass.callStaticMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_1, seed);
		} else if (genLayerClass.hasMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3)) {
			return (Object[]) genLayerClass.callStaticMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_3, seed, getWorldType(worldType).getObject(), generatorOptions);
		} else {
			return (Object[]) genLayerClass.callStaticMethod(SymbolicNames.METHOD_GEN_LAYER_INITIALIZE_ALL_BIOME_GENERATORS_2, seed, getWorldType(worldType).getObject());
		}
	}

	private SymbolicObject getWorldType(WorldType worldType)
			throws IllegalArgumentException, IllegalAccessException {
		return (SymbolicObject) worldTypeClass.getStaticFieldValue(worldType.getSymbolicFieldName());
	}
	// @formatter:on

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}
}
