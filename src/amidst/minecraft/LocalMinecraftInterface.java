package amidst.minecraft;

import amidst.version.VersionInfo;
import MoF.SaveLoader;
import MoF.SaveLoader.Type;

public class LocalMinecraftInterface implements IMinecraftInterface {
	private Minecraft minecraft;
	private MinecraftObject biomeGen;
	
	public LocalMinecraftInterface(Minecraft minecraft) {
		this.minecraft = minecraft;
	}
	
	@Override
	public int[] getBiomeData(int x, int y, int width, int height) {
		minecraft.getClassByName("IntCache").callFunction("resetIntCache");
		return (int[])biomeGen.callFunction("getInts", x, y, width, height);
	}

	@Override
	public void createWorld(long seed, String typeName) {
		Type type = Type.fromMixedCase(typeName);
		MinecraftClass genLayerClass = minecraft.getClassByName("GenLayer");
		MinecraftClass worldTypeClass = minecraft.getClassByName("WorldType");
		Object[] genLayers = null;
		if (worldTypeClass == null) {
			genLayers = (Object[])genLayerClass.callFunction("initializeAllBiomeGenerators", seed);
		} else {
			genLayers = (Object[])genLayerClass.callFunction("initializeAllBiomeGenerators", seed, ((MinecraftObject) worldTypeClass.getValue(type.getValue())).get());
		}
		biomeGen = new MinecraftObject(genLayerClass, genLayers[0]);
	}

	@Override
	public VersionInfo getVersion() {
		return minecraft.version;
	}


}
