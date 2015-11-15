package amidst.minecraft.world;

import java.io.File;
import java.util.Random;

import amidst.Options;
import amidst.utilties.Google;

public enum Worlds {
	;

	public static World random(WorldType worldType) {
		// TODO: no Google.track(), because this is only called with a random
		// seed?
		long seed = new Random().nextLong();
		Options.instance.seed = seed;
		return new SeedWorld(seed, worldType);
	}

	public static World fromSeed(String seed, WorldType worldType) {
		long realSeed = getSeedFromString(seed);
		Google.track("seed/" + seed + "/" + realSeed);
		Options.instance.seed = realSeed;
		return new SeedWorld(realSeed, worldType);
	}

	public static World fromFile(File worldFile) throws Exception {
		Google.track("seed/file/" + Options.instance.seed);
		WorldLoader worldLoader = new WorldLoader(worldFile);
		if (worldLoader.isLoadedSuccessfully()) {
			World world = worldLoader.get();
			Options.instance.seed = world.getSeed();
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
}
