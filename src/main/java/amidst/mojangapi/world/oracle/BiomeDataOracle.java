package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class BiomeDataOracle {
	private final MinecraftInterface.World minecraftWorld;
	private final BiomeList biomeList;
	private final boolean quarterResOverride;
	private final int middleOfChunkOffset;
	private final boolean accurateLocationCount;

	public static class Config {
		public boolean quarterResOverride = true;
		public int middleOfChunkOffset = 9;
		public boolean accurateLocationCount = true;
	}

	public BiomeDataOracle(MinecraftInterface.World minecraftWorld, BiomeList biomeList, Config config) {
		this.minecraftWorld = minecraftWorld;
		this.biomeList = biomeList;
		this.quarterResOverride = config.quarterResOverride;
		this.middleOfChunkOffset = config.middleOfChunkOffset;
		this.accurateLocationCount = config.accurateLocationCount;
	}

	public void populateArray(CoordinatesInWorld corner, short[][] result, boolean useQuarterResolution) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int width = result.length;
		if (width > 0) {
			int height = result[0].length;
			int left = (int) corner.getXAs(resolution);
			int top = (int) corner.getYAs(resolution);
			try {
				minecraftWorld.getBiomeData(left, top, width, height, useQuarterResolution, biomeData -> {
					copyToResult(result, width, height, biomeData);
					return null;
				});
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

	public boolean isValidBiomeAtMiddleOfChunk(int chunkX, int chunkY, List<Biome> validBiomes) {
		return isValidBiome(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), validBiomes);
	}

	private boolean isValidBiome(int x, int y, List<Biome> validBiomes) {
		try {
			if(quarterResOverride) {
				return validBiomes.contains(getBiomeAt(x >> 2, y >> 2, true));
			} else {
				return validBiomes.contains(getBiomeAt(x, y, false));
			}
		} catch (UnknownBiomeIdException | MinecraftInterfaceException e) {
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
			return getQuarterResolutionBiomeData(left, top, width, height, biomeData -> {
				for (int i = 0; i < width * height; i++) {
					Biome biome = biomeList.getByIdOrNull(biomeData[i]);
					if (!validBiomes.contains(biome)) {
						return false;
					}
				}

				return true;
			});
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
			List<Biome> validBiomes,
			Random random) {
		return findValidLocation(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomes, random);
	}

	public CoordinatesInWorld findValidLocation(int x, int y, int size, List<Biome> validBiomes, Random random) {
		return doFindValidLocation(x, y, size, validBiomes, random, accurateLocationCount);
	}

	// This algorithm slightly changed in the 1.13 snapshots: before,
	// numberOfValidLocations was only incremented if the random check
	// succeeded; it is now always incremented.
	private CoordinatesInWorld doFindValidLocation(
			int x, int y, int size, List<Biome> validBiomes,
			Random random, boolean accurateLocationCount) {
		int left = x - size >> 2;
		int top = y - size >> 2;
		int right = x + size >> 2;
		int bottom = y + size >> 2;
		int width = right - left + 1;
		int height = bottom - top + 1;
		try {
			return getQuarterResolutionBiomeData(left, top, width, height, biomeData -> {
				CoordinatesInWorld result = null;
				int numberOfValidLocations = 0;
				for (int i = 0; i < width * height; i++) {
					Biome biome = biomeList.getByIdOrNull(biomeData[i]);
					if (validBiomes.contains(biome)) {
						boolean updateResult = result == null || random.nextInt(numberOfValidLocations + 1) == 0;
						result = updateResult ? createCoordinates(left, top, width, i) : result;

						if(accurateLocationCount || updateResult) {
							numberOfValidLocations++;
						}
					}
				}
				return result;
			});
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

	private int getMiddleOfChunk(int chunkCoord) {
		return (chunkCoord << 4) + middleOfChunkOffset;
	}

	public Biome getBiomeAtMiddleOfChunk(int chunkX, int chunkY)
			throws UnknownBiomeIdException, MinecraftInterfaceException {
		return getBiomeAt(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), quarterResOverride);
	}

	public Biome getBiomeAt(int x, int y, boolean useQuarterResolution)
			throws UnknownBiomeIdException, MinecraftInterfaceException {
		int biomeIndex = minecraftWorld.getBiomeData(x, y, 1, 1, useQuarterResolution, biomeData -> biomeData[0]);
		return biomeList.getById(biomeIndex);
	}

	private<T> T getQuarterResolutionBiomeData(int x, int y, int width, int height, Function<int[], T> biomeDataMapper)
			throws MinecraftInterfaceException {
		return minecraftWorld.getBiomeData(x, y, width, height, true, biomeDataMapper);
	}
}
