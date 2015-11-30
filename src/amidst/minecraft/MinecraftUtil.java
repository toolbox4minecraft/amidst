package amidst.minecraft;

import amidst.version.VersionInfo;

public class MinecraftUtil {
	private static IMinecraftInterface minecraftInterface;

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
