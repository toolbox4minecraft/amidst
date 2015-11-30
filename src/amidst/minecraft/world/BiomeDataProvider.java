package amidst.minecraft.world;

import amidst.minecraft.MinecraftUtil;

public class BiomeDataProvider {
	public static final Resolution RESOLUTION = Resolution.QUARTER;

	public void populateArray(CoordinatesInWorld corner, short[][] result) {
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int xInQuarterResolution = (int) corner.getXAs(RESOLUTION);
			int yInQuarterResolution = (int) corner.getYAs(RESOLUTION);
			int[] biomeData = MinecraftUtil.getBiomeData(xInQuarterResolution,
					yInQuarterResolution, width, height, true);
			copyToResult(result, biomeData, width, height);
		}
	}

	private void copyToResult(short[][] result, int[] biomeData, int width,
			int height) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y, width)];
			}
		}
	}

	private int getBiomeDataIndex(int x, int y, int width) {
		return x + y * width;
	}
}
