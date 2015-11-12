package amidst.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import amidst.map.layers.BiomeLayer;

public class Map {
	public class Drawer {
		private AffineTransform mat = new AffineTransform();
		private boolean isFirstDraw = true;
		private Fragment currentFragment;

		public void draw(Graphics2D g, float time, int size) {
			isFirstDraw = false;
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
			currentFragment = startNode;
			if (currentFragment.hasNext()) {
				initMat(originalTransform);
				while (currentFragment.hasNext()) {
					currentFragment = currentFragment.getNext();
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
					currentFragment.drawObjects(g, mat);
				}
			};
		}

		private void initMat(AffineTransform originalTransform) {
			mat.setToIdentity();
			mat.concatenate(originalTransform);
			mat.translate(start.x, start.y);
			mat.scale(scale, scale);
		}

		public boolean isFirstDraw() {
			return isFirstDraw;
		}
	}

	private Drawer drawer = new Drawer();

	public static Map instance = null;
	private static final boolean START = true;
	private static final boolean END = false;
	private FragmentManager fragmentManager;

	private Fragment startNode = new Fragment();

	private double scale = 0.25;
	private Point2D.Double start = new Point2D.Double();

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object resizeLock = new Object();
	private final Object drawLock = new Object();

	public Map(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		this.fragmentManager.setMap(this);
		addStart(0, 0);
		instance = this;
	}

	public void repaintFragmentsLayer(int id) {
		Fragment fragment = startNode;
		while (fragment.hasNext()) {
			fragment = fragment.getNext();
			fragmentManager.repaintFragmentLayer(fragment, id);
		}
	}

	public void repaintFragments() {
		Fragment fragment = startNode;
		while (fragment.hasNext()) {
			fragment = fragment.getNext();
			fragmentManager.repaintFragment(fragment);
		}
	}

	public void draw(Graphics2D g, float time) {
		if (drawer.isFirstDraw()) {
			centerOn(0, 0);
		}
		synchronized (drawLock) {
			int scaledFragmentSize = (int) (Fragment.SIZE * scale);
			int desiredFragmentsPerRow = viewerWidth / scaledFragmentSize + 2;
			int desiredFragmentsPerColumn = viewerHeight / scaledFragmentSize
					+ 2;
			adjustNumberOfRowsAndColumns(desiredFragmentsPerRow,
					desiredFragmentsPerColumn);
			moveStart(scaledFragmentSize);
			drawer.draw(g, time, scaledFragmentSize);
		}
	}

	private void moveStart(int size) {
		while (start.x > 0) {
			start.x -= size;
			addColumn(START);
			removeColumn(END);
		}
		while (start.x < -size) {
			start.x += size;
			addColumn(END);
			removeColumn(START);
		}
		while (start.y > 0) {
			start.y -= size;
			addRow(START);
			removeRow(END);
		}
		while (start.y < -size) {
			start.y += size;
			addRow(END);
			removeRow(START);
		}
	}

	private void adjustNumberOfRowsAndColumns(int desiredFragmentsPerRow,
			int desiredFragmentsPerColumn) {
		while (fragmentsPerRow < desiredFragmentsPerRow) {
			addColumn(END);
		}
		while (fragmentsPerRow > desiredFragmentsPerRow) {
			removeColumn(END);
		}
		while (fragmentsPerColumn < desiredFragmentsPerColumn) {
			addRow(END);
		}
		while (fragmentsPerColumn > desiredFragmentsPerColumn) {
			removeRow(END);
		}
	}

	public void addStart(int x, int y) {
		synchronized (resizeLock) {
			Fragment start = fragmentManager.requestFragment(x, y);
			start.setEndOfLine(true);
			startNode.setNext(start);
			fragmentsPerRow = 1;
			fragmentsPerColumn = 1;
		}
	}

	public void addColumn(boolean start) {
		synchronized (resizeLock) {
			int x = 0;
			Fragment fragment = startNode;
			if (start) {
				x = fragment.getNext().getBlockX() - Fragment.SIZE;
				Fragment newFrag = fragmentManager.requestFragment(x, fragment
						.getNext().getBlockY());
				newFrag.setNext(startNode.getNext());
				startNode.setNext(newFrag);
			}
			while (fragment.hasNext()) {
				fragment = fragment.getNext();
				if (fragment.isEndOfLine()) {
					if (start) {
						if (fragment.hasNext()) {
							Fragment newFrag = fragmentManager.requestFragment(
									x, fragment.getBlockY() + Fragment.SIZE);
							newFrag.setNext(fragment.getNext());
							fragment.setNext(newFrag);
							fragment = newFrag;
						}
					} else {
						Fragment newFrag = fragmentManager.requestFragment(
								fragment.getBlockX() + Fragment.SIZE,
								fragment.getBlockY());

						if (fragment.hasNext()) {
							newFrag.setNext(fragment.getNext());
						}
						newFrag.setEndOfLine(true);
						fragment.setEndOfLine(false);
						fragment.setNext(newFrag);
						fragment = newFrag;
					}
				}
			}
			fragmentsPerRow++;
		}
	}

	public void removeRow(boolean start) {
		synchronized (resizeLock) {
			if (start) {
				for (int i = 0; i < fragmentsPerRow; i++) {
					Fragment frag = startNode.getNext();
					frag.remove();
					fragmentManager.recycleFragment(frag);
				}
			} else {
				Fragment fragment = startNode;
				while (fragment.hasNext()) {
					fragment = fragment.getNext();
				}
				for (int i = 0; i < fragmentsPerRow; i++) {
					fragment.remove();
					fragmentManager.recycleFragment(fragment);
					fragment = fragment.getPrevious();
				}
			}
			fragmentsPerColumn--;
		}
	}

	public void addRow(boolean start) {
		synchronized (resizeLock) {
			Fragment fragment = startNode;
			int y;
			if (start) {
				fragment = startNode.getNext();
				y = fragment.getBlockY() - Fragment.SIZE;
			} else {
				while (fragment.hasNext()) {
					fragment = fragment.getNext();
				}
				y = fragment.getBlockY() + Fragment.SIZE;
			}

			fragmentsPerColumn++;
			Fragment newFrag = fragmentManager.requestFragment(startNode
					.getNext().getBlockX(), y);
			Fragment chainFrag = newFrag;
			for (int i = 1; i < fragmentsPerRow; i++) {
				Fragment tempFrag = fragmentManager.requestFragment(
						chainFrag.getBlockX() + Fragment.SIZE,
						chainFrag.getBlockY());
				chainFrag.setNext(tempFrag);
				chainFrag = tempFrag;
				if (i == (fragmentsPerRow - 1)) {
					chainFrag.setEndOfLine(true);
				}
			}
			if (start) {
				chainFrag.setNext(fragment);
				startNode.setNext(newFrag);
			} else {
				fragment.setNext(newFrag);
			}
		}
	}

	public void removeColumn(boolean start) {
		synchronized (resizeLock) {
			Fragment fragment = startNode;
			if (start) {
				fragmentManager.recycleFragment(fragment.getNext());
				startNode.getNext().remove();
			}
			while (fragment.hasNext()) {
				fragment = fragment.getNext();
				if (fragment.isEndOfLine()) {
					if (start) {
						if (fragment.hasNext()) {
							Fragment tempFrag = fragment.getNext();
							tempFrag.remove();
							fragmentManager.recycleFragment(tempFrag);
						}
					} else {
						fragment.getPrevious().setEndOfLine(true);
						fragment.remove();
						fragmentManager.recycleFragment(fragment);
						fragment = fragment.getPrevious();
					}
				}
			}
			fragmentsPerRow--;
		}
	}

	public void moveBy(Point2D.Double speed) {
		moveBy(speed.x, speed.y);
	}

	public void moveBy(double x, double y) {
		start.x += x;
		start.y += y;
	}

	public void centerOn(long x, long y) {
		long fragOffsetX = x % Fragment.SIZE;
		long fragOffsetY = y % Fragment.SIZE;
		long startX = x - fragOffsetX;
		long startY = y - fragOffsetY;
		synchronized (drawLock) {
			while (fragmentsPerColumn > 1) {
				removeRow(false);
			}
			while (fragmentsPerRow > 1) {
				removeColumn(false);
			}
			Fragment frag = startNode.getNext();
			frag.remove();
			fragmentManager.recycleFragment(frag);
			// TODO: Support longs?
			double offsetX = viewerWidth >> 1;
			double offsetY = viewerHeight >> 1;

			offsetX -= (fragOffsetX) * scale;
			offsetY -= (fragOffsetY) * scale;

			start.x = offsetX;
			start.y = offsetY;

			addStart((int) startX, (int) startY);
		}
	}

	public void setZoom(double scale) {
		this.scale = scale;
	}

	public double getZoom() {
		return scale;
	}

	public Point2D.Double getScaled(double oldScale, double newScale, Point p) {
		double baseX = p.x - start.x;
		double scaledX = baseX - (baseX / oldScale) * newScale;

		double baseY = p.y - start.y;
		double scaledY = baseY - (baseY / oldScale) * newScale;

		return new Point2D.Double(scaledX, scaledY);
	}

	public void dispose() {
		synchronized (drawLock) {
			fragmentManager.reset();
		}
	}

	public Fragment getFragmentAt(Point position) {
		Fragment frag = startNode;
		Point cornerPosition = new Point(position.x >> Fragment.SIZE_SHIFT,
				position.y >> Fragment.SIZE_SHIFT);
		Point fragmentPosition = new Point();
		while (frag.hasNext()) {
			frag = frag.getNext();
			fragmentPosition.x = frag.getFragmentX();
			fragmentPosition.y = frag.getFragmentY();
			if (cornerPosition.equals(fragmentPosition))
				return frag;
		}
		return null;
	}

	public MapObject getObjectAt(Point position, double maxRange) {
		double x = start.x;
		double y = start.y;
		MapObject closestObject = null;
		double closestDistance = maxRange;
		Fragment frag = startNode;
		int size = (int) (Fragment.SIZE * scale);
		while (frag.hasNext()) {
			frag = frag.getNext();
			for (int i = 0; i < frag.getObjectsLength(); i++) {
				if (frag.getObjects()[i].parentLayer.isVisible()) {
					Point objPosition = frag.getObjects()[i].getLocation();
					objPosition.x *= scale;
					objPosition.y *= scale;
					objPosition.x += x;
					objPosition.y += y;

					double distance = objPosition.distance(position);
					if (distance < closestDistance) {
						closestDistance = distance;
						closestObject = frag.getObjects()[i];
					}
				}
			}
			x += size;
			if (frag.isEndOfLine()) {
				x = start.x;
				y += size;
			}
		}
		return closestObject;
	}

	public Point screenToLocal(Point inPoint) {
		Point point = inPoint.getLocation();

		point.x -= start.x;
		point.y -= start.y;

		// TODO: int -> double -> int = bad?
		point.x /= scale;
		point.y /= scale;

		point.x += startNode.getNext().getBlockX();
		point.y += startNode.getNext().getBlockY();

		return point;
	}

	public String getBiomeNameAt(Point point) {
		Fragment frag = startNode;
		while (frag.hasNext()) {
			frag = frag.getNext();
			if ((frag.getBlockX() <= point.x) && (frag.getBlockY() <= point.y)
					&& (frag.getBlockX() + Fragment.SIZE > point.x)
					&& (frag.getBlockY() + Fragment.SIZE > point.y)) {
				int x = point.x - frag.getBlockX();
				int y = point.y - frag.getBlockY();

				return BiomeLayer.getBiomeNameForFragment(frag, x, y);
			}
		}
		return "Unknown";
	}

	public String getBiomeAliasAt(Point point) {
		Fragment frag = startNode;
		while (frag.hasNext()) {
			frag = frag.getNext();
			if ((frag.getBlockX() <= point.x) && (frag.getBlockY() <= point.y)
					&& (frag.getBlockX() + Fragment.SIZE > point.x)
					&& (frag.getBlockY() + Fragment.SIZE > point.y)) {
				int x = point.x - frag.getBlockX();
				int y = point.y - frag.getBlockY();

				return BiomeLayer.getBiomeAliasForFragment(frag, x, y);
			}
		}
		return "Unknown";
	}

	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}

	public int getViewerWidth() {
		return viewerWidth;
	}

	public void setViewerWidth(int viewerWidth) {
		this.viewerWidth = viewerWidth;
	}

	public int getViewerHeight() {
		return viewerHeight;
	}

	public void setViewerHeight(int viewerHeight) {
		this.viewerHeight = viewerHeight;
	}
}
