package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class ScatteredFeaturesLocationChecker extends AllValidLocationChecker {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	public ScatteredFeaturesLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {

		super(
				new StructureAlgorithm(
					seed,
					MAGIC_NUMBER_FOR_SEED_1,
					MAGIC_NUMBER_FOR_SEED_2,
					magicNumber,
					MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					USE_TWO_VALUES_FOR_UPDATE,
					buggyStructureCoordinateMath),
				new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk));
	}
}
