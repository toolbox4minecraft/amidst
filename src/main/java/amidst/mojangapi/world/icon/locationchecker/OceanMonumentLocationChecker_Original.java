package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class OceanMonumentLocationChecker_Original extends AllValidLocationChecker {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387313L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 5;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = true;
	private static final int STRUCTURE_SIZE = 29;

	public OceanMonumentLocationChecker_Original(long seed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			List<Biome> validBiomesForStructure) {
		// @formatter:off
		super(new StructureAlgorithm(
						seed,
						MAGIC_NUMBER_FOR_SEED_1,
						MAGIC_NUMBER_FOR_SEED_2,
						MAGIC_NUMBER_FOR_SEED_3,
						MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						USE_TWO_VALUES_FOR_UPDATE
				), new BiomeLocationChecker(
						biomeDataOracle,
						validBiomesAtMiddleOfChunk
				), new StructureBiomeLocationChecker(
						biomeDataOracle,
						STRUCTURE_SIZE,
						validBiomesForStructure
				)
		);
		// @formatter:on
	}
}
