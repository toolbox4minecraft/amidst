package amidst.mojangapi.world;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.icon.producer.NetherFortressProducer;
import amidst.mojangapi.world.icon.producer.OceanMonumentProducer;
import amidst.mojangapi.world.icon.producer.PlayerProducer;
import amidst.mojangapi.world.icon.producer.SpawnProducer;
import amidst.mojangapi.world.icon.producer.StrongholdProducer;
import amidst.mojangapi.world.icon.producer.TempleProducer;
import amidst.mojangapi.world.icon.producer.VillageProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.PlayerInformationCache;
import amidst.mojangapi.world.player.WorldPlayerType;

@Immutable
public class WorldBuilder {
	private final PlayerInformationCache playerInformationCache;
	private final SeedHistoryLogger seedHistoryLogger;

	public WorldBuilder(PlayerInformationCache playerInformationCache,
			SeedHistoryLogger seedHistoryLogger) {
		this.playerInformationCache = playerInformationCache;
		this.seedHistoryLogger = seedHistoryLogger;
	}

	public World fromSeed(MinecraftInterface minecraftInterface,
			WorldSeed seed, WorldType worldType)
			throws MinecraftInterfaceException {
		return create(minecraftInterface, seed, worldType, "",
				MovablePlayerList.dummy());
	}

	public World fromFile(MinecraftInterface minecraftInterface,
			SaveDirectory saveDirectory) throws IOException,
			MinecraftInterfaceException, MojangApiParsingException {
		LevelDatNbt levelDat = saveDirectory.createLevelDat();
		MovablePlayerList movablePlayerList = new MovablePlayerList(
				playerInformationCache, saveDirectory,
				isSaveEnabled(minecraftInterface), WorldPlayerType.from(
						saveDirectory, levelDat));
		return create(minecraftInterface,
				WorldSeed.fromFile(levelDat.getSeed()),
				levelDat.getWorldType(), levelDat.getGeneratorOptions(),
				movablePlayerList);
	}

	// TODO: @skiphs why does it depend on the loaded minecraft version whether
	// we can save player locations or not? we do not use the minecraft jar file
	// to save player locations and it does not depend on the jar file which
	// worlds can be loaded.
	@Deprecated
	private boolean isSaveEnabled(MinecraftInterface minecraftInterface) {
		return minecraftInterface.getRecognisedVersion().isSaveEnabled();
	}

	private World create(MinecraftInterface minecraftInterface, WorldSeed seed,
			WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList)
			throws MinecraftInterfaceException {
		seedHistoryLogger.log(seed);
		// @formatter:off
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
				new SpawnProducer(         seed.getLong(), biomeDataOracle),
				new StrongholdProducer(    seed.getLong(), biomeDataOracle, recognisedVersion),
				new PlayerProducer(                                         movablePlayerList),
				new TempleProducer(        seed.getLong(), biomeDataOracle, recognisedVersion),
				new VillageProducer(       seed.getLong(), biomeDataOracle),
				new OceanMonumentProducer( seed.getLong(), biomeDataOracle),
				new NetherFortressProducer(seed.getLong()));
		// @formatter:on
	}
}
