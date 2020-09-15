package amidst.mojangapi.world.oracle;

import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.util.FastRand;

@ThreadSafe
public class EndIslandOracle {
	public static EndIslandOracle from(long seed) {
		return new EndIslandOracle(createNoiseFunction(seed));
	}

	/**
	 * Returns the noise function using the current seed.
	 */
	private static SimplexNoise createNoiseFunction(long seed) {
		FastRand random = new FastRand(seed);
		fakePerlin3dOctavesConstructor(random, 16);
		fakePerlin3dOctavesConstructor(random, 16);
		fakePerlin3dOctavesConstructor(random, 8);
		fakePerlin3dOctavesConstructor(random, 10);
		fakePerlin3dOctavesConstructor(random, 16);
		return new SimplexNoise(random);
	}

	/**
	 * Mimics the side-effects to the random number generator caused by
	 * constructing a Perlin3dOctaves instance.
	 */
	private static void fakePerlin3dOctavesConstructor(FastRand random, int octaveCount) {
		for (int i = 0; i < octaveCount; i++) {
			fakePerlin3dConstructor(random);
		}
	}

	/**
	 * Mimics the side-effects to the random number generator caused by
	 * constructing a Perlin3d instance.
	 */
	private static void fakePerlin3dConstructor(FastRand random) {
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		for (int i = 0; i < 256; i++) {
			random.nextInt(256 - i);
		}
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

	public List<EndIsland> getAt(CoordinatesInWorld corner) {
		int steps = Resolution.CHUNK.getStepsPerFragment();
		return findSurroundingIslands(
				(int) corner.getXAs(Resolution.CHUNK),
				(int) corner.getYAs(Resolution.CHUNK),
				steps,
				steps);
	}

	/**
	 * Returns a list of all islands that might be touching a chunk-area.
	 */
	private List<EndIsland> findSurroundingIslands(
			int chunkX,
			int chunkY,
			int chunksPerFragmentX,
			int chunksPerFragmentY) {
		List<EndIsland> result = new LinkedList<>();
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
	private EndIsland tryCreateEndIsland(int chunkX, int chunkY) {

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
	private EndIsland createMainEndIsland(int chunkX, int chunkY) {
		return new EndIsland(chunkX, chunkY, 8.0f);
	}

	/**
	 * The chunk is in the outer-islands band
	 */
	// TODO: check for threading, do we need synchonized or is the SimplexNoise
	// class thread safe?
	private synchronized EndIsland tryCreateEndIslandInOuterLands(int chunkX, int chunkY) {
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
	private int getErosionFactor(int chunkX, int chunkY) {
	    // Convert coordinates to long to guard against overflow
		return (int) ((Math.abs((long) chunkX) * 3439 + Math.abs((long) chunkY) * 147) % 13 + 9);
	}

    /**
     * Is the point (x, y) inside the disk of radius d centered at the origin?
     */
    private boolean isInRange(int x, int y, int d) {
        // Guard against overflow
        if (x < -d || x > d || y < -d || y > d)
            return false;
        return x * x + y * y <= d * d;
    }
}
