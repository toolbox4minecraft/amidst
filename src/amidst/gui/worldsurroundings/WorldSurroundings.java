package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.image.BufferedImage;

import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.minecraft.world.CoordinatesInWorld;

public class WorldSurroundings {
	private final FragmentGraphToScreenTranslator map;
	private final Viewer viewer;
	private final LayerReloader layerReloader;
	private final FragmentGraph graph;
	private final Zoom zoom;
	private final FragmentManager fragmentManager;

	public WorldSurroundings(FragmentGraphToScreenTranslator map, Viewer viewer,
			LayerReloader layerReloader, FragmentGraph graph, Zoom zoom,
			FragmentManager fragmentManager) {
		this.map = map;
		this.viewer = viewer;
		this.layerReloader = layerReloader;
		this.graph = graph;
		this.zoom = zoom;
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
		map.centerOn(coordinates);
	}

	public BufferedImage createCaptureImage() {
		return viewer.createCaptureImage();
	}

	public void adjustZoom(int notches) {
		zoom.adjustZoom(viewer.getMousePositionOrCenter(), notches);
	}
}
