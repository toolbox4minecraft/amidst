package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.LongFunction;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicMethod;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public class LocalMinecraftInterface implements MinecraftInterface {

    private boolean isInitialized = false;
	private final RecognisedVersion recognisedVersion;

	private final SymbolicClass registryClass;
	private final SymbolicClass worldTypeClass;
	private final SymbolicClass genSettingsClass;
	private final SymbolicClass levelDataClass;
	private final SymbolicClass layersClass;
	private final SymbolicClass lazyAreaClass;
	private final SymbolicClass lazyAreaContextClass;
	private final SymbolicClass fuzzyOffsetBiomeZoomerClass;

    /**
     * A PixelTransformer instance for the current world, giving direct
     * access to the quarter-scale biome data.
     */
    private SymbolicObject pixelTransformer;
    
    /**
     * A method used to retrieve the full resolution biome data.
     * We create a SymbolicMethod for it so we dont lose performance
     * searching the SymbolicClass for it every time it's called.
     */
    private SymbolicMethod getFiddleDistanceMethod;
    
    /**
     * The seed used by the FuzzyOffsetBiomeZoomer during interpolation.
     * It is derived from the world seed.
     */
	private long seedForBiomeZoomer;

    /**
     * An array used to return biome data
     */
    private volatile int[] dataArray = new int[256];

	public LocalMinecraftInterface(Map<String, SymbolicClass> symbolicClassMap, RecognisedVersion recognisedVersion) {
		this.recognisedVersion = recognisedVersion;
		this.registryClass = symbolicClassMap.get(SymbolicNames.CLASS_REGISTRY);
        this.worldTypeClass = symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE);
        this.genSettingsClass = symbolicClassMap.get(SymbolicNames.CLASS_GEN_SETTINGS);
        this.levelDataClass = symbolicClassMap.get(SymbolicNames.CLASS_LEVEL_DATA);
        this.layersClass = symbolicClassMap.get(SymbolicNames.CLASS_LAYERS);
        this.lazyAreaClass = symbolicClassMap.get(SymbolicNames.CLASS_LAZY_AREA);
        this.lazyAreaContextClass = symbolicClassMap.get(SymbolicNames.CLASS_LAZY_AREA_CONTEXT);
        this.fuzzyOffsetBiomeZoomerClass = symbolicClassMap.get(SymbolicNames.CLASS_FUZZY_OFFSET_BIOME_ZOOMER);
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
	    if (!isInitialized) {
	        throw new MinecraftInterfaceException("no world was created");
	    }

	    int[] data = ensureArrayCapacity(width * height);

	    try {
	    	/*
	    	 *	Re-implemented from older updates where this would speed
	    	 *	up the process due to they way LazyAreas worked. This
	    	 *	helps here quite a bit, giving a signifigant performance
	    	 *	boost.
	    	 *
	    	 *	OLD DESCRIPTION: 
	    	 *	We break the region in 16x16 chunks, to get better performance
			 * 	out of the LazyArea used by the game. Sadly, we get no
			 * 	performance gain in 18w16a and newer, but in previous snapshots
			 * 	we get a ~1.5x improvement.
	    	 * */
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
	    } catch (Throwable e) {
	        throw new MinecraftInterfaceException("unable to get biome data", e);
	    }

	    return data;
	}

	private int getBiomeIdAt(int x, int y, boolean useQuarterResolution) throws Throwable {
	    if(useQuarterResolution) {
	    	return (int) pixelTransformer.callMethod(SymbolicNames.METHOD_PIXEL_TRANSFORMER_APPLY, x, y);
	    } else {
	    	return getFullResolutionBiome(x, y);
	    }
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		initializeIfNeeded();
		
	    try {
            seedForBiomeZoomer = (long) levelDataClass.callStaticMethod(SymbolicNames.METHOD_LEVEL_DATA_MAP_SEED, seed);
            pixelTransformer = createPixelTransformerObject(seed, worldType);
            getFiddleDistanceMethod = fuzzyOffsetBiomeZoomerClass.getMethod(SymbolicNames.METHOD_FUZZY_OFFSET_BIOME_ZOOMER_GET_FIDDLE_DISTANCE);
            
        } catch(IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new MinecraftInterfaceException("unable to create world", e);
        }
	}
	
	private void initializeIfNeeded() throws MinecraftInterfaceException {
	    if (isInitialized) {
	        return;
	    }
	    try {
	    	registryClass.getStaticFieldValue(SymbolicNames.FIELD_REGISTRY_META_REGISTRY);
        } catch(IllegalArgumentException | IllegalAccessException e) {
            throw new MinecraftInterfaceException("unable to initialize the MinecraftInterface", e);
        }

	    isInitialized = true;
	}
	
	
	private SymbolicObject createPixelTransformerObject(long seed, WorldType worldType)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Object worldTypeObj = ((SymbolicObject) worldTypeClass.getStaticFieldValue(worldType.getSymbolicFieldName())).getObject();
		
		Object genSettingsObj = ((SymbolicObject) genSettingsClass.callConstructor(SymbolicNames.CONSTRUCTOR_GEN_SETTINGS)).getObject();
		
		SymbolicObject areaFactoryObj = (SymbolicObject) layersClass.callStaticMethod(
				SymbolicNames.METHOD_LAYERS_GET_DEFAULT_LAYER,
				worldTypeObj,
				genSettingsObj,
				(LongFunction<?>)l -> {
					try {
						return lazyAreaContextClass.callConstructor(
								SymbolicNames.CONSTRUCTOR_LAZY_AREA_CONTEXT, 25, seed, l
								).getObject();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
						return null;
					}
				}
				);
		
		SymbolicObject lazyAreaObj = new SymbolicObject(lazyAreaClass, areaFactoryObj.callMethod(SymbolicNames.METHOD_AREA_FACTORY_MAKE));
		
		return (SymbolicObject) lazyAreaObj.getFieldValue(SymbolicNames.FIELD_LAZY_AREA_PIXEL_TRANSFORMER);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
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
    
    private int getFullResolutionBiome(int x, int y) throws Throwable {
        int var7 = x - 2;
        int var8 = 0 - 2;
        int var9 = y - 2;
        int var10 = var7 >> 2;
        int var11 = var8 >> 2;
        int var12 = var9 >> 2;
        double var13 = (double)(var7 & 3) / 4.0D;
        double var15 = (double)(var8 & 3) / 4.0D;
        double var17 = (double)(var9 & 3) / 4.0D;
        double[] var19 = new double[8];

        for(int var20 = 0; var20 < 8; ++var20) {
           boolean var21 = (var20 & 4) == 0;
           boolean var22 = (var20 & 2) == 0;
           boolean var23 = (var20 & 1) == 0;
           int var24 = var21 ? var10 : var10 + 1;
           int var25 = var22 ? var11 : var11 + 1;
           int var26 = var23 ? var12 : var12 + 1;
           double var27 = var21 ? var13 : var13 - 1.0D;
           double var29 = var22 ? var15 : var15 - 1.0D;
           double var31 = var23 ? var17 : var17 - 1.0D;
           var19[var20] = (double) getFiddleDistanceMethod.callStatic(seedForBiomeZoomer, var24, var25, var26, var27, var29, var31);
        }

        int var33 = 0;
        double var34 = var19[0];

        for(int var35 = 1; var35 < 8; ++var35) {
           if (var34 > var19[var35]) {
              var33 = var35;
              var34 = var19[var35];
           }
        }

        int var36 = (var33 & 4) == 0 ? var10 : var10 + 1;
        int var38 = (var33 & 1) == 0 ? var12 : var12 + 1;
        return getBiomeIdAt(var36, var38, true);
    }
}
