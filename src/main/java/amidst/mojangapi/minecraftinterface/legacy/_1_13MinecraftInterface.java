package amidst.mojangapi.minecraftinterface.legacy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.local.SymbolicNames;
import amidst.mojangapi.world.WorldType;

import static amidst.mojangapi.minecraftinterface.legacy._1_13SymbolicNames.*;

public class _1_13MinecraftInterface implements MinecraftInterface {
    public static final RecognisedVersion LAST_COMPATIBLE_VERSION = RecognisedVersion._19w35a;

	private boolean isInitialized = false;
	private final RecognisedVersion recognisedVersion;
	
	private final SymbolicClass bootstrapClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass genSettingsClass;
	private final SymbolicClass layerUtilClass;
	private final SymbolicClass genLayerClass;
	private final SymbolicClass lazyAreaClass;
	private final SymbolicClass pixelTransformerClass;
	private final SymbolicClass areaDimensionClass;
	private final SymbolicClass utilClass;

	/**
	 * Two PixelTransformer instances for the current world that are given
	 * to each thread, giving direct access to the quarter and full scale
	 * biome data.
	 */
	private ThreadLocal<Object[]> threadedPixelTransformers;

	/**
	 * A method used to retrieve the biome data from a PixelTransformer.
	 * We create a SymbolicMethod for it so we dont lose performance
	 * searching the SymbolicClass for it every time it's called.
	 */
	private Method getBiomesMethod;

	public _1_13MinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this.recognisedVersion = recognisedVersion;
		this.bootstrapClass = symbolicClassMap.get(CLASS_BOOTSTRAP);
		this.worldTypeClass = symbolicClassMap.get(CLASS_WORLD_TYPE);
		this.genSettingsClass = symbolicClassMap.get(CLASS_GEN_SETTINGS);
		this.layerUtilClass = symbolicClassMap.get(CLASS_LAYER_UTIL);
		this.genLayerClass = symbolicClassMap.get(CLASS_GEN_LAYER);
		this.lazyAreaClass = symbolicClassMap.get(CLASS_LAZY_AREA);
		this.pixelTransformerClass = symbolicClassMap.get(CLASS_PIXEL_TRANSFORMER);
		this.areaDimensionClass = symbolicClassMap.get(CLASS_AREA_DIMENSION);
		this.utilClass = symbolicClassMap.get(CLASS_UTIL);
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
		} catch (Throwable e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}

		return data;
	}

	private int getBiomeIdAt(int x, int y, boolean useQuarterResolution) throws Throwable {
		if(useQuarterResolution) {
			return (int) getBiomesMethod.invoke(threadedPixelTransformers.get()[0], x, y);
		} else {
			return(int) getBiomesMethod.invoke(threadedPixelTransformers.get()[1], x, y);
		}
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		initializeIfNeeded();

		try {
			threadedPixelTransformers = ThreadLocal.withInitial(() -> {
				try {
					return createPixelTransformersArray(seed, worldType);
				} catch (Exception e) {
					MinecraftInterfaceException e1 = new MinecraftInterfaceException("unable to create pixel transformer", e);
					AmidstLogger.error(e1);
					System.exit(2);
					return null;
				}
			});

		} catch(IllegalArgumentException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	private void initializeIfNeeded() throws MinecraftInterfaceException {
		if (isInitialized) {
			return;
		}
		try {
			getBiomesMethod = pixelTransformerClass.getMethod(METHOD_PIXEL_TRANSFORMER_APPLY).getRawMethod();
			callBootstrapRegister();
			
			try {
				((ExecutorService) utilClass.getStaticFieldValue(SymbolicNames.FIELD_UTIL_SERVER_EXECUTOR)).shutdownNow();
			} catch (NullPointerException e) {
				AmidstLogger.warn("Unable to shut down Server-Worker threads");
			}

		} catch(IllegalArgumentException
				| IllegalAccessException
				| InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to initialize the MinecraftInterface", e);
		}

		isInitialized = true;
	}

	private Object[] createPixelTransformersArray(long seed, WorldType worldType)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Object worldTypeObj = worldTypeClass.getField(worldType.getSymbolicFieldName()).getRawField().get(null);
		
		Object genSettingsObj = genSettingsClass.getConstructor(CONSTRUCTOR_GEN_SETTINGS).getRawConstructor().newInstance(new Object[] {});
		
		Object[] genLayers = (Object[]) layerUtilClass.callStaticMethod(
								METHOD_LAYER_UTIL_GET_LAYERS,
								seed,
								worldTypeObj,
								genSettingsObj
							);
		
		Object[] pixelTransformers = new Object[2];
		
		if (RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._18w47b)) {
			SymbolicObject[] areaFactoryObjs = {
				(SymbolicObject) new SymbolicObject(genLayerClass, genLayers[0]).getFieldValue(FIELD_GEN_LAYER_LAZY_AREA_FACTORY),
				(SymbolicObject) new SymbolicObject(genLayerClass, genLayers[1]).getFieldValue(FIELD_GEN_LAYER_LAZY_AREA_FACTORY)
			};
			
			Object areaDimensionObj = areaDimensionClass.getConstructor(CONSTRUCTOR_AREA_DIMENSION).getRawConstructor().newInstance(0, 0, 0, 0);
			
			for (int i = 0; i < areaFactoryObjs.length; i++) {
				SymbolicObject lazyAreaObj = new SymbolicObject(lazyAreaClass, areaFactoryObjs[i].callMethod(METHOD_AREA_FACTORY_MAKE, areaDimensionObj));
				pixelTransformers[i] = ((SymbolicObject) lazyAreaClass.getFieldValue(FIELD_LAZY_AREA_PIXEL_TRANSFORMER, lazyAreaObj)).getObject();
			}
			
		} else {
			SymbolicObject[] lazyAreaObjs = {
				(SymbolicObject) new SymbolicObject(genLayerClass, genLayers[0]).getFieldValue(FIELD_GEN_LAYER_LAZY_AREA),
				(SymbolicObject) new SymbolicObject(genLayerClass, genLayers[1]).getFieldValue(FIELD_GEN_LAYER_LAZY_AREA)
			};
			
			for (int i = 0; i < lazyAreaObjs.length; i++) {
				pixelTransformers[i] = ((SymbolicObject) lazyAreaClass.getFieldValue(FIELD_LAZY_AREA_PIXEL_TRANSFORMER, lazyAreaObjs[i])).getObject();
			}
		}
		
		return pixelTransformers;
	}

	private void callBootstrapRegister()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER;
		if (RecognisedVersion.isNewer(recognisedVersion, RecognisedVersion._1_13_2)) {
			if (bootstrapClass.getMethod(_1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER3).hasModifiers(Modifier.PUBLIC)) {
				register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER3;
			} else if (bootstrapClass.getMethod(register).hasModifiers(Modifier.PRIVATE)) {
				register = _1_13SymbolicNames.METHOD_BOOTSTRAP_REGISTER2;
			}
		}
		
		bootstrapClass.callStaticMethod(register);
	}
	
	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

}
