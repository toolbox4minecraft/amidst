package amidst.dependency.injection;

import java.util.List;

import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.FragmentQueueProcessor;
import amidst.fragment.layer.LayerBuilder;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.Actions;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.Drawer;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.gui.main.viewer.Movement;
import amidst.gui.main.viewer.Viewer;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.ViewerMouseListener;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.gui.main.viewer.widget.Widget;
import amidst.gui.main.viewer.widget.WidgetBuilder;
import amidst.gui.main.viewer.widget.WidgetManager;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.export.WorldExporterFactory;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class PerViewerFacadeInjector {
	private final Graphics2DAccelerationCounter accelerationCounter;
	private final Movement movement;
	private final WorldIconSelection worldIconSelection;
	private final LayerManager layerManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final FragmentQueueProcessor fragmentQueueProcessor;
	private final LayerReloader layerReloader;
	private final WorldExporterFactory worldExporterFactory;
	private final WidgetBuilder widgetBuilder;
	private final List<Widget> widgets;
	private final Drawer drawer;
	private final WidgetManager widgetManager;
	private final ViewerMouseListener viewerMouseListener;
	private final Viewer viewer;
	private final ViewerFacade viewerFacade;

	@CalledOnlyBy(AmidstThread.EDT)
	public PerViewerFacadeInjector(
			AmidstSettings settings,
			WorkerExecutor workerExecutor,
			Zoom zoom,
			LayerBuilder layerBuilder,
			FragmentManager fragmentManager,
			BiomeSelection biomeSelection,
			World world,
			Actions actions) {
		this.accelerationCounter = new Graphics2DAccelerationCounter();
		this.movement = new Movement(settings.smoothScrolling);
		this.worldIconSelection = new WorldIconSelection();
		this.layerManager = layerBuilder
				.create(settings, world, biomeSelection, worldIconSelection, zoom, accelerationCounter);
		this.graph = new FragmentGraph(layerManager.getDeclarations(), fragmentManager);
		this.translator = new FragmentGraphToScreenTranslator(graph, zoom);
		this.fragmentQueueProcessor = fragmentManager.createQueueProcessor(layerManager, settings.dimension);
		this.layerReloader = layerManager.createLayerReloader(world);
		this.worldExporterFactory = new WorldExporterFactory(workerExecutor, world);
		this.widgetBuilder = new WidgetBuilder(
				world,
				graph,
				translator,
				zoom,
				biomeSelection,
				worldIconSelection,
				layerReloader,
				fragmentManager,
				accelerationCounter,
				settings,
				worldExporterFactory::getProgressMessage);
		this.widgets = widgetBuilder.create();
		this.drawer = new Drawer(
				graph,
				translator,
				zoom,
				movement,
				widgets,
				layerManager.getDrawers(),
				settings.dimension,
				accelerationCounter);
		this.widgetManager = new WidgetManager(widgets);
		this.viewerMouseListener = new ViewerMouseListener(widgetManager, graph, translator, zoom, movement, actions);
		this.viewer = new Viewer(viewerMouseListener, drawer);
		this.viewerFacade = new ViewerFacade(
				world,
				graph,
				translator,
				zoom,
				viewer,
				layerReloader,
				worldIconSelection,
				layerManager,
				workerExecutor,
				worldExporterFactory,
				this::onRepainterTick,
				this::onFragmentLoaderTick,
				this::onPlayerFinishedLoading);
	}

	@CalledOnlyBy(AmidstThread.REPAINTER)
	private void onRepainterTick() {
		viewer.repaintComponent();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void onFragmentLoaderTick() {
		fragmentQueueProcessor.processQueues();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void onPlayerFinishedLoading() {
		layerReloader.reloadPlayerLayer();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacade getViewerFacade() {
		return viewerFacade;
	}
}
