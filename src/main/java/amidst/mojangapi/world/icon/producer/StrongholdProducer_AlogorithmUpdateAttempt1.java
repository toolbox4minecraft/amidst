package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 * This is the buggy version of the 128 stronghold algorithm. It was introduced
 * in 15w43a and fixed in 16w06a.
 * 
 * see https://bugs.mojang.com/browse/MC-92289
 */
@ThreadSafe
public class StrongholdProducer_AlogorithmUpdateAttempt1 extends
		StrongholdProducer_Base {
	public StrongholdProducer_AlogorithmUpdateAttempt1(long seed,
			BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		super(seed, biomeDataOracle, validBiomes);
		Log.i("StrongholdProducer_AlogorithmUpdateAttempt1");
	}

	@Override
	protected int getTotalStructureCount() {
		return 128;
	}
}
