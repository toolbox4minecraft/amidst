package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.viewer.widget.ProgressWidget.ProgressEntryType;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.MovablePlayerList;
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
	private final BiomeExporterDialog biomeExporterDialog;
	private final Consumer<Entry<ProgressEntryType, Integer>> progressListener;
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
			BiomeExporterDialog biomeExporterDialog,
			Consumer<Entry<ProgressEntryType, Integer>> progressListener,
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
		this.biomeExporterDialog = biomeExporterDialog;
		this.progressListener = progressListener;
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
		fragmentManager.clear();
		fragmentManager.restart();
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
	public WorldOptions getWorldOptions() {
		return world.getWorldOptions();
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
		return world.getEnabledLayers().contains(layerId);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void openExportDialog() {
		biomeExporterDialog.createAndShow(world, translator, progressListener);
	}

	public boolean isFullyLoaded() {
		return fragmentManager.getLoadingQueueSize() == 0;
	}

}
