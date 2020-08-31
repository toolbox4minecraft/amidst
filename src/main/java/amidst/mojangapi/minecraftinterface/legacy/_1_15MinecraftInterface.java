package amidst.mojangapi.minecraftinterface.legacy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class _1_15MinecraftInterface implements MinecraftInterface {
    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._20w19a;

    private static final List<String> NETHER_BIOME_NAMES = Arrays.asList(
    	"nether_wastes",
    	"soul_sand_valley",
    	"crimson_forest",
    	"warped_forest",
    	"basalt_deltas"
    );

    private boolean isInitialized = false;
	private final RecognisedVersion recognisedVersion;

	private final SymbolicClass registryClass;
	private final SymbolicClass registryKeyClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass gameTypeClass;
	private final SymbolicClass worldSettingsClass;
	private final SymbolicClass worldDataClass;
	private final SymbolicClass noiseBiomeProviderClass;
	private final SymbolicClass overworldBiomeZoomerClass;
	private final SymbolicClass netherBiomeProviderClass;
	private final SymbolicClass netherBiomeSettingsClass;
	private final SymbolicClass utilClass;

	private MethodHandle registryGetIdMethod;
    private MethodHandle biomeProviderGetBiomeMethod;
    private MethodHandle biomeZoomerGetBiomeMethod;

	private Object biomeRegistry;
	private Object biomeProviderRegistry;

    /**
     * An array used to return biome data
     */
    private final ArrayCache<int[]> dataArray = ArrayCache.makeIntArrayCache(256);

	public _1_15MinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this.recognisedVersion = recognisedVersion;
		this.registryClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_REGISTRY);
        this.registryKeyClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_REGISTRY_KEY);
        this.worldTypeClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_WORLD_TYPE);
        this.gameTypeClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_GAME_TYPE);
        this.worldSettingsClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_WORLD_SETTINGS);
        this.worldDataClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_WORLD_DATA);
        this.noiseBiomeProviderClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_NOISE_BIOME_PROVIDER);
        this.overworldBiomeZoomerClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_OVERWORLD_BIOME_ZOOMER);
        this.netherBiomeProviderClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_NETHER_BIOME_PROVIDER);
        this.netherBiomeSettingsClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_NETHER_BIOME_SETTINGS);
        this.utilClass = symbolicClassMap.get(_1_15SymbolicNames.CLASS_UTIL);
	}

	@Override
	public synchronized MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
	    initializeIfNeeded();

	    try {
	        Object overworldBiomeProvider = createBiomeProviderObject(seed, worldType, generatorOptions);
	        Object netherBiomeProvider = manuallyCreateNetherBiomeProvider(seed);
	        Object biomeZoomer = overworldBiomeZoomerClass.getClazz().getEnumConstants()[0];
            long seedForBiomeZoomer = makeSeedForBiomeZoomer(seed);
            return new World(overworldBiomeProvider, netherBiomeProvider, biomeZoomer, seedForBiomeZoomer);

        } catch(IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new MinecraftInterfaceException("unable to create world", e);
        }
	}

	private static long makeSeedForBiomeZoomer(long seed) throws MinecraftInterfaceException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
			buf.putLong(seed);
			byte[] bytes = digest.digest(buf.array());

			long result = 0;
			for (int i = 0; i < 8; i++) {
				result |= (bytes[i] & 0xffL) << (i*8L);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new MinecraftInterfaceException("unable to hash seed", e);
		}
	}

	private Object createWorldDataObject(long seed, WorldType worldType, String generatorOptions)
	        throws IllegalArgumentException, IllegalAccessException, InstantiationException,
	            InvocationTargetException, MinecraftInterfaceException {
        if (!generatorOptions.isEmpty()) {
            AmidstLogger.warn("Custom generator options aren't supported in this version");
        }

        if (!worldSettingsClass.hasConstructor(_1_15SymbolicNames.CONSTRUCTOR_WORLD_SETTINGS)) {
            throw new MinecraftInterfaceException("unable to create world settings");
        }

	    // We don't care which GameType we pick
	    Object gameType = gameTypeClass.getClazz().getEnumConstants()[0];

	    SymbolicObject worldSettings = worldSettingsClass.callConstructor(_1_15SymbolicNames.CONSTRUCTOR_WORLD_SETTINGS,
	            seed, gameType, false, false, getWorldTypeObject(worldType));

	    SymbolicObject worldData;
	    if(worldDataClass.hasConstructor(_1_15SymbolicNames.CONSTRUCTOR_WORLD_DATA)) {
	    	worldData = worldDataClass.callConstructor(_1_15SymbolicNames.CONSTRUCTOR_WORLD_DATA,
	            worldSettings.getObject(), "<amidst-world>");
	    } else {
	    	worldData = worldDataClass.callConstructor(_1_15SymbolicNames.CONSTRUCTOR_WORLD_DATA,
	    		worldSettings.getObject());
	    }


        return worldData.getObject();
	}

	private Object createBiomeProviderObject(long seed, WorldType worldType, String generatorOptions)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException,
                    InvocationTargetException, MinecraftInterfaceException {
	    Object providerType = getFromRegistryByKey(biomeProviderRegistry, "vanilla_layered");

	    /*
	     * The BiomeProviderType class is hard to properly detect with the ClassTranslator, so
	     * we prefer working with it directly with the Java reflection API.
	     */
	    Method createMethod = null; // BiomeProvider create(BiomeProviderSettings settings)
	    Method createSettingsMethod = null; // BiomeProviderSettings createSettings(WorldData world)
	    Method createSettingsWithSeedMethod = null;  // BiomeProviderSettings createSettings(long seed)
	    for (Method meth: providerType.getClass().getDeclaredMethods()) {
	        if (!meth.isSynthetic() && meth.getParameterCount() == 1) {
	            Class<?> param = meth.getParameterTypes()[0];
	            if(param.equals(worldDataClass.getClazz())) {
	                createSettingsMethod = meth;
	            } else if(param.equals(Long.TYPE)) {
	                createSettingsWithSeedMethod = meth;
	            } else if (noiseBiomeProviderClass.getClazz().isAssignableFrom(meth.getReturnType())) {
	                createMethod = meth;
	            }
	        }
	    }

        Object providerSettings;
        if (createSettingsMethod != null) {
    	    Object worldData = createWorldDataObject(seed, worldType, generatorOptions);
    	    providerSettings = createSettingsMethod.invoke(providerType, worldData);
        } else if (createSettingsWithSeedMethod != null) {
            providerSettings = createSettingsWithSeedMethod.invoke(providerType, seed);
            populateBiomeProviderSettings(providerSettings, worldType, generatorOptions);
        } else {
            throw new MinecraftInterfaceException("unable to create biome provider settings");
        }
        return createMethod.invoke(providerType, providerSettings);
	}

	private void populateBiomeProviderSettings(Object providerSettings, WorldType worldType, String generatorOptions)
			throws IllegalArgumentException, IllegalAccessException, MinecraftInterfaceException {
        if (!generatorOptions.isEmpty()) {
        	// TODO: fix me
            AmidstLogger.warn("Custom generator options aren't supported in this version");
        }
        Object worldTypeObj = getWorldTypeObject(worldType);

        /*
         * We don't want to depend on a specific class, so we manually
         * set the WorldType field with reflection.
         */
        boolean worldTypeSet = false;
        for (Field field: providerSettings.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(worldTypeObj.getClass())) {
                field.setAccessible(true);
                field.set(providerSettings, worldTypeObj);
                worldTypeSet = true;
                break;
            }
        }

        if (!worldTypeSet) {
            throw new MinecraftInterfaceException("unable to populate biome provider settings");
        }
	}

	private Object getWorldTypeObject(WorldType worldType) throws IllegalArgumentException, IllegalAccessException {
		SymbolicObject worldTypeObj = (SymbolicObject) worldTypeClass
	            .getStaticFieldValue(worldType.getSymbolicFieldName());
		return worldTypeObj.getObject();
	}

	private Object manuallyCreateNetherBiomeProvider(long seed)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, MinecraftInterfaceException {
		// Minecraft doesn't give us an easy way to create the Nether biome provider, so we do it ourselves.
		if (netherBiomeProviderClass == null) {
			return null;
		}

		SymbolicObject biomeSettings = netherBiomeSettingsClass.callConstructor(
			_1_15SymbolicNames.CONSTRUCTOR_NETHER_BIOME_SETTINGS, seed);

		if (netherBiomeSettingsClass.hasMethod(_1_15SymbolicNames.METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES1)) {
			Set<Object> biomes = new HashSet<>();
			addNetherBiomesToCollection(biomes);
			biomeSettings.callMethod(_1_15SymbolicNames.METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES1, biomes);
		} else if (netherBiomeSettingsClass.hasMethod(_1_15SymbolicNames.METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES2)) {
			List<Object> biomes = new ArrayList<>();
			addNetherBiomesToCollection(biomes);
			biomeSettings.callMethod(_1_15SymbolicNames.METHOD_NETHER_BIOME_SETTINGS_SET_BIOMES2, biomes);
		} else {
			throw new MinecraftInterfaceException("couldn't create nether biomes");
		}

		return netherBiomeProviderClass.callConstructor(
			_1_15SymbolicNames.CONSTRUCTOR_NETHER_BIOME_PROVIDER, biomeSettings.getObject()).getObject();
	}

	private void addNetherBiomesToCollection(Collection<Object> biomes)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String biomeName: NETHER_BIOME_NAMES) {
			Object biome = getFromRegistryByKey(biomeRegistry, biomeName);
			if (biome != null) {
				biomes.add(biome);
			}
		}
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	private synchronized void initializeIfNeeded() throws MinecraftInterfaceException {
	    if (isInitialized) {
	        return;
	    }

	    try {
	        Object metaRegistry = ((SymbolicObject) registryClass
	                .getStaticFieldValue(_1_15SymbolicNames.FIELD_REGISTRY_META_REGISTRY)).getObject();

	        stopAllExecutors();

            biomeRegistry = Objects.requireNonNull(getFromRegistryByKey(metaRegistry, "biome"));
            biomeProviderRegistry = Objects.requireNonNull(getFromRegistryByKey(metaRegistry, "biome_source_type"));

            registryGetIdMethod = ReflectionUtils.getMethodHandle(registryClass, _1_15SymbolicNames.METHOD_REGISTRY_GET_ID);
            biomeProviderGetBiomeMethod = ReflectionUtils.getMethodHandle(noiseBiomeProviderClass, _1_15SymbolicNames.METHOD_NOISE_BIOME_PROVIDER_GET_BIOME);
            biomeZoomerGetBiomeMethod = ReflectionUtils.getMethodHandle(overworldBiomeZoomerClass, _1_15SymbolicNames.METHOD_BIOME_ZOOMER_GET_BIOME);
        } catch(IllegalArgumentException | IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            throw new MinecraftInterfaceException("unable to initialize the MinecraftInterface", e);
        }

	    isInitialized = true;
	}

	private void stopAllExecutors() throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = utilClass.getClazz();
		for (Field field: clazz.getDeclaredFields()) {
			if ((field.getModifiers() & Modifier.STATIC) > 0 && field.getType().equals(ExecutorService.class)) {
				field.setAccessible(true);
				ExecutorService exec = (ExecutorService) field.get(null);
				exec.shutdownNow();
			}
		}
	}

	private Object getFromRegistryByKey(Object registry, String key)
	        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	    Object registryKey = registryKeyClass
                .callConstructor(_1_15SymbolicNames.CONSTRUCTOR_REGISTRY_KEY, key)
                .getObject();

	    Method getByKey = registryClass.getMethod(_1_15SymbolicNames.METHOD_REGISTRY_GET_BY_KEY).getRawMethod();
	    return getByKey.invoke(registry, registryKey);
	}

	private class World implements MinecraftInterface.World {
		/**
		 * A BiomeProvider instance for the current overworld, giving
		 * access to the quarter-scale biome data.
		 */
	    private final Object overworldBiomeProvider;
	    /**
	     * A Biome provider instance for the current nether.
	     */
	    private final Object netherBiomeProvider;
	    /**
	     * The BiomeZoomer instance for the current world, which
	     * interpolates the quarter-scale BiomeProvider to give
	     * full-scale biome data.
	     */
	    private final Object biomeZoomer;
	    /**
	     * The seed used by the BiomeZoomer during interpolation.
	     * It is derived from the world seed.
	     */
		private final long seedForBiomeZoomer;

		private final Set<Dimension> supportedDimensions;

	    private World(Object overworldBiomeProvider, Object netherBiomeProvider, Object biomeZoomer, long seedForBiomeZoomer) {
	    	this.overworldBiomeProvider = overworldBiomeProvider;
	    	this.netherBiomeProvider = netherBiomeProvider;
	    	this.biomeZoomer = biomeZoomer;
	    	this.seedForBiomeZoomer = seedForBiomeZoomer;

	    	Set<Dimension> supportedDimensions = EnumSet.of(Dimension.OVERWORLD);
	    	if (netherBiomeProvider != null) {
	    		supportedDimensions.add(Dimension.NETHER);
	    	}
	    	this.supportedDimensions = Collections.unmodifiableSet(supportedDimensions);
	    }

		@Override
		public<T> T getBiomeData(
				Dimension dimension,
				int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper)
				throws MinecraftInterfaceException {
			Object biomeProvider;
			int biomeHeight;

			switch (dimension) {
			case OVERWORLD:
				biomeProvider = this.overworldBiomeProvider;
				biomeHeight = 0; // The overworld uses y=0 for all heights
				break;
			case NETHER:
				if (this.netherBiomeProvider != null) {
					biomeProvider = this.netherBiomeProvider;
					biomeHeight = 63; // Pick an arbitrary value
					break;
				}
			default:
				throw new UnsupportedDimensionException(dimension);
			}

			int size = width * height;
		    return dataArray.withArrayFaillible(size, data -> {
			    try {
			    	if(size == 1) {
			    		data[0] = getBiomeIdAt(biomeProvider, biomeHeight, x, y, useQuarterResolution);
			    		return biomeDataMapper.apply(data);
			    	}

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
		                            data[trueIdx] = getBiomeIdAt(biomeProvider, biomeHeight, x + x0 + i, y + y0 + j, useQuarterResolution);
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

		private int getBiomeIdAt(Object biomeProvider, int biomeHeight, int x, int y, boolean useQuarterResolution) throws Throwable {
		    Object biome;
		    if(useQuarterResolution) {
		        biome = biomeProviderGetBiomeMethod.invokeExact(biomeProvider, x, biomeHeight, y);
		    } else {
		        biome = biomeZoomerGetBiomeMethod.invokeExact(biomeZoomer, seedForBiomeZoomer, x, biomeHeight, y, biomeProvider);
		    }
		    return (int) registryGetIdMethod.invokeExact(biomeRegistry, biome);
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return supportedDimensions;
		}
	}
}
