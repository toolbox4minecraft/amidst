package amidst.minecraft.world;

import java.io.File;
import java.util.Random;

import amidst.utilities.Google;

public enum Worlds {
	;

	public static World random(WorldType worldType) {
		// TODO: no Google.track(), because this is only called with a random
		// seed?
		long seed = new Random().nextLong();
		return new World(seed, null, worldType);
	}

	public static World fromSeed(String seedText, WorldType worldType) {
		long seed = getSeedFromString(seedText);
		Google.track("seed/" + seedText + "/" + seed);
		if (isNumericSeed(seedText, seed)) {
			return new World(seed, null, worldType);
		} else {
			return new World(seed, seedText, worldType);
		}
	}

	public static World fromFile(File worldFile) throws Exception {
		WorldLoader worldLoader = new WorldLoader(worldFile);
		if (worldLoader.isLoadedSuccessfully()) {
			World world = worldLoader.get();
			Google.track("seed/file/" + world.getSeed());
			return world;
		} else {
			throw worldLoader.getException();
		}
	}

	private static long getSeedFromString(String seed) {
		try {
			return Long.parseLong(seed);
		} catch (NumberFormatException err) {
			return seed.hashCode();
		}
	}

	private static boolean isNumericSeed(String seedText, long seed) {
		return ("" + seed).equals(seedText);
	}
}
