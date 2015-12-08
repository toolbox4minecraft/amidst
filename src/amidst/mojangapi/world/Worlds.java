package amidst.mojangapi.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
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
			SaveDirectory saveDirectory) throws FileNotFoundException,
			IOException {
		World world = saveDirectory.createWorld(minecraftInterface);
		Google.track("seed/file/" + world.getSeed());
		return world;
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
