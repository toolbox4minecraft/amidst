package amidst.mojangapi.world.icon.locationchecker;

import java.util.Random;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public abstract class MineshaftAlgorithm implements LocationChecker {
	private final static double CHANCE_SINCE_V1_7_2 = 0.004D;
	private final static double CHANCE_BEFORE_V1_7_2 = 0.01D;

	public static MineshaftAlgorithm from(long seed,
			RecognisedVersion recognisedVersion) {
		if (recognisedVersion.isAtLeast(RecognisedVersion.V1_4_2)) {
			// Use the current mineshaft algorithm
			// Mineshafts became less common from version 1.7.2 onward
			if (recognisedVersion.isAtLeast(RecognisedVersion.V1_7_2)) {
				return new ChanceBasedMineshaftAlgorithm(seed,
						CHANCE_SINCE_V1_7_2);
			} else {
				return new ChanceBasedMineshaftAlgorithm(seed,
						CHANCE_BEFORE_V1_7_2);
			}
		} else {
			// Use the original mineshaft algorithm (retired in v1.4.2).
			return new OriginalMineshaftAlgorithm(seed);
		}
	}

	private final long seed;

	public MineshaftAlgorithm(long seed) {
		this.seed = seed;
	}

	@Override
	public boolean isValidLocation(int chunkX, int chunkY) {
		Random random = new Random(seed);

		long var13 = (long) chunkX * random.nextLong();
		long var15 = (long) chunkY * random.nextLong();

		random.setSeed(var13 ^ var15 ^ seed);
		random.nextInt();

		return getResult(chunkX, chunkY, random)
				&& random.nextInt(80) < Math.max(Math.abs(chunkX),
						Math.abs(chunkY));
	}

	protected abstract boolean getResult(int chunkX, int chunkY, Random random);
}
