package amidst.minecraft.world;

import amidst.map.Fragment;
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
		return x;
	}

	public long getY() {
		return y;
	}

	public long getXAsChunkResolution() {
		return x >> 4;
	}

	public long getYAsChunkResolution() {
		return y >> 4;
	}

	public long getXAsFragmentResolution() {
		return x >> Fragment.SIZE_SHIFT;
	}

	public long getYAsFragmentResolution() {
		return y >> Fragment.SIZE_SHIFT;
	}

	public long getXAsQuarterResolution() {
		return x >> 2;
	}

	public long getYAsQuarterResolution() {
		return y >> 2;
	}

	public long getXCornerOfFragment() {
		return CoordinateUtils.toFragmentCorner(x);
	}

	public long getYCornerOfFragment() {
		return CoordinateUtils.toFragmentCorner(y);
	}

	public long getXCornerOfFragmentAsChunkResolution() {
		return CoordinateUtils.toFragmentCorner(x) >> 4;
	}

	public long getYCornerOfFragmentAsChunkResolution() {
		return CoordinateUtils.toFragmentCorner(y) >> 4;
	}

	public long getXCornerOfFragmentAsFragmentResolution() {
		return CoordinateUtils.toFragmentCorner(x) >> Fragment.SIZE_SHIFT;
	}

	public long getYCornerOfFragmentAsFragmentResolution() {
		return CoordinateUtils.toFragmentCorner(y) >> Fragment.SIZE_SHIFT;
	}

	public long getXCornerOfFragmentAsQuarterResolution() {
		return CoordinateUtils.toFragmentCorner(x) >> 2;
	}

	public long getYCornerOfFragmentAsQuarterResolution() {
		return CoordinateUtils.toFragmentCorner(y) >> 2;
	}

	public long getXRelativeToFragment() {
		return CoordinateUtils.toFragmentRelative(x);
	}

	public long getYRelativeToFragment() {
		return CoordinateUtils.toFragmentRelative(y);
	}

	public long getXRelativeToFragmentAsChunkResolution() {
		return CoordinateUtils.toFragmentRelative(x) >> 4;
	}

	public long getYRelativeToFragmentAsChunkResolution() {
		return CoordinateUtils.toFragmentRelative(y) >> 4;
	}

	public long getXRelativeToFragmentAsFragmentResolution() {
		return CoordinateUtils.toFragmentRelative(x) >> Fragment.SIZE_SHIFT;
	}

	public long getYRelativeToFragmentAsFragmentResolution() {
		return CoordinateUtils.toFragmentRelative(y) >> Fragment.SIZE_SHIFT;
	}

	public long getXRelativeToFragmentAsQuarterResolution() {
		return CoordinateUtils.toFragmentRelative(x) >> 2;
	}

	public long getYRelativeToFragmentAsQuarterResolution() {
		return CoordinateUtils.toFragmentRelative(y) >> 2;
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
