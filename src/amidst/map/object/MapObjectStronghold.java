package amidst.map.object;

import amidst.map.MapMarkers;

public class MapObjectStronghold extends MapObject {
	public MapObjectStronghold(int x, int y) {
		super(MapMarkers.STRONGHOLD, x, y);
	}

	@Override
	public String toString() {
		return "Stronghold at (" + getX() + ", " + getY() + ")";
	}
}
