package amidst.minecraft;

import java.lang.reflect.Field;

import amidst.logging.Log;
import amidst.version.VersionInfo;
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
	public void createWorld(long seed, String typeName, String generatorOptions) {
		Log.debug("Attempting to create world with seed: " + seed + ", type: " + typeName + ", and the following generator options:");
		Log.debug(generatorOptions);
		MinecraftClass blockInit; // FIXME: This is a bit hackish!
		if ((blockInit = minecraft.getClassByName("BlockInit")) != null) {
			Class<?> clazz = blockInit.getClazz();
			try {
				Field isLoadedField = clazz.getDeclaredField("a");
				isLoadedField.setAccessible(true);
				isLoadedField.set(null, true);
			} catch (Exception e) {
				Log.crash(e, "Unable to use 14w02a hack.");
			}
		}
		
		Type type = Type.fromMixedCase(typeName);
		MinecraftClass genLayerClass = minecraft.getClassByName("GenLayer");
		MinecraftClass worldTypeClass = minecraft.getClassByName("WorldType");
		Object[] genLayers = null;
		if (worldTypeClass == null) {
			genLayers = (Object[])genLayerClass.callFunction("initializeAllBiomeGenerators", seed);
		} else {
			Object worldType = ((MinecraftObject) worldTypeClass.getValue(type.getValue())).get();
			if (genLayerClass.getMethod("initializeAllBiomeGeneratorsWithParams").exists()) {
				genLayers = (Object[])genLayerClass.callFunction("initializeAllBiomeGeneratorsWithParams", seed, worldType, generatorOptions);
			} else {
				genLayers = (Object[])genLayerClass.callFunction("initializeAllBiomeGenerators", seed, worldType);	
			}
				
		}

		biomeGen = new MinecraftObject(genLayerClass, genLayers[0]);
	}

	@Override
	public VersionInfo getVersion() {
		return minecraft.version;
	}


}
