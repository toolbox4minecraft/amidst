package amidst.mojangapi.world.oracle;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeData;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public abstract class BiomeDataOracle {

	public BiomeDataOracle() {}
		
	/**
	 * Returns the biome data of the specified region.
	 * 
	 * CAUTION: the BiomeData object returned is a direct view into
	 * {@link MinecraftInterface}'s internal buffers, and so will be invalidated
	 * by future calls to {@link #getBiomeData}. 
	 */
	public BiomeData getBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		region = region.getAs(Resolution.from(useQuarterResolution));
		return doGetBiomeData(region, useQuarterResolution);
	}
	
	protected abstract BiomeData doGetBiomeData(Region.Box region, boolean useQuarterResolution)
			 throws MinecraftInterfaceException;

	public boolean isValidBiomeAtMiddleOfChunk(int chunkX, int chunkY, List<Biome> validBiomes) {
		return isValidBiome(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY), validBiomes);
	}

	private boolean isValidBiome(int x, int y, List<Biome> validBiomes) {
		try {
			return validBiomes.contains(Biome.getByIndex(getBiomeAt(x, y)));
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
			BiomeData biomeData = doGetBiomeData(Region.box(left,  top, width, height), true);
			return biomeData.checkAll((bx, by, b) -> {
				try {
					return validBiomes.contains(Biome.getByIndex(b));
				} catch (UnknownBiomeIndexException e) {
					return false;
				}
			});
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
			Coordinates[] result = {null};
			int[] numberOfValidLocations = {0};
			
			BiomeData biomeData = doGetBiomeData(Region.box(left, top, width, height), true);
			biomeData.checkAll((bx, by, b) -> {
				try {
					if (validBiomes.contains(Biome.getByIndex(b))
							&& (result[0] == null || random.nextInt(numberOfValidLocations[0] + 1) == 0)) {
						result[0] = createCoordinates(left, top, bx, by);
						numberOfValidLocations[0]++;
					}
				} catch (UnknownBiomeIndexException e) {
					return false;
				}
			
				return true;
			});	
			
			return result[0];
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return null;
		}
	}

	private Coordinates createCoordinates(int left, int top, int x, int y) {
		x = left + x << 2;
		y = top + y << 2;
		return Coordinates.from(x, y);
	}

	private int getMiddleOfChunk(int coordinate) {
		return coordinate * 16 + 8;
	}

	public Biome getBiomeAtMiddleOfChunk(int chunkX, int chunkY)
			throws UnknownBiomeIndexException,
			MinecraftInterfaceException {
		return Biome.getByIndex(getBiomeAt(getMiddleOfChunk(chunkX), getMiddleOfChunk(chunkY)));
	}

	/**
	 * Gets the biome located at the block-coordinates. This is not a fast
	 * routine, it was added for rare things like accurately testing structures.
	 * (uses the 1:1 scale biome-map)
	 */
	public short getBiomeAt(int x, int y) throws MinecraftInterfaceException {
		return doGetBiomeData(Region.box(x, y, 1, 1), false).get(0, 0);
	}
}
