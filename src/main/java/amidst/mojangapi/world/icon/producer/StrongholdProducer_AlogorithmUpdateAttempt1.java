package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 *  Mojang's first attempt at raising the number of strongholds to 128.
 *  This algorithm introduced Issue MC-92289, see https://bugs.mojang.com/browse/MC-92289
 *  It was later fixed in version 16w06a.
 *  */
public class StrongholdProducer_AlogorithmUpdateAttempt1 extends StrongholdProducer_Base {

	public StrongholdProducer_AlogorithmUpdateAttempt1(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		super(seed, biomeDataOracle, validBiomes);
		
		Log.i("StrongholdProducer_AlogorithmUpdateAttempt1");
		
	}

	@Override
	protected int getTotalStructureCount() { return 128; }
}
