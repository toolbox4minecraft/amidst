package amidst.mojangapi.minecraftinterface.local;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

	public LocalMinecraftInterface(
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

	public LocalMinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this(
				symbolicClassMap.get(SymbolicNames.CLASS_BOOTSTRAP),
				symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE),
				symbolicClassMap.get(SymbolicNames.CLASS_GEN_SETTINGS),
				symbolicClassMap.get(SymbolicNames.CLASS_GEN_LAYER),
				symbolicClassMap.get(SymbolicNames.CLASS_LAYER_UTIL),
				symbolicClassMap.get(SymbolicNames.CLASS_BIOME),
				symbolicClassMap.get(SymbolicNames.CLASS_REGISTRY),
				symbolicClassMap.get(SymbolicNames.CLASS_REGISTRY_KEY),
				recognisedVersion);
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		try {
			if (biomeGetIdMethod == null) {
				biomeGetIdMethod = getBiomeGetIdHandle();
			}

			int[] data = ensureArrayCapacity(width * height);

			/**
			 * We break the region in 16x16 chunks, to get better performance
			 * out of the LazyArea used by the game. Sadly, we get no
			 * performance gain in 18w16a and newer, but in previous snapshots
			 * we get a ~1.5x improvement.
			 */
			if (RecognisedVersion.isNewerOrEqualTo(recognisedVersion, RecognisedVersion._18w16a)) {
				Object[] biomes = getBiomeDataInner(x, y, width, height, useQuarterResolution);
				for (int i = 0; i < biomes.length; i++) {
					data[i] = getBiomeId(biomes[i]);
				}
			} else {
				int chunkSize = 16;
				for (int x0 = 0; x0 < width; x0 += chunkSize) {
					int w = Math.min(chunkSize, width - x0);

					for (int y0 = 0; y0 < height; y0 += chunkSize) {
						int h = Math.min(chunkSize, height - y0);

						Object[] biomes = getBiomeDataInner(x + x0, y + y0, w, h, useQuarterResolution);

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

	private Object[] getBiomeDataInner(int x, int y, int width, int height, boolean useQuarterResolution)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		SymbolicObject biomeGen = getBiomeGenerator(useQuarterResolution);
		Object[] biomes = null;
		if(genLayerClass.hasMethod(SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA)) {
			biomes = (Object[]) biomeGen.callMethod(
					SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA, x, y, width, height, null);
		} else {
			biomes = (Object[]) biomeGen.callMethod(
					SymbolicNames.METHOD_GEN_LAYER_GET_BIOME_DATA2, x, y, width, height);
		}
		return biomes;
	}

	private MethodHandle getBiomeGetIdHandle()
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		Method biomeRawMethod = biomeClass.getMethod(SymbolicNames.METHOD_BIOME_GET_ID).getRawMethod();
		if (registryKeyClass != null && !biomeRawMethod.getReturnType().equals(Integer.TYPE)) {
			biomeRegistry = getBiomeRegistry();
			biomeRawMethod = registryClass.getMethod(SymbolicNames.METHOD_REGISTRY_GET_ID).getRawMethod();
		}
		return MethodHandles.lookup().unreflect(biomeRawMethod);
	}

	private Object getBiomeRegistry()
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		SymbolicObject metaRegistry = (SymbolicObject) registryClass
				.getStaticFieldValue(SymbolicNames.FIELD_REGISTRY_META_REGISTRY);
		Object biomeRegistryKey = registryKeyClass
				.callConstructor(SymbolicNames.CONSTRUCTOR_REGISTRY_KEY, "biome")
				.getObject();

		String getByKey = SymbolicNames.METHOD_REGISTRY_GET_BY_KEY;
		if (!registryClass.getMethod(getByKey).hasReturnType(Object.class)) {
			getByKey = SymbolicNames.METHOD_REGISTRY_GET_BY_KEY2;
		}

		Object biomeRegistryObj = metaRegistry.callMethod(getByKey, biomeRegistryKey);
		return biomeRegistryObj;
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {

		try {
			if (!isBootstrapCalled) {
				callBootstrapRegister();
				isBootstrapCalled = true;
			}

			// @formatter:off
			Object[] genLayers = (Object[]) layerUtilClass.callStaticMethod(
				SymbolicNames.METHOD_LAYER_UTIL_INITIALIZE_ALL,
				seed,
				getWorldType(worldType).getObject(),
				getGenSettings(generatorOptions).getObject()
			);
			// @formatter:on

			quarterResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[0]);
			fullResolutionBiomeGenerator = new SymbolicObject(genLayerClass, genLayers[1]);

		} catch (
				IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| InstantiationException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	private void callBootstrapRegister()
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		String register = SymbolicNames.METHOD_BOOTSTRAP_REGISTER;
		if(RecognisedVersion.isNewer(recognisedVersion, RecognisedVersion._1_13_2)) {
			if(bootstrapClass.getMethod(SymbolicNames.METHOD_BOOTSTRAP_REGISTER3).hasModifiers(Modifier.PUBLIC)) {
				register = SymbolicNames.METHOD_BOOTSTRAP_REGISTER3;
			} else if (bootstrapClass.getMethod(register).hasModifiers(Modifier.PRIVATE)) {
				register = SymbolicNames.METHOD_BOOTSTRAP_REGISTER2;
			}
		}

		bootstrapClass.callStaticMethod(register);
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

	private SymbolicObject getGenSettings(String generatorOptions)
			throws IllegalArgumentException,
			IllegalAccessException,
			InstantiationException,
			InvocationTargetException {
		if (!generatorOptions.isEmpty()) {
			// TODO fix me
			AmidstLogger.warn("Custom generator options aren't supported in this version");
		}
		return genSettingsClass.callConstructor(SymbolicNames.CONSTRUCTOR_GEN_SETTINGS);
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

	private int[] ensureArrayCapacity(int length) {
		int cur = dataArray.length;
		if (length <= cur)
			return dataArray;

		while (cur < length)
			cur *= 2;

		dataArray = new int[cur];
		return dataArray;
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

}
