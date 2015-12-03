package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.map.widget.Widget;
import amidst.resources.ResourceLoader;

public class Drawer {
	private static final BufferedImage DROP_SHADOW_BOTTOM_LEFT = ResourceLoader
			.getImage("dropshadow/inner_bottom_left.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_bottom_right.png");
	private static final BufferedImage DROP_SHADOW_TOP_LEFT = ResourceLoader
			.getImage("dropshadow/inner_top_left.png");
	private static final BufferedImage DROP_SHADOW_TOP_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_top_right.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM = ResourceLoader
			.getImage("dropshadow/inner_bottom.png");
	private static final BufferedImage DROP_SHADOW_TOP = ResourceLoader
			.getImage("dropshadow/inner_top.png");
	private static final BufferedImage DROP_SHADOW_LEFT = ResourceLoader
			.getImage("dropshadow/inner_left.png");
	private static final BufferedImage DROP_SHADOW_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_right.png");

	private final Object drawLock = new Object();

	private final AffineTransform originalLayerMatrix = new AffineTransform();
	private final AffineTransform layerMatrix = new AffineTransform();

	private final Map map;
	private final Movement movement;
	private final Zoom zoom;
	private final FragmentGraph graph;
	private final Iterable<FragmentDrawer> drawers;

	private Graphics2D g2d;
	private float time;
	private int width;
	private int height;
	private Point mousePosition;
	private List<Widget> widgets;
	private FontMetrics widgetFontMetrics;

	public Drawer(Map map, Movement movement, Zoom zoom,
			FragmentGraph graph, Iterable<FragmentDrawer> drawers) {
		this.map = map;
		this.movement = movement;
		this.zoom = zoom;
		this.graph = graph;
		this.drawers = drawers;
	}

	public void drawScreenshot(Graphics2D g2d, float time, int width,
			int height, Point mousePosition, List<Widget> widgets,
			FontMetrics widgetFontMetrics) {
		synchronized (drawLock) {
			this.g2d = g2d;
			this.time = time;
			this.width = width;
			this.height = height;
			this.mousePosition = mousePosition;
			this.widgets = widgets;
			this.widgetFontMetrics = widgetFontMetrics;
			updateMap();
			clear();
			drawMap();
			drawWidgets();
		}
	}

	public void draw(Graphics2D g2d, float time, int width, int height,
			Point mousePosition, List<Widget> widgets,
			FontMetrics widgetFontMetrics) {
		synchronized (drawLock) {
			this.g2d = g2d;
			this.time = time;
			this.width = width;
			this.height = height;
			this.mousePosition = mousePosition;
			this.widgets = widgets;
			this.widgetFontMetrics = widgetFontMetrics;
			updateZoom();
			updateMovement();
			updateMap();
			clear();
			drawMap();
			drawBorder();
			drawWidgets();
		}
	}

	private void updateZoom() {
		zoom.update(map);
	}

	private void updateMovement() {
		movement.update(map, mousePosition);
	}

	private void updateMap() {
		map.setViewerDimensions(width, height);
		map.processTasks();
		map.adjustNumberOfRowsAndColumns();
	}

	private void clear() {
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
	}

	private void drawMap() {
		// TODO: is this needed?
		Graphics2D old = g2d;
		g2d = (Graphics2D) old.create();
		doDrawMap();
		g2d = old;
	}

	private void doDrawMap() {
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		AffineTransform originalGraphicsTransform = g2d.getTransform();
		initOriginalLayerMatrix(originalGraphicsTransform);
		prepareDraw();
		drawLayers();
		g2d.setTransform(originalGraphicsTransform);
	}

	private void initOriginalLayerMatrix(
			AffineTransform originalGraphicsTransform) {
		double scale = zoom.getCurrentValue();
		originalLayerMatrix.setTransform(originalGraphicsTransform);
		originalLayerMatrix.translate(map.getStartXOnScreen(),
				map.getStartYOnScreen());
		originalLayerMatrix.scale(scale, scale);
	}

	private void prepareDraw() {
		for (FragmentGraphItem fragment : graph) {
			fragment.prepareDraw(time);
		}
	}

	private void drawLayers() {
		for (FragmentDrawer drawer : drawers) {
			if (drawer.getLayerDeclaration().isVisible()) {
				initLayerMatrix();
				for (FragmentGraphItem fragment : graph) {
					if (fragment.isLoaded()) {
						setAlphaComposite(fragment.getAlpha());
						g2d.setTransform(layerMatrix);
						drawer.draw(fragment, g2d);
					}
					updateLayerMatrix(fragment, graph.getFragmentsPerRow());
				}
			}
		}
		setAlphaComposite(1.0f);
	}

	private void initLayerMatrix() {
		layerMatrix.setTransform(originalLayerMatrix);
	}

	private void updateLayerMatrix(FragmentGraphItem fragment, int fragmentsPerRow) {
		layerMatrix.translate(FragmentGraphItem.SIZE, 0);
		if (fragment.isEndOfLine()) {
			layerMatrix.translate(-FragmentGraphItem.SIZE * fragmentsPerRow,
					FragmentGraphItem.SIZE);
		}
	}

	private void drawBorder() {
		int width10 = width - 10;
		int height10 = height - 10;
		int width20 = width - 20;
		int height20 = height - 20;
		g2d.drawImage(DROP_SHADOW_TOP_LEFT, 0, 0, null);
		g2d.drawImage(DROP_SHADOW_TOP_RIGHT, width10, 0, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_LEFT, 0, height10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_RIGHT, width10, height10, null);
		g2d.drawImage(DROP_SHADOW_TOP, 10, 0, width20, 10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM, 10, height10, width20, 10, null);
		g2d.drawImage(DROP_SHADOW_LEFT, 0, 10, 10, height20, null);
		g2d.drawImage(DROP_SHADOW_RIGHT, width10, 10, 10, height20, null);
	}

	private void drawWidgets() {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (Widget widget : widgets) {
			if (widget.isVisible()) {
				setAlphaComposite(widget.getAlpha());
				widget.draw(g2d, time, widgetFontMetrics);
			}
		}
	}

	private void setAlphaComposite(float alpha) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
	}
}
