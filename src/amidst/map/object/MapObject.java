package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.preferences.BooleanPrefModel;

public class MapObject {
	public static MapObject from(CoordinatesInWorld coordinates,
			MapMarkers type, BooleanPrefModel isVisiblePreference) {
		return new MapObject(isVisiblePreference, coordinates, type.getName(),
				type.getImage());
	}

	@Deprecated
	public static MapObject fromFragmentCoordinatesAndFragment(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInFragment, int yInFragment, Fragment fragment) {
		return from(fragment.getCorner().add(xInFragment, yInFragment), type,
				isVisiblePreference);
	}

	@Deprecated
	public static MapObject fromWorldCoordinates(
			BooleanPrefModel isVisiblePreference, MapMarkers type,
			int xInWorld, int yInWorld) {
		return from(CoordinatesInWorld.from(xInWorld, yInWorld), type,
				isVisiblePreference);
	}

	private final BooleanPrefModel isVisiblePreference;
	private final CoordinatesInWorld coordinates;
	private final String name;
	private final BufferedImage image;

	protected MapObject(BooleanPrefModel isVisiblePreference,
			CoordinatesInWorld coordinates, String name, BufferedImage image) {
		this.isVisiblePreference = isVisiblePreference;
		this.coordinates = coordinates;
		this.name = name;
		this.image = image;
	}

	@Deprecated
	public int getWidth() {
		return getImage().getWidth();
	}

	@Deprecated
	public int getHeight() {
		return getImage().getHeight();
	}

	@Deprecated
	public int getXInFragment() {
		return (int) coordinates.getXRelativeToFragment();
	}

	@Deprecated
	public int getYInFragment() {
		return (int) coordinates.getYRelativeToFragment();
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}

	@Deprecated
	public int getXInWorld() {
		return (int) coordinates.getX();
	}

	@Deprecated
	public int getYInWorld() {
		return (int) coordinates.getY();
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	public CoordinatesInWorld getCoordinates() {
		return coordinates;
	}

	@Override
	public String toString() {
		return name + " at (" + coordinates.getX() + ", " + coordinates.getY()
				+ ")";
	}
}
