package amidst.map.object;

import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;

public class MapObjectSpawn extends MapObject {
	private int xInWorld;
	private int yInWorld;

	public MapObjectSpawn(IconLayer iconLayer, int xInWorld, int yInWorld) {
		super(iconLayer, MapMarkers.SPAWN, toFragmentCoordinates(xInWorld),
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
