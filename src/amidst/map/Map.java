package amidst.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import amidst.Options;
import amidst.map.layer.BiomeLayer;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.utilities.CoordinateUtils;

public class Map {
	public class Drawer {
		private AffineTransform mat = new AffineTransform();
		private Fragment currentFragment;

		public void draw(Graphics2D g, float time) {
			AffineTransform originalTransform = g.getTransform();
			drawLayer(originalTransform, createImageLayersDrawer(g, time));
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			fragmentManager.updateAllLayers(time);
			drawLayer(originalTransform, createLiveLayersDrawer(g, time));
			drawLayer(originalTransform, createObjectsDrawer(g));
			g.setTransform(originalTransform);
		}

		private void drawLayer(AffineTransform originalTransform,
				Runnable theDrawer) {
			if (startNode != null) {
				initMat(originalTransform, zoom.getCurrentValue());
				for (Fragment fragment : startNode) {
					currentFragment = fragment;
					theDrawer.run();
					mat.translate(Fragment.SIZE, 0);
					if (currentFragment.isEndOfLine()) {
						mat.translate(-Fragment.SIZE * fragmentsPerRow,
								Fragment.SIZE);
					}
				}
			}
		}

		private Runnable createImageLayersDrawer(final Graphics2D g,
				final float time) {
			return new Runnable() {
				@Override
				public void run() {
					currentFragment.drawImageLayers(time, g, mat);
				}
			};
		}

		private Runnable createLiveLayersDrawer(final Graphics2D g,
				final float time) {
			return new Runnable() {
				@Override
				public void run() {
					currentFragment.drawLiveLayers(time, g, mat);
				}
			};
		}

		private Runnable createObjectsDrawer(final Graphics2D g) {
			return new Runnable() {
				@Override
				public void run() {
					currentFragment.drawObjects(g, mat, Map.this);
				}
			};
		}

		private void initMat(AffineTransform originalTransform, double scale) {
			mat.setToIdentity();
			mat.concatenate(originalTransform);
			mat.translate(start.x, start.y);
			mat.scale(scale, scale);
		}
	}

	private Drawer drawer = new Drawer();

	private MapObject selectedMapObject;

	private FragmentManager fragmentManager;

	private Fragment startNode;

	private Point2D.Double start = new Point2D.Double();

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object mapLock = new Object();

	private MapZoom zoom;

	public Map(FragmentManager fragmentManager, MapZoom zoom) {
		this.fragmentManager = fragmentManager;
		this.fragmentManager.setMap(this);
		this.zoom = zoom;
		safeAddStart(0, 0);
	}

	private void lockedDraw(Graphics2D g, float time) {
		int fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = viewerWidth / fragmentSizeOnScreen + 2;
		int desiredFragmentsPerColumn = viewerHeight / fragmentSizeOnScreen + 2;
		lockedAdjustNumberOfRowsAndColumns(fragmentSizeOnScreen,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		drawer.draw(g, time);
	}

	private void lockedAdjustNumberOfRowsAndColumns(int fragmentSizeOnScreen,
			int desiredFragmentsPerRow, int desiredFragmentsPerColumn) {
		int newColumns = desiredFragmentsPerRow - fragmentsPerRow;
		int newRows = desiredFragmentsPerColumn - fragmentsPerColumn;
		int newLeft = 0;
		int newAbove = 0;
		while (start.x > 0) {
			start.x -= fragmentSizeOnScreen;
			newLeft++;
		}
		while (start.x < -fragmentSizeOnScreen) {
			start.x += fragmentSizeOnScreen;
			newLeft--;
		}
		while (start.y > 0) {
			start.y -= fragmentSizeOnScreen;
			newAbove++;
		}
		while (start.y < -fragmentSizeOnScreen) {
			start.y += fragmentSizeOnScreen;
			newAbove--;
		}
		int newRight = newColumns - newLeft;
		int newBelow = newRows - newAbove;
		startNode = startNode.adjustRowsAndColumns(newAbove, newBelow, newLeft,
				newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	private void lockedAddStart(int x, int y) {
		startNode = fragmentManager.requestFragment(x, y);
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	// TODO: Support longs?
	private void lockedCenterOn(long xInWorld, long yInWorld) {
		if (startNode != null) {
			startNode = startNode.recycleAll(fragmentManager);
		}
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = CoordinateUtils.toFragmentRelative(xInWorld);
		long yFragmentRelative = CoordinateUtils.toFragmentRelative(yInWorld);
		start.x = xCenterOnScreen - zoom.worldToScreen(xFragmentRelative);
		start.y = yCenterOnScreen - zoom.worldToScreen(yFragmentRelative);
		long xFragmentCorner = CoordinateUtils.toFragmentCorner(xInWorld);
		long yFragmentCorner = CoordinateUtils.toFragmentCorner(yInWorld);
		lockedAddStart((int) xFragmentCorner, (int) yFragmentCorner);
	}

	private void safeAddStart(int startX, int startY) {
		synchronized (mapLock) {
			lockedAddStart(startX, startY);
		}
	}

	public void safeDraw(Graphics2D g, float time) {
		synchronized (mapLock) {
			lockedDraw(g, time);
		}
	}

	public void safeCenterOn(long x, long y) {
		synchronized (mapLock) {
			lockedCenterOn(x, y);
		}
	}

	public void safeDispose() {
		synchronized (mapLock) {
			lockedDispose();
		}
	}

	public Fragment getFragmentAt(Point position) {
		Point cornerPosition = new Point(position.x >> Fragment.SIZE_SHIFT,
				position.y >> Fragment.SIZE_SHIFT);
		Point fragmentPosition = new Point();
		if (startNode != null) {
			for (Fragment fragment : startNode) {
				fragmentPosition.x = fragment.getFragmentXInWorld();
				fragmentPosition.y = fragment.getFragmentYInWorld();
				if (cornerPosition.equals(fragmentPosition)) {
					return fragment;
				}
			}
		}
		return null;
	}

	public MapObject getMapObjectAt(Point positionOnScreen, double maxRange) {
		double x = start.x;
		double y = start.y;
		MapObject closestObject = null;
		double closestDistance = maxRange;
		int fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		if (startNode != null) {
			for (Fragment fragment : startNode) {
				for (MapObject mapObject : fragment.getMapObjects()) {
					if (mapObject.isVisible()) {
						double distance = getPositionOnScreen(x, y, mapObject)
								.distance(positionOnScreen);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestObject = mapObject;
						}
					}
				}
				x += fragmentSizeOnScreen;
				if (fragment.isEndOfLine()) {
					x = start.x;
					y += fragmentSizeOnScreen;
				}
			}
		}
		return closestObject;
	}

	private Point getPositionOnScreen(double x, double y, MapObject mapObject) {
		Point result = new Point(mapObject.getXInFragment(),
				mapObject.getYInFragment());
		result.x = zoom.worldToScreen(result.x);
		result.y = zoom.worldToScreen(result.y);
		result.x += x;
		result.y += y;
		return result;
	}

	public String getBiomeNameAt(Point point) {
		if (startNode != null) {
			for (Fragment fragment : startNode) {
				if ((fragment.getXInWorld() <= point.x)
						&& (fragment.getYInWorld() <= point.y)
						&& (fragment.getXInWorld() + Fragment.SIZE > point.x)
						&& (fragment.getYInWorld() + Fragment.SIZE > point.y)) {
					int x = point.x - fragment.getXInWorld();
					int y = point.y - fragment.getYInWorld();

					return getBiomeNameForFragment(fragment, x, y);
				}
			}
		}
		return "Unknown";
	}

	public String getBiomeAliasAt(Point point) {
		if (startNode != null) {
			for (Fragment fragment : startNode) {
				if ((fragment.getXInWorld() <= point.x)
						&& (fragment.getYInWorld() <= point.y)
						&& (fragment.getXInWorld() + Fragment.SIZE > point.x)
						&& (fragment.getYInWorld() + Fragment.SIZE > point.y)) {
					int x = point.x - fragment.getXInWorld();
					int y = point.y - fragment.getYInWorld();

					return getBiomeAliasForFragment(fragment, x, y);
				}
			}
		}
		return "Unknown";
	}

	private String getBiomeNameForFragment(Fragment fragment, int blockX,
			int blockY) {
		return Biome.biomes[getBiomeForFragment(fragment, blockX, blockY)].name;
	}

	private String getBiomeAliasForFragment(Fragment fragment, int blockX,
			int blockY) {
		return Options.instance.biomeColorProfile
				.getAliasForId(getBiomeForFragment(fragment, blockX, blockY));
	}

	private int getBiomeForFragment(Fragment fragment, int blockX, int blockY) {
		int index = (blockY >> 2) * Fragment.BIOME_SIZE + (blockX >> 2);
		return fragment.getBiomeData()[index];
	}

	public void moveBy(Point2D.Double speed) {
		moveBy(speed.x, speed.y);
	}

	public void moveBy(double x, double y) {
		start.x += x;
		start.y += y;
	}

	public Point screenToWorld(Point pointOnScreen) {
		Point result = pointOnScreen.getLocation();

		result.x -= start.x;
		result.y -= start.y;

		result.x = zoom.screenToWorld(result.x);
		result.y = zoom.screenToWorld(result.y);

		result.x += startNode.getXInWorld();
		result.y += startNode.getYInWorld();

		return result;
	}

	public Point2D.Double getScaled(double oldScale, double newScale, Point p) {
		double baseX = p.x - start.x;
		double scaledX = baseX - (baseX / oldScale) * newScale;

		double baseY = p.y - start.y;
		double scaledY = baseY - (baseY / oldScale) * newScale;

		return new Point2D.Double(scaledX, scaledY);
	}

	private void repaintImageLayer(int id) {
		if (startNode != null) {
			for (Fragment fragment : startNode) {
				fragmentManager.repaintFragmentImageLayer(fragment, id);
			}
		}
	}

	private void lockedDispose() {
		fragmentManager.reset();
	}

	public double getZoom() {
		return zoom.getCurrentValue();
	}

	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}

	public void setViewerWidth(int viewerWidth) {
		this.viewerWidth = viewerWidth;
	}

	public void setViewerHeight(int viewerHeight) {
		this.viewerHeight = viewerHeight;
	}

	public MapObject getSelectedMapObject() {
		return selectedMapObject;
	}

	public void setSelectedMapObject(MapObject selectedMapObject) {
		this.selectedMapObject = selectedMapObject;
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	// TODO: move the thread somewhere else?
	@Deprecated
	public void repaintBiomeLayer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				repaintImageLayer(BiomeLayer.getInstance().getLayerId());
			}
		}).start();
	}
}
