package amidst.mojangapi.world.coordinates;

import java.awt.Point;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class Coordinates implements Comparable<Coordinates> {
	public static Coordinates tryParse(String coordinates) {
		String[] parsedCoordinates = coordinates.replaceAll(" ", "").split(",");
		if (parsedCoordinates.length != 2) {
			return null;
		}
		try {
			return Coordinates.from(Long.parseLong(parsedCoordinates[0]), Long.parseLong(parsedCoordinates[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Coordinates from(long xInWorld, long yInWorld) {
		return new Coordinates(xInWorld, yInWorld);
	}

	public static Coordinates from(long xAsResolution, long yAsResolution, Resolution resolution) {
		return new Coordinates(
				resolution.convertFromThisToWorld(xAsResolution),
				resolution.convertFromThisToWorld(yAsResolution));
	}

	private static Coordinates from(Coordinates base, long deltaXInWorld, long deltaYInWorld) {
		return new Coordinates(base.xInWorld + deltaXInWorld, base.yInWorld + deltaYInWorld);
	}

	public static Coordinates origin() {
		return ORIGIN;
	}

	private static final Coordinates ORIGIN = Coordinates.from(0, 0);

	private final long xInWorld;
	private final long yInWorld;

	public Coordinates(long xInWorld, long yInWorld) {
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

	public Coordinates toFragmentCorner() {
		return from(getXCornerOfFragment(), getYCornerOfFragment());
	}

	public double getDistance(Coordinates other) {
		return Point.distance(xInWorld, yInWorld, other.xInWorld, other.yInWorld);
	}

	public double getDistance(long xInWorld, long yInWorld) {
		return Point.distance(this.xInWorld, this.yInWorld, xInWorld, yInWorld);
	}

	public double getDistanceSq(Coordinates other) {
		return Point.distanceSq(xInWorld, yInWorld, other.xInWorld, other.yInWorld);
	}

	public double getDistanceSq(long xInWorld, long yInWorld) {
		return Point.distanceSq(this.xInWorld, this.yInWorld, xInWorld, yInWorld);
	}

	public Coordinates add(Coordinates other) {
		return add(other.xInWorld, other.yInWorld);
	}

	public Coordinates add(long xInWorld, long yInWorld) {
		return from(this, xInWorld, yInWorld);
	}

	public Coordinates substract(Coordinates other) {
		return substract(other.xInWorld, other.yInWorld);
	}

	public Coordinates substract(long xInWorld, long yInWorld) {
		return from(this, -xInWorld, -yInWorld);
	}

	public boolean isInBoundsOf(Coordinates corner, long size) {
		return CoordinateUtils.isInBounds(xInWorld, yInWorld, corner.xInWorld, corner.yInWorld, size, size);
	}

	@Override
	public int compareTo(Coordinates o) {
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
		if (!(obj instanceof Coordinates)) {
			return false;
		}
		Coordinates other = (Coordinates) obj;
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
