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

public class WorldSurroundings {
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final FragmentManager fragmentManager;

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
	}

	public Component getComponent() {
		return viewer.getComponent();
	}

	public LayerReloader getLayerReloader() {
		return layerReloader;
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

	@Deprecated
	public String getRecognisedVersionName() {
		return world.getRecognisedVersionName();
	}

	@Deprecated
	public long getSeed() {
		return world.getSeed();
	}

	@Deprecated
	public WorldIcon getSpawnWorldIcon() {
		return world.getSpawnWorldIcon();
	}

	@Deprecated
	public List<WorldIcon> getStrongholdWorldIcons() {
		return world.getStrongholdWorldIcons();
	}

	@Deprecated
	public List<WorldIcon> getPlayerWorldIcons() {
		return world.getPlayerWorldIcons();
	}

	@Deprecated
	public boolean canSavePlayerLocations() {
		return world.getMovablePlayerList().canSavePlayerLocations();
	}

	@Deprecated
	public void savePlayerLocations() {
		world.getMovablePlayerList().savePlayerLocations();
	}
}
