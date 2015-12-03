package amidst.map;

import java.util.List;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
import amidst.minecraft.world.World;

public class WorldSurroundingsBuilder {
	private final Zoom zoom = new Zoom();
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	public WorldSurroundingsBuilder(LayerBuilder layerBuilder) {
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(
				layerBuilder.getConstructors(),
				layerBuilder.getNumberOfLayers());
	}

	public WorldSurroundings create(World world) {
		Movement movement = new Movement();
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		List<LayerDeclaration> declarations = layerBuilder.getDeclarations();
		final FragmentGraph graph = new FragmentGraph(declarations,
				fragmentManager);
		Map map = new Map(zoom, graph);
		LayerManager layerManager = layerBuilder.createLayerManager(world, map,
				biomeSelection);
		fragmentManager.setLayerManager(layerManager);
		Iterable<FragmentDrawer> drawers = layerBuilder.createDrawers(zoom,
				worldIconSelection);
		MapDrawer drawer = new MapDrawer(map, movement, zoom, graph, drawers);
		LayerReloader layerReloader = layerBuilder
				.createLayerReloader(fragmentManager);
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, map,
				biomeSelection, worldIconSelection, layerReloader, graph, zoom);
		MapViewer mapViewer = new MapViewer(movement, zoom, world, map, drawer,
				worldIconSelection, layerReloader, widgetBuilder);
		return new WorldSurroundings(map, mapViewer, layerReloader, graph, zoom);
	}
}
