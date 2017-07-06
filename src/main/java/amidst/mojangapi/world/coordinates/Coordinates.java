package amidst.mojangapi.world.coordinates;

import java.awt.Point;

import com.google.gson.annotations.JsonAdapter;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@JsonAdapter(CoordinatesJsonAdapter.class)
@GsonObject
public class Coordinates implements Comparable<Coordinates> {
	
	public static Coordinates tryParse(String coordinates) {
		String[] parsedCoordinates = coordinates.replaceAll(" ", "").split(",");
		if (parsedCoordinates.length != 2) {
			return null;
		}
		try {
			return Coordinates.from(
					Integer.parseInt(parsedCoordinates[0]),
					Integer.parseInt(parsedCoordinates[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Coordinates from(int xInWorld, int yInWorld) {
		return new Coordinates(xInWorld, yInWorld);
	}

	public static Coordinates from(int xAsResolution,
			int yAsResolution, Resolution resolution) {
		return new Coordinates(
				resolution.convertFromThisToWorld(xAsResolution),
				resolution.convertFromThisToWorld(yAsResolution));
	}
	
	public static Coordinates from(Coordinates coordAsResolution, Resolution resolution) {
		return Coordinates.from(coordAsResolution.getX(), coordAsResolution.getY(), resolution);
	}

	private static Coordinates from(Coordinates base,
			int deltaXInWorld, int deltaYInWorld) {
		return new Coordinates(base.xInWorld + deltaXInWorld,
				base.yInWorld + deltaYInWorld);
	}

	public static Coordinates origin() {
		return ORIGIN;
	}

	private static final Coordinates ORIGIN = Coordinates.from(0,
			0);

	private final int xInWorld;
	private final int yInWorld;

	public Coordinates(int xInWorld, int yInWorld) {
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	public int getX() {
		return xInWorld;
	}

	public int getY() {
		return yInWorld;
	}

	public int getXAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(xInWorld);
	}

	public int getYAs(Resolution targetResolution) {
		return targetResolution.convertFromWorldToThis(yInWorld);
	}
	
	public Coordinates getAs(Resolution targetResolution) {
		return Coordinates.from(getXAs(targetResolution), getYAs(targetResolution));
	}
	
	public int snapXTo(Resolution targetResolution) {
		return targetResolution.snapToResolution(xInWorld);
	}
	
	public int snapYTo(Resolution targetResolution) {
		return targetResolution.snapToResolution(yInWorld);
	}
	
	public Coordinates snapTo(Resolution targetResolution) {
		return Coordinates.from(snapXTo(targetResolution), snapYTo(targetResolution));
	}
	
	public int snapUpwardsXTo(Resolution targetResolution) {
		return targetResolution.snapUpwardsToResolution(xInWorld);
	}
	
	public int snapUpwardsYTo(Resolution targetResolution) {
		return targetResolution.snapUpwardsToResolution(yInWorld);
	}

	public Coordinates snapUpwardsTo(Resolution targetResolution) {
		return Coordinates.from(snapUpwardsXTo(targetResolution), snapUpwardsYTo(targetResolution));
	}
	public int getXRelativeTo(Resolution resolution) {
		return resolution.toRelative(xInWorld);
	}

	public int getYRelativeTo(Resolution resolution) {
		return resolution.toRelative(yInWorld);
	}
	
	public Coordinates getRelativeTo(Resolution resolution) {
		return Coordinates.from(getXRelativeTo(resolution), getYRelativeTo(resolution));
	}

	public double getDistance(Coordinates other) {
		return Point.distance(xInWorld, yInWorld, other.xInWorld,
				other.yInWorld);
	}

	public double getDistance(int xInWorld, int yInWorld) {
		return Point.distance(this.xInWorld, this.yInWorld, xInWorld, yInWorld);
	}

	public double getDistanceSq(Coordinates other) {
		return Point.distanceSq(xInWorld, yInWorld, other.xInWorld,
				other.yInWorld);
	}

	public double getDistanceSq(int xInWorld, int yInWorld) {
		return Point.distanceSq(this.xInWorld, this.yInWorld, xInWorld,
				yInWorld);
	}

	public Coordinates add(Coordinates other) {
		return add(other.xInWorld, other.yInWorld);
	}

	public Coordinates add(int xInWorld, int yInWorld) {
		return from(this, xInWorld, yInWorld);
	}

	public Coordinates substract(Coordinates other) {
		return substract(other.xInWorld, other.yInWorld);
	}

	public Coordinates substract(int xInWorld, int yInWorld) {
		return from(this, -xInWorld, -yInWorld);
	}

	public boolean isInBoundsOf(Coordinates corner, int size) {
		return xInWorld >= corner.xInWorld
			&& xInWorld < corner.xInWorld + size
			&& yInWorld >= corner.yInWorld 
			&& yInWorld < corner.yInWorld + size;
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
		result = prime * result + xInWorld;
		result = prime * result + yInWorld;
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
