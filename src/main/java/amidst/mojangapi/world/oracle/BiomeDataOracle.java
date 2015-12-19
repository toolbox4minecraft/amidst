package amidst.mojangapi.world.oracle;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class BiomeDataOracle {
	private final MinecraftInterface minecraftInterface;

	public BiomeDataOracle(MinecraftInterface minecraftInterface) {
		this.minecraftInterface = minecraftInterface;
	}

	public void populateArrayUsingQuarterResolution(CoordinatesInWorld corner,
			short[][] result) {
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left = (int) corner.getXAs(Resolution.QUARTER);
			int top = (int) corner.getYAs(Resolution.QUARTER);
			try {
				copyToResult(result, width, height,
						getQuarterResolutionBiomeData(left, top, width, height));
			} catch (MinecraftInterfaceException e) {
				Log.e(e.getMessage());
				e.printStackTrace();
			}
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
		try {
			int[] biomeData = getQuarterResolutionBiomeData(left, top, width,
					height);
			for (int i = 0; i < width * height; i++) {
				if (!validBiomes.contains(Biome.getByIndex(biomeData[i]))) {
					return false;
				}
			}
			return true;
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// TODO: Find out if we should useQuarterResolution or not
	public Point findValidLocation(int x, int y, int size,
			List<Biome> validBiomes, Random random) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			int[] biomeData = getQuarterResolutionBiomeData(left, top, width,
					height);
			Point location = null;
			int numberOfValidLocations = 0;
			for (int i = 0; i < width * height; i++) {
				if (validBiomes.contains(Biome.getByIndex(biomeData[i]))
						&& (location == null || random
								.nextInt(numberOfValidLocations + 1) == 0)) {
					location = createLocation(left, top, width, i);
					numberOfValidLocations++;
				}
			}
			return location;
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private Point createLocation(int left, int top, int width, int i) {
		int x = left + i % width << 2;
		int y = top + i / width << 2;
		return new Point(x, y);
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 */
	public Biome getBiomeAt(int x, int y) throws UnknownBiomeIndexException,
			MinecraftInterfaceException {
		int[] biomeData = getFullResolutionBiomeData(x, y, 1, 1);
		return Biome.getByIndex(biomeData[0]);
	}

	private int[] getQuarterResolutionBiomeData(int x, int y, int width,
			int height) throws MinecraftInterfaceException {
		return getBiomeData(x, y, width, height, true);
	}

	private int[] getFullResolutionBiomeData(int x, int y, int width, int height)
			throws MinecraftInterfaceException {
		return getBiomeData(x, y, width, height, false);
	}

	private int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolution) throws MinecraftInterfaceException {
		return minecraftInterface.getBiomeData(x, y, width, height,
				useQuarterResolution);
	}
}
