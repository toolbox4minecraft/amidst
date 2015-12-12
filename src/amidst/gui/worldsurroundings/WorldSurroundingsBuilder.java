package amidst.gui.worldsurroundings;

import java.util.List;

import amidst.Settings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.FragmentQueueProcessor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerLoader;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.menu.Actions;
import amidst.gui.widget.Widget;
import amidst.gui.widget.WidgetBuilder;
import amidst.gui.widget.WidgetManager;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class WorldSurroundingsBuilder {
	private final Zoom zoom;
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final Settings settings;
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSurroundingsBuilder(Settings settings, LayerBuilder layerBuilder) {
		this.settings = settings;
		this.zoom = new Zoom(settings.maxZoom);
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(
				layerBuilder.getConstructors(),
				layerBuilder.getNumberOfLayers());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSurroundings create(World world, Actions actions) {
		Movement movement = new Movement(settings.smoothScrolling);
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		List<LayerDeclaration> declarations = layerBuilder.getDeclarations();
		FragmentGraph graph = new FragmentGraph(declarations, fragmentManager);
		FragmentGraphToScreenTranslator translator = new FragmentGraphToScreenTranslator(
				graph, zoom);
		LayerLoader layerLoader = layerBuilder.createLayerLoader(world,
				biomeSelection, settings);
		FragmentQueueProcessor fragmentQueueProcessor = fragmentManager
				.createLayerLoader(layerLoader);
		Iterable<FragmentDrawer> drawers = layerBuilder.createDrawers(zoom,
				worldIconSelection);
		LayerReloader layerReloader = layerBuilder.createLayerReloader(world,
				fragmentQueueProcessor);
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, graph,
				translator, zoom, biomeSelection, worldIconSelection,
				layerReloader, fragmentManager, settings);
		List<Widget> widgets = widgetBuilder.create();
		Drawer drawer = new Drawer(graph, translator, zoom, movement, widgets,
				drawers);
		Viewer viewer = new Viewer(new ViewerMouseListener(new WidgetManager(
				widgets), graph, translator, zoom, movement, actions), drawer);
		return new WorldSurroundings(world, graph, translator, zoom, viewer,
				layerReloader, worldIconSelection,
				createOnRepainterTick(viewer),
				createOnFragmentLoaderTick(fragmentQueueProcessor),
				createOnPlayerFinishedLoading(layerReloader));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Runnable createOnRepainterTick(final Viewer viewer) {
		return new Runnable() {
			@CalledOnlyBy(AmidstThread.REPAINTER)
			@Override
			public void run() {
				viewer.repaintComponent();
			}
		};
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Runnable createOnFragmentLoaderTick(
			final FragmentQueueProcessor fragmentQueueProcessor) {
		return new Runnable() {
			@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
			@Override
			public void run() {
				fragmentQueueProcessor.processQueues();
			}
		};
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Runnable createOnPlayerFinishedLoading(
			final LayerReloader layerReloader) {
		return new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				layerReloader.reloadPlayerLayer();
			}
		};
	}
}
