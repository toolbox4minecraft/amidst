package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.*;

public class LocalMinecraftInterface implements MinecraftInterface {
	
	private boolean isInitialized = false;
	private final RecognisedVersion recognisedVersion;
	
	private final SymbolicClass registryClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass worldSettingsClass;
	private final SymbolicClass worldDataClass;
	private final SymbolicClass biomeProviderSettingsClass;
	private final SymbolicClass biomeZoomerClass;
	private final SymbolicClass noiseBiomeProviderClass;
	private final SymbolicClass overworldBiomeProviderClass;
	private final SymbolicClass mappedRegistryClass;
	private final SymbolicClass utilClass;
	
	/**
	 * A BiomeProvider instance for the current world that is given to
	 * each thread, giving access to the quarter-scale biome data.
	 */
	private ThreadLocal<Object> threadedBiomeSource;
	
	/**
	 * An instance of fuzzyColumnBiomeZoomer that we use to get the full
	 * resolution biome data.
	 */
	private Object fuzzyColumnBiomeZoomer;
	
	/**
	 * A method used to retrieve the full resolution biome data.
	 * We create a Method for it so we dont lose performance searching
	 * the SymbolicClass for it every time it's called.
	 */
	private Method getFullResBiomeMethod;
	
	/**
	 * A method used to retrieve the quarter resolution biome data.
	 * We create a Method for it so we dont lose performance searching
	 * the SymbolicClass for it every time it's called.
	 */
	private Method getQuarterResBiomeMethod;
	
	/**
	 * A method used for converting between ints and biomes. We
	 * create a Method for it so we dont lose performance searching
	 * the SymbolicClass for it every time it's called.
	 */
	private Method getIdFromBiomeMethod;
	
	/**
	 * The registry for converting between ints and biomes.
	 */
	private Object biomeRegistry;
	
	/**
	 * The seed used by the BiomeZoomer during interpolation. It is
	 * derived from the world seed.
	 */
	private long seedForBiomeZoomer;
	
	public LocalMinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this.recognisedVersion = recognisedVersion;
		// @formatter:off
		this.registryClass =				symbolicClassMap.get(CLASS_REGISTRY);
		this.worldTypeClass =				symbolicClassMap.get(CLASS_WORLD_TYPE);
		this.worldSettingsClass =			symbolicClassMap.get(CLASS_WORLD_SETTINGS);
		this.worldDataClass =				symbolicClassMap.get(CLASS_WORLD_DATA);
		this.biomeProviderSettingsClass =	symbolicClassMap.get(CLASS_BIOME_PROVIDER_SETTINGS);
		this.biomeZoomerClass =				symbolicClassMap.get(CLASS_BIOME_ZOOMER);
		this.noiseBiomeProviderClass =		symbolicClassMap.get(CLASS_NOISE_BIOME_PROVIDER);
		this.overworldBiomeProviderClass =	symbolicClassMap.get(CLASS_OVERWORLD_BIOME_PROVIDER);
		this.mappedRegistryClass =			symbolicClassMap.get(CLASS_MAPPED_REGISTRY);
		this.utilClass =					symbolicClassMap.get(CLASS_UTIL);
		// @formatter:on
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		if (!isInitialized) {
			throw new MinecraftInterfaceException("no world was created");
		}
		
		int[] data = new int[width * height];
		
		try {
			if (data.length == 1) {
				data[0] = getBiomeIdAt(x, y, useQuarterResolution);
				
			} else {
				/**
				 * We break the region in 16x16 chunks, to get better performance out
				 * of the LazyArea used by the game. This gives a ~2x improvement.
				 */
				int chunkSize = 16;
				for (int x0 = 0; x0 < width; x0 += chunkSize) {
					int w = Math.min(chunkSize, width - x0);
					
					for (int y0 = 0; y0 < height; y0 += chunkSize) {
						int h = Math.min(chunkSize, height - y0);
						
						for (int i = 0; i < w; i++) {
							for (int j = 0; j < h; j++) {
								int trueIdx = (x0 + i) + (y0 + j) * width;
								data[trueIdx] = getBiomeIdAt(x + x0 + i, y + y0 + j, useQuarterResolution);
							}
						}
					}
				}
			}
		} catch (IllegalAccessException
				|IllegalArgumentException
				| InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
		
		return data;
	}

	private int getBiomeIdAt(int x, int y, boolean useQuarterResolution)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (useQuarterResolution) {
			return getIdFromBiome((Object) getQuarterResBiomeMethod.invoke(threadedBiomeSource.get(), x, 0, y));
		} else {
			return getIdFromBiome((Object) getFullResBiomeMethod.invoke(fuzzyColumnBiomeZoomer, seedForBiomeZoomer, x, 0, y, threadedBiomeSource.get()));
		}
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		initializeIfNeeded();

		try {
			seedForBiomeZoomer = (long) worldDataClass.callStaticMethod(METHOD_WORLD_DATA_MAP_SEED, seed);
			threadedBiomeSource = ThreadLocal.withInitial(() -> {
				try {
					return createBiomeSourceObject(seed, worldType, generatorOptions);
				} catch (Exception e) {
					throw new RuntimeException("unable to create pixel transformer", e);
				}
			});

		} catch(IllegalArgumentException
				| IllegalAccessException
				| InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
		
		isInitialized = true;
	}

	private void initializeIfNeeded() throws MinecraftInterfaceException {
		if (isInitialized) {
			return;
		}
		try {
			fuzzyColumnBiomeZoomer = biomeZoomerClass.getClazz().getEnumConstants()[0];
			getFullResBiomeMethod = biomeZoomerClass.getMethod(METHOD_BIOME_ZOOMER_GET_BIOME).getRawMethod();
			getQuarterResBiomeMethod = noiseBiomeProviderClass.getMethod(METHOD_NOISE_BIOME_PROVIDER_GET_BIOME).getRawMethod();
			getIdFromBiomeMethod = mappedRegistryClass.getMethod(METHOD_MAPPED_REGISTRY_GET_ID).getRawMethod();
			biomeRegistry = registryClass.getField(FIELD_REGISTRY_BIOME).getRawField().get(null);
			
			try {
				((ExecutorService) utilClass.getStaticFieldValue(FIELD_UTIL_SERVER_EXECUTOR)).shutdownNow();
			} catch (NullPointerException e) {
				AmidstLogger.warn("Unable to shut down Server-Worker threads");
			}
			
		} catch(IllegalArgumentException | IllegalAccessException e) {
			throw new MinecraftInterfaceException("unable to initialize the MinecraftInterface", e);
		}
		
	}

	private Object createBiomeSourceObject(long seed, WorldType worldType, String generatorOptions)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (!generatorOptions.isEmpty()) {
            //TODO: fix me
            AmidstLogger.warn("Custom generator options aren't supported in this version");
        }
        
		Object worldTypeObj = worldTypeClass.getField(worldType.getSymbolicFieldName()).getRawField().get(null);
		
	    Object worldSettings = worldSettingsClass.getConstructor(CONSTRUCTOR_WORLD_SETTINGS).getRawConstructor()
	    		.newInstance(seed, null, false, false, worldTypeObj);
	    
	    Object worldData = worldDataClass.getConstructor(CONSTRUCTOR_WORLD_DATA).getRawConstructor()
	    		.newInstance(worldSettings, "");
		
		Object biomeProviderSettingsObj = biomeProviderSettingsClass.getConstructor(CONSTRUCTOR_BIOME_PROVIDER_SETTINGS).getRawConstructor()
				.newInstance(worldData);
		
		return overworldBiomeProviderClass.getConstructor(CONSTRUCTOR_OVERWORLD_BIOME_PROVIDER).getRawConstructor().newInstance(biomeProviderSettingsObj);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	private int getIdFromBiome(Object biome)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int) getIdFromBiomeMethod.invoke(biomeRegistry, biome);
	}
}
