package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.util.FastRand;

@ThreadSafe
public class BiomeDataOracle {
	private final MinecraftInterface.World minecraftWorld;
	private final Dimension dimension;
	private final BiomeList biomeList;
	private final boolean quarterResOverride;
	private final int middleOfChunkOffset;
	private final boolean accurateLocationCount;

	public static class Config {
		public boolean quarterResOverride = true;
		public int middleOfChunkOffset = 9;
		public boolean accurateLocationCount = true;
	}

	public BiomeDataOracle(MinecraftInterface.World minecraftWorld, Dimension dimension, BiomeList biomeList, Config config) {
		this.minecraftWorld = minecraftWorld;
		this.dimension = dimension;
		this.biomeList = biomeList;
		this.quarterResOverride = config.quarterResOverride;
		this.middleOfChunkOffset = config.middleOfChunkOffset;
		this.accurateLocationCount = config.accurateLocationCount;
	}

	public void getBiomeData(CoordinatesInWorld corner, int width, int height, boolean useQuarterResolution,
			Consumer<int[]> biomeDataConsumer) {
		getBiomeData(corner, width, height, useQuarterResolution, data -> {
			biomeDataConsumer.accept(data);
			return null;
		}, () -> null);
	}

	// Pass biome data to the mapper as a row-major int array; or return the default value if an error occured.
	// width and height represent the number of samples, NOT the size of the region in the world.
	public<T> T getBiomeData(CoordinatesInWorld corner, int width, int height, boolean useQuarterResolution,
			Function<int[], T> biomeDataMapper, Supplier<T> defaultValue) {
		Resolution resolution = Resolution.from(useQuarterResolution);
		int left = (int) corner.getXAs(resolution);
		int top = (int) corner.getYAs(resolution);
		try {
			return minecraftWorld.getBiomeData(dimension, left, top, width, height, useQuarterResolution, biomeDataMapper);
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return defaultValue.get();
		}
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

	public boolean isValidBiomeForStructureAtMiddleOfChunk(int chunkX, int chunkY, int size, List<Biome> validBiomes) { //FIXME: 1.16 changed to quarter res?
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
			FastRand random) {
		return findValidLocation(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), size, validBiomes, random);
	}

	public CoordinatesInWorld findValidLocation(int x, int y, int size, List<Biome> validBiomes, FastRand random) {
		return doFindValidLocation(x, y, size, validBiomes, random, accurateLocationCount);
	}

	// This algorithm slightly changed in the 1.13 snapshots: before,
	// numberOfValidLocations was only incremented if the random check
	// succeeded; it is now always incremented.
	private CoordinatesInWorld doFindValidLocation(
			int x, int y, int size, List<Biome> validBiomes,
			FastRand random, boolean accurateLocationCount) {
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
		int biomeIndex = minecraftWorld.getBiomeData(dimension, x, y, 1, 1, useQuarterResolution, biomeData -> biomeData[0]);
		return biomeList.getById(biomeIndex);
	}

	private<T> T getQuarterResolutionBiomeData(int x, int y, int width, int height, Function<int[], T> biomeDataMapper)
			throws MinecraftInterfaceException {
		return minecraftWorld.getBiomeData(dimension, x, y, width, height, true, biomeDataMapper);
	}
}
