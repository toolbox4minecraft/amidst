package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.preferences.BooleanPrefModel;
import amidst.utilities.CoordinateUtils;

public class MapObject {
	public static MapObject fromFragmentCoordinatesAndFragment(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInFragment, int yInFragment, Fragment fragment) {
		int xInWorld = CoordinateUtils.toWorld(fragment.getXInWorld(),
				xInFragment);
		int yInWorld = CoordinateUtils.toWorld(fragment.getYInWorld(),
				yInFragment);
		return new MapObject(isVisiblePreference, xInFragment, yInFragment,
				xInWorld, yInWorld, type.getName(), type.getImage());
	}

	public static MapObject fromWorldCoordinates(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInWorld, int yInWorld) {
		return new MapObject(isVisiblePreference,
				CoordinateUtils.toFragmentRelative(xInWorld),
				CoordinateUtils.toFragmentRelative(yInWorld), xInWorld,
				yInWorld, type.getName(), type.getImage());
	}

	private final BooleanPrefModel isVisiblePreference;
	private final int xInFragment;
	private final int yInFragment;
	private final int xInWorld;
	private final int yInWorld;
	private final String name;
	private final BufferedImage image;

	protected MapObject(BooleanPrefModel isVisiblePreference, int xInFragment,
			int yInFragment, int xInWorld, int yInWorld, String name,
			BufferedImage image) {
		this.isVisiblePreference = isVisiblePreference;
		this.xInFragment = xInFragment;
		this.yInFragment = yInFragment;
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
		this.name = name;
		this.image = image;
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
		return name;
	}

	public BufferedImage getImage() {
		return image;
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
		return name + " at (" + xInWorld + ", " + yInWorld + ")";
	}
}
