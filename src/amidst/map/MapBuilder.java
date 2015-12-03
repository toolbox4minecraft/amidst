package amidst.map;

import amidst.fragment.layer.LayerBuilder;
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
			MapMovement mapMovement, BiomeSelection biomeSelection) {
		Map map = new Map(layerBuilder.getDeclarations(), mapZoom,
				biomeSelection, fragmentManager, world);
		fragmentManager.setLayerManager(layerBuilder.createLayerManager(world,
				map));
		MapDrawer drawer = new MapDrawer(map, mapMovement, mapZoom,
				layerBuilder.createDrawers(map));
		MapViewer mapViewer = new MapViewer(mapMovement, mapZoom, world, map,
				drawer);
		return new MapFactory(map, mapViewer);
	}
}
