package amidst.map;

import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerManager;
import amidst.minecraft.world.World;

public class MapBuilder {
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	public MapBuilder(LayerBuilder layerBuilder) {
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(
				layerBuilder.getConstructors(),
				layerBuilder.getNumberOfLayers());
	}

	public MapFactory create(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection, MapMovement mapMovement) {
		Map map = new Map(layerBuilder.getDeclarations(), mapZoom,
				biomeSelection, fragmentManager, world);
		LayerManager layerManager = layerBuilder.createLayerManager(world, map);
		fragmentManager.setLayerManager(layerManager);
		MapViewer mapViewer = new MapViewer(mapMovement, mapZoom, world, map,
				layerBuilder.createDrawers(map));
		return new MapFactory(map, mapViewer, mapZoom, mapMovement,
				biomeSelection);
	}
}
