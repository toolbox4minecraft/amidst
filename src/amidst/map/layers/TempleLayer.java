package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObject;
import amidst.map.MapObjectDesertTemple;
import amidst.map.MapObjectJungleTemple;
import amidst.map.MapObjectWitchHut;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class TempleLayer extends IconLayer {
	// @formatter:off
	private static final List<Biome> VALID_BIOMES_DEFAULT = Arrays.asList(
			Biome.desert,
			Biome.desertHills
	);
	
	private static final List<Biome> VALID_BIOMES_12w22a = Arrays.asList(
			Biome.desert,
			Biome.desertHills,
			Biome.jungle
	);
	
	private static final List<Biome> VALID_BIOMES_1_4_2 = Arrays.asList(
			Biome.desert,
			Biome.desertHills,
			Biome.jungle,
			Biome.jungleHills,
			Biome.swampland
	);
	// @formatter:on

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 14357617L;

	private static final int MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final int MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;

	private Random random = new Random();

	@Override
	public boolean isVisible() {
		return Options.instance.showTemples.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		List<Biome> validBiomes = getValidBiomes();
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y, validBiomes);
			}
		}
	}

	private List<Biome> getValidBiomes() {
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V1_4_2)) {
			return VALID_BIOMES_1_4_2;
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w22a)) {
			return VALID_BIOMES_12w22a;
		} else {
			return VALID_BIOMES_DEFAULT;
		}
	}

	private void generateAt(Fragment fragment, int x, int y,
			List<Biome> validBiomes) {
		int chunkX = x + fragment.getChunkX();
		int chunkY = y + fragment.getChunkY();
		Biome chunkBiome = checkChunk(chunkX, chunkY, validBiomes);
		if (chunkBiome != null) {
			MapObject mapObject = getTempleMapObject(chunkBiome, x, y);
			if (mapObject != null) {
				fragment.addObject(mapObject);
			} else {
				Log.e("No known structure for this biome type. checkChunk() may be faulting.");
			}
		}
	}

	/**
	 * @return null if there is no structure in the chunk, otherwise returns the
	 *         biome (from validBiomes) that determines the type of structure.
	 */
	private Biome checkChunk(int chunkX, int chunkY, List<Biome> validBiomes) {
		int n = getInitialValue(chunkX);
		int i1 = getInitialValue(chunkY);
		updateSeed(n, i1);
		int distanceRange = getDistanceRange();
		n = updateValue(n, distanceRange);
		i1 = updateValue(i1, distanceRange);
		if (isSuccessful(chunkX, chunkY, n, i1)) {
			return getBiome(middleOfChunk(chunkX), middleOfChunk(chunkY),
					validBiomes);
		} else {
			return null;
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
		value += random.nextInt(distanceRange);
		return value;
	}

	private boolean isSuccessful(int chunkX, int chunkY, int n, int i1) {
		return chunkX == n && chunkY == i1;
	}

	private int middleOfChunk(int value) {
		return value * 16 + 8;
	}

	private Biome getBiome(int middleOfChunkX, int middleOfChunkY,
			List<Biome> validBiomes) {
		// This is a potential feature biome

		// Since the structure-size that would be passed to
		// MinecraftUtil.isValidBiome()
		// is 0, we can use MinecraftUtil.getBiomeAt() here instead, which
		// tells us what kind of
		// structure it is.
		Biome result = MinecraftUtil.getBiomeAt(middleOfChunkX, middleOfChunkY);
		if (validBiomes.contains(result)) {
			return result;
		} else {
			return null;
		}
	}

	private MapObject getTempleMapObject(Biome chunkBiome, int x, int y) {
		if (chunkBiome == Biome.swampland) {
			return new MapObjectWitchHut(x << 4, y << 4).setParent(this);
		} else if (chunkBiome.name.contains("Jungle")) {
			return new MapObjectJungleTemple(x << 4, y << 4).setParent(this);
		} else if (chunkBiome.name.contains("Desert")) {
			return new MapObjectDesertTemple(x << 4, y << 4).setParent(this);
		} else {
			return null;
		}
	}
}
