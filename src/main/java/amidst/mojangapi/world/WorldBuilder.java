package amidst.mojangapi.world;

import java.io.IOException;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.TempleLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.producer.PlayerProducer;
import amidst.mojangapi.world.icon.producer.SpawnProducer;
import amidst.mojangapi.world.icon.producer.StrongholdProducer;
import amidst.mojangapi.world.icon.producer.StructureProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.EndCityWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.TempleWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
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
		long seedAsLong = seed.getLong();
		// @formatter:off
		minecraftInterface.createWorld(seedAsLong, worldType, generatorOptions);
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		BiomeDataOracle biomeDataOracle = new BiomeDataOracle(minecraftInterface);
		return new World(
				seed,
				worldType,
				generatorOptions,
				movablePlayerList,
				biomeDataOracle,
				EndIslandOracle.from(  seedAsLong),
				new SlimeChunkOracle(  seedAsLong),
				new SpawnProducer(     seedAsLong, biomeDataOracle),
				StrongholdProducer.from(seedAsLong, biomeDataOracle, recognisedVersion),
				new PlayerProducer(movablePlayerList),
				new StructureProducer<Void>(
						Resolution.CHUNK,
						4,
						new VillageLocationChecker(seedAsLong, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
						false
				), new StructureProducer<Void>(
						Resolution.CHUNK,
						8,
						new TempleLocationChecker(seedAsLong, biomeDataOracle, recognisedVersion),
						new TempleWorldIconTypeProvider(biomeDataOracle),
						false
				), new StructureProducer<Void>(
						Resolution.CHUNK,
						8,
						MineshaftAlgorithm.from(seedAsLong, recognisedVersion),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						false
				), new StructureProducer<Void>(
						Resolution.NETHER_CHUNK,
						88,
						new NetherFortressAlgorithm(seedAsLong),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.NETHER_FORTRESS),
						true
				), new StructureProducer<Void>(
						Resolution.CHUNK,
						8,
						new OceanMonumentLocationChecker(seedAsLong, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						false
				), new StructureProducer<List<EndIsland>>(
						Resolution.CHUNK,
						8,
						new EndCityLocationChecker(seedAsLong),
						new EndCityWorldIconTypeProvider(),
						false
				)
		);
		// @formatter:on
	}
}
