package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.dependency.injection.Factory1;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.export.WorldExporter;
import amidst.mojangapi.world.export.WorldExporterConfiguration;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.versionfeatures.FeatureKey;
import amidst.threading.WorkerExecutor;

/**
 * This class works as wrapper around a world instance. It holds everything that
 * is needed to display the world on the screen. This allows us to easily
 * exchange the currently displayed world.
 */
@NotThreadSafe
public class ViewerFacade {
	private final World world;
	private final FragmentManager fragmentManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final WorldIconSelection worldIconSelection;
	private final LayerManager layerManager;
	private final WorkerExecutor workerExecutor;
	private final Factory1<WorldExporterConfiguration, WorldExporter> worldExporterFactory;
	private final Runnable onRepainterTick;
	private final Runnable onFragmentLoaderTick;
	private final Runnable onPlayerFinishedLoading;

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerFacade(
			World world,
			FragmentManager fragmentManager,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Zoom zoom,
			Viewer viewer,
			LayerReloader layerReloader,
			WorldIconSelection worldIconSelection,
			LayerManager layerManager,
			WorkerExecutor workerExecutor,
			Factory1<WorldExporterConfiguration, WorldExporter> worldExporterFactory,
			Runnable onRepainterTick,
			Runnable onFragmentLoaderTick,
			Runnable onPlayerFinishedLoading) {
		this.world = world;
		this.fragmentManager = fragmentManager;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.worldIconSelection = worldIconSelection;
		this.layerManager = layerManager;
		this.workerExecutor = workerExecutor;
		this.worldExporterFactory = worldExporterFactory;
		this.onRepainterTick = onRepainterTick;
		this.onFragmentLoaderTick = onFragmentLoaderTick;
		this.onPlayerFinishedLoading = onPlayerFinishedLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Component getComponent() {
		return viewer.getComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadBackgroundLayer() {
		layerReloader.reloadBackgroundLayer();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reloadPlayerLayer() {
		layerReloader.reloadPlayerLayer();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		graph.dispose();
		zoom.skipFading();
		zoom.reset();
		world.dispose();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Runnable getOnRepainterTick() {
		return onRepainterTick;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Runnable getOnFragmentLoaderTick() {
		return onFragmentLoaderTick;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void centerOn(CoordinatesInWorld coordinates) {
		translator.centerOn(coordinates);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void centerOn(WorldIcon worldIcon) {
		translator.centerOn(worldIcon.getCoordinates());
		worldIconSelection.select(worldIcon);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public BufferedImage createScreenshot() {
		return viewer.createScreenshot();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(int notches) {
		zoom.adjustZoom(viewer.getMousePositionOrCenter(), notches);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustZoom(Point mousePosition, int notches) {
		zoom.adjustZoom(mousePosition, notches);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectWorldIcon(WorldIcon worldIcon) {
		worldIconSelection.select(worldIcon);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSeed getWorldSeed() {
		return world.getWorldSeed();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldType getWorldType() {
		return world.getWorldType();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldIcon getSpawnWorldIcon() {
		return world.getSpawnWorldIcon();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public List<WorldIcon> getStrongholdWorldIcons() {
		return world.getStrongholdWorldIcons();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public List<WorldIcon> getPlayerWorldIcons() {
		return world.getPlayerWorldIcons();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public MovablePlayerList getMovablePlayerList() {
		return world.getMovablePlayerList();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean canLoadPlayerLocations() {
		return world.getMovablePlayerList().canLoad();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void loadPlayers() {
		worldIconSelection.clear();
		world.getMovablePlayerList().load(workerExecutor, onPlayerFinishedLoading);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSave();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void savePlayerLocations() {
		world.getMovablePlayerList().save();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean calculateIsLayerEnabled(int layerId, Dimension dimension) {
		return layerManager.calculateIsEnabled(layerId, dimension);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean hasLayer(int layerId) {
		return world.getVersionFeatures().get(FeatureKey.ENABLED_LAYERS).contains(layerId);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void export(WorldExporterConfiguration configuration) {
		worldExporterFactory.create(configuration).export();
	}

	public boolean isFullyLoaded() {
		return fragmentManager.getLoadingQueueSize() == 0;
	}

}
