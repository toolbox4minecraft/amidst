package amidst.mojangapi.world.oracle;

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
	
	public void populateArray(CoordinatesInWorld corner, short[][] result, Resolution resolution) {
		if(result.length == 0)
			return;
		
		int width = result.length;
		int height = result[0].length;
		
		boolean useQuarterResolution = resolution.getStep() >= Resolution.QUARTER.getStep();
		Resolution targetRes = Resolution.from(useQuarterResolution);
		int shift = resolution.getShift() - targetRes.getShift();
		
		corner = corner.getAs(targetRes);
		
		try {
			int[] data = getBiomeData((int) corner.getX(), (int) corner.getY(), width << shift, height << shift, useQuarterResolution);
			copyToResult(result, width, height, shift, data);
			
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void copyToResult(short[][] result, int width, int height, int shift, int[] biomeData) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[x][y] = (short) biomeData[getBiomeDataIndex(x, y, width, shift)];
			}
		}
	}

	public static int getBiomeDataIndex(int x, int y, int width, int shift) {
		return (x + (y*width) << shift) << shift;
	}

	public boolean isValidBiomeAtMiddleOfChunk(int chunkX, int chunkY, List<Biome> validBiomes) {
		return isValidBiome(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), validBiomes);
	}

	private boolean isValidBiome(int x, int y, List<Biome> validBiomes) {
		try {
			return validBiomes.contains(getBiomeAt(x, y));
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
			int[] biomeData = getQuarterResolutionBiomeData(left, top, width, height);
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

	public CoordinatesInWorld findValidLocationAtMiddleOfChunk(
			int chunkX,
			int chunkY,
			int size,
			List<Biome> validBiomes,
			Random random) {
		return findValidLocation(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomes, random);
	}

	// TODO: Find out if we should useQuarterResolution or not
	public CoordinatesInWorld findValidLocation(int x, int y, int size, List<Biome> validBiomes, Random random) {
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
				if (validBiomes.contains(Biome.getByIndex(biomeData[i]))
						&& (result == null || random.nextInt(numberOfValidLocations + 1) == 0)) {
					result = createCoordinates(left, top, width, i);
					numberOfValidLocations++;
				}
			}
			return result;
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

	private CoordinatesInWorld createCoordinates(int left, int top, int width, int i) {
		int x = left + i % width << 2;
		int y = top + i / width << 2;
		return CoordinatesInWorld.from(x, y);
	}

	private int getMiddleOfChunk(int coordinate) {
		return coordinate * 16 + 8;
	}

	public Biome getBiomeAtMiddleOfChunk(int chunkX, int chunkY) throws UnknownBiomeIndexException,
			MinecraftInterfaceException {
		return getBiomeAt(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY));
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 */
	private Biome getBiomeAt(int x, int y) throws UnknownBiomeIndexException, MinecraftInterfaceException {
		int[] biomeData = getFullResolutionBiomeData(x, y, 1, 1);
		return Biome.getByIndex(biomeData[0]);
	}

	private int[] getQuarterResolutionBiomeData(int x, int y, int width, int height) throws MinecraftInterfaceException {
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
