package amidst.mojangapi.minecraftinterface.local;

import java.util.Map;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.IMinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public class LocalMinecraftInterface implements IMinecraftInterface {
	/**
	 * A GenLayer instance, at quarter scale to the final biome layer (i.e. both
	 * axis are divided by 4). Minecraft calculates biomes at
	 * quarter-resolution, then noisily interpolates the biome-map up to 1:1
	 * resolution when needed, this is the biome GenLayer before it is
	 * interpolated.
	 */
	private SymbolicObject quarterResolutionBiomeGenerator;

	/**
	 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
	 * biomes at quarter-resolution, then noisily interpolates the biome-map up
	 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
	 */
	private SymbolicObject fullResolutionBiomeGenerator;

	private final SymbolicClass intCacheClass;
	private final SymbolicClass blockInitClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass worldTypeClass;
	private final RecognisedVersion recognisedVersion;

	LocalMinecraftInterface(Map<String, SymbolicClass> minecraftClassMap,
			RecognisedVersion recognisedVersion) {
		this.intCacheClass = minecraftClassMap.get("IntCache");
		this.blockInitClass = minecraftClassMap.get("BlockInit");
		this.genLayerClass = minecraftClassMap.get("GenLayer");
		this.worldTypeClass = minecraftClassMap.get("WorldType");
		this.recognisedVersion = recognisedVersion;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolution) {
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
	public void createWorld(long seed, String typeName, String generatorOptions) {
		Log.debug("Attempting to create world with seed: " + seed + ", type: "
				+ typeName + ", and the following generator options:");
		Log.debug(generatorOptions);
		initializeBlock();
		Object[] genLayers = getGenLayers(seed, typeName, generatorOptions);
		quarterResolutionBiomeGenerator = new SymbolicObject(genLayerClass,
				genLayers[0]);
		fullResolutionBiomeGenerator = new SymbolicObject(genLayerClass,
				genLayers[1]);
	}

	private void initializeBlock() {
		// Minecraft 1.8 and higher require block initialization to be called
		// before creating a biome generator.
		if (blockInitClass != null) {
			blockInitClass.callStaticMethod("initialize");
		}
	}

	private Object[] getGenLayers(long seed, String typeName,
			String generatorOptions) {
		if (worldTypeClass == null) {
			return initializeAllBiomeGenerators(seed);
		} else if (initializeAllBiomeGeneratorsWithParamsExists()) {
			return initializeAllBiomeGeneratorsWithParams(seed,
					generatorOptions, getWorldType(typeName));
		} else {
			return initializeAllBiomeGenerators(seed, getWorldType(typeName));
		}
	}

	private Object getWorldType(String typeName) {
		String type = WorldType.from(typeName).getValue();
		SymbolicObject object = (SymbolicObject) worldTypeClass
				.getStaticFieldValue(type);
		return object.getObject();
	}

	private boolean initializeAllBiomeGeneratorsWithParamsExists() {
		return genLayerClass
				.hasMethod("initializeAllBiomeGeneratorsWithParams");
	}

	private Object[] initializeAllBiomeGenerators(long seed, Object worldType) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators", seed, worldType);
	}

	private Object[] initializeAllBiomeGenerators(long seed) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGenerators", seed);
	}

	private Object[] initializeAllBiomeGeneratorsWithParams(long seed,
			String generatorOptions, Object worldType) {
		return (Object[]) genLayerClass.callStaticMethod(
				"initializeAllBiomeGeneratorsWithParams", seed, worldType,
				generatorOptions);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}
}
