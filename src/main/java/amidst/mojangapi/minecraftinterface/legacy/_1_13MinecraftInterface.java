package amidst.mojangapi.minecraftinterface.legacy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.ReflectionUtils;
import amidst.mojangapi.minecraftinterface.UnsupportedDimensionException;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;
import amidst.util.ArrayCache;

public class _1_13MinecraftInterface implements MinecraftInterface {
    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._19w35a;

	private final SymbolicClass bootstrapClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass genSettingsClass;
	private final SymbolicClass layerUtilClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass biomeClass;
	private final SymbolicClass registryClass;
	private final SymbolicClass registryKeyClass;
	private final SymbolicClass utilClass;

	private boolean isInitialized = false;

	private final RecognisedVersion recognisedVersion;

	/**
	 * The Biome.getId method handle. If version < 18w33a, the signature is: int
	 * getId(Biome) If version >= 18w33a, the signature is: int
	 * getId(Registry<Biome>, Biome)
	 */
	private MethodHandle biomeGetIdMethod;
	
	/**
	 * A MethodHandle for getting the quarter resolution
	 * biome data.
	 */
	private MethodHandle getBiomesMethod;
	
	/**
	 * A boolean that is later set to provide whether the
	 * 1.13 method parameters should be used to invoke the
	 * method. We make it its own boolean so we don't
	 * have to check the SymbolicClass every time.
	 */
	private boolean use113GetBiomesMethod;

	/**
	 * The biome registry, for use with versions >= 18w33a
	 */
	private Object biomeRegistry;

	/**
	 * An array used to return biome data
	 */
	private final ArrayCache<int[]> dataArray = ArrayCache.makeIntArrayCache(256);

	public _1_13MinecraftInterface(
			SymbolicClass bootstrapClass,
			SymbolicClass worldTypeClass,
			SymbolicClass genSettingsClass,
			SymbolicClass genLayerClass,
			SymbolicClass layerUtilClass,
			SymbolicClass biomeClass,
			SymbolicClass registryClass,
			SymbolicClass registryKeyClass,
			SymbolicClass utilClass,
			RecognisedVersion recognisedVersion) {
		this.bootstrapClass = bootstrapClass;
		this.worldTypeClass = worldTypeClass;
		this.genSettingsClass = genSettingsClass;
		this.genLayerClass = genLayerClass;
		this.layerUtilClass = layerUtilClass;
		this.biomeClass = biomeClass;
		this.registryClass = registryClass;
		this.registryKeyClass = registryKeyClass;
		this.utilClass = utilClass;

		this.recognisedVersion = recognisedVersion;
	}

	public _1_13MinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this(
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_BOOTSTRAP),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_WORLD_TYPE),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_GEN_SETTINGS),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_GEN_LAYER),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_LAYER_UTIL),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_BIOME),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_REGISTRY),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_REGISTRY_KEY),
				symbolicClassMap.get(_1_13SymbolicNames.CLASS_UTIL),
				recognisedVersion);
	}

	@Override
	public synchronized MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {

		try {
			initializeIfNeeded();

			// @formatter:off
			Object[] genLayers = (Object[]) layerUtilClass.callStaticMethod(
				_1_13SymbolicNames.METHOD_LAYER_UTIL_INITIALIZE_ALL,
				seed,
				getWorldType(worldType).getObject(),
				getGenSettings(generatorOptions).getObject()
			);
			// @formatter:on
			
			return new World(genLayers[0], genLayers[1]);

		} catch (IllegalAccessException
			   | IllegalArgumentException
			   | InvocationTargetException
			   | InstantiationException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	private synchronized void initializeIfNeeded()
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException,
			InstantiationException {
		if (isInitialized) {
			return;
		}

		String register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER;
		if(RecognisedVersion.isNewer(recognisedVersion, RecognisedVersion._1_13_2)) {
			if(bootstrapClass.getMethod(_1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER3).hasModifiers(Modifier.PUBLIC)) {
				register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER3;
			} else if (bootstrapClass.getMethod(register).hasModifiers(Modifier.PRIVATE)) {
				register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER2;
			}
		}
		
		initBiomeGetIdHandle();
		
		if(genLayerClass.hasMethod(_1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA)) {
			getBiomesMethod = ReflectionUtils.getMethodHandle(genLayerClass, _1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA);
			use113GetBiomesMethod = true;
		} else {
			getBiomesMethod = ReflectionUtils.getMethodHandle(genLayerClass, _1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA2);
		}
		
		bootstrapClass.callStaticMethod(register);
		
		// Minecraft's datafixers have been created during the initialization
		// of the DataFixesManager class since 1.13 (I think). In our case,
		// the class gets initialized during the bootstrapping stage. For our
		// use case of the minecraft code, the datafixers are useless. The
		// creation of the datafixers take valuable processor time and memory,
		// so it's best to disable them in any way we can. Unfortunately, the
		// only way to do this is to just shut down the thread pool that creates
		// them. This doesn't work for versions before 1.14 because they use
		// ForkJoinPool.commonPool(), which is unaffected by shutdown() and
		// shutdownNow().
		
		if(RecognisedVersion.isNewer(recognisedVersion, RecognisedVersion._1_13_2)) {
			try {
				((ExecutorService) utilClass.getStaticFieldValue(_1_13SymbolicNames.FIELD_UTIL_SERVER_EXECUTOR)).shutdownNow();
			} catch (NullPointerException e) {
				AmidstLogger.warn("Unable to shut down Server-Worker threads");
			}
		}
		
		isInitialized = true;
	}
	
	private synchronized void initBiomeGetIdHandle()
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		if (biomeGetIdMethod != null) {
			return;
		}
		
		boolean biomeGetIdMethodReturnsInt = biomeClass.getMethod(_1_13SymbolicNames.METHOD_BIOME_GET_ID)
															.getRawMethod().getReturnType().equals(Integer.TYPE);
		
		if (registryKeyClass != null && !biomeGetIdMethodReturnsInt) {
			biomeRegistry = getBiomeRegistry();
			biomeGetIdMethod = ReflectionUtils.getMethodHandle(registryClass, _1_13SymbolicNames.METHOD_REGISTRY_GET_ID);
		} else {
			biomeGetIdMethod = ReflectionUtils.getMethodHandle(biomeClass, _1_13SymbolicNames.METHOD_BIOME_GET_ID);
		}
	}

	private Object getBiomeRegistry()
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		SymbolicObject metaRegistry = (SymbolicObject) registryClass
				.getStaticFieldValue(_1_13SymbolicNames.FIELD_REGISTRY_META_REGISTRY);
		Object biomeRegistryKey = registryKeyClass
				.callConstructor(_1_13SymbolicNames.CONSTRUCTOR_REGISTRY_KEY, "biome")
				.getObject();

		String getByKey = _1_13SymbolicNames.METHOD_REGISTRY_GET_BY_KEY;
		if (!registryClass.getMethod(getByKey).hasReturnType(Object.class)) {
			getByKey = _1_13SymbolicNames.METHOD_REGISTRY_GET_BY_KEY2;
		}

		Object biomeRegistryObj = metaRegistry.callMethod(getByKey, biomeRegistryKey);
		return biomeRegistryObj;
	}

	private SymbolicObject getWorldType(WorldType worldType) throws IllegalArgumentException, IllegalAccessException {
		return (SymbolicObject) worldTypeClass.getStaticFieldValue(worldType.getSymbolicFieldName());
	}

	private SymbolicObject getGenSettings(String generatorOptions)
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		if (!generatorOptions.isEmpty()) {
			AmidstLogger.warn("Custom generator options aren't supported in this version");
		}
		return genSettingsClass.callConstructor(_1_13SymbolicNames.CONSTRUCTOR_GEN_SETTINGS);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	private class World implements MinecraftInterface.World {
		/**
		 * A GenLayer instance, at quarter scale to the final biome layer (i.e. both
		 * axis are divided by 4). Minecraft calculates biomes at
		 * quarter-resolution, then noisily interpolates the biome-map up to 1:1
		 * resolution when needed, this is the biome GenLayer before it is
		 * interpolated.
		 */
		private final Object quarterResolutionBiomeGenerator;
		
		/**
		 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
		 * biomes at quarter-resolution, then noisily interpolates the biome-map up
		 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
		 */
		private final Object fullResolutionBiomeGenerator;

		private World(Object quarterResolutionGen, Object fullResolutionGen) {
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

			Object biomeGenerator = useQuarterResolution ? quarterResolutionBiomeGenerator : fullResolutionBiomeGenerator;
			int size = width * height;
		    return dataArray.withArrayFaillible(size, data -> {
			    try {
			    	if(size <= 16) {
			    		data[0] = getBiomeId(getBiomeData(x, y, width, height, biomeGenerator));
			    		return biomeDataMapper.apply(data);
			    	}

			        /**
			         * We break the region in 16x16 chunks, to get better performance out
			         * of the LazyArea used by the game. This gives a ~2x improvement.
			         * 
			         * In the period of time between 18w16a to 18w47a, this caused the
			         * performance to be worse, so we exclude those versions here.
		             */
					if (RecognisedVersion.isNewerOrEqualTo(recognisedVersion, RecognisedVersion._18w16a)
					 && RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._18w47b)) {
						Object[] biomes = getBiomeData(x, y, width, height, biomeGenerator);
						for (int i = 0; i < biomes.length; i++) {
							data[i] = getBiomeId(biomes[i]);
						}
					} else {
						int chunkSize = 16;
						for (int x0 = 0; x0 < width; x0 += chunkSize) {
							int w = Math.min(chunkSize, width - x0);
	
							for (int y0 = 0; y0 < height; y0 += chunkSize) {
								int h = Math.min(chunkSize, height - y0);
	
								Object[] biomes = getBiomeData(x + x0, y + y0, w, h, biomeGenerator);
	
								for (int i = 0; i < w; i++) {
									for (int j = 0; j < h; j++) {
										int idx = i + j * w;
										int trueIdx = (x0 + i) + (y0 + j) * width;
										data[trueIdx] = getBiomeId(biomes[idx]);
									}
								}
							}
						}
					}
			    } catch (Throwable e) {
			        throw new MinecraftInterfaceException("unable to get biome data", e);
			    }

			    return biomeDataMapper.apply(data);
		    });
		}

		private Object[] getBiomeData(int x, int y, int width, int height, Object biomeGen) throws Throwable {
			if(use113GetBiomesMethod) {
				return (Object[]) getBiomesMethod.invokeExact(biomeGen, x, y, width, height, (Object) null);
			} else {
				return (Object[]) getBiomesMethod.invokeExact(biomeGen, x, y, width, height);
			}
		}
		
		private int getBiomeId(Object biome) throws MinecraftInterfaceException {
			try {
				if (biomeRegistry != null) {
					return (int) biomeGetIdMethod.invokeExact(biomeRegistry, biome);
				} else {
					return (int) biomeGetIdMethod.invokeExact(biome);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				throw new MinecraftInterfaceException("unable to get biome data", e);
			}
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return Collections.singleton(Dimension.OVERWORLD);
		}
	}
}
