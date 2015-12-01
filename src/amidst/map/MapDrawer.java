package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.map.widget.Widget;
import amidst.minecraft.world.CoordinatesInWorld;
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

	private Map map;
	private MapMovement movement;
	private MapZoom zoom;
	private List<Widget> widgets;
	private FontMetrics widgetFontMetrics;

	private Graphics2D g2d;
	private float time;
	private int width;
	private int height;
	private Point mousePosition;

	private AffineTransform originalGraphicsTransform;
	private AffineTransform layerMatrix = new AffineTransform();

	public MapDrawer(Map map, MapMovement movement, MapZoom zoom,
			List<Widget> widgets, FontMetrics widgetFontMetrics) {
		this.map = map;
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
			map.safeCenterOn(CoordinatesInWorld.origin());
		}
	}

	private void clear() {
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
	}

	private void drawMap() {
		// TODO: is this needed?
		Graphics2D old = g2d;
		g2d = (Graphics2D) old.create();
		map.safeDraw(this);
		g2d = old;
	}

	public void doDrawMap(Point2D.Double startOnScreen, Fragment startFragment) {
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		originalGraphicsTransform = g2d.getTransform();
		prepareDraw(startFragment);
		drawLayers(startOnScreen, startFragment);
		g2d.setTransform(originalGraphicsTransform);
	}

	private void prepareDraw(Fragment startFragment) {
		for (Fragment fragment : startFragment) {
			fragment.prepareDraw(time);
		}
	}

	private void drawLayers(Point2D.Double startOnScreen, Fragment startFragment) {
		for (FragmentDrawer drawer : map.getFragmentDrawers()) {
			if (drawer.getLayerDeclaration().isVisible()) {
				initLayerDrawMatrix(startOnScreen, zoom.getCurrentValue());
				for (Fragment fragment : startFragment) {
					setAlphaComposite(fragment.getAlpha());
					drawer.draw(fragment, g2d, layerMatrix);
					updateLayerDrawMatrix(fragment);
				}
			}
		}
		setAlphaComposite(1.0f);
	}

	private void initLayerDrawMatrix(Point2D.Double startOnScreen, double scale) {
		layerMatrix.setToIdentity();
		layerMatrix.concatenate(originalGraphicsTransform);
		layerMatrix.translate(startOnScreen.x, startOnScreen.y);
		layerMatrix.scale(scale, scale);
	}

	private void updateLayerDrawMatrix(Fragment fragment) {
		layerMatrix.translate(Fragment.SIZE, 0);
		if (fragment.isEndOfLine()) {
			layerMatrix.translate(-Fragment.SIZE * map.getFragmentsPerRow(),
					Fragment.SIZE);
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
