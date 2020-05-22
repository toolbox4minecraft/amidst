package amidst.mojangapi.world.oracle;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.versionfeatures.DefaultBiomes;

import kaptainwutax.seedutils.lcg.rand.JRand;

@ThreadSafe
public class EndIslandOracle {
	public static EndIslandOracle from(long seed) {
		return new EndIslandOracle(createNoiseFunction(seed));
	}

	/**
	 * Returns the noise function using the current seed.
	 */
	private static SimplexNoise createNoiseFunction(long seed) {
		JRand random = new JRand(seed);
		// Mimics the side-effects to the random number generator caused by Minecraft.
		random.advance(17292);
		return new SimplexNoise(random);
	}

	/**
	 * Minecraft checks 12 chunks either side of a chunk when assessing island
	 * influence.
	 */
	private static final int SURROUNDING_CHUNKS = 12;

	/**
	 * When cast to double, -0.9 will become -0.8999999761581421, which is why
	 * you might see that value in Minecraft's .jar
	 */
	private static final float ISLAND_DENSITY_THRESHOLD = -0.9f;

	/**
	 * The distance from (0;0) at which islands start to generated
	 */
	private static final int OUTER_LANDS_DISTANCE_IN_CHUNKS = 64;

	private final SimplexNoise noiseFunction;

	public EndIslandOracle(SimplexNoise noiseFunction) {
		this.noiseFunction = noiseFunction;
	}

	public int getBiomeAtBlock(long x, long y) {
		if (x * x + y * y <= 4096L) {
			return DefaultBiomes.theEnd;
		} else {
			float influence = getInfluenceAtBlock(x, y);
			if (influence > 40.0F) {
				return DefaultBiomes.theEndHigh;
			} else if (influence >= 0.0F) {
				return DefaultBiomes.theEndMedium;
			} else {
				return influence < -20.0F ? DefaultBiomes.theEndLow : DefaultBiomes.theEndBarren;
			}
		}
	}
	
	public int getBiomeAtBlock(CoordinatesInWorld coords) {
		long x = coords.getX();
		long y = coords.getY();
		
		if (x * x + y * y <= 4096L) {
			return DefaultBiomes.theEnd;
		} else {
			float influence = getInfluenceAtBlock(coords);
			if (influence > 40.0F) {
				return DefaultBiomes.theEndHigh;
			} else if (influence >= 0.0F) {
				return DefaultBiomes.theEndMedium;
			} else {
				return influence < -20.0F ? DefaultBiomes.theEndLow : DefaultBiomes.theEndBarren;
			}
		}
	}
	
	public float getInfluenceAtBlock(long x, long y) {
		float highestInfluence = -100.0f;
		
		for(EndIsland island : getAt(new CoordinatesInWorld(x, y))) {
			float tempInfluence = island.influenceAtBlock(x, y);
			if(tempInfluence > highestInfluence) {
				highestInfluence = tempInfluence;
			}
		}
		return highestInfluence;
	}
	
	public float getInfluenceAtBlock(CoordinatesInWorld coords) {
		float highestInfluence = -100.0f;
		long x = coords.getX();
		long y = coords.getY();
		
		for (EndIsland island : getAt(coords)) {
			float tempInfluence = island.influenceAtBlock(x, y);
			if (tempInfluence > highestInfluence) {
				highestInfluence = tempInfluence;
			}
		}
		return highestInfluence;
	}

	public List<EndIsland> getAt(CoordinatesInWorld corner) {
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
	private List<EndIsland> findSurroundingIslands(
			long chunkX,
			long chunkY,
			int chunksPerFragmentX,
			int chunksPerFragmentY) {
		List<EndIsland> result = new ArrayList<>();
		for (int y = -SURROUNDING_CHUNKS; y <= chunksPerFragmentY + SURROUNDING_CHUNKS; y++) {
			for (int x = -SURROUNDING_CHUNKS; x <= chunksPerFragmentX + SURROUNDING_CHUNKS; x++) {
				EndIsland island = tryCreateEndIsland(chunkX + x, chunkY + y);
				if (island != null) {
					result.add(island);
				}
			}
		}
		return result;
	}

	/**
	 * Returns an EndIsland if one has 'grown out' from the chunk, otherwise
	 * null
	 */
	private EndIsland tryCreateEndIsland(long chunkX, long chunkY) {

		if (chunkX == 0 && chunkY == 0) {
			return createMainEndIsland(chunkX, chunkY);
		} else if (!isInRange(chunkX, chunkY, OUTER_LANDS_DISTANCE_IN_CHUNKS)) {
			return tryCreateEndIslandInOuterLands(chunkX, chunkY);
		} else {
			return null;
		}
	}

	/**
	 * The main island grows from the origin, with a hard-coded erosion factor
	 * of 8
	 */
	private EndIsland createMainEndIsland(long chunkX, long chunkY) {
		return new EndIsland(chunkX, chunkY, 8.0f);
	}

	/**
	 * The chunk is in the outer-islands band
	 */
	private EndIsland tryCreateEndIslandInOuterLands(long chunkX, long chunkY) {
		if (noiseFunction.noise(chunkX, chunkY) < ISLAND_DENSITY_THRESHOLD) {
			return new EndIsland(chunkX, chunkY, getErosionFactor(chunkX, chunkY));
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

    /**
     * Is the point (x, y) inside the disk of radius d centered at the origin?
     */
    private boolean isInRange(long x, long y, int d) {
        // Guard against overflow
        if (x < -d || x > d || y < -d || y > d)
            return false;
        return x * x + y * y <= d * d;
    }
}
