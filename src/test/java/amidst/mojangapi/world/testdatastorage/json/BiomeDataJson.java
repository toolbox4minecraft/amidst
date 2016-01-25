package amidst.mojangapi.world.testdatastorage.json;

import amidst.documentation.GsonConstructor;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class BiomeDataJson {
	public static BiomeDataJson from(BiomeDataOracle biomeDataOracle,
			int fragmentsAroundOrigin, boolean useQuarterResolution) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int steps = resolution.getStepsPerFragment();
		int stepsAroungOrigin = steps * fragmentsAroundOrigin;
		short[][] biomeData = new short[2 * stepsAroungOrigin][2 * stepsAroungOrigin];
		short[][] transferArray = new short[steps][steps];
		CoordinatesInWorld corner;
		for (int x = -fragmentsAroundOrigin; x < fragmentsAroundOrigin; x++) {
			for (int y = -fragmentsAroundOrigin; y < fragmentsAroundOrigin; y++) {
				corner = CoordinatesInWorld.from(x * Fragment.SIZE, y
						* Fragment.SIZE);
				biomeDataOracle.populateArray(corner, transferArray,
						useQuarterResolution);
				int xBase = (int) (corner.getXAs(resolution) + stepsAroungOrigin);
				int yBase = (int) (corner.getYAs(resolution) + stepsAroungOrigin);
				transfer(transferArray, biomeData, xBase, yBase);
			}
		}
		return new BiomeDataJson(-stepsAroungOrigin, -stepsAroungOrigin,
				2 * stepsAroungOrigin, 2 * stepsAroungOrigin, biomeData);
	}

	private static void transfer(short[][] from, short[][] to, int xBase,
			int yBase) {
		for (int xOffset = 0; xOffset < from.length; xOffset++) {
			for (int yOffset = 0; yOffset < from[xOffset].length; yOffset++) {
				to[xBase + xOffset][yBase + yOffset] = from[xOffset][yOffset];
			}
		}
	}

	private volatile long x;
	private volatile long y;
	@SuppressWarnings("unused")
	private volatile long width;
	@SuppressWarnings("unused")
	private volatile long height;
	private volatile short[][] biomeData;

	@GsonConstructor
	public BiomeDataJson() {
	}

	public BiomeDataJson(long x, long y, long width, long height,
			short[][] biomeData) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.biomeData = biomeData;
	}

	public int[] get(int x, int y, int width, int height) {
		int xBase = (int) (x - this.x);
		int yBase = (int) (y - this.y);
		int[] result = new int[width * height];
		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int yOffset = 0; yOffset < height; yOffset++) {
				int biomeDataIndex = BiomeDataOracle.getBiomeDataIndex(xOffset,
						yOffset, width);
				result[biomeDataIndex] = biomeData[xBase + xOffset][yBase
						+ yOffset];
			}
		}
		return result;
	}
}
