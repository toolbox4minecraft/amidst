package amidst.mojangapi.world.icon;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public class WorldIcon {
	private final CoordinatesInWorld coordinates;
	private final String name;
	private final WorldIconImage image;
	private final Dimension dimension;
	private final boolean displayDimension;

	public WorldIcon(
			CoordinatesInWorld coordinates,
			String name,
			WorldIconImage image,
			Dimension dimension,
			boolean displayDimension) {
		this.coordinates = coordinates;
		this.name = name;
		this.image = image;
		this.dimension = dimension;
		this.displayDimension = displayDimension;
	}

	public CoordinatesInWorld getCoordinates() {
		return coordinates;
	}

	public String getName() {
		return name;
	}

	public WorldIconImage getImage() {
		return image;
	}

	public Dimension getDimension() {
		return dimension;
	}

	@Override
	@NotNull
	public String toString() {
		return toString(false);
	}

	@NotNull
	public String toString(boolean multiline) {
		if (dimension.getResolution() != Resolution.WORLD) {
			if (multiline) {
				// @formatter:off
				return name
						+ "\n    in the " + dimension.getDisplayName() + " " + coordinates.toString(dimension.getResolution())
						+ "\n    in the Overworld "                   + coordinates.toString();
				// @formatter:on
			} else {
				return name + " in the " + dimension.getDisplayName() + " " + coordinates.toString(dimension.getResolution())
						+ " -> " + coordinates.toString() + " in the Overworld";
			}
		} else if (displayDimension) {
			return name + " in the " + dimension.getDisplayName() + " " + coordinates.toString();
		} else {
			return name + " " + coordinates.toString();
		}
	}
}
