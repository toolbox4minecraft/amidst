package amidst.mojangapi.world.icon.locationchecker;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class OceanMonumentLocationChecker extends AllValidLocationChecker {
	private static List<Biome> getValidBiomesForStructure() {
		// @formatter:off
		// Not sure if the extended biomes count
		return Arrays.asList(
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
	}

	private static List<Biome> getValidBiomesAtMiddleOfChunk() {
		// @formatter:off
		// Not sure if the extended biomes count
		return Arrays.asList(
				Biome.deepOcean
		//		Biome.deepOceanM
		);
		// @formatter:on
	}

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387313L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 5;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = true;
	private static final int STRUCTURE_SIZE = 29;
	private static final List<Biome> VALID_BIOMES_FOR_STRUCTURE = getValidBiomesForStructure();
	private static final List<Biome> VALID_BIOMES_AT_MIDDLE_OF_CHUNK = getValidBiomesAtMiddleOfChunk();

	public OceanMonumentLocationChecker(long seed,
			BiomeDataOracle biomeDataOracle) {
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
						VALID_BIOMES_AT_MIDDLE_OF_CHUNK
				), new StructureBiomeLocationChecker(
						biomeDataOracle,
						STRUCTURE_SIZE,
						VALID_BIOMES_FOR_STRUCTURE
				)
		);
		// @formatter:on
	}
}
