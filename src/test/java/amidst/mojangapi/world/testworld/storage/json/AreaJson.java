package amidst.mojangapi.world.testworld.storage.json;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.Dimension;

@Immutable
public class AreaJson implements Comparable<AreaJson> {
	// Default to Overworld, to support old areas
	private volatile int dimension = Dimension.OVERWORLD.getId();
	private volatile long x;
	private volatile long y;
	private volatile long width;
	private volatile long height;

	@GsonConstructor
	public AreaJson() {
	}

	public AreaJson(Dimension dimension, long x, long y, long width, long height) {
		this.dimension = dimension.getId();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Dimension getDimension() {
		return Dimension.fromId(dimension);
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public long getWidth() {
		return width;
	}

	public long getHeight() {
		return height;
	}

	@Override
	public int compareTo(AreaJson o) {
		if (this == o) {
			return 0;
		} else if (this.dimension < o.dimension) {
			return -1;
		} else if (this.dimension > o.dimension) {
			return 1;
		} else if (this.x < o.x) {
			return -1;
		} else if (this.x > o.x) {
			return 1;
		} else if (this.y < o.y) {
			return -1;
		} else if (this.y > o.y) {
			return 1;
		} else if (this.width < o.width) {
			return -1;
		} else if (this.width > o.width) {
			return 1;
		} else if (this.height < o.height) {
			return -1;
		} else if (this.height > o.height) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dimension;
		result = prime * result + (int) (height ^ (height >>> 32));
		result = prime * result + (int) (width ^ (width >>> 32));
		result = prime * result + (int) (x ^ (x >>> 32));
		result = prime * result + (int) (y ^ (y >>> 32));
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
		if (!(obj instanceof AreaJson)) {
			return false;
		}
		AreaJson other = (AreaJson) obj;
		if (dimension != other.dimension) {
			return false;
		}
		if (height != other.height) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
}
