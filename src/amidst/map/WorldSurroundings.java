package amidst.map;

import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerReloader;
import amidst.minecraft.world.CoordinatesInWorld;

public class WorldSurroundings {
	private final Map map;
	private final MapViewer mapViewer;
	private final LayerReloader layerReloader;
	private final FragmentGraph graph;
	private final Zoom zoom;
	private final FragmentManager fragmentManager;

	public WorldSurroundings(Map map, MapViewer mapViewer,
			LayerReloader layerReloader, FragmentGraph graph, Zoom zoom,
			FragmentManager fragmentManager) {
		this.map = map;
		this.mapViewer = mapViewer;
		this.layerReloader = layerReloader;
		this.graph = graph;
		this.zoom = zoom;
		this.fragmentManager = fragmentManager;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
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
		mapViewer.repaint();
	}

	public void centerOn(CoordinatesInWorld coordinates) {
		map.centerOn(coordinates);
	}

	public BufferedImage createCaptureImage() {
		return mapViewer.createCaptureImage();
	}

	public void adjustZoom(int notches) {
		zoom.adjustZoom(mapViewer.getMousePositionOrCenter(), notches);
	}
}
