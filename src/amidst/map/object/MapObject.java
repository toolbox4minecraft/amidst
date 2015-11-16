package amidst.map.object;

import java.awt.Point;
import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;

public abstract class MapObject {
	public static int toFragmentCoordinates(int coordinate) {
		return getOffset(coordinate) + coordinate % Fragment.SIZE;
	}

	private static int getOffset(int coordinate) {
		if (coordinate < 0) {
			return Fragment.SIZE;
		} else {
			return 0;
		}
	}

	private MapMarkers type;
	private final int x;
	private final int y;

	private int rx;
	private int ry;
	private double localScale = 1.0;
	private IconLayer parentLayer;

	public MapObject(MapMarkers type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public int getWidth() {
		return (int) (getImage().getWidth() * localScale);
	}

	public int getHeight() {
		return (int) (getImage().getHeight() * localScale);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return type.getName();
	}

	public BufferedImage getImage() {
		return type.getImage();
	}

	@Deprecated
	public Point getAsPoint() {
		return new Point(x, y);
	}

	protected MapMarkers getType() {
		return type;
	}

	public int getRx() {
		return rx;
	}

	public void setRx(int rx) {
		this.rx = rx;
	}

	public int getRy() {
		return ry;
	}

	public void setRy(int ry) {
		this.ry = ry;
	}

	public void setLocalScale(double localScale) {
		this.localScale = localScale;
	}

	public void setParentLayer(IconLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	@Deprecated
	public boolean isParentLayerVisible() {
		return parentLayer.isVisible();
	}

	@Deprecated
	public double getMapZoom() {
		return parentLayer.getMap().getZoom();
	}

	@Override
	public String toString() {
		return getName() + " at (" + x + ", " + y + ")";
	}
}
