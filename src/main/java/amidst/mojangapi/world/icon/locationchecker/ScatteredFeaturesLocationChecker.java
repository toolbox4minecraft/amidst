package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class ScatteredFeaturesLocationChecker extends AllValidLocationChecker {
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	public ScatteredFeaturesLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {

		this(seed, biomeDataOracle,
			MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
			MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
			validBiomesAtMiddleOfChunk,
			magicNumber,
			buggyStructureCoordinateMath);
	}

	public ScatteredFeaturesLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			byte maxDistanceBetweenFeatures, byte minDistanceBetweenFeatures,
			List<Biome> validBiomesAtMiddleOfChunk,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {
		super(getLocationChecker(seed, biomeDataOracle,
			maxDistanceBetweenFeatures,
			minDistanceBetweenFeatures,
			validBiomesAtMiddleOfChunk,
			magicNumber,
			buggyStructureCoordinateMath));
	}

	private static LocationChecker[] getLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			byte maxDistanceBetweenFeatures, byte minDistanceBetweenFeatures,
			List<Biome> validBiomesAtMiddleOfChunk,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {
		LocationChecker structure = new StructureAlgorithm(
			seed,
			magicNumber,
			maxDistanceBetweenFeatures,
			minDistanceBetweenFeatures,
			USE_TWO_VALUES_FOR_UPDATE,
			buggyStructureCoordinateMath);

		if (validBiomesAtMiddleOfChunk == null) {
			return new LocationChecker[] { structure };
		} else {
			return new LocationChecker[] { structure,
					new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk) };
		}
	}
}
