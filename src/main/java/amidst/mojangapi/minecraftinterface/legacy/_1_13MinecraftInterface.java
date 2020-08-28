package amidst.mojangapi.minecraftinterface.legacy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
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

	private boolean isBootstrapCalled = false;

	private final RecognisedVersion recognisedVersion;

	/**
	 * The Biome.getId method handle. If version < 18w33a, the signature is: int
	 * getId(Biome) If version >= 18w33a, the signature is: int
	 * getId(Registry<Biome>, Biome)
	 */
	private MethodHandle biomeGetIdMethod;

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
			RecognisedVersion recognisedVersion) {
		this.bootstrapClass = bootstrapClass;
		this.worldTypeClass = worldTypeClass;
		this.genSettingsClass = genSettingsClass;
		this.genLayerClass = genLayerClass;
		this.layerUtilClass = layerUtilClass;
		this.biomeClass = biomeClass;
		this.registryClass = registryClass;
		this.registryKeyClass = registryKeyClass;

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
				recognisedVersion);
	}

	private int[] populateBiomeData(int[] data, int x, int y, int width, int height, SymbolicObject biomeGen)
			throws MinecraftInterfaceException {
		try {
			initBiomeGetIdHandle();

			/**
			 * We break the region in 16x16 chunks, to get better performance
			 * out of the LazyArea used by the game. Sadly, we get no
			 * performance gain in 18w16a and newer, but in previous snapshots
			 * we get a ~1.5x improvement.
			 */
			if (RecognisedVersion.isNewerOrEqualTo(recognisedVersion, RecognisedVersion._18w16a)) {
				Object[] biomes = getBiomeDataInner(x, y, width, height, biomeGen);
				for (int i = 0; i < biomes.length; i++) {
					data[i] = getBiomeId(biomes[i]);
				}
			} else {
				int chunkSize = 16;
				for (int x0 = 0; x0 < width; x0 += chunkSize) {
					int w = Math.min(chunkSize, width - x0);

					for (int y0 = 0; y0 < height; y0 += chunkSize) {
						int h = Math.min(chunkSize, height - y0);

						Object[] biomes = getBiomeDataInner(x + x0, y + y0, w, h, biomeGen);

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
			return data;
		} catch (
				IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| InstantiationException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}

	private Object[] getBiomeDataInner(int x, int y, int width, int height, SymbolicObject biomeGen)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		if(genLayerClass.hasMethod(_1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA)) {
			return (Object[]) biomeGen.callMethod(
					_1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA, x, y, width, height, null);
		} else {
			return (Object[]) biomeGen.callMethod(
					_1_13SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA2, x, y, width, height);
		}
	}

	private synchronized void initBiomeGetIdHandle()
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		if (biomeGetIdMethod != null) {
			return;
		}
		Method biomeRawMethod = biomeClass.getMethod(_1_13SymbolicNames.METHOD_BIOME_GET_ID).getRawMethod();
		if (registryKeyClass != null && !biomeRawMethod.getReturnType().equals(Integer.TYPE)) {
			biomeRegistry = getBiomeRegistry();
			biomeRawMethod = registryClass.getMethod(_1_13SymbolicNames.METHOD_REGISTRY_GET_ID).getRawMethod();
		}
		biomeGetIdMethod = MethodHandles.lookup().unreflect(biomeRawMethod);
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

	@Override
	public synchronized MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {

		try {
			callBootstrapRegister();

			// @formatter:off
			Object[] genLayers = (Object[]) layerUtilClass.callStaticMethod(
				_1_13SymbolicNames.METHOD_LAYER_UTIL_INITIALIZE_ALL,
				seed,
				getWorldType(worldType).getObject(),
				getGenSettings(generatorOptions).getObject()
			);
			// @formatter:on

			SymbolicObject quarterResolutionGen = new SymbolicObject(genLayerClass, genLayers[0]);
			SymbolicObject fullResolutionGen = new SymbolicObject(genLayerClass, genLayers[1]);
			return new World(quarterResolutionGen, fullResolutionGen);

		} catch (
				IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| InstantiationException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	private synchronized void callBootstrapRegister()
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		if (isBootstrapCalled) {
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

		bootstrapClass.callStaticMethod(register);
		isBootstrapCalled = true;
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

	private int getBiomeId(Object biome) throws MinecraftInterfaceException {
		try {
			if (biomeRegistry != null) {
				return (int) biomeGetIdMethod.invoke(biomeRegistry, biome);
			} else {
				return (int) biomeGetIdMethod.invoke(biome);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
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
		private final SymbolicObject quarterResolutionBiomeGenerator;

		/**
		 * A GenLayer instance, the biome layer. (1:1 scale) Minecraft calculates
		 * biomes at quarter-resolution, then noisily interpolates the biome-map up
		 * to 1:1 resolution when needed, this is the interpolated biome GenLayer.
		 */
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
			return dataArray.withArrayFaillible(width * height, data -> {
				populateBiomeData(data, x, y, width, height, biomeGenerator);
				return biomeDataMapper.apply(data);
			});
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return Collections.singleton(Dimension.OVERWORLD);
		}
	}
}
