package amidst.map.object;

import amidst.map.MapMarkers;

/** Used mainly to be override its toString method for use in choices
 */
public class MapObjectStronghold extends MapObject {
	public MapObjectStronghold(int x, int y) {
		super(MapMarkers.STRONGHOLD, x, y);
	}
	
	@Override
	public String toString() {
		return "Stronghold at (" + x + ", " + y + ")";
	}
}
