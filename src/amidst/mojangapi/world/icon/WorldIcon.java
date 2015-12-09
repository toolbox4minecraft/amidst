package amidst.mojangapi.world.icon;

import java.awt.image.BufferedImage;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.CoordinatesInWorld;

@Immutable
public class WorldIcon {
	private final CoordinatesInWorld coordinates;
	private final String name;
	private final BufferedImage image;

	public WorldIcon(CoordinatesInWorld coordinates, String name,
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
		return name + " " + coordinates.toString();
	}
}
