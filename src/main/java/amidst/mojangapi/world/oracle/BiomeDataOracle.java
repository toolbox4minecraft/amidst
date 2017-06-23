package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class BiomeDataOracle {
	private final MinecraftInterface minecraftInterface;

	public BiomeDataOracle(MinecraftInterface minecraftInterface) {
		this.minecraftInterface = minecraftInterface;
	}

	public void populateArray(Coordinates corner, short[][] result, boolean useQuarterResolution) {
		if(result.length == 0)
			return;

		int width = result.length;
		int height = result[0].length;
		if(height == 0)
			return;

		Resolution res = Resolution.from(useQuarterResolution);
		Region.Box region = Region.box(corner.getAs(res), width, height);
		try {
			int[] data = getBiomeData(region, useQuarterResolution);
			copyToResult(result, data);
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
		}
	}

	public static void copyToResult(short[][] result, int[] biomeData) {		
		int width = result[0].length;
		for (int y = 0; y < result.length; y++) {
			for (int x = 0; x < result[y].length; x++) {
				result[x][y] = (short) biomeData[x + y*width];
			}
		}
	}

	public static int getBiomeDataIndex(int x, int y, int width) {
		return x + y * width;
	}

	public boolean isValidBiomeAtMiddleOfChunk(int chunkX, int chunkY, List<Biome> validBiomes) {
		return isValidBiome(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), validBiomes);
	}

	private boolean isValidBiome(int x, int y, List<Biome> validBiomes) {
		try {
			return validBiomes.contains(getBiomeAt(x, y));
		} catch (UnknownBiomeIndexException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		}
	}

	public boolean isValidBiomeForStructureAtMiddleOfChunk(int chunkX, int chunkY, int size, List<Biome> validBiomes) {
		return isValidBiomeForStructure(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomes);
	}

	public boolean isValidBiomeForStructure(int x, int y, int size, List<Biome> validBiomes) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			int[] biomeData = getQuarterResolutionBiomeData(Region.box(left, top, width, height));
			for (int i = 0; i < width * height; i++) {
				if (!validBiomes.contains(Biome.getByIndex(biomeData[i]))) {
					return false;
				}
			}
			return true;
		} catch (UnknownBiomeIndexException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		}
	}

	public Coordinates findValidLocationAtMiddleOfChunk(
			int chunkX,
			int chunkY,
			int size,
			List<Biome> validBiomes,
			Random random) {
		return findValidLocation(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomes, random);
	}

	// TODO: Find out if we should useQuarterResolution or not
	public Coordinates findValidLocation(int x, int y, int size, List<Biome> validBiomes, Random random) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			int[] biomeData = getQuarterResolutionBiomeData(Region.box(left, top, width, height));
			Coordinates result = null;
			int numberOfValidLocations = 0;
			for (int i = 0; i < width * height; i++) {
				if (validBiomes.contains(Biome.getByIndex(biomeData[i]))
						&& (result == null || random.nextInt(numberOfValidLocations + 1) == 0)) {
					result = createCoordinates(left, top, width, i);
					numberOfValidLocations++;
				}
			}
			return result;
		} catch (UnknownBiomeIndexException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return null;
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return null;
		}
	}

	private Coordinates createCoordinates(int left, int top, int width, int i) {
		int x = left + i % width << 2;
		int y = top + i / width << 2;
		return Coordinates.from(x, y);
	}

	private int getMiddleOfChunk(int coordinate) {
		return coordinate * 16 + 8;
	}

	public Biome getBiomeAtMiddleOfChunk(int chunkX, int chunkY)
			throws UnknownBiomeIndexException,
			MinecraftInterfaceException {
		return getBiomeAt(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY));
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 */
	private Biome getBiomeAt(int x, int y) throws UnknownBiomeIndexException, MinecraftInterfaceException {
		int[] biomeData = getFullResolutionBiomeData(Region.box(x, y, 1, 1));
		return Biome.getByIndex(biomeData[0]);
	}

	private int[] getQuarterResolutionBiomeData(Region.Box region)
			throws MinecraftInterfaceException {
		return getBiomeData(region, true);
	}

	private int[] getFullResolutionBiomeData(Region.Box region) throws MinecraftInterfaceException {
		return getBiomeData(region, false);
}

	private int[] getBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		return minecraftInterface.getBiomeData(region, useQuarterResolution);
	}
}
