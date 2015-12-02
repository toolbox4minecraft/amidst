package amidst.map;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerDeclaration;

public class MapFactory {
	private final Map map;
	private final MapViewer mapViewer;
	private final MapZoom mapZoom;
	private final MapMovement mapMovement;
	private final BiomeSelection biomeSelection;
	private final Iterable<LayerDeclaration> layerDeclarations;
	private final Iterable<FragmentDrawer> fragmentDrawers;

	public MapFactory(Map map, MapViewer mapViewer, MapZoom mapZoom,
			MapMovement mapMovement, BiomeSelection biomeSelection,
			Iterable<LayerDeclaration> layerDeclarations,
			Iterable<FragmentDrawer> fragmentDrawers) {
		this.map = map;
		this.mapViewer = mapViewer;
		this.mapZoom = mapZoom;
		this.mapMovement = mapMovement;
		this.biomeSelection = biomeSelection;
		this.layerDeclarations = layerDeclarations;
		this.fragmentDrawers = fragmentDrawers;
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

	public Iterable<LayerDeclaration> getLayerDeclarations() {
		return layerDeclarations;
	}

	public Iterable<FragmentDrawer> getFragmentDrawers() {
		return fragmentDrawers;
	}
}
