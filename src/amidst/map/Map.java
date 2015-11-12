package amidst.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import amidst.map.layers.BiomeLayer;

public class Map {
	public static Map instance = null;
	private static final boolean START = true, END = false;
	private FragmentManager fragmentManager;

	private Fragment startNode = new Fragment();

	private double scale = 0.25;
	private Point2D.Double start;

	public int tileWidth, tileHeight;
	public int width = 1, height = 1;

	private final Object resizeLock = new Object(), drawLock = new Object();
	private AffineTransform mat;

	private boolean firstDraw = true;

	// TODO : This must be changed with the removal of ChunkManager
	public Map(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		fragmentManager.setMap(this);
		mat = new AffineTransform();

		start = new Point2D.Double();
		addStart(0, 0);

		instance = this;
	}

	public void resetImageLayer(int id) {
		Fragment frag = startNode;
		while (frag.hasNext()) {
			frag = frag.getNext();
			fragmentManager.repaintFragmentLayer(frag, id);
		}
	}

	public void resetFragments() {
		Fragment frag = startNode;
		while (frag.hasNext()) {
			frag = frag.getNext();
			fragmentManager.repaintFragment(frag);
		}
	}

	public void draw(Graphics2D g, float time) {
		AffineTransform originalTransform = g.getTransform();
		if (firstDraw) {
			firstDraw = false;
			centerOn(0, 0);
		}

		synchronized (drawLock) {
			int size = (int) (Fragment.SIZE * scale);
			int w = width / size + 2;
			int h = height / size + 2;

			while (tileWidth < w)
				addColumn(END);
			while (tileWidth > w)
				removeColumn(END);
			while (tileHeight < h)
				addRow(END);
			while (tileHeight > h)
				removeRow(END);

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

			Fragment frag = startNode;
			size = Fragment.SIZE;
			if (frag.hasNext()) {
				mat.setToIdentity();
				mat.concatenate(originalTransform);
				mat.translate(start.x, start.y);
				mat.scale(scale, scale);
				while (frag.hasNext()) {
					frag = frag.getNext();
					frag.drawImageLayers(time, g, mat);
					mat.translate(size, 0);
					if (frag.isEndOfLine())
						mat.translate(-size * w, size);
				}
			}
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			fragmentManager.updateAllLayers(time);
			frag = startNode;
			if (frag.hasNext()) {
				mat.setToIdentity();
				mat.concatenate(originalTransform);
				mat.translate(start.x, start.y);
				mat.scale(scale, scale);
				while (frag.hasNext()) {
					frag = frag.getNext();
					frag.drawLiveLayers(time, g, mat);
					mat.translate(size, 0);
					if (frag.isEndOfLine())
						mat.translate(-size * w, size);
				}
			}

			frag = startNode;
			if (frag.hasNext()) {
				mat.setToIdentity();
				mat.concatenate(originalTransform);
				mat.translate(start.x, start.y);
				mat.scale(scale, scale);
				while (frag.hasNext()) {
					frag = frag.getNext();
					frag.drawObjects(g, mat);
					mat.translate(size, 0);
					if (frag.isEndOfLine())
						mat.translate(-size * w, size);
				}
			}

			g.setTransform(originalTransform);
		}
	}

	public void addStart(int x, int y) {
		synchronized (resizeLock) {
			Fragment start = fragmentManager.requestFragment(x, y);
			start.setEndOfLine(true);
			startNode.setNext(start);
			tileWidth = 1;
			tileHeight = 1;
		}
	}

	public void addColumn(boolean start) {
		synchronized (resizeLock) {
			int x = 0;
			Fragment frag = startNode;
			if (start) {
				x = frag.getNext().getBlockX() - Fragment.SIZE;
				Fragment newFrag = fragmentManager.requestFragment(x,
						frag.getNext().getBlockY());
				newFrag.setNext(startNode.getNext());
				startNode.setNext(newFrag);
			}
			while (frag.hasNext()) {
				frag = frag.getNext();
				if (frag.isEndOfLine()) {
					if (start) {
						if (frag.hasNext()) {
							Fragment newFrag = fragmentManager.requestFragment(
									x, frag.getBlockY() + Fragment.SIZE);
							newFrag.setNext(frag.getNext());
							frag.setNext(newFrag);
							frag = newFrag;
						}
					} else {
						Fragment newFrag = fragmentManager.requestFragment(
								frag.getBlockX() + Fragment.SIZE, frag.getBlockY());

						if (frag.hasNext()) {
							newFrag.setNext(frag.getNext());
						}
						newFrag.setEndOfLine(true);
						frag.setEndOfLine(false);
						frag.setNext(newFrag);
						frag = newFrag;

					}
				}
			}
			tileWidth++;
		}
	}

	public void removeRow(boolean start) {
		synchronized (resizeLock) {
			if (start) {
				for (int i = 0; i < tileWidth; i++) {
					Fragment frag = startNode.getNext();
					frag.remove();
					fragmentManager.returnFragment(frag);
				}
			} else {
				Fragment frag = startNode;
				while (frag.hasNext())
					frag = frag.getNext();
				for (int i = 0; i < tileWidth; i++) {
					frag.remove();
					fragmentManager.returnFragment(frag);
					frag = frag.getPrevious();
				}
			}
			tileHeight--;
		}
	}

	public void addRow(boolean start) {
		synchronized (resizeLock) {
			Fragment frag = startNode;
			int y;
			if (start) {
				frag = startNode.getNext();
				y = frag.getBlockY() - Fragment.SIZE;
			} else {
				while (frag.hasNext())
					frag = frag.getNext();
				y = frag.getBlockY() + Fragment.SIZE;
			}

			tileHeight++;
			Fragment newFrag = fragmentManager.requestFragment(
					startNode.getNext().getBlockX(), y);
			Fragment chainFrag = newFrag;
			for (int i = 1; i < tileWidth; i++) {
				Fragment tempFrag = fragmentManager.requestFragment(
						chainFrag.getBlockX() + Fragment.SIZE, chainFrag.getBlockY());
				chainFrag.setNext(tempFrag);
				chainFrag = tempFrag;
				if (i == (tileWidth - 1))
					chainFrag.setEndOfLine(true);
			}
			if (start) {
				chainFrag.setNext(frag);
				startNode.setNext(newFrag);
			} else {
				frag.setNext(newFrag);
			}
		}
	}

	public void removeColumn(boolean start) {
		synchronized (resizeLock) {
			Fragment frag = startNode;
			if (start) {
				fragmentManager.returnFragment(frag.getNext());
				startNode.getNext().remove();
			}
			while (frag.hasNext()) {
				frag = frag.getNext();
				if (frag.isEndOfLine()) {
					if (start) {
						if (frag.hasNext()) {
							Fragment tempFrag = frag.getNext();
							tempFrag.remove();
							fragmentManager.returnFragment(tempFrag);
						}
					} else {
						frag.getPrevious().setEndOfLine(true);
						frag.remove();
						fragmentManager.returnFragment(frag);
						frag = frag.getPrevious();
					}
				}
			}
			tileWidth--;
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
			while (tileHeight > 1)
				removeRow(false);
			while (tileWidth > 1)
				removeColumn(false);
			Fragment frag = startNode.getNext();
			frag.remove();
			fragmentManager.returnFragment(frag);
			// TODO: Support longs?
			double offsetX = width >> 1;
			double offsetY = height >> 1;

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

}
