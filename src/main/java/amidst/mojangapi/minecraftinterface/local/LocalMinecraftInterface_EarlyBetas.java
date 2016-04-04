package amidst.mojangapi.minecraftinterface.local;

import java.awt.Rectangle;
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

	/** The current system that obtains ocean data is too slow - every chunk gets partially generated */
	public static final boolean DISPLAY_OCEANS = false;
	
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
	private int[] biomeReturnData = null;
	private int[] biomeChunkData  = null;
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

	@SuppressWarnings("unused")
	private boolean useChunkGenerator() {
		// b1.6 makes the Dimension class abstract, with a DimensionOverworld subclass, but
		// we currently have no way to match DimensionOverworld, which means we use a DimensionEnd
		// as a stand-in instead - but that means the chunkGenerator will call generation functions
		// for the wrong dimension. So currently in b1.6+ we can't use the chunkGenerator.
		return DISPLAY_OCEANS && RecognisedVersion.isOlder(recognisedVersion, RecognisedVersion._b1_6_6);
	}
	
	/**
	 * @param useQuarterResolution
	 *            Versions of Minecraft from b1.8 onward calculates biomes at 
	 *            quarter-resolution, then noisily interpolates the biome-map 
	 *            up to 1:1 resolution when needed. We must emulate this here
	 *            even though this class is for early betas, and full resolution
	 *            isn't needed (no structures).
	 * 
	 *            When useQuarterResolution is true, the x, y, width, and height
	 *            paramaters must all correspond to a quarter of the Minecraft
	 *            block coordinates/sizes you wish to obtain the biome data for.
	 * 
	 *            Amidst displays the quarter-resolution biome map, however full
	 *            resolution is required to determine the position and nature of
	 *            structures, as the noisy interpolation can change which biome
	 *            a structure is located in (if the structure is located on a
	 *            biome boundary).
	 */
	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {

		try {
			if (biomeReturnData == null || biomeReturnData.length < width * height) {
				biomeReturnData = new int[width * height];
			}
		
			if (useChunkGenerator()) {
				// partially generate chunks at full 16x16x127 resolution - *very* slow, but provides oceans
				return getBiomeData_sloooooooooow(x, y, width, height, useQuarterResolution);
			} else {
			
				int shift = useQuarterResolution ? 2 : 0;							
				for (int j = 0; j < height; j++) {
					for (int i = 0; i < width; i++) {
						biomeReturnData[j * width + i] = getBiomeIndex(getBiomeAt((x + i) << shift, (y + j) << shift).getObject());
					}
				}
				return biomeReturnData;
			}
		
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MinecraftInterfaceException("unable to get biome data", e);
		}
		
	}
		
	private SymbolicObject getBiomeAt(int x, int y) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		SymbolicObject biome = (SymbolicObject) biomeGenerator.callMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOME, x, y);
		return biome;
	}
	
	private int getBiomeIndex(Object biome_NOT_symbolic) throws IllegalArgumentException, IllegalAccessException, MinecraftInterfaceException {
		
		Integer result = LocalMinecraftInterface_EarlyBetas.biomeIndexMap.get(biome_NOT_symbolic);
		
		if (result == null) {
			SymbolicObject symbolicBiome = new SymbolicObject(biomeClass, biome_NOT_symbolic);
			String biomeName = (String)biomeClass.getFieldValue(SymbolicNames.FIELD_BETA_BIOME_NAME, symbolicBiome);

			Biome amidstBiome = Biome.getBetaBiomeByName(biomeName);
			if (amidstBiome != null) {
				result = amidstBiome.getIndex();
				LocalMinecraftInterface_EarlyBetas.biomeIndexMap.put(biome_NOT_symbolic, result);
				Log.i("Added " + biomeName + " to biomeIndexMap");				
			} else {
				Log.i("Failed to recognise biome " + biomeName);
				throw new MinecraftInterfaceException("Failed to recognise biome " + biomeName);
			}
		}
		return result.intValue();
	}

		
	/** 
	 * Gets biome data including oceans and frozen oceans, by having minecraft generate the full
	 * resolution FULL HEIGHT 3D noise array. This method is too slow - we need to find a way to 
	 * generate only the slice of noise at height 63.
	 * This method also requires that getDimensionBiomeGenerator() was called instead of 
	 * construct_BiomeGenerator()
	 * 
	 * suffix _bl means the value is in BLock coordinates
	 * suffix _ch means the value is in CHunk coordinates
	 */
	public synchronized int[] getBiomeData_sloooooooooow(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (biomeReturnData == null || biomeReturnData.length < width * height) {
			biomeReturnData = new int[width * height];
		}
		
		int shift = useQuarterResolution ? 2 : 0;			
		Rectangle destArea_bl = new Rectangle(x << shift, y << shift, width << shift, height << shift);
		
		// Expand destArea so that it runs along chunk boundaries
		Rectangle chunkArea_bl = new Rectangle(
				destArea_bl.x & 0xFFFFFFF0,
				destArea_bl.y & 0xFFFFFFF0,
				(destArea_bl.width  & 0xF) == 0 ? destArea_bl.width  : ((destArea_bl.width  >> 4) + 1) << 4,
				(destArea_bl.height & 0xF) == 0 ? destArea_bl.height : ((destArea_bl.height >> 4) + 1) << 4
		);				
		int chunksWide = chunkArea_bl.width  >> 4;
		int chunksHigh = chunkArea_bl.height >> 4;
		
		for(int yChunk_ch = 0; yChunk_ch < chunksHigh; yChunk_ch++) {
			for(int xChunk_ch = 0; xChunk_ch < chunksWide; xChunk_ch++) {
				
				int[] chunkData = getBiomeAtChunk((chunkArea_bl.x >> 4) + xChunk_ch, (chunkArea_bl.y >> 4) + yChunk_ch);
				copyChunkDataTo2dMapArray(
						chunkData,
						chunkArea_bl.x + (xChunk_ch << 4), 
						chunkArea_bl.y + (yChunk_ch << 4),
						biomeReturnData, 
						destArea_bl,
						useQuarterResolution
				);
			}
		}
		return biomeReturnData;			
	}
	
	
	/**
	 * suffix _bl means the value is in BLock coordinates
	 * suffix _ch means the value is in CHunk coordinates
	 * 
	 * @param sourceChunkData is a 256 element array representing 16 x 16 blocks, to be copied to destData 
	 * @param xChunk_bl is the x coordinate of the chunk, measured in blocks (i.e. this should be evenly divisible by 16).
	 * @param zChunk_bl is the z coordinate of the chunk, measured in blocks (i.e. this should be evenly divisible by 16).
	 * @param destData is an array with block-coordinates and bounds specified by destBounds in combination 
	 *        with destIsQuarterResolution.
	 * @param destBounds_bl provides the area (in block-coordinates) covered by the destData array.   
	 * @param destIsQuarterResolution - if true then destData is 1/16th the size indicated by destBounds, and 
	 *        only every 4th value (in both axes) should be written to it.  
	 *        If destIsQuarterResolution then destBounds coordinates and dimensions MUST be evenly divisible by 4.
	 */ 
	private void copyChunkDataTo2dMapArray(int[] sourceChunkData, int xChunk_bl, int zChunk_bl, int[] destData, Rectangle destBounds_bl, boolean destIsQuarterResolution) {
	
		int inc   = destIsQuarterResolution ? 4 : 1;
		int shift = destIsQuarterResolution ? 2 : 0;
		int bottomBound_bl = destBounds_bl.y + destBounds_bl.height;
		int rightBound_bl  = destBounds_bl.x + destBounds_bl.width;
		
		for(int z_bl = 0; z_bl < 16; z_bl += inc) {
			
			int zCoord_bl = zChunk_bl + z_bl;
			if (zCoord_bl >= destBounds_bl.y && zCoord_bl < bottomBound_bl) {
			
				int zSourceArrayOffset = z_bl << 4;
				int zDestArrayOffset = ((zCoord_bl - destBounds_bl.y) >> shift) * (destBounds_bl.width >> shift);
				
				for(int x_bl = 0; x_bl < 16; x_bl += inc) {
					
					int xCoord_bl = xChunk_bl + x_bl;
					if (xCoord_bl >= destBounds_bl.x && xCoord_bl < rightBound_bl) {
					
						int xDestArrayOffset = (xCoord_bl - destBounds_bl.x) >> shift;
						
						destData[zDestArrayOffset + xDestArrayOffset] = sourceChunkData[zSourceArrayOffset + x_bl];
					}
				}
			}
		}
	}

	
	/**
	 * suffix _bl means the value is in BLock coordinates
	 * suffix _ch means the value is in CHunk coordinates
	 */	
	private int[] getBiomeAtChunk(int xChunk_ch, int zChunk_ch) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, MinecraftInterfaceException {
		
		// This method is experimental.
		
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
		
		if (biomeChunkData == null || biomeChunkData.length < 256) {
			biomeChunkData = new int[256];
		}
		
		
		Object[] params = new Object[] {null, xChunk_ch * 16, zChunk_ch * 16, 16, 16};
		
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

		chunkGeneratorPRNG.setSeed(xChunk_ch * 341873128712L + zChunk_ch * 132897987541L);

		chunkGenerator.callMethod(
			SymbolicNames.METHOD_BETA_CHUNKGENERATOR_PREPARECHUNK,
			xChunk_ch,
			zChunk_ch,
			chunkData,
			biomesArray,
			temperatureArray			
		);
		
		Object[] biomes = (Object[])biomesArray;
		
		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				
				// Notch seems have put z in the inner loop, meaning the biomes array is (z by x) instead of (x by z) 
				int biomeId = getBiomeIndex(biomes[z + (x << 4)]);
				
				// 9 = water, 79 = ice, 0 = air (shouldn't happen at level 63), 1 = stone
				byte oceanType = chunkData[x << 11 | z << 7 | 63]; // x and z backwards again 
				if (oceanType ==  9) biomeId = Biome.fake_oceanB.getIndex();
				if (oceanType == 79) biomeId = Biome.fake_frozenOceanB.getIndex();
				
				biomeChunkData[x + (z << 4)] = biomeId;
			}
		}
		return biomeChunkData;		
	}
	
	
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		try {
			Log.i("Creating beta world with seed... '" + seed);
			
			this.overworldDimension = construct_OverworldDimension();
			this.world              = construct_World(overworldDimension, seed);
			
			if (useChunkGenerator()) {
				this.biomeGenerator = getDimensionBiomeGenerator(overworldDimension); // Use if you HAVE found a way to get the correct dimension subclass in b1.6
			} else {
				this.biomeGenerator = construct_BiomeGenerator(world);                // Use if you have NOT found a way to get the correct dimension subclass in b1.6
			}
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
		SymbolicObject chunkGenerator = chunkGeneratorClass.callConstructor(SymbolicNames.CLASS_BETA_CHUNKGENERATOR, world.getObject(), seed);

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
