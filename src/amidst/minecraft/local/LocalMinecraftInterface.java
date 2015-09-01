package amidst.minecraft.local;

import MoF.SaveLoader.Type;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.version.VersionInfo;

public class LocalMinecraftInterface implements IMinecraftInterface {
	private Minecraft minecraft;
	/**
	 * A GenLayer instance, at quarter scale to the final biome layer (i.e. both
	 * axis are divided by 4). Minecraft calculates biomes at
	 * quarter-resolution, then noisily interpolates the biome-map up to 1:1
	 * resolution when needed, this is the biome GenLayer before it is
	 * interpolated.
	 */
	private MinecraftObject biomeGen;
	/**
	 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
	 * biomes at quarter-resolution, then noisily interpolates the biome-map up
	 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
	 */
	private MinecraftObject biomeGen_fullResolution;

	public LocalMinecraftInterface(Minecraft minecraft) {
		this.minecraft = minecraft;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolutionMap) {
		minecraft.getMinecraftClassByMinecraftClassName("IntCache")
				.callMethod("resetIntCache");
		return (int[]) (useQuarterResolutionMap ? biomeGen
				: biomeGen_fullResolution).callMethod("getInts", x, y, width,
				height);
	}

	@Override
	public void createWorld(long seed, String typeName, String generatorOptions) {
		Log.debug("Attempting to create world with seed: " + seed + ", type: "
				+ typeName + ", and the following generator options:");
		Log.debug(generatorOptions);

		// Minecraft 1.8 and higher require block initialization to be called
		// before creating a biome generator.
		MinecraftClass blockInit;
		if ((blockInit = minecraft
				.getMinecraftClassByMinecraftClassName("BlockInit")) != null)
			blockInit.callMethod("initialize");

		Type type = Type.fromMixedCase(typeName);
		MinecraftClass genLayerClass = minecraft
				.getMinecraftClassByMinecraftClassName("GenLayer");
		MinecraftClass worldTypeClass = minecraft
				.getMinecraftClassByMinecraftClassName("WorldType");
		Object[] genLayers = null;
		if (worldTypeClass == null) {
			genLayers = (Object[]) genLayerClass.callMethod(
					"initializeAllBiomeGenerators", seed);
		} else {
			Object worldType = ((MinecraftObject) worldTypeClass.getStaticPropertyValue(type
					.getValue())).getObject();
			if (genLayerClass.getMethod(
					"initializeAllBiomeGeneratorsWithParams").exists()) {
				genLayers = (Object[]) genLayerClass.callMethod(
						"initializeAllBiomeGeneratorsWithParams", seed,
						worldType, generatorOptions);
			} else {
				genLayers = (Object[]) genLayerClass.callMethod(
						"initializeAllBiomeGenerators", seed, worldType);
			}

		}

		biomeGen = new MinecraftObject(genLayerClass, genLayers[0]);
		biomeGen_fullResolution = new MinecraftObject(genLayerClass,
				genLayers[1]);
	}

	@Override
	public VersionInfo getVersion() {
		return minecraft.getVersion();
	}

}
