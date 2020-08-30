package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.util.FastRand;

@ThreadSafe
public class BuriedTreasureLocationChecker extends AllValidLocationChecker {
	private static final float BURIED_TREASURE_CHANCE = 0.01F;

	public BuriedTreasureLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomesForStructure, long seedForStructure) {
		super(
			new BuriedTreasureAlgorithm(seed + seedForStructure, BURIED_TREASURE_CHANCE),
			new BiomeLocationChecker(biomeDataOracle, validBiomesForStructure)
		);
	}

	private static class BuriedTreasureAlgorithm implements LocationChecker {
		private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
		private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;

		private final long seed;
		private final float chance;

		public BuriedTreasureAlgorithm(long seed, float chance) {
			this.seed = seed;
			this.chance = chance;
		}

		@Override
		public boolean isValidLocation(int x, int y) {
			FastRand random = new FastRand(x*MAGIC_NUMBER_FOR_SEED_1 + y*MAGIC_NUMBER_FOR_SEED_2 + seed);
			return random.nextFloat() < chance;
		}
	}
}
