package amidst.mojangapi.world.oracle.end;

public class SmallEndIsland {
	private final long blockX;
	private final long blockY;
	private final int height;
	private final int size;
	
	protected SmallEndIsland(long blockX, long blockY, int height, int size) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.height = height;
		this.size = size;
	}

	public long getX() {
		return blockX;
	}

	public long getY() {
		return blockY;
	}
	
	public int getHeight() {
		return height;
	}

	public int getSize() {
		return size;
	}
}
