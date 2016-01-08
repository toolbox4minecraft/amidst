package amidst.mojangapi.world.oracle;

import java.util.Random;

import amidst.documentation.Immutable;

@Immutable
public class SlimeChunkOracle {
	private final long seed;

	public SlimeChunkOracle(long seed) {
		this.seed = seed;
	}

	public boolean isSlimeChunk(long chunkX, long chunkY) {
		Random random = new Random(getSeed(chunkX, chunkY));
		return isSlimeChunk(random);
	}

	private long getSeed(long chunkX, long chunkY) {
		return seed + chunkX * chunkX * 0x4c1906 + chunkX * 0x5ac0db + chunkY
				* chunkY * 0x4307a7L + chunkY * 0x5f24f ^ 0x3ad8025f;
	}

	private boolean isSlimeChunk(Random random) {
		return random.nextInt(10) == 0;
	}
}
