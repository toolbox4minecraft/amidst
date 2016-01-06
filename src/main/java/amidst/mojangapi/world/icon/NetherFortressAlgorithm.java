package amidst.mojangapi.world.icon;

import java.util.Random;

import amidst.documentation.Immutable;

@Immutable
public class NetherFortressAlgorithm {
	private final long seed;

	public NetherFortressAlgorithm(long seed) {
		this.seed = seed;
	}

	public boolean isValid(int x, int y) {
		int i = x >> 4;
		int j = y >> 4;
		Random random = new Random(i ^ j << 4 ^ seed);
		random.nextInt();
		// @formatter:off
		return random.nextInt(3) == 0
				&& x == (i << 4) + 4 + random.nextInt(8)
				&& y == (j << 4) + 4 + random.nextInt(8);
		// @formatter:on
	}
}
