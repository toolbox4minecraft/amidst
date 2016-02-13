package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 *  This algorithm fixes Issue MC-92289, see https://bugs.mojang.com/browse/MC-92289
 *  It was introduced in version V16w06a 
 *  */
public class StrongholdProducer_AlogorithmUpdated extends StrongholdProducer_AlogorithmUpdateAttempt1 {

	public StrongholdProducer_AlogorithmUpdated(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		super(seed, biomeDataOracle, validBiomes);
		
		Log.i("StrongholdProducer_AlogorithmUpdated");
	}

	/** The Minecraft version that introduced this stronghold position algorithm */
	public static RecognisedVersion IntroducedInVersion() {
		// The buggy implementation described in StrongholdProducer_AlogorithmUpdateAttempt1
		// was fixed in 6w06a.
		return RecognisedVersion.V16w06a; 
	}		
		
	@Override
	protected int getInitialValue_ring() { return 0; }

	@Override
	protected double getNextValue_startAngle(double currentValue, Random random) {
		// In this algorithm, the getInitialValue_startAngle() formula is used
		// at the start of each new ring.
		return currentValue + getInitialValue_startAngle(random); 
	}
	
	@Override
	protected double getAngleDelta(int ring, int structuresPerRing) {
		return 6.283185307179586D / structuresPerRing;
	}	
	
	@Override
	protected double getNextValue_distance(int currentRing, Random random) {
		return 
			(4.0 * DISTANCE_IN_CHUNKS) + 
			(6.0 * currentRing * DISTANCE_IN_CHUNKS) + 
			(random.nextDouble() - 0.5) * (DISTANCE_IN_CHUNKS * 2.5);
	}
	
	@Override
	protected int getNextValue_structuresPerRing(int currentValue, int currentRing, int structuresRemaining, Random random) { 
		int result = currentValue + 2 * currentValue / (currentRing + 1);
		result = Math.min(result, structuresRemaining);
		return result;
	}			
}
