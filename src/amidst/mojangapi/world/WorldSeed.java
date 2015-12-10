package amidst.mojangapi.world;

import java.util.Random;

import amidst.documentation.Immutable;
import amidst.utilities.Google;

@Immutable
public class WorldSeed {
	public static WorldSeed random() {
		return new WorldSeed(new Random().nextLong(), null, null);
	}

	public static WorldSeed fromUserInput(String input) {
		try {
			long seed = Long.parseLong(input);
			return new WorldSeed(seed, null, "seed/" + input + "/" + seed);
		} catch (NumberFormatException err) {
			int seed = input.hashCode();
			return new WorldSeed(seed, input, "seed/" + input + "/" + seed);
		}
	}

	public static WorldSeed fromFile(long seed) {
		return new WorldSeed(seed, null, "seed/file/" + seed);
	}

	private final long seed;
	private final String text;
	private final String label;
	private final String trackingString;

	private WorldSeed(long seed, String text, String trackingString) {
		this.seed = seed;
		this.text = text;
		this.label = createLabel();
		this.trackingString = trackingString;
	}

	private String createLabel() {
		if (text != null) {
			return text + " (" + seed + ")";
		} else {
			return "" + seed;
		}
	}

	public long getLong() {
		return seed;
	}

	public String getText() {
		return text;
	}

	public String getLabel() {
		return label;
	}

	// TODO: make google tracking non-static
	@Deprecated
	public void trackIfNecessary() {
		if (trackingString != null) {
			Google.track(trackingString);
		}
	}
}
