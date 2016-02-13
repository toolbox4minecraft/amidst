package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 *  Mojang's first attempt at raising the number of strongholds to 128.
 *  This algorithm introduced Issue MC-92289, see https://bugs.mojang.com/browse/MC-92289
 *  It was later fixed in version V16w06a 
 *  */
public class StrongholdProducer_AlogorithmUpdateAttempt1 extends StrongholdProducer_Base {

	public StrongholdProducer_AlogorithmUpdateAttempt1(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		super(seed, biomeDataOracle, validBiomes);
		
		Log.i("StrongholdProducer_AlogorithmUpdateAttempt1");
		
	}

	/** The Minecraft version that introduced this stronghold position algorithm */
	public static RecognisedVersion IntroducedInVersion() {
		// 128 strongholds were actually introduced in 15w43a, but that snapshot is
		// no longer available, and Amidst wouldn't recognize it.
		return RecognisedVersion.V15w43c; 
	}		
	
	@Override
	protected int getTotalStructureCount() { return 128; }
}
