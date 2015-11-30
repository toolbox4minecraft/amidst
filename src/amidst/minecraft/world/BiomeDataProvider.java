package amidst.minecraft.world;

import amidst.map.Fragment;
import amidst.minecraft.MinecraftUtil;

public class BiomeDataProvider {
	public static final int SIZE = (int) Resolution.QUARTER
			.convertFromWorldToThis(Fragment.SIZE);

	/**
	 * x and y of corner have to be divisible by Fragment.SIZE
	 */
	public void populateArray(CoordinatesInWorld corner, short[][] result) {
		int xInQuarterResolution = (int) corner.getXAs(Resolution.QUARTER);
		int yInQuarterResolution = (int) corner.getYAs(Resolution.QUARTER);
		int[] biomeData = MinecraftUtil.getBiomeData(xInQuarterResolution,
				yInQuarterResolution, SIZE, SIZE, true);
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y)];
			}
		}
	}

	private int getBiomeDataIndex(int x, int y) {
		return x + y * SIZE;
	}
}
