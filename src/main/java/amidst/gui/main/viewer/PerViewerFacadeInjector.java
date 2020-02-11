package amidst.gui.main.viewer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
import amidst.gui.main.viewer.widget.BiomeToggleWidget;
import amidst.gui.main.viewer.widget.BiomeWidget;
import amidst.gui.main.viewer.widget.ChangeableTextWidget;
import amidst.gui.main.viewer.widget.CursorInformationWidget;
import amidst.gui.main.viewer.widget.DebugWidget;
import amidst.gui.main.viewer.widget.FpsWidget;
import amidst.gui.main.viewer.widget.FramerateTimer;
import amidst.gui.main.viewer.widget.ScaleWidget;
import amidst.gui.main.viewer.widget.SeedAndWorldTypeWidget;
import amidst.gui.main.viewer.widget.SelectedIconWidget;
import amidst.gui.main.viewer.widget.Widget;
import amidst.gui.main.viewer.widget.Widget.CornerAnchorPoint;
import amidst.gui.main.viewer.widget.WidgetManager;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.export.WorldExporter;
import amidst.mojangapi.world.export.WorldExporterConfiguration;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class PerViewerFacadeInjector {
	@CalledOnlyBy(AmidstThread.EDT)
	private static List<Widget> createWidgets(
			World world,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Zoom zoom,
			BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection,
			LayerReloader layerReloader,
			FragmentManager fragmentManager,
			Graphics2DAccelerationCounter accelerationCounter,
			AmidstSettings settings,
			Supplier<String> progressText) {
		// @formatter:off
		BiomeWidget biomeWidget = new BiomeWidget(CornerAnchorPoint.NONE, biomeSelection, layerReloader, settings.biomeProfileSelection);
		WorldOptions worldOptions = world.getWorldOptions();
		return Arrays.asList(
				new ChangeableTextWidget(   CornerAnchorPoint.CENTER,        progressText),
				new FpsWidget(              CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              settings.showFPS),
				new ScaleWidget(            CornerAnchorPoint.BOTTOM_CENTER, zoom,                               settings.showScale),
				new SeedAndWorldTypeWidget( CornerAnchorPoint.TOP_LEFT,      worldOptions.getWorldSeed(), worldOptions.getWorldType()),
				new SelectedIconWidget(     CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new DebugWidget(            CornerAnchorPoint.BOTTOM_RIGHT,  graph,             fragmentManager, settings.showDebug, accelerationCounter),
				new CursorInformationWidget(CornerAnchorPoint.TOP_RIGHT,     graph,             translator,      settings.dimension),
				new BiomeToggleWidget(      CornerAnchorPoint.BOTTOM_RIGHT,  biomeWidget, biomeSelection),
				biomeWidget
		);
		// @formatter:on
	}

	private final WorkerExecutor workerExecutor;
	private final World world;
	private final Graphics2DAccelerationCounter accelerationCounter;
	private final Movement movement;
	private final WorldIconSelection worldIconSelection;
	private final LayerManager layerManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final FragmentQueueProcessor fragmentQueueProcessor;
	private final LayerReloader layerReloader;
	private final ProgressMessageHolder progressMessageHolder;
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
		this.workerExecutor = workerExecutor;
		this.world = world;
		this.accelerationCounter = new Graphics2DAccelerationCounter();
		this.movement = new Movement(settings.smoothScrolling);
		this.worldIconSelection = new WorldIconSelection();
		this.layerManager = layerBuilder
				.create(settings, world, biomeSelection, worldIconSelection, zoom, accelerationCounter);
		this.graph = new FragmentGraph(layerManager.getDeclarations(), fragmentManager);
		this.translator = new FragmentGraphToScreenTranslator(graph, zoom);
		this.fragmentQueueProcessor = fragmentManager.createQueueProcessor(layerManager, settings.dimension);
		this.layerReloader = layerManager.createLayerReloader(world);
		this.progressMessageHolder = new ProgressMessageHolder();
		this.widgets = createWidgets(
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
				progressMessageHolder::getProgressMessage);
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
				fragmentManager,
				graph,
				translator,
				zoom,
				viewer,
				layerReloader,
				worldIconSelection,
				layerManager,
				workerExecutor,
				this::createWorldExporter,
				this::onRepainterTick,
				this::onFragmentLoaderTick,
				this::onPlayerFinishedLoading);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldExporter createWorldExporter(WorldExporterConfiguration configuration) {
		return new WorldExporter(workerExecutor, world, configuration, progressMessageHolder::setProgressMessage);
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
