package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.preferences.BooleanPrefModel;
import amidst.utilities.CoordinateUtils;

public class MapObject {
	public static MapObject fromFragmentCoordinates(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInFragment, int yInFragment) {
		return new MapObject(isVisiblePreference, type, xInFragment,
				yInFragment);
	}

	public static MapObject fromWorldCoordinates(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInWorld, int yInWorld) {
		return new MapObject(isVisiblePreference, type,
				CoordinateUtils.toFragmentRelative(xInWorld),
				CoordinateUtils.toFragmentRelative(yInWorld), xInWorld, yInWorld);
	}

	private final BooleanPrefModel isVisiblePreference;
	private final MapMarkers type;
	private final int xInFragment;
	private final int yInFragment;
	private int xInWorld;
	private int yInWorld;
	private Fragment fragment;

	protected MapObject(BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInFragment, int yInFragment, int xInWorld, int yInWorld) {
		this(isVisiblePreference, type, xInFragment, yInFragment);
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
	}

	protected MapObject(BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInFragment, int yInFragment) {
		this.isVisiblePreference = isVisiblePreference;
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

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	@Override
	public String toString() {
		return getName() + " at (" + getXInWorld() + ", " + getYInWorld() + ")";
	}

	@Deprecated
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
