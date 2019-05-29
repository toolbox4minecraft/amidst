package amidst.mojangapi.world.icon.locationchecker;

import java.util.Random;

import amidst.documentation.Immutable;

@Immutable
public abstract class MineshaftAlgorithm_Base implements LocationChecker {
	private final long seed;

	public MineshaftAlgorithm_Base(long seed) {
		this.seed = seed;
	}

	@Override
	public boolean isValidLocation(int chunkX, int chunkY) {
		/**
		 * Note: even if this check succeeds, the mineshaft may fail to generate if the
		 * central room isn't in a suitable location (for example, if it spawns inside
		 * a cave or a ravine). We can't check these cases, so we will have to accept
		 * some false positives.
		 */
		Random random = new Random(seed);

		long var13 = chunkX * random.nextLong();
		long var15 = chunkY * random.nextLong();

		random.setSeed(var13 ^ var15 ^ seed);
		if(doExtraCheck())
			random.nextInt();

		if(!getResult(chunkX, chunkY, random))
			return false;
		return !doExtraCheck() || random.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkY));
	}

	protected abstract boolean getResult(int chunkX, int chunkY, Random random);

	protected boolean doExtraCheck() {
		return true;
	}
}
