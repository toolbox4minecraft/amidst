package MoF;

import amidst.Options;
import amidst.map.MapMarkers;

public class NetherMapObject extends MapObject {
	public NetherMapObject(int eX, int eY) {
		super(MapMarkers.NETHER_FORTRESS, eX, eY);
		model = Options.instance.showNetherFortresses;
	}
}
