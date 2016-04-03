package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

	private volatile SymbolicObject overworldDimension;
	private volatile SymbolicObject world;
	private volatile SymbolicObject biomeGenerator;
	private volatile SymbolicObject chunkGenerator;
	private volatile Random chunkGeneratorPRNG;

	private final SymbolicClass worldClass;
	private final SymbolicClass dimensionClass;
	private final SymbolicClass biomeGeneratorClass;
	private final SymbolicClass chunkGeneratorClass;
	private final SymbolicClass biomeClass;
	private final RecognisedVersion recognisedVersion;
	
	private static final Map<Object, Integer> biomeIndexMap = new HashMap<Object, Integer>();	
	private int[] biomeData = null;
	private byte[] chunkData = new byte[32768];

	LocalMinecraftInterface_EarlyBetas(SymbolicClass worldClass,
			SymbolicClass concrete_dimensionClass,
			SymbolicClass biomeGeneratorClass, SymbolicClass chunkGeneratorClass,
			SymbolicClass biomeClass, RecognisedVersion recognisedVersion) {
		
		this.worldClass = worldClass;
		this.dimensionClass = concrete_dimensionClass;
		this.biomeGeneratorClass = biomeGeneratorClass;
		this.chunkGeneratorClass = chunkGeneratorClass;
		this.biomeClass = biomeClass;
		this.recognisedVersion = recognisedVersion;
	}

	
	private SymbolicObject getBiomeAt(int x, int y) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		SymbolicObject biome = (SymbolicObject) biomeGenerator.callMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOME, x, y);
		return biome;
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
	
	
	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		try {
			if (biomeData == null || biomeData.length < width * height) {
				biomeData = new int[width * height];
			}
			
			int shift = useQuarterResolution ? 2 : 0;
			
			//getBiomeAtChunk(x >> 4, y >> 4);
			
			// Todo: When oceans are working, this stuff will be calculated a chunk at a time, instead of
			// a pixel at a time:
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					biomeData[j * width + i] = getBiomeIndex(getBiomeAt((x + i) << shift, (y + j) << shift));
				}
			}
			return biomeData;
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
	}
	
	
	private int[] getBiomeAtChunk(int chunk_x, int chunk_z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		// This method is highly experimental code under development.
		// You don't need to call this. It doesn't work yet.
		//
		// (If calls can be made successfully to METHOD_BETA_CHUNKGENERATOR_PREPARECHUNK
		// then we should be able to construct the oceans from the returned blockArray, 
		// which will only be filled in at quarter resolution, but that's all we need).
		
		/*
		RandomLevelSource.prepareChunk(
			x_chunk, 
			z_chunk, 
			array of byte[32768], accessed by (x << 11 | z << 7 | y) where x an z are 4 bit values and y is 7 bits 
			array of Biome[256], access by (x + z * 16). Not used in this call though - Created by calling biomeGenerator.getBiomes(
				this.v, 
				x_chunk * 16, 
				z_chunk * 16, 
				16, 
				16
			), 
			BiomeGenerator.temperatureLayer - the first double[] field of the BiomeGenerator class, cached from the call to getBiomes
		);*/
		
		Object[] params = new Object[] {null, chunk_x * 16, chunk_z * 16, 16, 16};
		
		Object biomesArray = biomeGenerator.callMethod(
			SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOMES_ARRAY,
			params
		);
		
		// TODO: The temperatureArray was already generated during our call to GET_BIOMES_ARRAY,
		// so for extra speed we can pluck it out of the instance's field instead of regenerating it here.
		Object temperatureArray = biomeGenerator.callMethod(
			SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_TEMPERATURE_ARRAY,
			params
		);

		chunkGeneratorPRNG.setSeed(chunk_x * 341873128712L + chunk_z * 132897987541L);

		Object blockArray = chunkGenerator.callMethod(
			SymbolicNames.METHOD_BETA_CHUNKGENERATOR_PREPARECHUNK,
			chunk_x,
			chunk_z,
			chunkData,
			biomesArray,
			temperatureArray			
		);
		
		Log.i("byte 0: " + chunkData[0]);
		
		return null;		
	}
	
	
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		try {
			Log.i("Creating beta world with seed... '" + seed);
			
			this.overworldDimension = construct_OverworldDimension();
			this.world              = construct_World(overworldDimension, seed);
			this.biomeGenerator     = construct_BiomeGenerator(world);                  // Use if you have NOT found a way to get the correct dimension subclass in b1.6			
			//this.biomeGenerator     = getDimensionBiomeGenerator(overworldDimension); // Use if you HAVE found a way to get the correct dimension subclass in b1.6			
			this.chunkGenerator     = construct_ChunkGenerator(world, seed);
			this.chunkGeneratorPRNG = getChunkGeneratorPRNG(chunkGenerator);
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to create world", e);
		}
	}

	
	private SymbolicObject construct_OverworldDimension() 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		Object dimension = dimensionClass.callDefaultConstructor();
		SymbolicObject symbolicDimension = new SymbolicObject(dimensionClass, dimension);		
				
		Log.i("  Overworld Dimension constructed");
		return symbolicDimension;
	}
	
	
	private SymbolicObject construct_World(SymbolicObject overworldDimension, long seed) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		Object[] params;
		if (RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._b1_3b)) {		
			params = new Object[] {"level name", overworldDimension.getObject(), seed};
		} else {
			params = new Object[] {null, "level name", overworldDimension.getObject(), seed};
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
			Log.i("  Partial construction of early beta World instance complete.");
		}
		SymbolicObject symbolicWorld = (SymbolicObject) dimensionClass.getFieldValue(SymbolicNames.FIELD_BETA_DIMENSION_WORLD, overworldDimension);
					
		//Log.i("  World constructed");
		return symbolicWorld;
	}
	
	
	private SymbolicObject construct_BiomeGenerator(SymbolicObject world) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		// If the world was constructed with the correct dimension then we can read the biome 
		// generator straight from that, but as of 1.6 we can't match the Overworld dimension so 
		// I'm instead matching the End dimension, which would give us the wrong biome generator, so 
		// this method constructs the correct biome generator separately (until I can find a way to 
		// get the right dimension).
		SymbolicObject biomeGenerator = (SymbolicObject) biomeGeneratorClass.callConstructor(SymbolicNames.CLASS_BETA_BIOMEGENERATOR, world.getObject());
		
		Log.i("  BiomeGenerator constructed");		
		return biomeGenerator;
	}
	
	private SymbolicObject getDimensionBiomeGenerator(SymbolicObject dimension) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		// If the world was constructed with the correct dimension then we can read the biome 
		// generator straight from that, but as of 1.6 we can't match the Overworld dimension so 
		// I'm instead matching the End dimension, which would give us the wrong biome generator, so 
		// construct_BiomeGenerator() is used instead of this method.
		SymbolicObject biomeGenerator = (SymbolicObject) dimensionClass.getFieldValue(SymbolicNames.FIELD_BETA_DIMENSION_BIOMEGENERATOR, dimension);
		
		Log.i("  BiomeGenerator constructed");		
		return biomeGenerator;
	}
	
	
	private SymbolicObject construct_ChunkGenerator(SymbolicObject world, long seed) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		// The correct seed isn't technically needed here as we will re-seed the PRNG before each chunk		
		SymbolicObject chunkGenerator = chunkGeneratorClass.callConstructor(SymbolicNames.CLASS_BETA_CHUNKGENERATOR, null, seed);

		Log.i("  ChunkGenerator constructed");			
		return chunkGenerator;
	}

	
	private Random getChunkGeneratorPRNG(SymbolicObject chunkGenerator) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Object symbolicPRNG = chunkGeneratorClass.getFieldValue(SymbolicNames.FIELD_BETA_CHUNKGENERATOR_PRGN, chunkGenerator);
		
		if (symbolicPRNG instanceof Random) {
			Log.i("  ChunkGenerator PRNG hooked");						
			return (Random)symbolicPRNG;
		} else {
			throw new NoSuchFieldError("The returned class (" + symbolicPRNG.toString() + " was not of type Random");
		}
	}
		
	
	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}
}
