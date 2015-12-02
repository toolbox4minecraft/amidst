package amidst.map;

public class MapFactory {
	private final Map map;
	private final MapViewer mapViewer;
	private final MapZoom mapZoom;
	private final MapMovement mapMovement;
	private final BiomeSelection biomeSelection;

	public MapFactory(Map map, MapViewer mapViewer, MapZoom mapZoom,
			MapMovement mapMovement, BiomeSelection biomeSelection) {
		this.map = map;
		this.mapViewer = mapViewer;
		this.mapZoom = mapZoom;
		this.mapMovement = mapMovement;
		this.biomeSelection = biomeSelection;
	}

	public Map getMap() {
		return map;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public MapZoom getMapZoom() {
		return mapZoom;
	}

	public MapMovement getMapMovement() {
		return mapMovement;
	}

	public BiomeSelection getBiomeSelection() {
		return biomeSelection;
	}
}
