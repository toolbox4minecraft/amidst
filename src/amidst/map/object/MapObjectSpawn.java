package amidst.map.object;

import amidst.map.MapMarkers;

public class MapObjectSpawn extends MapObject {
	private int xInWorld;
	private int yInWorld;

	public MapObjectSpawn(int xInWorld, int yInWorld) {
		super(MapMarkers.SPAWN, toFragmentCoordinates(xInWorld),
				toFragmentCoordinates(yInWorld));
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	@Override
	public int getXInWorld() {
		return xInWorld;
	}

	@Override
	public int getYInWorld() {
		return yInWorld;
	}
}
