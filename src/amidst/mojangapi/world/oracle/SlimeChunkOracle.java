package amidst.mojangapi.world.oracle;

import java.util.Random;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class SlimeChunkOracle {
	private final World world;
	private final Random random = new Random();

	public SlimeChunkOracle(World world) {
		this.world = world;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public boolean isSlimeChunk(long chunkX, long chunkY) {
		updateSeed(chunkX, chunkY);
		return isSlimeChunk();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void updateSeed(long chunkX, long chunkY) {
		random.setSeed(getSeed(chunkX, chunkY));
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private long getSeed(long chunkX, long chunkY) {
		return world.getSeed() + chunkX * chunkX * 0x4c1906 + chunkX * 0x5ac0db
				+ chunkY * chunkY * 0x4307a7L + chunkY * 0x5f24f ^ 0x3ad8025f;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private boolean isSlimeChunk() {
		return random.nextInt(10) == 0;
	}
}
