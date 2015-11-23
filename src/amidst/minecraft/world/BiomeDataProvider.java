package amidst.minecraft.world;

import java.util.Map;
import java.util.WeakHashMap;

import amidst.map.Fragment;
import amidst.minecraft.MinecraftUtil;

public class BiomeDataProvider {
	public static final int SIZE = (int) Resolution.QUARTER
			.convertFromWorldToThis(Fragment.SIZE);

	private Map<CoordinatesInWorld, short[][]> cache = new WeakHashMap<CoordinatesInWorld, short[][]>();

	/**
	 * x and y of coordinates have to be divisible by BiomeDataProvider.SIZE
	 */
	public short[][] getBiomeDataForFragment(CoordinatesInWorld corner) {
		short[][] result = cache.get(corner);
		if (result == null) {
			result = create(corner);
			cache.put(corner, result);
		}
		return result;
	}

	private short[][] create(CoordinatesInWorld corner) {
		short[][] result = new short[SIZE][SIZE];
		int xInQuarterResolution = (int) corner.getXAs(Resolution.QUARTER);
		int yInQuarterResolution = (int) corner.getYAs(Resolution.QUARTER);
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
