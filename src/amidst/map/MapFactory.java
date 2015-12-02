package amidst.map;

public class MapFactory {
	private final Map map;
	private final MapViewer mapViewer;

	public MapFactory(Map map, MapViewer mapViewer) {
		this.map = map;
		this.mapViewer = mapViewer;
	}

	public Map getMap() {
		return map;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}
}
