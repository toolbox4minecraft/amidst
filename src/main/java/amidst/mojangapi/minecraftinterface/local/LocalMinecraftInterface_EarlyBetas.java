package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicObject;
import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.biome.Biome;

@ThreadSafe
public class LocalMinecraftInterface_EarlyBetas implements MinecraftInterface {

	private volatile SymbolicObject biomeGenerator;

	private final SymbolicClass worldClass;
	private final SymbolicClass dimensionClass;
	private final SymbolicClass biomeGeneratorClass;
	private final SymbolicClass biomeClass;
	private final RecognisedVersion recognisedVersion;
	
	private static final Map<Object, Integer> biomeIndexMap = new HashMap<Object, Integer>();	
	private int[] biomeData = null;

	LocalMinecraftInterface_EarlyBetas(SymbolicClass worldClass,
			SymbolicClass concrete_dimensionClass,
			SymbolicClass biomeGeneratorClass,
			SymbolicClass biomeClass, RecognisedVersion recognisedVersion) {
		
		this.worldClass = worldClass;
		this.dimensionClass = concrete_dimensionClass;
		this.biomeGeneratorClass = biomeGeneratorClass;
		this.biomeClass = biomeClass;
		this.recognisedVersion = recognisedVersion;
	}

	// @formatter:off
	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		try {
			if (biomeData == null || biomeData.length < width * height) {
				biomeData = new int[width * height];
			}
			
			int shift = useQuarterResolution ? 2 : 0;
			
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					SymbolicObject biome = (SymbolicObject) biomeGenerator.callMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOME, (x + i) << shift, (y + j) << shift);					
					biomeData[j * width + i] = getBiomeIndex(biome);
				}
			}
			return biomeData;
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}

	private int getBiomeIndex(SymbolicObject biome) throws IllegalArgumentException, IllegalAccessException, MinecraftInterfaceException {
		
		Integer result = LocalMinecraftInterface_EarlyBetas.biomeIndexMap.get(biome.getObject());
		
		if (result == null) {
			String biomeName = (String)biomeClass.getFieldValue(SymbolicNames.FIELD_BETA_BIOME_NAME, biome);

			Biome amidstBiome = Biome.getBetaBiomeByName(biomeName);
			if (amidstBiome != null) {
				result = amidstBiome.getIndex();
				LocalMinecraftInterface_EarlyBetas.biomeIndexMap.put(biome.getObject(), result);
				Log.i("Added " + biomeName + " to biomeIndexMap");				
			} else {
				Log.i("Failed to recognise biome " + biomeName);
				throw new MinecraftInterfaceException("Failed to recognise biome " + biomeName);
			}
		}
		return result.intValue();
	}
	
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		try {
			Log.i("Creating beta world with seed '" + seed);
			this.biomeGenerator = getBiomeGenerator(seed);
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}
	
	private SymbolicObject getBiomeGenerator(long seed) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Log.i("Creating world");

		Object dimension = dimensionClass.callDefaultConstructor();
		SymbolicObject symbolicDimension = new SymbolicObject(dimensionClass, dimension);		
		
		Object[] params;
		if (RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._b1_3b)) {		
			params = new Object[] {"level name", dimension, seed};
		} else {
			params = new Object[] {null, "level name", dimension, seed};
		}
		
		try {
			worldClass.callConstructor(SymbolicNames.CLASS_BETA_WORLD, params);
		} catch(NullPointerException | InvocationTargetException  ex) {
			// Bwahaha, too late! Before the World constructor raised this NullPointerException
			// (from trying to use a chunkLoader returned by NullSaveHandler instance, or by trying to get
			// a chunkLoader from a null SaveHandler, depending on what you passed as the first parameter
			// of the World constructor) it already assigned
			// its own seed field and called dimension.setWorld(this), so we have a reference to
			// a functional-enough World instance despite the constructor failing!
			// Plus, the Dimension instance has already used the partially constructed World instance
			// to create the BiomeGenerator, which is the whole goal of this, so World's work here is done.
			
			if (ex instanceof InvocationTargetException) {				
				if (((InvocationTargetException)ex).getTargetException() instanceof NullPointerException) {
					// If we pass a null for the SaveHandler parameter (which saves us having to 
					// identify the obfuscated ISaveHandle interface and NullSaveHandler class) then 
					// for some reason the NullPointerException bubbles up inside a InvocationTargetException, 
					// so here I confirm that the exception is still a NullPointerException.
				} else {
					throw ex;
				}
			}
			Log.i("Partial construction of early beta World instance complete.");
		}
		
		/* If we have the correct dimension then we can read the biome generator straight from
		 * that, but as of 1.6 we can't match the Overworld dimension so I'm instead matching 
		 * the End dimension, which would give us the wrong biome generator, so this is 
		 * commented out in favor of constructing the correct biome generator manually
		SymbolicObject biomeGenerator = (SymbolicObject) dimensionOverworldClass.getFieldValue(SymbolicNames.FIELD_BETA_DIMENSION_BIOMEGENERATOR, symbolicDimension);
		*/
		SymbolicObject symbolicWorld = (SymbolicObject) dimensionClass.getFieldValue(SymbolicNames.FIELD_BETA_DIMENSION_WORLD, symbolicDimension);
		SymbolicObject biomeGenerator = (SymbolicObject) biomeGeneratorClass.callConstructor(SymbolicNames.CLASS_BETA_BIOMEGENERATOR, symbolicWorld.getObject());
		
		Log.i("Created world!!");	
		return biomeGenerator;			
	}
	
	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}
}
