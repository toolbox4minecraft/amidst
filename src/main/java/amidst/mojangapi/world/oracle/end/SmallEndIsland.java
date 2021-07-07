package amidst.mojangapi.world.oracle.end;

import java.awt.Point;
import java.util.Objects;

public class SmallEndIsland {
	private final long blockX;
	private final long blockY;
	private final int height;
	private int size;
	
	protected SmallEndIsland(long blockX, long blockY, int height, int size) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.height = height;
		this.size = size;
	}
	
	protected SmallEndIsland(long blockX, long blockY, int height) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.height = height;
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
	
	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
	
	public boolean isOnIsland(long x, long y) {
		return Point.distanceSq(x, y, blockX, blockY) <= size * size;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(blockX, blockY, height, size);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SmallEndIsland)) {
			return false;
		}
		SmallEndIsland other = (SmallEndIsland) obj;
		if (blockX != other.blockX) {
			return false;
		}
		if (blockY != other.blockY) {
			return false;
		}
		if (height != other.height) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}
}
