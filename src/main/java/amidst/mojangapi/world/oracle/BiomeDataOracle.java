package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class BiomeDataOracle {
	private final MinecraftInterface minecraftInterface;

	public BiomeDataOracle(MinecraftInterface minecraftInterface) {
		this.minecraftInterface = minecraftInterface;
	}

	public void populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left = (int) corner.getXAs(resolution);
			int top = (int) corner.getYAs(resolution);
			try {
				copyToResult(result, width, height, getBiomeData(left, top, width, height, useQuarterResolution));
			} catch (MinecraftInterfaceException e) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
		}
	}

	public static void copyToResult(short[][] result, int width, int height, int[] biomeData) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y, width)];
			}
		}
	}

	public static int getBiomeDataIndex(int x, int y, int width) {
		return x + y * width;
	}

	public boolean isValidBiomeAtMiddleOfChunk(int chunkX, int chunkY, List<Integer> validBiomeIds) {
		return isValidBiome(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), validBiomeIds);
	}

	private boolean isValidBiome(int x, int y, List<Integer> validBiomeIds) {
		try {
			return validBiomeIds.contains(getBiomeAt(x, y));
		} catch (UnknownBiomeIdException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		}
	}

	public boolean isValidBiomeForStructureAtMiddleOfChunk(int chunkX, int chunkY, int size, List<Integer> validBiomeIds) {
		return isValidBiomeForStructure(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomeIds);
	}

	public boolean isValidBiomeForStructure(int x, int y, int size, List<Integer> validBiomeIds) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			int[] biomeData = getQuarterResolutionBiomeData(left, top, width, height);
			for (int i = 0; i < width * height; i++) {
				if (!validBiomeIds.contains(biomeData[i])) {
					return false;
				}
			}
			return true;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		}
	}

	public CoordinatesInWorld findValidLocationAtMiddleOfChunk(
			int chunkX,
			int chunkY,
			int size,
			List<Integer> validBiomeIds,
			Random random) {
		return findValidLocation(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomeIds, random);
	}

	public CoordinatesInWorld findValidLocation(int x, int y, int size, List<Integer> validBiomeIds, Random random) {
		if(RecognisedVersion.isNewerOrEqualTo(minecraftInterface.getRecognisedVersion(), RecognisedVersion._18w06a)) {
			return doFindValidLocation(x, y, size, validBiomeIds, random, true);
		} else {
			return doFindValidLocation(x, y, size, validBiomeIds, random, false);
		}
	}

	// This algorithm slightly changed in 18w06: prior to this version,
	// numberOfValidLocations was only incremented if the random check
	// succeeded; it is now always incremented.
	private CoordinatesInWorld doFindValidLocation(
			int x, int y, int size, List<Integer> validBiomeIds,
			Random random, boolean accurateLocationCount) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			int[] biomeData = getQuarterResolutionBiomeData(left, top, width, height);
			CoordinatesInWorld result = null;
			int numberOfValidLocations = 0;
			for (int i = 0; i < width * height; i++) {
				if(validBiomeIds.contains(biomeData[i])) {
					boolean updateResult = result == null || random.nextInt(numberOfValidLocations + 1) == 0;
					result = updateResult ? createCoordinates(left, top, width, i) : result;

					if(accurateLocationCount || updateResult) {
						numberOfValidLocations++;
					}
				}
			}
			return result;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return null;
		}
	}

	private CoordinatesInWorld createCoordinates(int left, int top, int width, int i) {
		int x = left + i % width << 2;
		int y = top + i / width << 2;
		return CoordinatesInWorld.from(x, y);
	}

	private int getMiddleOfChunk(int coordinate) {
		return coordinate * 16 + 8;
	}

	public int getBiomeAtMiddleOfChunk(int chunkX, int chunkY)
			throws UnknownBiomeIdException,
			MinecraftInterfaceException {
		return getBiomeAt(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY));
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 */
	private int getBiomeAt(int x, int y) throws UnknownBiomeIdException, MinecraftInterfaceException {
		int[] biomeData = getFullResolutionBiomeData(x, y, 1, 1);
		return biomeData[0];
	}

	private int[] getQuarterResolutionBiomeData(int x, int y, int width, int height)
			throws MinecraftInterfaceException {
		return getBiomeData(x, y, width, height, true);
	}

	private int[] getFullResolutionBiomeData(int x, int y, int width, int height) throws MinecraftInterfaceException {
		return getBiomeData(x, y, width, height, false);
	}

	private int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		return minecraftInterface.getBiomeData(x, y, width, height, useQuarterResolution);
	}
}
