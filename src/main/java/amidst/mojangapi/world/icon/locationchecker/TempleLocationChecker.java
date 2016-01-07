package amidst.mojangapi.world.icon.locationchecker;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class TempleLocationChecker extends AllValidLocationChecker {
	private static List<Biome> getValidBiomesAtMiddleOfChunk(
			RecognisedVersion recognisedVersion) {
		// @formatter:off
		if (recognisedVersion.isAtLeast(RecognisedVersion.V15w43c)) {
			return Arrays.asList(
				Biome.desert,
				Biome.desertHills,
				Biome.jungle,
				Biome.jungleHills,
				Biome.swampland,
				Biome.icePlains,
				Biome.coldTaiga
			);
		} else if (recognisedVersion.isAtLeast(RecognisedVersion.V1_4_2)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle,
					Biome.jungleHills,
					Biome.swampland
			);
		} else if (recognisedVersion.isAtLeast(RecognisedVersion.V12w22a)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle
			);
		} else {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills
			);
		}
		// @formatter:on
	}

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 14357617L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	public TempleLocationChecker(long seed, BiomeDataOracle biomeDataOracle,
			RecognisedVersion recognisedVersion) {
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
						getValidBiomesAtMiddleOfChunk(recognisedVersion)
				)
		);
		// @formatter:on
	}
}
