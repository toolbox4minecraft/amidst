package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class StrongholdProducer_Original extends StrongholdProducer_Base {
	public StrongholdProducer_Original(long seed, BiomeDataOracle biomeDataOracle, List<Integer> validBiomeIds) {
		super(seed, biomeDataOracle, validBiomeIds);
	}
}
