package amidst.minecraft.world;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.minecraft.Biome;
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

	public boolean isValidBiome(int x, int y, int size, List<Biome> validBiomes) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		int[] biomeData = MinecraftUtil.getBiomeData(left, top, width, height,
				true);

		for (int i = 0; i < width * height; i++) {
			if (!validBiomes.contains(Biome.getByIndex(biomeData[i]))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 * 
	 * @return Assume this may return null.
	 */
	public Biome getBiomeAt(int x, int y) {
		int[] biomeData = MinecraftUtil.getBiomeData(x, y, 1, 1, false);
		return Biome.getByIndex(biomeData[0]);
	}

	// TODO: Find out if we should useQuarterResolutionMap or not
	public Point findValidLocation(int searchX, int searchY, int size,
			List<Biome> validBiomes, Random random) {
		int left = searchX - size >> 2;
		int top = searchY - size >> 2;
		int right = searchX + size >> 2;
		int bottom = searchY + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		int[] biomeData = MinecraftUtil.getBiomeData(left, top, width, height,
				true);

		Point location = null;
		int numberOfValidLocations = 0;
		for (int i = 0; i < width * height; i++) {
			int biomeIndex = biomeData[i];
			ensureBiomeIsSupported(biomeIndex);
			if (validBiomes.contains(Biome.getByIndex(biomeIndex))
					&& (location == null || random
							.nextInt(numberOfValidLocations + 1) == 0)) {
				int x = left + i % width << 2;
				int y = top + i / width << 2;
				location = new Point(x, y);
				numberOfValidLocations++;
			}
		}
		return location;
	}

	private static void ensureBiomeIsSupported(int biomeIndex) {
		if (!Biome.isSupportedBiomeIndex(biomeIndex)) {
			Log.crash("Unsupported biome type detected");
		}
	}
}
