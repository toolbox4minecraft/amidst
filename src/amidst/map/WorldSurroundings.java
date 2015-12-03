package amidst.map;

import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerReloader;
import amidst.minecraft.world.CoordinatesInWorld;

public class WorldSurroundings {
	private final Map map;
	private final MapViewer mapViewer;
	private final LayerReloader layerReloader;
	private final FragmentGraph graph;
	private final MapZoom zoom;
	private final MapMovement movement;

	public WorldSurroundings(Map map, MapViewer mapViewer,
			LayerReloader layerReloader, FragmentGraph graph, MapZoom zoom,
			MapMovement movement) {
		this.map = map;
		this.mapViewer = mapViewer;
		this.layerReloader = layerReloader;
		this.graph = graph;
		this.zoom = zoom;
		this.movement = movement;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public LayerReloader getLayerReloader() {
		return layerReloader;
	}

	public void dispose() {
		graph.recycleAll();
		zoom.skipFading();
		movement.reset();
	}

	public void tickFragmentLoader() {
		map.tickFragmentLoader();
	}

	public void tickRepainter() {
		mapViewer.repaint();
	}

	public void centerOn(CoordinatesInWorld coordinates) {
		map.safeCenterOn(coordinates);
	}

	public BufferedImage createCaptureImage() {
		return mapViewer.createCaptureImage();
	}

	public void adjustZoom(int notches) {
		zoom.adjustZoom(mapViewer.getMousePositionOrCenter(), notches);
	}
}
