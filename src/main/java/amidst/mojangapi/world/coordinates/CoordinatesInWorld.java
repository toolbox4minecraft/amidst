package amidst.mojangapi.world.coordinates;

import java.awt.Point;

import amidst.documentation.Immutable;

@Immutable
public class CoordinatesInWorld implements Comparable<CoordinatesInWorld> {
	public static CoordinatesInWorld tryParse(String coordinates) {
		String[] parsedCoordinates = coordinates.replaceAll(" ", "").split(",");
		if (parsedCoordinates.length != 2) {
			return null;
		}
		try {
			return CoordinatesInWorld.from(Long.parseLong(parsedCoordinates[0]), Long.parseLong(parsedCoordinates[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static CoordinatesInWorld from(long xInWorld, long yInWorld) {
		return new CoordinatesInWorld(xInWorld, yInWorld);
	}

	public static CoordinatesInWorld from(long xAsResolution, long yAsResolution, Resolution resolution) {
		return new CoordinatesInWorld(
				resolution.convertFromThisToWorld(xAsResolution),
				resolution.convertFromThisToWorld(yAsResolution));
	}

	private static CoordinatesInWorld from(CoordinatesInWorld base, long deltaXInWorld, long deltaYInWorld) {
		return new CoordinatesInWorld(base.xInWorld + deltaXInWorld, base.yInWorld + deltaYInWorld);
	}

	public static CoordinatesInWorld origin() {
		return ORIGIN;
	}

	private static final CoordinatesInWorld ORIGIN = CoordinatesInWorld.from(0, 0);

	private final long xInWorld;
	private final long yInWorld;

	public CoordinatesInWorld(long xInWorld, long yInWorld) {
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	public long getX() {
		return xInWorld;
	}

	public long getY() {
		return yInWorld;
	}

	public long getXAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(xInWorld);
	}

	public long getYAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(yInWorld);
	}

	public long getXCornerOfFragment() {
		return CoordinateUtils.toFragmentCorner(xInWorld);
	}

	public long getYCornerOfFragment() {
		return CoordinateUtils.toFragmentCorner(yInWorld);
	}

	public long getXCornerOfFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils.toFragmentCorner(xInWorld));
	}

	public long getYCornerOfFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils.toFragmentCorner(yInWorld));
	}

	public long getXRelativeToFragment() {
		return CoordinateUtils.toFragmentRelative(xInWorld);
	}

	public long getYRelativeToFragment() {
		return CoordinateUtils.toFragmentRelative(yInWorld);
	}

	public long getXRelativeToFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils.toFragmentRelative(xInWorld));
	}

	public long getYRelativeToFragmentAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(CoordinateUtils.toFragmentRelative(yInWorld));
	}

	public CoordinatesInWorld toFragmentCorner() {
		return from(getXCornerOfFragment(), getYCornerOfFragment());
	}

	public double getDistance(CoordinatesInWorld other) {
		return Point.distance(xInWorld, yInWorld, other.xInWorld, other.yInWorld);
	}

	public double getDistance(long xInWorld, long yInWorld) {
		return Point.distance(this.xInWorld, this.yInWorld, xInWorld, yInWorld);
	}

	public double getDistanceSq(CoordinatesInWorld other) {
		return Point.distanceSq(xInWorld, yInWorld, other.xInWorld, other.yInWorld);
	}

	public double getDistanceSq(long xInWorld, long yInWorld) {
		return Point.distanceSq(this.xInWorld, this.yInWorld, xInWorld, yInWorld);
	}

	public long getDistanceChebyshev(CoordinatesInWorld other) {
		return Math.max(Math.abs(xInWorld - other.xInWorld), Math.abs(yInWorld - other.yInWorld));
	}

	public long getDistanceChebyshev(long xInWorld, long yInWorld) {
		return Math.max(Math.abs(this.xInWorld - xInWorld), Math.abs(this.yInWorld - yInWorld));
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
		return CoordinateUtils.isInBounds(xInWorld, yInWorld, corner.xInWorld, corner.yInWorld, size, size);
	}

	@Override
	public int compareTo(CoordinatesInWorld o) {
		if (this == o) {
			return 0;
		} else if (this.xInWorld < o.xInWorld) {
			return -1;
		} else if (this.xInWorld > o.xInWorld) {
			return 1;
		} else if (this.yInWorld < o.yInWorld) {
			return -1;
		} else if (this.yInWorld > o.yInWorld) {
			return 1;
		} else {
			return 0;
		}
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

	public String toString(Resolution resolution) {
		return "[" + getXAs(resolution) + ", " + getYAs(resolution) + "]";
	}
}
