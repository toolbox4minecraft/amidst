package amidst.minecraft;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.version.VersionInfo;

public class MinecraftUtil {
	private static IMinecraftInterface minecraftInterface;

	public static boolean isValidBiome(int x, int y, int size,
			List<Biome> validBiomes) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		int[] biomeData = getBiomeData(left, top, width, height, true);

		for (int i = 0; i < width * height; i++) {
			if (!validBiomes.contains(Biome.getByIndex(biomeData[i]))) {
				return false;
			}
		}
		return true;
	}

	// TODO: Find out if we should useQuarterResolutionMap or not
	public static Point findValidLocation(int searchX, int searchY, int size,
			List<Biome> validBiomes, Random random) {
		int left = searchX - size >> 2;
		int top = searchY - size >> 2;
		int right = searchX + size >> 2;
		int bottom = searchY + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		int[] biomeData = getBiomeData(left, top, width, height, true);

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

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 * 
	 * @return Assume this may return null.
	 */
	public static Biome getBiomeAt(int x, int y) {
		int[] biomeData = getBiomeData(x, y, 1, 1, false);
		return Biome.getByIndex(biomeData[0]);
	}

	/**
	 * Returns a copy of the biome data. This is NOT threadsafe without the
	 * synchronized keyword!
	 */
	public synchronized static int[] getBiomeData(int x, int y, int width,
			int height, boolean useQuarterResolutionMap) {
		return minecraftInterface.getBiomeData(x, y, width, height,
				useQuarterResolutionMap);
	}

	public static void createWorld(long seed, String type,
			String generatorOptions) {
		minecraftInterface.createWorld(seed, type, generatorOptions);
	}

	public static VersionInfo getVersion() {
		return minecraftInterface.getVersion();
	}

	public static void setInterface(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.minecraftInterface = minecraftInterface;
	}

	public static boolean hasInterface() {
		return minecraftInterface != null;
	}
}
