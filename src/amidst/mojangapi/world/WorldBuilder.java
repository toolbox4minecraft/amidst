package amidst.mojangapi.world;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.ThreadSafe;
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
import amidst.utilities.GoogleTracker;

@ThreadSafe
public class WorldBuilder {
	private final GoogleTracker googleTracker;

	public WorldBuilder(GoogleTracker googleTracker) {
		this.googleTracker = googleTracker;
	}

	public World fromSeed(MinecraftInterface minecraftInterface,
			WorldSeed seed, WorldType worldType) {
		return create(minecraftInterface, seed, worldType, "",
				MovablePlayerList.empty());
	}

	public World fromFile(MinecraftInterface minecraftInterface,
			SaveDirectory saveDirectory) throws FileNotFoundException,
			IOException {
		LevelDat levelDat = saveDirectory.createLevelDat();
		if (saveDirectory.isMultiPlayer()) {
			Log.i("Multiplayer world detected.");
			return create(
					minecraftInterface,
					WorldSeed.fromFile(levelDat.getSeed()),
					levelDat.getWorldType(),
					levelDat.getGeneratorOptions(),
					createMovablePlayerList(minecraftInterface,
							new MultiPlayerPlayerLoader(saveDirectory),
							new MultiPlayerPlayerMover(saveDirectory)));
		} else {
			Log.i("Singleplayer world detected.");
			return create(
					minecraftInterface,
					WorldSeed.fromFile(levelDat.getSeed()),
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

	private World create(MinecraftInterface minecraftInterface, WorldSeed seed,
			WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList) {
		// @formatter:off
		googleTracker.trackSeed(seed);
		minecraftInterface.createWorld(seed.getLong(), worldType, generatorOptions);
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		BiomeDataOracle biomeDataOracle = new BiomeDataOracle(minecraftInterface);
		return new World(
				seed,
				worldType,
				generatorOptions,
				movablePlayerList,
				biomeDataOracle,
				new SlimeChunkOracle(      seed.getLong()),
				new PlayerProducer(        recognisedVersion, movablePlayerList),
				new SpawnProducer(         recognisedVersion, seed.getLong(), biomeDataOracle),
				new StrongholdProducer(    recognisedVersion, seed.getLong(), biomeDataOracle),
				new TempleProducer(        recognisedVersion, seed.getLong(), biomeDataOracle),
				new VillageProducer(       recognisedVersion, seed.getLong(), biomeDataOracle),
				new OceanMonumentProducer( recognisedVersion, seed.getLong(), biomeDataOracle),
				new NetherFortressProducer(recognisedVersion, seed.getLong(), biomeDataOracle));
		// @formatter:on
	}
}
