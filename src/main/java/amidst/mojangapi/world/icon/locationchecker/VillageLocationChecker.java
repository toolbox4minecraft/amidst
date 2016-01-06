package amidst.mojangapi.world.icon.locationchecker;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class VillageLocationChecker extends AllValidLocationChecker {
	private static List<Biome> getValidBiomesForStructure() {
		// @formatter:off
		return Arrays.asList(
				Biome.plains,
				Biome.desert,
				Biome.savanna
		);
		// @formatter:on
	}

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387312L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;
	private static final int STRUCTURE_SIZE = 0;
	private static final List<Biome> VALID_BIOMES_FOR_STRUCTURE = getValidBiomesForStructure();

	public VillageLocationChecker(long seed, BiomeDataOracle biomeDataOracle) {
		// @formatter:off
		super(new StructureAlgorithm(
						seed,
						MAGIC_NUMBER_FOR_SEED_1,
						MAGIC_NUMBER_FOR_SEED_2,
						MAGIC_NUMBER_FOR_SEED_3,
						MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						USE_TWO_VALUES_FOR_UPDATE
				), new StructureBiomeLocationChecker(
						biomeDataOracle,
						STRUCTURE_SIZE,
						VALID_BIOMES_FOR_STRUCTURE
				)
		);
		// @formatter:on
	}
}
