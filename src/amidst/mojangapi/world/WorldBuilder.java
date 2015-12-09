package amidst.mojangapi.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDat;
import amidst.mojangapi.file.nbt.playerloader.MultiPlayerPlayerLoader;
import amidst.mojangapi.file.nbt.playerloader.PlayerLoader;
import amidst.mojangapi.file.nbt.playerloader.SinglePlayerPlayerLoader;
import amidst.mojangapi.file.nbt.playermover.MultiPlayerPlayerMover;
import amidst.mojangapi.file.nbt.playermover.PlayerMover;
import amidst.mojangapi.file.nbt.playermover.SinglePlayerPlayerMover;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.icon.NetherFortressProducer;
import amidst.mojangapi.world.icon.OceanMonumentProducer;
import amidst.mojangapi.world.icon.PlayerProducer;
import amidst.mojangapi.world.icon.SpawnProducer;
import amidst.mojangapi.world.icon.StrongholdProducer;
import amidst.mojangapi.world.icon.TempleProducer;
import amidst.mojangapi.world.icon.VillageProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.utilities.Google;

public class WorldBuilder {
	public World random(MinecraftInterface minecraftInterface,
			WorldType worldType) {
		// TODO: no Google.track(), because a random seed is not interesting?
		long seed = new Random().nextLong();
		return simple(minecraftInterface, seed, null, worldType);
	}

	public World fromSeed(MinecraftInterface minecraftInterface,
			String seedText, WorldType worldType) {
		long seed = getSeedFromString(seedText);
		Google.track("seed/" + seedText + "/" + seed);
		if (isNumericSeed(seedText, seed)) {
			return simple(minecraftInterface, seed, null, worldType);
		} else {
			return simple(minecraftInterface, seed, seedText, worldType);
		}
	}

	private long getSeedFromString(String seed) {
		try {
			return Long.parseLong(seed);
		} catch (NumberFormatException err) {
			return seed.hashCode();
		}
	}

	private boolean isNumericSeed(String seedText, long seed) {
		return ("" + seed).equals(seedText);
	}

	public World fromFile(MinecraftInterface minecraftInterface,
			SaveDirectory saveDirectory) throws FileNotFoundException,
			IOException {
		World world = createFromFile(minecraftInterface, saveDirectory);
		Google.track("seed/file/" + world.getSeed());
		return world;
	}

	private World createFromFile(MinecraftInterface minecraftInterface,
			SaveDirectory saveDirectory) throws FileNotFoundException,
			IOException {
		LevelDat levelDat = saveDirectory.createLevelDat();
		if (saveDirectory.isMultiPlayer()) {
			Log.i("Multiplayer world detected.");
			return file(
					minecraftInterface,
					levelDat.getSeed(),
					levelDat.getWorldType(),
					levelDat.getGeneratorOptions(),
					createMovablePlayerList(minecraftInterface,
							new MultiPlayerPlayerLoader(saveDirectory),
							new MultiPlayerPlayerMover(saveDirectory)));
		} else {
			Log.i("Singleplayer world detected.");
			return file(
					minecraftInterface,
					levelDat.getSeed(),
					levelDat.getWorldType(),
					levelDat.getGeneratorOptions(),
					createMovablePlayerList(minecraftInterface,
							new SinglePlayerPlayerLoader(saveDirectory),
							new SinglePlayerPlayerMover(saveDirectory)));
		}
	}

	private MovablePlayerList createMovablePlayerList(
			MinecraftInterface minecraftInterface, PlayerLoader playerLoader,
			PlayerMover playerMover) {
		if (minecraftInterface.getRecognisedVersion().isSaveEnabled()) {
			return new MovablePlayerList(playerLoader, playerMover);
		} else {
			return new MovablePlayerList(playerLoader);
		}
	}

	private World simple(MinecraftInterface minecraftInterface, long seed,
			String seedText, WorldType worldType) {
		return create(minecraftInterface, seed, seedText, worldType, "",
				MovablePlayerList.empty());
	}

	public World file(MinecraftInterface minecraftInterface, long seed,
			WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList) {
		return create(minecraftInterface, seed, null, worldType,
				generatorOptions, movablePlayerList);
	}

	private World create(MinecraftInterface minecraftInterface, long seed,
			String seedText, WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList) {
		// @formatter:off
		minecraftInterface.createWorld(seed, worldType, generatorOptions);
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		BiomeDataOracle biomeDataOracle = new BiomeDataOracle(minecraftInterface);
		return new World(
				seed,
				seedText,
				worldType,
				generatorOptions,
				movablePlayerList,
				biomeDataOracle,
				new SlimeChunkOracle(      seed),
				new PlayerProducer(        recognisedVersion, movablePlayerList),
				new SpawnProducer(         recognisedVersion, seed, biomeDataOracle),
				new StrongholdProducer(    recognisedVersion, seed, biomeDataOracle),
				new TempleProducer(        recognisedVersion, seed, biomeDataOracle),
				new VillageProducer(       recognisedVersion, seed, biomeDataOracle),
				new OceanMonumentProducer( recognisedVersion, seed, biomeDataOracle),
				new NetherFortressProducer(recognisedVersion, seed, biomeDataOracle));
		// @formatter:on
	}
}
