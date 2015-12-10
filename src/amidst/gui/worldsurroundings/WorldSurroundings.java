package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.SkinLoader;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.MovablePlayerList;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.icon.WorldIcon;

public class WorldSurroundings {
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final FragmentManager fragmentManager;
	private final Runnable onFinished;
	private final WorldIconSelection worldIconSelection;

	public WorldSurroundings(World world, FragmentGraph graph,
			FragmentGraphToScreenTranslator translator, Zoom zoom,
			Viewer viewer, LayerReloader layerReloader,
			FragmentManager fragmentManager,
			WorldIconSelection worldIconSelection) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.fragmentManager = fragmentManager;
		this.worldIconSelection = worldIconSelection;
		this.onFinished = createOnFinished();
	}

	private Runnable createOnFinished() {
		return new Runnable() {
			@Override
			public void run() {
				reloadPlayerLayer();
			}
		};
	}

	public Component getComponent() {
		return viewer.getComponent();
	}

	public void reloadBiomeLayer() {
		layerReloader.reloadBiomeLayer();
	}

	public void reloadPlayerLayer() {
		world.reloadPlayerWorldIcons();
		layerReloader.reloadPlayerLayer();
	}

	public void dispose() {
		graph.dispose();
		zoom.skipFading();
		zoom.reset();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void tickFragmentLoader() {
		fragmentManager.tick();
	}

	@CalledOnlyBy(AmidstThread.REPAINTER)
	public void tickRepainter() {
		viewer.repaint();
	}

	public void centerOn(CoordinatesInWorld coordinates) {
		translator.centerOn(coordinates);
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

	// TODO: use this
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
		return world.getMovablePlayerList().canReload();
	}

	public void reloadPlayerLocations(SkinLoader skinLoader) {
		world.getMovablePlayerList().reload();
		reloadPlayerLayer();
		loadPlayerSkins(skinLoader);
	}

	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSave();
	}

	public void savePlayerLocations() {
		world.getMovablePlayerList().save();
	}

	public void loadPlayerSkins(SkinLoader skinLoader) {
		skinLoader.loadSkins(world.getMovablePlayerList(), onFinished);
	}
}
