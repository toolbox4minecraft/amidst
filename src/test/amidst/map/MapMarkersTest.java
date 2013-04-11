package test.amidst.map;

import java.io.IOException;

import amidst.map.MapMarkers;

/** Tests if MapMarkers finds all files*/
public class MapMarkersTest {
	@org.junit.Test
	public void testAll() throws IOException {
		for (MapMarkers marker : MapMarkers.values()) {
			System.out.println(marker + ": " + marker.image);
		}
	}
}
