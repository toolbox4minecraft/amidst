package amidst.map.object;

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
	private double scale = 1.0;
	private IconLayer iconLayer;
	private Fragment fragment;

	public MapObject(MapMarkers type, int xInFragment, int yInFragment) {
		this.type = type;
		this.xInFragment = xInFragment;
		this.yInFragment = yInFragment;
	}

	public int getWidth() {
		return (int) (getImage().getWidth() * scale);
	}

	public int getHeight() {
		return (int) (getImage().getHeight() * scale);
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

	protected MapMarkers getType() {
		return type;
	}

	public int getXInWorld() {
		return xInWorld;
	}

	public int getYInWorld() {
		return yInWorld;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setIconLayer(IconLayer iconLayer) {
		this.iconLayer = iconLayer;
	}

	@Deprecated
	public boolean isIconLayerVisible() {
		return iconLayer.isVisible();
	}

	@Deprecated
	public double getMapZoom() {
		return iconLayer.getMap().getZoom();
	}

	@Override
	public String toString() {
		return getName() + " at (" + getXInWorld() + ", " + getYInWorld() + ")";
	}

	public void setFragment(Fragment fragment) {
		clearFragment();
		this.fragment = fragment;
		initFragment();
	}

	private void clearFragment() {
		if (fragment != null) {
			fragment.removeObject(this);
			fragment = null;
		}
	}

	private void initFragment() {
		if (fragment != null) {
			xInWorld = getXInFragment() + fragment.getXInWorld();
			yInWorld = getYInFragment() + fragment.getYInWorld();
			fragment.addObject(this);
		}
	}
}
