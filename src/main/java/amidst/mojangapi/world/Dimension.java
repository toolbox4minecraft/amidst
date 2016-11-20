package amidst.mojangapi.world;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public enum Dimension {
	// @formatter:off
	NETHER(  -1, "Nether",    Resolution.NETHER),
	OVERWORLD(0, "Overworld", Resolution.WORLD),
	END(      1, "End",       Resolution.WORLD);
	// @formatter:on

	public static Dimension from(int id) {
		if (id == NETHER.getId()) {
			return NETHER;
		} else if (id == OVERWORLD.getId()) {
			return OVERWORLD;
		} else if (id == END.getId()) {
			return END;
		} else {
			AmidstLogger.warn("Unsupported dimension id: " + id + ". Falling back to Overworld.");
			return OVERWORLD;
		}
	}

	public static String[] getSelectable() {
		return new String[] { OVERWORLD.getName(), END.getName() };
	}

	private final int id;
	private final String name;
	private final Resolution resolution;

	private Dimension(int id, String name, Resolution resolution) {
		this.id = id;
		this.name = name;
		this.resolution = resolution;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Resolution getResolution() {
		return resolution;
	}

	@Override
	public String toString() {
		return name;
	}
}
