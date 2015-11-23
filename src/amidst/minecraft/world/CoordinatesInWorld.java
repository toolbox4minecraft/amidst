package amidst.minecraft.world;

import amidst.utilities.CoordinateUtils;

public class CoordinatesInWorld {
	public static CoordinatesInWorld from(long xAsResolution,
			long yAsResolution, CoordinatesResolution resolution) {
		return new CoordinatesInWorld(
				resolution.convertFromThisToWorld(xAsResolution),
				resolution.convertFromThisToWorld(yAsResolution));
	}

	public static CoordinatesInWorld from(long xInWorld, long yInWorld) {
		return new CoordinatesInWorld(xInWorld, yInWorld);
	}

	private static CoordinatesInWorld from(CoordinatesInWorld base,
			long deltaXInWorld, long deltaYInWorld) {
		return new CoordinatesInWorld(base.xInWorld + deltaXInWorld,
				base.yInWorld + deltaYInWorld);
	}

	public static CoordinatesInWorld origin() {
		return ORIGIN;
	}

	private static final CoordinatesInWorld ORIGIN = CoordinatesInWorld.from(0,
			0);

	private final long xInWorld;
	private final long yInWorld;

	public CoordinatesInWorld(long xInWorld, long yInWorld) {
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	public long getX() {
		return getXAs(CoordinatesResolution.WORLD);
	}

	public long getY() {
		return getYAs(CoordinatesResolution.WORLD);
	}

	public long getXAsChunkResolution() {
		return getXAs(CoordinatesResolution.CHUNK);
	}

	public long getYAsChunkResolution() {
		return getYAs(CoordinatesResolution.CHUNK);
	}

	public long getXAsFragmentResolution() {
		return getXAs(CoordinatesResolution.FRAGMENT);
	}

	public long getYAsFragmentResolution() {
		return getYAs(CoordinatesResolution.FRAGMENT);
	}

	public long getXAsQuarterResolution() {
		return getXAs(CoordinatesResolution.QUARTER);
	}

	public long getYAsQuarterResolution() {
		return getYAs(CoordinatesResolution.QUARTER);
	}

	public long getXAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(xInWorld);
	}

	public long getYAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(yInWorld);
	}

	public long getXCornerOfFragment() {
		return getXCornerOfFragmentAs(CoordinatesResolution.WORLD);
	}

	public long getYCornerOfFragment() {
		return getYCornerOfFragmentAs(CoordinatesResolution.WORLD);
	}

	public long getXCornerOfFragmentAsChunkResolution() {
		return getXCornerOfFragmentAs(CoordinatesResolution.CHUNK);
	}

	public long getYCornerOfFragmentAsChunkResolution() {
		return getYCornerOfFragmentAs(CoordinatesResolution.CHUNK);
	}

	public long getXCornerOfFragmentAsFragmentResolution() {
		return getXCornerOfFragmentAs(CoordinatesResolution.FRAGMENT);
	}

	public long getYCornerOfFragmentAsFragmentResolution() {
		return getYCornerOfFragmentAs(CoordinatesResolution.FRAGMENT);
	}

	public long getXCornerOfFragmentAsQuarterResolution() {
		return getXCornerOfFragmentAs(CoordinatesResolution.QUARTER);
	}

	public long getYCornerOfFragmentAsQuarterResolution() {
		return getYCornerOfFragmentAs(CoordinatesResolution.QUARTER);
	}

	public long getXCornerOfFragmentAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentCorner(xInWorld));
	}

	public long getYCornerOfFragmentAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentCorner(yInWorld));
	}

	public long getXRelativeToFragment() {
		return getXRelativeToFragmentAs(CoordinatesResolution.WORLD);
	}

	public long getYRelativeToFragment() {
		return getYRelativeToFragmentAs(CoordinatesResolution.WORLD);
	}

	public long getXRelativeToFragmentAsChunkResolution() {
		return getXRelativeToFragmentAs(CoordinatesResolution.CHUNK);
	}

	public long getYRelativeToFragmentAsChunkResolution() {
		return getYRelativeToFragmentAs(CoordinatesResolution.CHUNK);
	}

	public long getXRelativeToFragmentAsFragmentResolution() {
		return getXRelativeToFragmentAs(CoordinatesResolution.FRAGMENT);
	}

	public long getYRelativeToFragmentAsFragmentResolution() {
		return getYRelativeToFragmentAs(CoordinatesResolution.FRAGMENT);
	}

	public long getXRelativeToFragmentAsQuarterResolution() {
		return getXRelativeToFragmentAs(CoordinatesResolution.QUARTER);
	}

	public long getYRelativeToFragmentAsQuarterResolution() {
		return getYRelativeToFragmentAs(CoordinatesResolution.QUARTER);
	}

	public long getXRelativeToFragmentAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentRelative(xInWorld));
	}

	public long getYRelativeToFragmentAs(CoordinatesResolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentRelative(yInWorld));
	}

	public CoordinatesInWorld toFragmentCorner() {
		return from(getXCornerOfFragment(), getYCornerOfFragment());
	}

	public CoordinatesInWorld add(CoordinatesInWorld other) {
		return add(other.xInWorld, other.yInWorld);
	}

	public CoordinatesInWorld add(long xInWorld, long yInWorld) {
		return from(this, xInWorld, yInWorld);
	}

	public CoordinatesInWorld substract(CoordinatesInWorld other) {
		return substract(other.xInWorld, other.yInWorld);
	}

	public CoordinatesInWorld substract(long xInWorld, long yInWorld) {
		return from(this, -xInWorld, -yInWorld);
	}

	public boolean isInBoundsOf(CoordinatesInWorld corner, long size) {
		return CoordinateUtils.isInBounds(xInWorld, yInWorld, corner.xInWorld,
				corner.yInWorld, size, size);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (xInWorld ^ (xInWorld >>> 32));
		result = prime * result + (int) (yInWorld ^ (yInWorld >>> 32));
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
		if (xInWorld != other.xInWorld) {
			return false;
		}
		if (yInWorld != other.yInWorld) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[" + xInWorld + ", " + yInWorld + "]";
	}
}
