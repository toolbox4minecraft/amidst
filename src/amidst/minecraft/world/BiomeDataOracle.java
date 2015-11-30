package amidst.minecraft.world;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class BiomeDataOracle {
	public void populateArrayUsingQuarterResolution(CoordinatesInWorld corner,
			short[][] result) {
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left = (int) corner.getXAs(Resolution.QUARTER);
			int top = (int) corner.getYAs(Resolution.QUARTER);
			copyToResult(result, width, height,
					getQuarterResolutionBiomeData(left, top, width, height));
		}
	}

	private void copyToResult(short[][] result, int width, int height,
			int[] biomeData) {
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
		int[] biomeData = getQuarterResolutionBiomeData(left, top, width,
				height);

		for (int i = 0; i < width * height; i++) {
			if (!validBiomes.contains(Biome.getByIndex(biomeData[i]))) {
				return false;
			}
		}
		return true;
	}

	// TODO: Find out if we should useQuarterResolutionMap or not
	public Point findValidLocation(int x, int y, int size,
			List<Biome> validBiomes, Random random) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		int[] biomeData = getQuarterResolutionBiomeData(left, top, width,
				height);

		Point location = null;
		int numberOfValidLocations = 0;
		for (int i = 0; i < width * height; i++) {
			int biomeIndex = biomeData[i];
			ensureBiomeIsSupported(biomeIndex);
			if (validBiomes.contains(Biome.getByIndex(biomeIndex))
					&& (location == null || random
							.nextInt(numberOfValidLocations + 1) == 0)) {
				location = createLocation(left, top, width, i);
				numberOfValidLocations++;
			}
		}
		return location;
	}

	private Point createLocation(int left, int top, int width, int i) {
		int x = left + i % width << 2;
		int y = top + i / width << 2;
		return new Point(x, y);
	}

	private static void ensureBiomeIsSupported(int biomeIndex) {
		if (!Biome.isSupportedBiomeIndex(biomeIndex)) {
			Log.crash("Unsupported biome type detected");
		}
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 * 
	 * @return Assume this may return null.
	 */
	public Biome getBiomeAt(int x, int y) {
		int[] biomeData = getFullResolutionBiomeData(x, y, 1, 1);
		return Biome.getByIndex(biomeData[0]);
	}

	private int[] getQuarterResolutionBiomeData(int x, int y, int width,
			int height) {
		return MinecraftUtil.getBiomeData(x, y, width, height, true);
	}

	private int[] getFullResolutionBiomeData(int x, int y, int width, int height) {
		return MinecraftUtil.getBiomeData(x, y, width, height, false);
	}
}
