package amidst.map.object;

import amidst.map.MapMarkers;

public class MapObjectSpawn extends MapObject {
	private int globalX;
	private int globalY;

	public MapObjectSpawn(int x, int y) {
		super(MapMarkers.SPAWN, calc(x), calc(y));
		this.globalX = x;
		this.globalY = y;
	}

	@Override
	public String toString() {
		return "Spawn point at (" + getX() + ", " + getY() + ")";
	}

	public int getGlobalX() {
		return globalX;
	}

	public int getGlobalY() {
		return globalY;
	}
}
