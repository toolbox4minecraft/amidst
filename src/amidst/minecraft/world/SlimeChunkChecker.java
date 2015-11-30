package amidst.minecraft.world;

import java.util.Random;

public class SlimeChunkChecker {
	private final World world;
	private final Random random = new Random();

	public SlimeChunkChecker(World world) {
		this.world = world;
	}

	public boolean isSlimeChunk(long chunkX, long chunkY) {
		updateSeed(chunkX, chunkY);
		return isSlimeChunk();
	}

	private void updateSeed(long chunkX, long chunkY) {
		random.setSeed(getSeed(chunkX, chunkY));
	}

	private long getSeed(long chunkX, long chunkY) {
		return world.getSeed() + chunkX * chunkX * 0x4c1906 + chunkX * 0x5ac0db
				+ chunkY * chunkY * 0x4307a7L + chunkY * 0x5f24f ^ 0x3ad8025f;
	}

	private boolean isSlimeChunk() {
		return random.nextInt(10) == 0;
	}
}
