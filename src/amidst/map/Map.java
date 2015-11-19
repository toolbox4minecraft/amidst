package amidst.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.layer.BiomeLayer;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;

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

	private Fragment startNode = new Fragment();

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
		int scaledFragmentSize = (int) (Fragment.SIZE * zoom.getCurrentValue());
		int desiredFragmentsPerRow = viewerWidth / scaledFragmentSize + 2;
		int desiredFragmentsPerColumn = viewerHeight / scaledFragmentSize + 2;
		lockedAdjustNumberOfRowsAndColumns(scaledFragmentSize,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		drawer.draw(g, time);
	}

	private void lockedAdjustNumberOfRowsAndColumns(int scaledFragmentSize,
			int desiredFragmentsPerRow, int desiredFragmentsPerColumn) {
		int newLeft = 0;
		int newRight = 0;
		int newAbove = 0;
		int newBelow = 0;
		while (start.x > 0) {
			start.x -= scaledFragmentSize;
			newLeft++;
		}
		while (start.x < -scaledFragmentSize) {
			start.x += scaledFragmentSize;
			newRight++;
		}
		while (start.y > 0) {
			start.y -= scaledFragmentSize;
			newAbove++;
		}
		while (start.y < -scaledFragmentSize) {
			start.y += scaledFragmentSize;
			newBelow++;
		}
		if (desiredFragmentsPerRow != fragmentsPerRow + newLeft + newRight) {
			Log.w("columns don't match");
		}
		if (desiredFragmentsPerColumn != fragmentsPerColumn + newAbove
				+ newBelow) {
			Log.w("rows don't match");
		}
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

	private void lockedCenterOn(long x, long y) {
		long fragOffsetX = x % Fragment.SIZE;
		long fragOffsetY = y % Fragment.SIZE;
		long startX = x - fragOffsetX;
		long startY = y - fragOffsetY;
		startNode = startNode.recycleAll(fragmentManager);
		// TODO: Support longs?
		double offsetX = viewerWidth >> 1;
		double offsetY = viewerHeight >> 1;

		offsetX -= (fragOffsetX) * zoom.getCurrentValue();
		offsetY -= (fragOffsetY) * zoom.getCurrentValue();

		start.x = offsetX;
		start.y = offsetY;

		lockedAddStart((int) startX, (int) startY);
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
		for (Fragment fragment : startNode) {
			fragmentPosition.x = fragment.getFragmentXInWorld();
			fragmentPosition.y = fragment.getFragmentYInWorld();
			if (cornerPosition.equals(fragmentPosition)) {
				return fragment;
			}
		}
		return null;
	}

	public MapObject getObjectAt(Point position, double maxRange) {
		double x = start.x;
		double y = start.y;
		MapObject closestObject = null;
		double closestDistance = maxRange;
		int size = (int) (Fragment.SIZE * zoom.getCurrentValue());
		for (Fragment fragment : startNode) {
			for (MapObject mapObject : fragment.getMapObjects()) {
				if (mapObject.isVisible()) {
					double distance = getPosition(x, y, mapObject).distance(
							position);
					if (closestDistance > distance) {
						closestDistance = distance;
						closestObject = mapObject;
					}
				}
			}
			x += size;
			if (fragment.isEndOfLine()) {
				x = start.x;
				y += size;
			}
		}
		return closestObject;
	}

	private Point getPosition(double x, double y, MapObject mapObject) {
		Point result = new Point(mapObject.getXInFragment(),
				mapObject.getYInFragment());
		result.x *= zoom.getCurrentValue();
		result.y *= zoom.getCurrentValue();
		result.x += x;
		result.y += y;
		return result;
	}

	public String getBiomeNameAt(Point point) {
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
		return "Unknown";
	}

	public String getBiomeAliasAt(Point point) {
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

	public Point screenToLocal(Point inPoint) {
		Point point = inPoint.getLocation();

		point.x -= start.x;
		point.y -= start.y;

		// TODO: int -> double -> int = bad?
		point.x /= zoom.getCurrentValue();
		point.y /= zoom.getCurrentValue();

		point.x += startNode.getXInWorld();
		point.y += startNode.getYInWorld();

		return point;
	}

	public Point2D.Double getScaled(double oldScale, double newScale, Point p) {
		double baseX = p.x - start.x;
		double scaledX = baseX - (baseX / oldScale) * newScale;

		double baseY = p.y - start.y;
		double scaledY = baseY - (baseY / oldScale) * newScale;

		return new Point2D.Double(scaledX, scaledY);
	}

	private void repaintImageLayer(int id) {
		for (Fragment fragment : startNode) {
			fragmentManager.repaintFragmentImageLayer(fragment, id);
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
