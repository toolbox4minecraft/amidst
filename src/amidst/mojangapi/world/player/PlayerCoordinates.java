package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.CoordinatesInWorld;

@Immutable
public class PlayerCoordinates {
	private final CoordinatesInWorld coordinates;
	private final long y;

	public PlayerCoordinates(long x, long y, long z) {
		this.coordinates = CoordinatesInWorld.from(x, z);
		this.y = y;
	}

	public PlayerCoordinates(CoordinatesInWorld coordinates, long y) {
		this.coordinates = coordinates;
		this.y = y;
	}

	public long getX() {
		return coordinates.getX();
	}

	public long getY() {
		return y;
	}

	public long getZ() {
		return coordinates.getY();
	}

	public CoordinatesInWorld getCoordinatesInWorld() {
		return coordinates;
	}
}
