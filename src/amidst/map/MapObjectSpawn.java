package amidst.map;

/** Used mainly to be override its toString method for use in choices
 */
public class MapObjectSpawn extends MapObject {
	public int globalX, globalY;
	public MapObjectSpawn(int x, int y) {
		super(MapMarkers.SPAWN,
				((x < 0)?Fragment.SIZE:0) + x % Fragment.SIZE,
				((y < 0)?Fragment.SIZE:0) + y % Fragment.SIZE);
		
		globalX = x;
		globalY = y;
	}
	
	@Override
	public String toString() {
		return "Spawn point at (" + x + ", " + y + ")";
	}
}
