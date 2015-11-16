package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectOceanMonument;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class OceanMonumentLayer extends IconLayer {
	// @formatter:off
	// TODO:why is this unused?
	// Not sure if the extended biomes count
	@SuppressWarnings("unused")
	private static final List<Biome> VALID_BIOMES = Arrays.asList(
			Biome.deepOcean,
			Biome.deepOceanM
	);
	
	// Not sure if the extended biomes count
	private static final List<Biome> VALID_SURROUNDING_BIOMES = Arrays.asList(
			Biome.ocean,
			Biome.deepOcean,
			Biome.frozenOcean,
			Biome.river,
			Biome.frozenRiver,
			Biome.oceanM,
			Biome.deepOceanM,
			Biome.frozenOceanM,
			Biome.riverM,
			Biome.frozenRiverM
	);
	// @formatter:on

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387313L;

	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 5;

	private static final int STRUCTURE_SIZE = 29;

	private Random random = new Random();

	@Override
	public boolean isVisible() {
		return Options.instance.showOceanMonuments.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y);
			}
		}
	}

	private void generateAt(Fragment fragment, int x, int y) {
		int chunkX = x + fragment.getChunkX();
		int chunkY = y + fragment.getChunkY();
		if (checkChunk(chunkX, chunkY)) {
			fragment.addObject(new MapObjectOceanMonument(x << 4, y << 4)
					.setParent(this));
		}
	}

	private boolean checkChunk(int chunkX, int chunkY) {
		int n = getInitialValue(chunkX);
		int i1 = getInitialValue(chunkY);
		updateSeed(n, i1);
		int distanceRange = getDistanceRange();
		n = updateValue(n, distanceRange);
		i1 = updateValue(i1, distanceRange);
		if (isSuccessful(chunkX, chunkY, n, i1)) {
			return isValid(middleOfChunk(chunkX), middleOfChunk(chunkY));
		} else {
			return false;
		}
	}

	private int getInitialValue(int value) {
		return getModified(value) / MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES;
	}

	private int getModified(int value) {
		if (value < 0) {
			return value - MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES + 1;
		} else {
			return value;
		}
	}

	private void updateSeed(int n, int i1) {
		random.setSeed(getSeed(n, i1));
	}

	private int getDistanceRange() {
		return MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES
				- MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES;
	}

	private long getSeed(int n, int i1) {
		return n * MAGIC_NUMBER_FOR_SEED_1 + i1 * MAGIC_NUMBER_FOR_SEED_2
				+ Options.instance.seed + MAGIC_NUMBER_FOR_SEED_3;
	}

	private int updateValue(int value, int distanceRange) {
		value *= MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES;
		value += (random.nextInt(distanceRange) + random.nextInt(distanceRange)) / 2;
		return value;
	}

	private boolean isSuccessful(int chunkX, int chunkY, int n, int i1) {
		return chunkX == n && chunkY == i1;
	}

	private int middleOfChunk(int value) {
		return value * 16 + 8;
	}

	private boolean isValid(int middleOfChunkX, int middleOfChunkY) {
		// Note that getBiomeAt() is full-resolution biome data, while
		// isValidBiome() is calculated using
		// quarter-resolution biome data. This is identical to how Minecraft
		// calculates it.
		return MinecraftUtil.getBiomeAt(middleOfChunkX, middleOfChunkY) == Biome.deepOcean
				&& MinecraftUtil.isValidBiome(middleOfChunkX, middleOfChunkY,
						STRUCTURE_SIZE, VALID_SURROUNDING_BIOMES);
	}
}
