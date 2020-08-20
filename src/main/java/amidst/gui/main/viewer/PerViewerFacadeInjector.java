package amidst.gui.main.viewer;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
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
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.Actions;
import amidst.gui.main.viewer.widget.BiomeExporterProgressWidget;
import amidst.gui.main.viewer.widget.BiomeToggleWidget;
import amidst.gui.main.viewer.widget.BiomeWidget;
import amidst.gui.main.viewer.widget.CursorInformationWidget;
import amidst.gui.main.viewer.widget.DebugWidget;
import amidst.gui.main.viewer.widget.FpsWidget;
import amidst.gui.main.viewer.widget.FramerateTimer;
import amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType;
import amidst.gui.main.viewer.widget.ScaleWidget;
import amidst.gui.main.viewer.widget.SeedAndWorldTypeWidget;
import amidst.gui.main.viewer.widget.SelectedIconWidget;
import amidst.gui.main.viewer.widget.Widget;
import amidst.gui.main.viewer.widget.Widget.CornerAnchorPoint;
import amidst.gui.main.viewer.widget.WidgetManager;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
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
			Supplier<Entry<ProgressEntryType, Integer>> progressEntrySupplier) {
		// @formatter:off
		DebugWidget debugWidget = new DebugWidget(CornerAnchorPoint.BOTTOM_RIGHT, graph, fragmentManager, settings.showDebug, accelerationCounter, zoom);
		BiomeWidget biomeWidget = new BiomeWidget(CornerAnchorPoint.NONE, biomeSelection, layerReloader, settings.biomeProfileSelection, world.getBiomeList());
		BiomeToggleWidget biomeToggleWidget = new BiomeToggleWidget(CornerAnchorPoint.BOTTOM_RIGHT, biomeWidget, biomeSelection);
		WorldOptions worldOptions = world.getWorldOptions();
		return Arrays.asList(
				new FpsWidget(                  CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              settings.showFPS),
				new ScaleWidget(                CornerAnchorPoint.BOTTOM_CENTER, zoom,                               settings.showScale),
				new SeedAndWorldTypeWidget(     CornerAnchorPoint.TOP_LEFT,      worldOptions.getWorldSeed(), worldOptions.getWorldType()),
				new SelectedIconWidget(         CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				debugWidget,
				new CursorInformationWidget(	CornerAnchorPoint.TOP_RIGHT,     graph,             translator,      settings.dimension, world.getBiomeList()),
				biomeToggleWidget,
				new BiomeExporterProgressWidget(CornerAnchorPoint.BOTTOM_RIGHT,  progressEntrySupplier,    -20,      settings.showDebug, debugWidget, biomeToggleWidget.getWidth()),
				biomeWidget
		);
		// @formatter:on
	}

	private final Graphics2DAccelerationCounter accelerationCounter;
	private final Movement movement;
	private final WorldIconSelection worldIconSelection;
	private final LayerManager layerManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final FragmentQueueProcessor fragmentQueueProcessor;
	private final LayerReloader layerReloader;
	private final AtomicReference<Entry<ProgressEntryType, Integer>> progressEntryHolder;
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
			BiomeExporterDialog biomeExporterDialog,
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
		this.progressEntryHolder = new AtomicReference<Entry<ProgressEntryType, Integer>>();
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
				progressEntryHolder::get);
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
				biomeExporterDialog,
				progressEntryHolder::set,
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
