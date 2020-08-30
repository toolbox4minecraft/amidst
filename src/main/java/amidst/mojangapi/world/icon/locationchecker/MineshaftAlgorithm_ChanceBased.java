package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.Immutable;
import amidst.util.FastRand;

/**
 * As of v1.4.2 Minecraft switched to this version of the algorithm
 */
@Immutable
public class MineshaftAlgorithm_ChanceBased extends MineshaftAlgorithm_Base {
	private final double chancePerChunk;
	private final boolean extraCheck;

	public MineshaftAlgorithm_ChanceBased(long seed, double chancePerChunk, boolean extraCheck) {
		super(seed);
		this.chancePerChunk = chancePerChunk;
		this.extraCheck = extraCheck;
	}

	@Override
	protected boolean getResult(int chunkX, int chunkY, FastRand random) {
		return random.nextDouble() < chancePerChunk;
	}

	@Override
	protected boolean doExtraCheck() {
		return extraCheck;
	}
}
