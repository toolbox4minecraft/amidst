package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@Immutable
public class PlayerCoordinates {
	public static PlayerCoordinates fromNBTFile(long x, long y, long z, Dimension dimension) {
		return new PlayerCoordinates(CoordinatesInWorld.from(x, z, dimension.getResolution()), y, dimension);
	}

	private final CoordinatesInWorld coordinates;
	private final long y;
	private final Dimension dimension;

	public PlayerCoordinates(CoordinatesInWorld coordinates, long y, Dimension dimension) {
		this.coordinates = coordinates;
		this.y = y;
		this.dimension = dimension;
	}

	public CoordinatesInWorld getCoordinatesInWorld() {
		return coordinates;
	}

	public long getY() {
		return y;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public long getXForNBTFile() {
		return coordinates.getXAs(dimension.getResolution());
	}

	public long getYForNBTFile() {
		return y;
	}

	public long getZForNBTFile() {
		return coordinates.getYAs(dimension.getResolution());
	}
}
