package amidst.mojangapi.world.oracle.end;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.SimplexNoise;
import amidst.mojangapi.world.versionfeatures.DefaultBiomes;
import amidst.util.FastRand;

@ThreadSafe
public class EndIslandOracle {
	public static EndIslandOracle from(long worldSeed, boolean canGenerateSmallIslands) {
		return new EndIslandOracle(createNoiseFunction(worldSeed), worldSeed, canGenerateSmallIslands);
	}

	/**
	 * Returns the noise function using the current seed.
	 */
	private static SimplexNoise createNoiseFunction(long worldSeed) {
		FastRand random = new FastRand(worldSeed);
		// Mimics the side-effects to the random number generator caused by Minecraft.
		// Past 1.13, it just skips the random 17292 times.
		random.advance(17292);
		return new SimplexNoise(random);
	}

	/**
	 * Minecraft checks 12 chunks either side of a chunk when assessing island
	 * influence.
	 */
	private static final int LARGE_ISLAND_SURROUNDING_CHUNKS = 12;
	
	/**
	 * add some padding for small islands on fragment borders.
	 */
	private static final int SMALL_ISLAND_SURROUNDING_CHUNKS = 1;

	/**
	 * When cast to double, -0.9 will become -0.8999999761581421, which is why
	 * you might see that value in Minecraft's .jar
	 */
	private static final float ISLAND_DENSITY_THRESHOLD = -0.9f;

	/**
	 * The distance from (0;0) at which islands start to generated
	 */
	private static final int OUTER_LANDS_DISTANCE_IN_CHUNKS = 64;

	private static final long OUTER_LANDS_DISTANCE_IN_BLOCKS = Resolution.CHUNK.convertFromThisToWorld(OUTER_LANDS_DISTANCE_IN_CHUNKS);

	private final SimplexNoise noiseFunction;
	private final long worldSeed;
	private final boolean canGenerateSmallIslands;

	public EndIslandOracle(SimplexNoise noiseFunction, long worldSeed, boolean canGenerateSmallIslands) {
		this.noiseFunction = noiseFunction;
		this.worldSeed = worldSeed;
		this.canGenerateSmallIslands = canGenerateSmallIslands;
	}

	public static int getBiomeAtBlock(long x, long y, List<LargeEndIsland> largeIslands) {		
		if (isInRange(x, y, OUTER_LANDS_DISTANCE_IN_BLOCKS)) {
			return DefaultBiomes.theEnd;
		} else {
			float influence = getInfluenceAtBlock(x, y, largeIslands);
			if (influence > 40.0F) {
				return DefaultBiomes.theEndHigh;
			} else if (influence >= 0.0F) {
				return DefaultBiomes.theEndMedium;
			} else {
				return influence < -20.0F ? DefaultBiomes.theEndLow : DefaultBiomes.theEndBarren;
			}
		}
	}

	public static float getInfluenceAtBlock(long x, long y, List<LargeEndIsland> largeIslands) {
		float highestInfluence = -100.0f;
		
		for (LargeEndIsland island : largeIslands) {
			if (island instanceof LargeEndIsland) {
				float tempInfluence = ((LargeEndIsland) island).influenceAtBlock(x, y);
				if (tempInfluence > highestInfluence) {
					highestInfluence = tempInfluence;
				}
			}
		}
		return highestInfluence;
	}

	public EndIslandList getAt(CoordinatesInWorld corner) {
		int steps = Resolution.CHUNK.getStepsPerFragment();
		return findSurroundingIslands(
				corner.getXAs(Resolution.CHUNK),
				corner.getYAs(Resolution.CHUNK),
				steps,
				steps);
	}

	/**
	 * Returns a list of all islands that might be touching a chunk-area.
	 */
	private EndIslandList findSurroundingIslands(
			long chunkX,
			long chunkY,
			int chunksPerFragmentX,
			int chunksPerFragmentY) {
		List<LargeEndIsland> largeEndIslands = findSurroundingLargeIslands(chunkX, chunkY, chunksPerFragmentX, chunksPerFragmentY);
		List<SmallEndIsland> smallEndIslands = findSurroundingSmallIslands(chunkX, chunkY, chunksPerFragmentX, chunksPerFragmentY, largeEndIslands);
		return new EndIslandList(smallEndIslands, largeEndIslands);
	}

	public List<LargeEndIsland> getLargeIslandsAt(CoordinatesInWorld corner) {
		int steps = Resolution.CHUNK.getStepsPerFragment();
		return findSurroundingLargeIslands(
				corner.getXAs(Resolution.CHUNK),
				corner.getYAs(Resolution.CHUNK),
				steps,
				steps);
	}

	/**
	 * Returns a list of all large islands that might be touching a chunk-area.
	 */
	private List<LargeEndIsland> findSurroundingLargeIslands(
			long chunkX,
			long chunkY,
			int chunksPerFragmentX,
			int chunksPerFragmentY) {
		List<LargeEndIsland> result = new ArrayList<>();
		for (int y = -LARGE_ISLAND_SURROUNDING_CHUNKS; y <= chunksPerFragmentY + LARGE_ISLAND_SURROUNDING_CHUNKS; y++) {
			for (int x = -LARGE_ISLAND_SURROUNDING_CHUNKS; x <= chunksPerFragmentX + LARGE_ISLAND_SURROUNDING_CHUNKS; x++) {
				LargeEndIsland island = tryCreateLargeEndIsland(chunkX + x, chunkY + y);
				if (island != null) {
					result.add(island);
				}
			}
		}
		return result;
	}

	/**
	 * Returns a LargeEndIsland if one has 'grown out' from the chunk, otherwise
	 * null
	 */
	private LargeEndIsland tryCreateLargeEndIsland(long chunkX, long chunkY) {

		if (chunkX == 0 && chunkY == 0) {
			return createMainEndIsland(chunkX, chunkY);
		} else if (!isInRange(chunkX, chunkY, OUTER_LANDS_DISTANCE_IN_CHUNKS)) {
			return tryCreateLargeEndIslandInOuterLands(chunkX, chunkY);
		} else {
			return null;
		}
	}

	/**
	 * The main island grows from the origin, with a hard-coded erosion factor
	 * of 8
	 */
	private LargeEndIsland createMainEndIsland(long chunkX, long chunkY) {
		return new LargeEndIsland(chunkX, chunkY, 8.0f);
	}

	/**
	 * The chunk is in the outer-islands band
	 */
	private LargeEndIsland tryCreateLargeEndIslandInOuterLands(long chunkX, long chunkY) {
		if (noiseFunction.noise(chunkX, chunkY) < ISLAND_DENSITY_THRESHOLD) {
			return new LargeEndIsland(chunkX, chunkY, getErosionFactor(chunkX, chunkY));
		} else {
			return null;
		}
	}

	/**
	 * An island (or part of an island) grows out from this chunk, with an
	 * erosion factor between 9 and 21 (i.e. they will be smaller than the main
	 * island).
	 */
	private int getErosionFactor(long chunkX, long chunkY) {
	    // Convert coordinates to long to guard against overflow
		return (int) ((Math.abs(chunkX) * 3439 + Math.abs(chunkY) * 147) % 13 + 9);
	}
	
	private static boolean isInRange(long x, long y, long d) {
		return x * x + y * y <= d * d;
	}

	private List<SmallEndIsland> findSurroundingSmallIslands(
			long chunkX,
			long chunkY,
			int chunksPerFragmentX,
			int chunksPerFragmentY,
			List<LargeEndIsland> largeIslands) {
		List<SmallEndIsland> result = null;
		if(canGenerateSmallIslands) {
			result = new ArrayList<>();
			for (int y = -SMALL_ISLAND_SURROUNDING_CHUNKS; y <= chunksPerFragmentY + SMALL_ISLAND_SURROUNDING_CHUNKS; y++) {
				for (int x = -SMALL_ISLAND_SURROUNDING_CHUNKS; x <= chunksPerFragmentX + SMALL_ISLAND_SURROUNDING_CHUNKS; x++) {
					List<SmallEndIsland> smallIslands = getSmallIslandsInChunk(chunkX + x, chunkY + y, largeIslands);
					if(smallIslands != null) {
						result.addAll(smallIslands);
					}
				}
			}
		}
		return result;
	}

	private static final int SMALL_ISLANDS_FEATURE_INDEX = 0;
	private static final int SMALL_ISLANDS_GENERATION_STAGE = 0;
   
    private List<SmallEndIsland>  getSmallIslandsInChunk(long chunkX, long chunkY, List<LargeEndIsland> largeIslands) {
		long blockX = chunkX << 4;
		long blockY = chunkY << 4;
		if (getBiomeAtBlock(blockX, blockY, largeIslands) == DefaultBiomes.theEndLow) {
			FastRand rand = new FastRand(worldSeed);
			long a = rand.nextLong() | 1L;
			long b = rand.nextLong() | 1L;
			long populationSeed = (long)(int) blockX * a + (long)(int) blockY * b ^ worldSeed; // we do the long -> int -> long conversion to replicate what minecraft does.
			long decoratorSeed = populationSeed + SMALL_ISLANDS_FEATURE_INDEX + 10000 * SMALL_ISLANDS_GENERATION_STAGE;
			rand.setSeed(decoratorSeed);
			
			List<SmallEndIsland> smallIslands = new ArrayList<>();
			
			if (rand.nextInt(14) == 0) {
				long resultX = blockX + rand.nextInt(16);
				int resultH = 55 + rand.nextInt(16);
				long rexultY = blockY + rand.nextInt(16);
				smallIslands.add(new SmallEndIsland(resultX, rexultY, resultH));
				if (rand.nextInt(4) == 0) {
					resultX = blockX + rand.nextInt(16);
					resultH = 55 + rand.nextInt(16);
					rexultY = blockY + rand.nextInt(16);
					smallIslands.add(new SmallEndIsland(resultX, rexultY, resultH));
				}
			}
			
			// mc calculates the sizes of the islands after their locations are generated
			for(SmallEndIsland island : smallIslands) {
				int size = rand.nextInt(3) + 4;
				
				island.setSize(size + 1); // the size gets padded with an extra block when generated in mc
				
				// we have to do this so the random gets set correctly for the next end island
				float sizeFloat = (float) size;
				
				while(sizeFloat > 0.5F) {				
					sizeFloat = (float) ((double) sizeFloat - ((double) rand.nextInt(2) + 0.5D));
				}
			}
			
			return smallIslands;
		}
		return null;
	}

    public boolean canGenerateSmallIslands() {
    	return canGenerateSmallIslands;
    }
}
