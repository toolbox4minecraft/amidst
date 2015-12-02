package amidst.map;

import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerManagerFactory;
import amidst.minecraft.world.World;

public class MapBuilder {
	private final LayerManagerFactory layerManagerFactory;
	private final FragmentManager fragmentManager;

	public MapBuilder(LayerManagerFactory layerManagerFactory) {
		this.layerManagerFactory = layerManagerFactory;
		this.fragmentManager = new FragmentManager(
				layerManagerFactory.getConstructors(),
				layerManagerFactory.getNumberOfLayers());
	}

	public MapFactory create(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection, MapMovement mapMovement) {
		Map map = new Map(layerManagerFactory.getDeclarations(), mapZoom,
				biomeSelection, fragmentManager, layerManagerFactory, world);
		LayerManager layerManager = layerManagerFactory.createLayerManager(
				world, map);
		fragmentManager.setLayerManager(layerManager);
		MapViewer mapViewer = new MapViewer(mapMovement, mapZoom, world, map,
				layerManager.getFragmentDrawers());
		return new MapFactory(map, mapViewer, mapZoom, mapMovement,
				biomeSelection, layerManagerFactory.getDeclarations(),
				layerManager.getFragmentDrawers());
	}
}
