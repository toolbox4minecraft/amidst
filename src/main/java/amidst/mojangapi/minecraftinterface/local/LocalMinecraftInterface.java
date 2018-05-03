package amidst.mojangapi.minecraftinterface.local;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public class LocalMinecraftInterface implements MinecraftInterface {
	
	private final SymbolicClass bootstrapClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass genSettingsClass;
	private final SymbolicClass layerUtilClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass biomeClass; 
	private MethodHandle biomeGetIdMethod;
	
	private final RecognisedVersion recognisedVersion;
	
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
	
	/**
	 * An array used to return biome data
	 */
	private volatile int[] dataArray = new int[256];
	
	public LocalMinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this.bootstrapClass = symbolicClassMap.get(SymbolicNames.CLASS_BOOTSTRAP);
		this.worldTypeClass = symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE);
		this.genSettingsClass = symbolicClassMap.get(SymbolicNames.CLASS_GEN_SETTINGS);
		this.layerUtilClass = symbolicClassMap.get(SymbolicNames.CLASS_LAYER_UTIL);
		this.genLayerClass = symbolicClassMap.get(SymbolicNames.CLASS_GEN_LAYER);
		this.biomeClass = symbolicClassMap.get(SymbolicNames.CLASS_BIOME);
		
		this.recognisedVersion = recognisedVersion;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		try {
			if(biomeGetIdMethod == null) {
				Method biomeRawMethod = biomeClass.getMethod(SymbolicNames.METHOD_BIOME_GET_ID).getRawMethod();
				biomeGetIdMethod = MethodHandles.lookup().unreflect(biomeRawMethod);
			}
			
			SymbolicObject biomeGen = getBiomeGenerator(useQuarterResolution);
			Object[] biomes = (Object[]) biomeGen.callMethod(SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA, x, y, width, height, null);
			
			int[] data = ensureArrayCapacity(biomes.length);
			
			for(int i = 0; i < biomes.length; i++) {
				data[i] = getBiomeId(biomes[i]);
			}
			
			return data;			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}

	@Override
	public void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {

		try {
			bootstrapClass.callStaticMethod(SymbolicNames.METHOD_BOOTSTRAP_REGISTER);
			
			// @formatter:off
			Object[] genLayers = (Object[]) layerUtilClass.callStaticMethod(
				SymbolicNames.METHOD_LAYER_UTIL_INITIALIZE_ALL,
				seed,
				getWorldType(worldType).getObject(),
				getGenSettings(generatorOptions).getObject()
			);
			// @formatter:on
			
			quarterResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[0]);
			fullResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[0]);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}
	
	private SymbolicObject getBiomeGenerator(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterResolutionBiomeGenerator;
		} else {
			return fullResolutionBiomeGenerator;
		}
	}
	
	private SymbolicObject getWorldType(WorldType worldType) throws IllegalArgumentException, IllegalAccessException {
		return (SymbolicObject) worldTypeClass.getStaticFieldValue(worldType.getSymbolicFieldName());
	}
	
	private SymbolicObject getGenSettings(String generatorOptions) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		if(!generatorOptions.isEmpty()) {
			//TODO fix me
			AmidstLogger.warn("Custom generator options aren't supported in this version");
		}
		return (SymbolicObject) genSettingsClass.callConstructor(SymbolicNames.CONSTRUCTOR_GEN_SETTINGS);
	}
	
	private int getBiomeId(Object biome) throws MinecraftInterfaceException {
		try {
			Object res = biomeGetIdMethod.invoke(biome);
			return (int) res;
		} catch (Throwable e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}
	
	private int[] ensureArrayCapacity(int length) {
		int cur = dataArray.length;
		if(length <= cur)
			return dataArray;
		
		while(cur < length)
			cur *= 2;
		
		dataArray = new int[cur];
		return dataArray;
	}
	

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

}
