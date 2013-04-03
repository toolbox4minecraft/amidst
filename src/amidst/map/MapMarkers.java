package amidst.map;

import amidst.resources.ResourceLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/** Contains information about all possible Map Markers.
 * Map objects use either its or their own icon
 * TODO: link to test.amidst.map object class
 */
public enum MapMarkers {
	NETHER_FORTRESS,
	PLAYER,
	SLIME,
	STRONGHOLD,
	TEMPLE,
	VILLAGE,
	WITCH;
	
	public final BufferedImage image;
	
	//we test, therefore this exception isn’t thrown.
	private MapMarkers() {
		String fileName = this.toString().toLowerCase() + ".png";
		try {
			image = ResourceLoader.getImage(fileName);
		} catch (IOException e) {
			throw new ExceptionInInitializerError("Enum creation " + this + " failed.\n" + e.getLocalizedMessage());
		}
	}
}
