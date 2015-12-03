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

	public MapFactory create(World world, MapZoom zoom, MapMovement movement,
			BiomeSelection biomeSelection) {
		Map map = new Map(layerBuilder.getDeclarations(), zoom, biomeSelection,
				new WorldIconSelection(), fragmentManager, world);
		fragmentManager.setLayerManager(layerBuilder.createLayerManager(world,
				map));
		MapDrawer drawer = new MapDrawer(map, movement, zoom,
				layerBuilder.createDrawers(map));
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, map);
		MapViewer mapViewer = new MapViewer(movement, zoom, world, map, drawer,
				widgetBuilder);
		return new MapFactory(map, mapViewer);
	}
}
