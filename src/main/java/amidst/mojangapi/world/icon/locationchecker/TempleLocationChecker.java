package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class TempleLocationChecker implements LocationChecker {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	private final LocationChecker desertTempleChecker;
	private final LocationChecker iglooChecker;
	private final LocationChecker jungleTempleChecker;
	private final LocationChecker witchHutChecker;

	public TempleLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk_DesertTemple,
			List<Biome> validBiomesAtMiddleOfChunk_Igloo,
			List<Biome> validBiomesAtMiddleOfChunk_JungleTemple,
			List<Biome> validBiomesAtMiddleOfChunk_WitchHut,
			long magicNumber_DesertTemple,
			long magicNumber_Igloo,
			long magicNumber_JungleTemple,
			long magicNumber_WitchHut,
			boolean buggyStructureCoordinateMath) {

		this.desertTempleChecker = new AllValidLocationChecker(
			new StructureAlgorithm(
					seed,
					MAGIC_NUMBER_FOR_SEED_1,
					MAGIC_NUMBER_FOR_SEED_2,
					magicNumber_DesertTemple,
					MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					USE_TWO_VALUES_FOR_UPDATE,
					buggyStructureCoordinateMath),
			new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk_DesertTemple));

		this.iglooChecker = new AllValidLocationChecker(
			new StructureAlgorithm(
					seed,
					MAGIC_NUMBER_FOR_SEED_1,
					MAGIC_NUMBER_FOR_SEED_2,
					magicNumber_Igloo,
					MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					USE_TWO_VALUES_FOR_UPDATE,
					buggyStructureCoordinateMath),
			new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk_Igloo));

		this.jungleTempleChecker = new AllValidLocationChecker(
			new StructureAlgorithm(
					seed,
					MAGIC_NUMBER_FOR_SEED_1,
					MAGIC_NUMBER_FOR_SEED_2,
					magicNumber_JungleTemple,
					MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					USE_TWO_VALUES_FOR_UPDATE,
					buggyStructureCoordinateMath),
			new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk_JungleTemple));

		this.witchHutChecker = new AllValidLocationChecker(
			new StructureAlgorithm(
					seed,
					MAGIC_NUMBER_FOR_SEED_1,
					MAGIC_NUMBER_FOR_SEED_2,
					magicNumber_WitchHut,
					MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
					USE_TWO_VALUES_FOR_UPDATE,
					buggyStructureCoordinateMath),
			new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk_WitchHut));
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		return this.desertTempleChecker.isValidLocation(x, y) ||
			this.iglooChecker.isValidLocation(x,  y) ||
			this.jungleTempleChecker.isValidLocation(x, y) ||
			this.witchHutChecker.isValidLocation(x, y);
	}
}
