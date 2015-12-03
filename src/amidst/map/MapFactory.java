package amidst.map;

import amidst.fragment.layer.LayerReloader;

public class MapFactory {
	private final Map map;
	private final MapViewer mapViewer;
	private final LayerReloader layerReloader;

	public MapFactory(Map map, MapViewer mapViewer, LayerReloader layerReloader) {
		this.map = map;
		this.mapViewer = mapViewer;
		this.layerReloader = layerReloader;
	}

	public Map getMap() {
		return map;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public LayerReloader getLayerReloader() {
		return layerReloader;
	}
}
