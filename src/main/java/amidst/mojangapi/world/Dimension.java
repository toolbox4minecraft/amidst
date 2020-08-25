package amidst.mojangapi.world;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public enum Dimension {
	// @formatter:off
	NETHER(  -1, "minecraft:the_nether"	, "Nether",    Resolution.NETHER),
	OVERWORLD(0, "minecraft:overworld",   "Overworld", Resolution.WORLD),
	END(      1, "minecraft:the_end",     "End",       Resolution.WORLD);
	// @formatter:on

	public static Dimension fromId(int id) {
		if (id == NETHER.getId()) {
			return NETHER;
		} else if (id == OVERWORLD.getId()) {
			return OVERWORLD;
		} else if (id == END.getId()) {
			return END;
		} else {
			AmidstLogger.warn("Unsupported dimension id: {}. Falling back to Overworld.", id);
			return OVERWORLD;
		}
	}

	public static Dimension fromName(String name) {
		if (NETHER.getName().equals(name)) {
			return NETHER;
		} else if (OVERWORLD.getName().equals(name)) {
			return OVERWORLD;
		} else if (END.getName().equals(name)) {
			return END;
		} else {
			AmidstLogger.warn("Unsupported dimension name: {}. Falling back to Overworld.", name);
			return OVERWORLD;
		}
	}

	public static String[] getSelectable() {
		return new String[] { OVERWORLD.getDisplayName(), END.getDisplayName() };
	}

	private final int id;
	private final String name;
	private final String displayName;
	private final Resolution resolution;

	private Dimension(int id, String name, String displayName, Resolution resolution) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.resolution = resolution;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Resolution getResolution() {
		return resolution;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
