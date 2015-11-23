package amidst.minecraft.world;

import amidst.utilities.CoordinateUtils;

public class CoordinatesInWorld {
	public static CoordinatesInWorld from(long xAsResolution,
			long yAsResolution, Resolution resolution) {
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
		return getXAs(Resolution.WORLD);
	}

	public long getY() {
		return getYAs(Resolution.WORLD);
	}

	public long getXAsChunkResolution() {
		return getXAs(Resolution.CHUNK);
	}

	public long getYAsChunkResolution() {
		return getYAs(Resolution.CHUNK);
	}

	public long getXAsFragmentResolution() {
		return getXAs(Resolution.FRAGMENT);
	}

	public long getYAsFragmentResolution() {
		return getYAs(Resolution.FRAGMENT);
	}

	public long getXAsQuarterResolution() {
		return getXAs(Resolution.QUARTER);
	}

	public long getYAsQuarterResolution() {
		return getYAs(Resolution.QUARTER);
	}

	public long getXAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(xInWorld);
	}

	public long getYAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(yInWorld);
	}

	public long getXCornerOfFragment() {
		return getXCornerOfFragmentAs(Resolution.WORLD);
	}

	public long getYCornerOfFragment() {
		return getYCornerOfFragmentAs(Resolution.WORLD);
	}

	public long getXCornerOfFragmentAsChunkResolution() {
		return getXCornerOfFragmentAs(Resolution.CHUNK);
	}

	public long getYCornerOfFragmentAsChunkResolution() {
		return getYCornerOfFragmentAs(Resolution.CHUNK);
	}

	public long getXCornerOfFragmentAsFragmentResolution() {
		return getXCornerOfFragmentAs(Resolution.FRAGMENT);
	}

	public long getYCornerOfFragmentAsFragmentResolution() {
		return getYCornerOfFragmentAs(Resolution.FRAGMENT);
	}

	public long getXCornerOfFragmentAsQuarterResolution() {
		return getXCornerOfFragmentAs(Resolution.QUARTER);
	}

	public long getYCornerOfFragmentAsQuarterResolution() {
		return getYCornerOfFragmentAs(Resolution.QUARTER);
	}

	public long getXCornerOfFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentCorner(xInWorld));
	}

	public long getYCornerOfFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentCorner(yInWorld));
	}

	public long getXRelativeToFragment() {
		return getXRelativeToFragmentAs(Resolution.WORLD);
	}

	public long getYRelativeToFragment() {
		return getYRelativeToFragmentAs(Resolution.WORLD);
	}

	public long getXRelativeToFragmentAsChunkResolution() {
		return getXRelativeToFragmentAs(Resolution.CHUNK);
	}

	public long getYRelativeToFragmentAsChunkResolution() {
		return getYRelativeToFragmentAs(Resolution.CHUNK);
	}

	public long getXRelativeToFragmentAsFragmentResolution() {
		return getXRelativeToFragmentAs(Resolution.FRAGMENT);
	}

	public long getYRelativeToFragmentAsFragmentResolution() {
		return getYRelativeToFragmentAs(Resolution.FRAGMENT);
	}

	public long getXRelativeToFragmentAsQuarterResolution() {
		return getXRelativeToFragmentAs(Resolution.QUARTER);
	}

	public long getYRelativeToFragmentAsQuarterResolution() {
		return getYRelativeToFragmentAs(Resolution.QUARTER);
	}

	public long getXRelativeToFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils
				.toFragmentRelative(xInWorld));
	}

	public long getYRelativeToFragmentAs(Resolution targetResolution) {
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
