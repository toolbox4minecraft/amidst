package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.map.widget.Widget;
import amidst.minecraft.world.World;
import amidst.resources.ResourceLoader;

public class MapDrawer {
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
	private boolean isFirstDraw = true;

	private World world;
	private Map map;
	private MapViewer mapViewer;
	private MapMovement movement;
	private MapZoom zoom;
	private List<Widget> widgets;
	private FontMetrics widgetFontMetrics;

	private Graphics2D g2d;
	private float time;
	private int width;
	private int height;
	private Point mousePosition;

	public MapDrawer(World world, Map map, MapViewer mapViewer,
			MapMovement movement, MapZoom zoom, List<Widget> widgets,
			FontMetrics widgetFontMetrics) {
		this.world = world;
		this.map = map;
		this.mapViewer = mapViewer;
		this.movement = movement;
		this.zoom = zoom;
		this.widgets = widgets;
		this.widgetFontMetrics = widgetFontMetrics;
	}

	public void drawScreenshot(Graphics2D g2d, float time, int width,
			int height, Point mousePosition) {
		synchronized (drawLock) {
			this.g2d = g2d;
			this.time = time;
			this.width = width;
			this.height = height;
			this.mousePosition = mousePosition;
			setViewerDimensions();
			centerMapIfNecessary();
			clear();
			drawMap();
			drawWidgets();
		}
	}

	public void draw(Graphics2D g2d, float time, int width, int height,
			Point mousePosition) {
		synchronized (drawLock) {
			this.g2d = g2d;
			this.time = time;
			this.width = width;
			this.height = height;
			this.mousePosition = mousePosition;
			setViewerDimensions();
			updateMapZoom();
			updateMapMovement();
			centerMapIfNecessary();
			clear();
			drawMap();
			drawBorder();
			drawWidgets();
		}
	}

	private void setViewerDimensions() {
		map.setViewerWidth(width);
		map.setViewerHeight(height);
	}

	private void updateMapZoom() {
		zoom.update(map);
	}

	private void updateMapMovement() {
		movement.update(map, mousePosition);
	}

	private void centerMapIfNecessary() {
		if (isFirstDraw) {
			isFirstDraw = false;
			map.safeCenterOn(0, 0);
		}
	}

	private void clear() {
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
	}

	private void drawMap() {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		map.safeDraw((Graphics2D) g2d.create(), time);
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
		for (Widget widget : widgets) {
			if (widget.isVisible()) {
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, widget.getAlpha()));
				widget.draw(g2d, time, widgetFontMetrics);
			}
		}
	}
}
