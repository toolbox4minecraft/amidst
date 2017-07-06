package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.Coordinates;

@Immutable
public class PlayerCoordinates {
	public static PlayerCoordinates fromNBTFile(int x, int y, int z, int dimensionId) {
		Dimension dimension = Dimension.from(dimensionId);
		return new PlayerCoordinates(Coordinates.from(x, z, dimension.getResolution()), y, dimension);
	}

	private final Coordinates coordinates;
	private final int y;
	private final Dimension dimension;

	public PlayerCoordinates(Coordinates coordinates, int y, Dimension dimension) {
		this.coordinates = coordinates;
		this.y = y;
		this.dimension = dimension;
	}

	public Coordinates getCoordinatesInWorld() {
		return coordinates;
	}

	public int getY() {
		return y;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public int getXForNBTFile() {
		return coordinates.getXAs(dimension.getResolution());
	}

	public int getYForNBTFile() {
		return y;
	}

	public int getZForNBTFile() {
		return coordinates.getYAs(dimension.getResolution());
	}
}
