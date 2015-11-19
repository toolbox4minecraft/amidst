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

import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.object.MapObject;
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

	private final Runnable imageLayersDrawer = createImageLayersDrawer();
	private final Runnable liveLayersDrawer = createLiveLayersDrawer();
	private final Runnable objectsDrawer = createObjectsDrawer();
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

	private Fragment currentFragment;
	private AffineTransform layerDrawMatrix = new AffineTransform();
	private AffineTransform mapObjectDrawMatrix = new AffineTransform();
	private AffineTransform imageLayerDrawMatrix = new AffineTransform();

	private Runnable createImageLayersDrawer() {
		return new Runnable() {
			@Override
			public void run() {
				drawImageLayers();
			}
		};
	}

	private Runnable createLiveLayersDrawer() {
		return new Runnable() {
			@Override
			public void run() {
				drawLiveLayers();
			}
		};
	}

	private Runnable createObjectsDrawer() {
		return new Runnable() {
			@Override
			public void run() {
				drawObjects();
			}
		};
	}

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
		// TODO: is this needed?
		Graphics2D old = g2d;
		g2d = (Graphics2D) old.create();
		map.safeDraw(this);
		g2d = old;
	}

	public void doDrawMap() {
		AffineTransform originalTransform = g2d.getTransform();
		drawLayer(originalTransform, imageLayersDrawer);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		map.updateAllLayers(time);
		drawLayer(originalTransform, liveLayersDrawer);
		drawLayer(originalTransform, objectsDrawer);
		g2d.setTransform(originalTransform);
	}

	private void drawLayer(AffineTransform originalTransform, Runnable drawer) {
		Fragment startFragment = map.getStartFragment();
		if (startFragment != null) {
			initLayerDrawMatrix(originalTransform, zoom.getCurrentValue(),
					map.getStartOnScreen());
			for (Fragment fragment : startFragment) {
				currentFragment = fragment;
				drawer.run();
				updateLayerDrawMatrix();
			}
		}
	}

	private void initLayerDrawMatrix(AffineTransform originalTransform,
			double scale, Point2D.Double startOnScreen) {
		layerDrawMatrix.setToIdentity();
		layerDrawMatrix.concatenate(originalTransform);
		layerDrawMatrix.translate(startOnScreen.x, startOnScreen.y);
		layerDrawMatrix.scale(scale, scale);
	}

	private void updateLayerDrawMatrix() {
		layerDrawMatrix.translate(Fragment.SIZE, 0);
		if (currentFragment.isEndOfLine()) {
			layerDrawMatrix.translate(
					-Fragment.SIZE * map.getFragmentsPerRow(), Fragment.SIZE);
		}
	}

	private void drawImageLayers() {
		if (currentFragment.isLoaded()) {
			currentFragment.updateAlpha(time);
			ImageLayer[] imageLayers = map.getImageLayers();
			BufferedImage[] images = currentFragment.getImages();
			for (int i = 0; i < images.length; i++) {
				ImageLayer imageLayer = imageLayers[i];
				if (imageLayer.isVisible()) {
					drawImageLayer(imageLayer, images[i]);
				}
			}
			setAlphaComposite(1.0f);
		}
	}

	private void drawImageLayer(ImageLayer imageLayer, BufferedImage image) {
		setAlphaComposite(currentFragment.getAlpha() * imageLayer.getAlpha());
		initImageLayerDrawMatrix(imageLayer.getScale());
		g2d.setTransform(imageLayerDrawMatrix);
		if (g2d.getTransform().getScaleX() < 1.0f) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		g2d.drawImage(image, 0, 0, null);
	}

	// TODO: is this transformation correct?
	public void initImageLayerDrawMatrix(double scale) {
		imageLayerDrawMatrix.setTransform(layerDrawMatrix);
		imageLayerDrawMatrix.scale(scale, scale);
	}

	private void drawLiveLayers() {
		for (LiveLayer liveLayer : map.getLiveLayers()) {
			if (liveLayer.isVisible()) {
				liveLayer.drawLive(currentFragment, g2d, layerDrawMatrix);
			}
		}
	}

	private void drawObjects() {
		if (currentFragment.getAlpha() != 1.0f) {
			setAlphaComposite(currentFragment.getAlpha());
		}
		if (currentFragment.isLoaded()) {
			double invZoom = 1.0 / zoom.getCurrentValue();
			for (MapObject mapObject : currentFragment.getMapObjects()) {
				drawObject(mapObject, invZoom);
			}
		}
		if (currentFragment.getAlpha() != 1.0f) {
			setAlphaComposite(1.0f);
		}
	}

	private void drawObject(MapObject mapObject, double invZoom) {
		if (mapObject.isVisible()) {
			int width = mapObject.getWidth();
			int height = mapObject.getHeight();
			if (map.getSelectedMapObject() == mapObject) {
				width *= 1.5;
				height *= 1.5;
			}
			initMapObjectDrawMatrix(mapObject, invZoom);
			g2d.setTransform(mapObjectDrawMatrix);
			g2d.drawImage(mapObject.getImage(), -(width >> 1), -(height >> 1),
					width, height, null);
		}
	}

	private void initMapObjectDrawMatrix(MapObject mapObject, double invZoom) {
		mapObjectDrawMatrix.setTransform(layerDrawMatrix);
		mapObjectDrawMatrix.translate(mapObject.getXInFragment(),
				mapObject.getYInFragment());
		mapObjectDrawMatrix.scale(invZoom, invZoom);
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
