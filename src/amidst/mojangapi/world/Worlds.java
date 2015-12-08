package amidst.mojangapi.world;

import java.io.File;
import java.util.Random;

import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.world.loader.WorldLoader;
import amidst.utilities.Google;

public enum Worlds {
	;

	public static World random(MinecraftInterface minecraftInterface,
			WorldType worldType) {
		// TODO: no Google.track(), because this is only called with a random
		// seed?
		long seed = new Random().nextLong();
		return World.simple(minecraftInterface, seed, null, worldType);
	}

	public static World fromSeed(MinecraftInterface minecraftInterface,
			String seedText, WorldType worldType) {
		long seed = getSeedFromString(seedText);
		Google.track("seed/" + seedText + "/" + seed);
		if (isNumericSeed(seedText, seed)) {
			return World.simple(minecraftInterface, seed, null, worldType);
		} else {
			return World.simple(minecraftInterface, seed, seedText, worldType);
		}
	}

	public static World fromFile(MinecraftInterface minecraftInterface,
			File worldFile) throws Exception {
		WorldLoader worldLoader = new WorldLoader(worldFile);
		if (worldLoader.isLoadedSuccessfully()) {
			World world = worldLoader.get(minecraftInterface);
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
