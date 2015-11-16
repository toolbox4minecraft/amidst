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
	private final int xInFragment;
	private final int yInFragment;

	private int xInWorld;
	private int yInWorld;
	private double localScale = 1.0;
	private IconLayer parentLayer;

	public MapObject(MapMarkers type, int xInFragment, int yInFragment) {
		this.type = type;
		this.xInFragment = xInFragment;
		this.yInFragment = yInFragment;
	}

	public int getWidth() {
		return (int) (getImage().getWidth() * localScale);
	}

	public int getHeight() {
		return (int) (getImage().getHeight() * localScale);
	}

	public int getXInFragment() {
		return xInFragment;
	}

	public int getYInFragment() {
		return yInFragment;
	}

	public String getName() {
		return type.getName();
	}

	public BufferedImage getImage() {
		return type.getImage();
	}

	@Deprecated
	public Point getAsPoint() {
		return new Point(xInFragment, yInFragment);
	}

	protected MapMarkers getType() {
		return type;
	}

	public int getXInWorld() {
		return xInWorld;
	}

	public int getYInWorld() {
		return yInWorld;
	}

	public void setXInWorld(int xInWorld) {
		this.xInWorld = xInWorld;
	}

	public void setYInWorld(int yInWorld) {
		this.yInWorld = yInWorld;
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
		return getName() + " at (" + getXInWorld() + ", " + getYInWorld() + ")";
	}
}
