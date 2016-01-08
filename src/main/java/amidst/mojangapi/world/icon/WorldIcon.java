package amidst.mojangapi.world.icon;

import java.awt.image.BufferedImage;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@Immutable
public class WorldIcon {
	private final CoordinatesInWorld coordinates;
	private final String name;
	private final BufferedImage image;
	private final boolean displayNetherCoordinates;

	public WorldIcon(CoordinatesInWorld coordinates, String name,
			BufferedImage image) {
		this(coordinates, name, image, false);
	}

	public WorldIcon(CoordinatesInWorld coordinates, String name,
			BufferedImage image, boolean displayNetherCoordinates) {
		this.coordinates = coordinates;
		this.name = name;
		this.image = image;
		this.displayNetherCoordinates = displayNetherCoordinates;
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

	public boolean isDisplayNetherCoordinates() {
		return displayNetherCoordinates;
	}

	@Override
	public String toString() {
		if (displayNetherCoordinates) {
			return name + " " + coordinates.toString() + " -> "
					+ coordinates.toNetherString() + " in the Nether";
		} else {
			return name + " " + coordinates.toString();
		}
	}
}
