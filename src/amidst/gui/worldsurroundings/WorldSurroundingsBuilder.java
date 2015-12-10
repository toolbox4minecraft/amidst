package amidst.gui.worldsurroundings;

import java.util.List;

import amidst.Options;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerLoader;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.menu.MenuActions;
import amidst.gui.widget.Widget;
import amidst.gui.widget.WidgetBuilder;
import amidst.gui.widget.WidgetManager;
import amidst.mojangapi.world.World;

public class WorldSurroundingsBuilder {
	private final Zoom zoom;
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final Options options;
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	public WorldSurroundingsBuilder(Options options, LayerBuilder layerBuilder) {
		this.options = options;
		this.zoom = new Zoom(options.maxZoom);
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(
				layerBuilder.getConstructors(),
				layerBuilder.getNumberOfLayers());
	}

	public WorldSurroundings create(World world, MenuActions actions) {
		Movement movement = new Movement(options.smoothScrolling);
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		List<LayerDeclaration> declarations = layerBuilder.getDeclarations();
		FragmentGraph graph = new FragmentGraph(declarations, fragmentManager);
		FragmentGraphToScreenTranslator translator = new FragmentGraphToScreenTranslator(
				graph, zoom);
		LayerLoader layerLoader = layerBuilder.createLayerLoader(world,
				biomeSelection, options);
		fragmentManager.setLayerLoader(layerLoader);
		Iterable<FragmentDrawer> drawers = layerBuilder.createDrawers(zoom,
				worldIconSelection);
		LayerReloader layerReloader = layerBuilder
				.createLayerReloader(fragmentManager);
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, graph,
				translator, zoom, biomeSelection, worldIconSelection,
				layerReloader, fragmentManager, options);
		List<Widget> widgets = widgetBuilder.create();
		Drawer drawer = new Drawer(graph, translator, zoom, movement, widgets,
				drawers);
		Viewer viewer = new Viewer(new ViewerMouseListener(new WidgetManager(
				widgets), graph, translator, zoom, movement, actions), drawer);
		return new WorldSurroundings(world, graph, translator, zoom, viewer,
				layerReloader, fragmentManager, worldIconSelection);
	}
}
