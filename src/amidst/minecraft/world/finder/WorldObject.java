package amidst.minecraft.world.finder;

import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.minecraft.world.CoordinatesInWorld;

public class WorldObject {
	private final CoordinatesInWorld coordinates;
	private final String name;
	private final BufferedImage image;

	public WorldObject(CoordinatesInWorld coordinates, MapMarkers marker) {
		this(coordinates, marker.getName(), marker.getImage());
	}

	public WorldObject(CoordinatesInWorld coordinates, String name,
			BufferedImage image) {
		this.coordinates = coordinates;
		this.name = name;
		this.image = image;
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

	@Override
	public String toString() {
		return name + " at (" + coordinates.getX() + ", " + coordinates.getY()
				+ ")";
	}
}
