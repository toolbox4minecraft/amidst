package amidst.gui.main.viewer;

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
import amidst.gui.main.viewer.widget.Widget;
import amidst.gui.main.viewer.widget.WidgetBuilder;
import amidst.gui.main.viewer.widget.WidgetManager;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.export.WorldExporterFactory;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class ViewerFacadeBuilder {
	private final Zoom zoom;
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final AmidstSettings settings;
	private final WorkerExecutor workerExecutor;
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacadeBuilder(AmidstSettings settings, WorkerExecutor workerExecutor, LayerBuilder layerBuilder) {
		this.settings = settings;
		this.workerExecutor = workerExecutor;
		this.zoom = new Zoom(settings.maxZoom);
		this.layerBuilder = layerBuilder;
		this.fragmentManager = new FragmentManager(layerBuilder.getConstructors(), layerBuilder.getNumberOfLayers());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacade create(World world, Actions actions) {
		Graphics2DAccelerationCounter accelerationCounter = new Graphics2DAccelerationCounter();
		Movement movement = new Movement(settings.smoothScrolling);
		WorldIconSelection worldIconSelection = new WorldIconSelection();
		LayerManager layerManager = layerBuilder.create(
				settings,
				world,
				biomeSelection,
				worldIconSelection,
				zoom,
				accelerationCounter);
		FragmentGraph graph = new FragmentGraph(layerManager.getDeclarations(), fragmentManager);
		FragmentGraphToScreenTranslator translator = new FragmentGraphToScreenTranslator(graph, zoom);
		FragmentQueueProcessor fragmentQueueProcessor = fragmentManager.createQueueProcessor(
				layerManager,
				settings.dimension);
		LayerReloader layerReloader = layerManager.createLayerReloader(world);
		WorldExporterFactory worldExporterFactory = new WorldExporterFactory(workerExecutor, world);
		WidgetBuilder widgetBuilder = new WidgetBuilder(
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
		List<Widget> widgets = widgetBuilder.create();
		Drawer drawer = new Drawer(
				graph,
				translator,
				zoom,
				movement,
				widgets,
				layerManager.getDrawers(),
				settings.dimension,
				accelerationCounter);
		Viewer viewer = new Viewer(new ViewerMouseListener(
				new WidgetManager(widgets),
				graph,
				translator,
				zoom,
				movement,
				actions), drawer);
		return new ViewerFacade(
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
	private Runnable createOnFragmentLoaderTick(final FragmentQueueProcessor fragmentQueueProcessor) {
		return new Runnable() {
			@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
			@Override
			public void run() {
				fragmentQueueProcessor.processQueues();
			}
		};
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Runnable createOnPlayerFinishedLoading(final LayerReloader layerReloader) {
		return new Runnable() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void run() {
				layerReloader.reloadPlayerLayer();
			}
		};
	}
}
