package amidst.mojangapi.world.icon;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class VillageProducer extends StructureProducer {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387312L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	private final LocationChecker checker;

	public VillageProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle, recognisedVersion);
		this.checker = new StructureAlgorithm(seed, MAGIC_NUMBER_FOR_SEED_1,
				MAGIC_NUMBER_FOR_SEED_2, MAGIC_NUMBER_FOR_SEED_3,
				MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
				MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
				USE_TWO_VALUES_FOR_UPDATE);
	}

	@Override
	protected boolean isValidLocation() {
		return checker.isValidLocation(chunkX, chunkY)
				&& biomeDataOracle.isValidBiomeForStructureAtMiddleOfChunk(
						chunkX, chunkY, structureSize, validBiomesForStructure);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		return DefaultWorldIconTypes.VILLAGE;
	}

	@Override
	protected List<Biome> getValidBiomesForStructure() {
		// @formatter:off
		return Arrays.asList(
				Biome.plains,
				Biome.desert,
				Biome.savanna
		);
		// @formatter:on
	}

	@Override
	protected List<Biome> getValidBiomesAtMiddleOfChunk() {
		return null; // not used
	}

	@Override
	protected int getStructureSize() {
		return 0;
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
