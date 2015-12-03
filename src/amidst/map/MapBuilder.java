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
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		Map map = new Map(layerBuilder.getDeclarations(), zoom, fragmentManager);
		fragmentManager.setLayerManager(layerBuilder.createLayerManager(world,
				map, biomeSelection));
		MapDrawer drawer = new MapDrawer(map, movement, zoom,
				layerBuilder.createDrawers(map, worldIconSelection));
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, map,
				biomeSelection, worldIconSelection);
		MapViewer mapViewer = new MapViewer(movement, zoom, world, map, drawer,
				worldIconSelection, widgetBuilder);
		return new MapFactory(map, mapViewer);
	}
}
