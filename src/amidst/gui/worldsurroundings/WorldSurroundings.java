package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.fragment.FragmentGraph;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.SkinLoader;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.player.MovablePlayerList;

public class WorldSurroundings {
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final WorldIconSelection worldIconSelection;
	private final Runnable onRepainterTick;
	private final Runnable onFragmentLoaderTick;
	private final Runnable onSkinFinishedLoading;

	public WorldSurroundings(World world, FragmentGraph graph,
			FragmentGraphToScreenTranslator translator, Zoom zoom,
			Viewer viewer, LayerReloader layerReloader,
			WorldIconSelection worldIconSelection, Runnable onRepainterTick,
			Runnable onFragmentLoaderTick, Runnable onSkinFinishedLoading) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.worldIconSelection = worldIconSelection;
		this.onRepainterTick = onRepainterTick;
		this.onFragmentLoaderTick = onFragmentLoaderTick;
		this.onSkinFinishedLoading = onSkinFinishedLoading;
	}

	public Component getComponent() {
		return viewer.getComponent();
	}

	public void reloadBiomeLayer() {
		layerReloader.reloadBiomeLayer();
	}

	public void reloadPlayerLayer() {
		layerReloader.reloadPlayerLayer();
	}

	public void dispose() {
		graph.dispose();
		zoom.skipFading();
		zoom.reset();
	}

	public Runnable getOnRepainterTick() {
		return onRepainterTick;
	}

	public Runnable getOnFragmentLoaderTick() {
		return onFragmentLoaderTick;
	}

	public void centerOn(CoordinatesInWorld coordinates) {
		translator.centerOn(coordinates);
	}

	public void centerOn(WorldIcon worldIcon) {
		translator.centerOn(worldIcon.getCoordinates());
		worldIconSelection.select(worldIcon);
	}

	public BufferedImage createCaptureImage() {
		return viewer.createCaptureImage();
	}

	public void adjustZoom(int notches) {
		zoom.adjustZoom(viewer.getMousePositionOrCenter(), notches);
	}

	public void adjustZoom(Point mousePosition, int notches) {
		zoom.adjustZoom(mousePosition, notches);
	}

	public void selectWorldIcon(WorldIcon worldIcon) {
		worldIconSelection.select(worldIcon);
	}

	public WorldSeed getWorldSeed() {
		return world.getWorldSeed();
	}

	public WorldIcon getSpawnWorldIcon() {
		return world.getSpawnWorldIcon();
	}

	public List<WorldIcon> getStrongholdWorldIcons() {
		return world.getStrongholdWorldIcons();
	}

	public List<WorldIcon> getPlayerWorldIcons() {
		return world.getPlayerWorldIcons();
	}

	public MovablePlayerList getMovablePlayerList() {
		return world.getMovablePlayerList();
	}

	public boolean canReloadPlayerLocations() {
		return world.getMovablePlayerList().canLoad();
	}

	public void reloadPlayerLocations(SkinLoader skinLoader) {
		worldIconSelection.clear();
		world.getMovablePlayerList().load();
		layerReloader.reloadPlayerLayer();
		loadPlayerSkins(skinLoader);
	}

	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSave();
	}

	public void savePlayerLocations() {
		world.getMovablePlayerList().save();
	}

	public void loadPlayerSkins(SkinLoader skinLoader) {
		skinLoader.loadSkins(world.getMovablePlayerList(),
				onSkinFinishedLoading);
	}
}
