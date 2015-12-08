package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.icon.WorldIcon;
import amidst.threading.SkinLoader;

public class WorldSurroundings {
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final FragmentManager fragmentManager;
	private final Runnable onFinished;

	public WorldSurroundings(World world, FragmentGraph graph,
			FragmentGraphToScreenTranslator translator, Zoom zoom,
			Viewer viewer, LayerReloader layerReloader,
			FragmentManager fragmentManager) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.fragmentManager = fragmentManager;
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

	public void tickFragmentLoader() {
		fragmentManager.tick();
	}

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

	public String getRecognisedVersionName() {
		return world.getRecognisedVersion().getName();
	}

	public long getSeed() {
		return world.getSeed();
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

	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSavePlayerLocations();
	}

	public void savePlayerLocations() {
		world.getMovablePlayerList().savePlayerLocations();
	}

	public void loadPlayerSkins(SkinLoader skinLoader) {
		skinLoader.loadSkins(world.getMovablePlayerList(), onFinished);
	}
}
