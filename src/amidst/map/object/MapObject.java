package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.preferences.BooleanPrefModel;

public class MapObject {
	public static MapObject from(CoordinatesInWorld coordinates, String name,
			BufferedImage image, BooleanPrefModel isVisiblePreference) {
		return new MapObject(coordinates, name, image, isVisiblePreference);
	}

	public static MapObject from(CoordinatesInWorld coordinates,
			MapMarkers marker, BooleanPrefModel isVisiblePreference) {
		return from(coordinates, marker.getName(), marker.getImage(),
				isVisiblePreference);
	}

	private final CoordinatesInWorld coordinates;
	private final String name;
	private final BufferedImage image;
	private final BooleanPrefModel isVisiblePreference;

	protected MapObject(CoordinatesInWorld coordinates, String name,
			BufferedImage image, BooleanPrefModel isVisiblePreference) {
		this.coordinates = coordinates;
		this.name = name;
		this.image = image;
		this.isVisiblePreference = isVisiblePreference;
	}

	public CoordinatesInWorld getCoordinates() {
		return coordinates;
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	@Override
	public String toString() {
		return name + " at (" + coordinates.getX() + ", " + coordinates.getY()
				+ ")";
	}
}
