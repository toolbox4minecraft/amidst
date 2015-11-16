package amidst.map.object;

import amidst.map.MapMarkers;

public class MapObjectSpawn extends MapObject {
	private int worldX;
	private int worldY;

	public MapObjectSpawn(int x, int y) {
		super(MapMarkers.SPAWN, toFragmentCoordinates(x),
				toFragmentCoordinates(y));
		this.worldX = x;
		this.worldY = y;
	}

	@Override
	public String toString() {
		return "Spawn point at (" + getX() + ", " + getY() + ")";
	}

	public int getWorldX() {
		return worldX;
	}

	public int getWorldY() {
		return worldY;
	}
}
