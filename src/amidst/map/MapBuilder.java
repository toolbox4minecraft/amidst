package amidst.map;

import java.util.List;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
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
		List<LayerDeclaration> declarations = layerBuilder.getDeclarations();
		FragmentGraph graph = new FragmentGraph(declarations, fragmentManager);
		Map map = new Map(zoom, graph);
		LayerManager layerManager = layerBuilder.createLayerManager(world, map,
				biomeSelection);
		fragmentManager.setLayerManager(layerManager);
		Iterable<FragmentDrawer> drawers = layerBuilder.createDrawers(map,
				worldIconSelection);
		MapDrawer drawer = new MapDrawer(map, movement, zoom, graph, drawers);
		LayerReloader layerReloader = layerBuilder
				.createLayerReloader(fragmentManager);
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, map,
				biomeSelection, worldIconSelection, layerReloader);
		MapViewer mapViewer = new MapViewer(movement, zoom, world, map, drawer,
				worldIconSelection, layerReloader, widgetBuilder);
		return new MapFactory(map, mapViewer, layerReloader);
	}
}
