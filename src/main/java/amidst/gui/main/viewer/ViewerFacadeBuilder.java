package amidst.gui.main.viewer;

import java.util.List;

import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.FragmentQueueProcessor;
import amidst.fragment.dimension.DimensionSelector;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerLoader;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.Actions;
import amidst.gui.main.viewer.widget.Widget;
import amidst.gui.main.viewer.widget.WidgetBuilder;
import amidst.gui.main.viewer.widget.WidgetManager;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class ViewerFacadeBuilder {
	private final Zoom zoom;
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final AmidstSettings settings;
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacadeBuilder(AmidstSettings settings, LayerBuilder layerBuilder) {
		this.settings = settings;
		this.zoom = new Zoom(settings.maxZoom);
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(
				layerBuilder.getConstructors(),
				layerBuilder.getNumberOfLayers());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacade create(World world, Actions actions, Dimension dimension) {
		Movement movement = new Movement(settings.smoothScrolling);
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		DimensionSelection dimensionSelection = new DimensionSelection(
				dimension);
		List<LayerDeclaration> declarations = layerBuilder.getDeclarations();
		FragmentGraph graph = new FragmentGraph(declarations, fragmentManager);
		FragmentGraphToScreenTranslator translator = new FragmentGraphToScreenTranslator(
				graph, zoom);
		LayerLoader layerLoader = layerBuilder.createLayerLoader(world,
				biomeSelection, dimensionSelection, settings);
		FragmentQueueProcessor fragmentQueueProcessor = fragmentManager
				.createQueueProcessor(layerLoader, dimensionSelection);
		Iterable<FragmentDrawer> drawers = layerBuilder.createDrawers(zoom,
				worldIconSelection, dimensionSelection);
		LayerReloader layerReloader = layerBuilder.createLayerReloader(world,
				fragmentQueueProcessor);
		DimensionSelector dimensionSelector = new DimensionSelector(
				fragmentQueueProcessor);
		WidgetBuilder widgetBuilder = new WidgetBuilder(world, graph,
				translator, zoom, biomeSelection, worldIconSelection,
				layerReloader, fragmentManager, settings);
		List<Widget> widgets = widgetBuilder.create(dimensionSelection);
		Drawer drawer = new Drawer(graph, translator, zoom, movement, widgets,
				drawers, dimensionSelection);
		Viewer viewer = new Viewer(new ViewerMouseListener(new WidgetManager(
				widgets), graph, translator, zoom, movement, actions), drawer);
		return new ViewerFacade(world, graph, translator, zoom, viewer,
				layerReloader, dimensionSelector, worldIconSelection,
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
