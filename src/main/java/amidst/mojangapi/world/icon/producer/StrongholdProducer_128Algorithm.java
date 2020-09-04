package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.util.FastRand;

/**
 * This is the fixed version of the 128 stronghold algorithm. It introduced in
 * 16w06a.
 *
 * see https://bugs.mojang.com/browse/MC-92289
 */
@ThreadSafe
public class StrongholdProducer_128Algorithm extends StrongholdProducer_Buggy128Algorithm {
	public StrongholdProducer_128Algorithm(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		super(seed, biomeDataOracle, validBiomes);
	}

	@Override
	protected int getInitialValue_ring() {
		return 0;
	}

	@Override
	protected double getNextValue_startAngle(double currentValue, FastRand random) {
		// In this algorithm, the getInitialValue_startAngle() formula is used
		// at the start of each new ring.
		return currentValue + getInitialValue_startAngle(random);
	}

	@Override
	protected double getAngleDelta(int ring, int structuresPerRing) {
		return 6.283185307179586D / structuresPerRing;
	}

	@Override
	protected double getNextValue_distance(int currentRing, FastRand random) {
		return (4.0 * DISTANCE_IN_CHUNKS) + (6.0 * currentRing * DISTANCE_IN_CHUNKS)
				+ (random.nextDouble() - 0.5) * (DISTANCE_IN_CHUNKS * 2.5);
	}

	@Override
	protected int getNextValue_structuresPerRing(
			int currentValue,
			int currentRing,
			int structuresRemaining,
			FastRand random) {
		int result = currentValue + 2 * currentValue / (currentRing + 1);
		result = Math.min(result, structuresRemaining);
		return result;
	}
}
