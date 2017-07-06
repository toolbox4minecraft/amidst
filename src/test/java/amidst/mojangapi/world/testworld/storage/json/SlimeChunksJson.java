package amidst.mojangapi.world.testworld.storage.json;

import java.util.Arrays;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

@Immutable
@GsonObject
public class SlimeChunksJson {
	public static SlimeChunksJson from(World world) {
		SlimeChunkOracle oracle = world.getSlimeChunkOracle();
		int steps = Resolution.CHUNK.getStepsPer(Resolution.FRAGMENT);
		boolean[][] isSlimeChunk = new boolean[2 * steps][2 * steps];
		for (int x = 0; x < isSlimeChunk.length; x++) {
			for (int y = 0; y < isSlimeChunk[x].length; y++) {
				isSlimeChunk[x][y] = oracle.isSlimeChunk(x - steps, y - steps);
			}
		}
		return new SlimeChunksJson(isSlimeChunk);
	}

	private volatile boolean[][] isSlimeChunk;

	public SlimeChunksJson() {
	}

	public SlimeChunksJson(boolean[][] isSlimeChunk) {
		this.isSlimeChunk = isSlimeChunk;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(isSlimeChunk);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlimeChunksJson)) {
			return false;
		}
		SlimeChunksJson other = (SlimeChunksJson) obj;
		if (!Arrays.deepEquals(isSlimeChunk, other.isSlimeChunk)) {
			return false;
		}
		return true;
	}
}
