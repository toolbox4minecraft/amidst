package amidst.minecraft.world;

import java.util.Map;
import java.util.WeakHashMap;

import amidst.map.Fragment;
import amidst.minecraft.MinecraftUtil;

public class BiomeDataProvider {
	public static final int SIZE = Fragment.BIOME_SIZE;

	private Map<CoordinatesInWorld, short[][]> cache = new WeakHashMap<CoordinatesInWorld, short[][]>();

	/**
	 * x and y of coordinates have to be divisible by BiomeDataProvider.SIZE
	 */
	public short[][] getBiomeDataAt(CoordinatesInWorld coordinates) {
		short[][] result = cache.get(coordinates);
		if (result == null) {
			result = create(coordinates);
			cache.put(coordinates, result);
		}
		return result;
	}

	private short[][] create(CoordinatesInWorld coordinates) {
		short[][] result = new short[SIZE][SIZE];
		int xInQuarterResolution = (int) coordinates.getXAs(Resolution.QUARTER);
		int yInQuarterResolution = (int) coordinates.getYAs(Resolution.QUARTER);
		int[] biomeData = MinecraftUtil.getBiomeData(xInQuarterResolution,
				yInQuarterResolution, SIZE, SIZE, true);
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y)];
			}
		}
		return result;
	}

	private int getBiomeDataIndex(int x, int y) {
		return x + y * SIZE;
	}
}
