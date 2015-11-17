package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;
import amidst.utilities.CoordinateUtils;

public class MapObject {
	public static MapObject fromFragmentCoordinates(IconLayer iconLayer,
			MapMarkers type, int xInFragment, int yInFragment) {
		return new MapObject(iconLayer, type, xInFragment, yInFragment);
	}

	public static MapObject fromWorldCoordinates(IconLayer iconLayer,
			MapMarkers type, int xInWorld, int yInWorld) {
		return new MapObject(iconLayer, type,
				CoordinateUtils.toFragment(xInWorld),
				CoordinateUtils.toFragment(yInWorld), xInWorld, yInWorld);
	}

	private final IconLayer iconLayer;
	private final MapMarkers type;
	private final int xInFragment;
	private final int yInFragment;
	private int xInWorld;
	private int yInWorld;
	private Fragment fragment;

	protected MapObject(IconLayer iconLayer, MapMarkers type, int xInFragment,
			int yInFragment, int xInWorld, int yInWorld) {
		this(iconLayer, type, xInFragment, yInFragment);
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	protected MapObject(IconLayer iconLayer, MapMarkers type, int xInFragment,
			int yInFragment) {
		this.iconLayer = iconLayer;
		this.type = type;
		this.xInFragment = xInFragment;
		this.yInFragment = yInFragment;
	}

	public int getWidth() {
		return getImage().getWidth();
	}

	public int getHeight() {
		return getImage().getHeight();
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

	@Deprecated
	public boolean isIconLayerVisible() {
		return iconLayer.isVisible();
	}

	@Deprecated
	public double getMapZoom() {
		return iconLayer.getMap().getZoom();
	}

	@Deprecated
	public boolean isSelected() {
		return iconLayer.getMap().getSelectedMapObject() == this;
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
			xInWorld = CoordinateUtils.toWorld(fragment.getXInWorld(),
					getXInFragment());
			yInWorld = CoordinateUtils.toWorld(fragment.getYInWorld(),
					getYInFragment());
			fragment.addObject(this);
		}
	}
}
