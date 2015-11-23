package amidst.minecraft.world;

import amidst.utilities.CoordinateUtils;

public class CoordinatesInWorld {
	public static CoordinatesInWorld from(long x, long y) {
		return new CoordinatesInWorld(x, y);
	}

	private static CoordinatesInWorld from(CoordinatesInWorld base,
			long deltaX, long deltaY) {
		return new CoordinatesInWorld(base.x + deltaX, base.y + deltaY);
	}

	public static CoordinatesInWorld origin() {
		return ORIGIN;
	}

	private static final CoordinatesInWorld ORIGIN = CoordinatesInWorld.from(0,
			0);

	private final long x;
	private final long y;

	private CoordinatesInWorld(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return CoordinatesResolution.WORLD.shift(x);
	}

	public long getY() {
		return CoordinatesResolution.WORLD.shift(y);
	}

	public long getXAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(x);
	}

	public long getYAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(y);
	}

	public long getXAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(x);
	}

	public long getYAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(y);
	}

	public long getXAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(x);
	}

	public long getYAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(y);
	}

	public long getXCornerOfFragment() {
		return CoordinatesResolution.WORLD.shift(CoordinateUtils
				.toFragmentCorner(x));
	}

	public long getYCornerOfFragment() {
		return CoordinatesResolution.WORLD.shift(CoordinateUtils
				.toFragmentCorner(y));
	}

	public long getXCornerOfFragmentAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(CoordinateUtils
				.toFragmentCorner(x));
	}

	public long getYCornerOfFragmentAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(CoordinateUtils
				.toFragmentCorner(y));
	}

	public long getXCornerOfFragmentAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(CoordinateUtils
				.toFragmentCorner(x));
	}

	public long getYCornerOfFragmentAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(CoordinateUtils
				.toFragmentCorner(y));
	}

	public long getXCornerOfFragmentAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(CoordinateUtils
				.toFragmentCorner(x));
	}

	public long getYCornerOfFragmentAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(CoordinateUtils
				.toFragmentCorner(y));
	}

	public long getXRelativeToFragment() {
		return CoordinatesResolution.WORLD.shift(CoordinateUtils
				.toFragmentRelative(x));
	}

	public long getYRelativeToFragment() {
		return CoordinatesResolution.WORLD.shift(CoordinateUtils
				.toFragmentRelative(y));
	}

	public long getXRelativeToFragmentAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(CoordinateUtils
				.toFragmentRelative(x));
	}

	public long getYRelativeToFragmentAsChunkResolution() {
		return CoordinatesResolution.CHUNK.shift(CoordinateUtils
				.toFragmentRelative(y));
	}

	public long getXRelativeToFragmentAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(CoordinateUtils
				.toFragmentRelative(x));
	}

	public long getYRelativeToFragmentAsFragmentResolution() {
		return CoordinatesResolution.FRAGMENT.shift(CoordinateUtils
				.toFragmentRelative(y));
	}

	public long getXRelativeToFragmentAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(CoordinateUtils
				.toFragmentRelative(x));
	}

	public long getYRelativeToFragmentAsQuarterResolution() {
		return CoordinatesResolution.QUARTER.shift(CoordinateUtils
				.toFragmentRelative(y));
	}

	public CoordinatesInWorld toFragmentCorner() {
		return from(getXCornerOfFragment(), getYCornerOfFragment());
	}

	public CoordinatesInWorld add(long x, long y) {
		return from(this, x, y);
	}

	public CoordinatesInWorld add(CoordinatesInWorld other) {
		return from(this, other.x, other.y);
	}

	public CoordinatesInWorld substract(long x, long y) {
		return from(this, -x, -y);
	}

	public CoordinatesInWorld substract(CoordinatesInWorld other) {
		return from(this, -other.x, -other.y);
	}

	public boolean isInBoundsOf(CoordinatesInWorld corner, long size) {
		return CoordinateUtils.isInBounds(x, y, corner.x, corner.y, size, size);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (!(obj instanceof CoordinatesInWorld)) {
			return false;
		}
		CoordinatesInWorld other = (CoordinatesInWorld) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
