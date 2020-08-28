package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class TwinScatteredFeaturesLocationChecker extends AllValidLocationChecker {
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;
	private static final int ALTERNATE_THRESHOLD = 2;

	public TwinScatteredFeaturesLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			byte maxDistanceBetweenFeatures, byte minDistanceBetweenFeatures,
			List<Biome> validBiomesAtMiddleOfChunk,
			int alternateVersionAbundance, boolean selectAlternate,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {
		super(getLocationChecker(seed, biomeDataOracle,
			maxDistanceBetweenFeatures, minDistanceBetweenFeatures,
			validBiomesAtMiddleOfChunk,
			alternateVersionAbundance, selectAlternate,
			magicNumber,
			buggyStructureCoordinateMath));
	}

	private static LocationChecker[] getLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle,
			byte maxDistanceBetweenFeatures, byte minDistanceBetweenFeatures,
			List<Biome> validBiomesAtMiddleOfChunk,
			int alternateVersionAbundance, boolean selectAlternate,
			long magicNumber,
			boolean buggyStructureCoordinateMath) {
		LocationChecker structure = new TwinStructureAlgorithm(
			seed, magicNumber,
			maxDistanceBetweenFeatures,
			minDistanceBetweenFeatures,
			alternateVersionAbundance, selectAlternate,
			buggyStructureCoordinateMath);

		if (validBiomesAtMiddleOfChunk == null) {
			return new LocationChecker[] { structure };
		} else {
			return new LocationChecker[] { structure,
					new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk) };
		}
	}

	private static class TwinStructureAlgorithm extends StructureAlgorithm {

		private final int alternateVersionAbundance;
		private final boolean selectAlternate;

		public TwinStructureAlgorithm(
				long seed, long structureSalt,
				byte maxDistanceBetweenScatteredFeatures, byte minDistanceBetweenScatteredFeatures,
				int alternateVersionAbundance, boolean selectAlternate,
				boolean buggyStructureCoordinateMath) {
			super(seed, structureSalt,
				maxDistanceBetweenScatteredFeatures,
				minDistanceBetweenScatteredFeatures,
				USE_TWO_VALUES_FOR_UPDATE,
				buggyStructureCoordinateMath);
			this.alternateVersionAbundance = alternateVersionAbundance;
			this.selectAlternate = selectAlternate;
		}

		@Override
		protected boolean doExtraCheck(Random random) {
			boolean alternate = random.nextInt(alternateVersionAbundance) >= ALTERNATE_THRESHOLD;
			return alternate == selectAlternate;
		}

	}
}
