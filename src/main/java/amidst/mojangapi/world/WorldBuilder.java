package amidst.mojangapi.world;

import java.io.IOException;
import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.ImmutablePlayerInformationProvider;
import amidst.mojangapi.file.PlayerInformationProvider;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.ScatteredFeaturesLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.WoodlandMansionLocationChecker;
import amidst.mojangapi.world.icon.producer.MultiProducer;
import amidst.mojangapi.world.icon.producer.PlayerProducer;
import amidst.mojangapi.world.icon.producer.SpawnProducer;
import amidst.mojangapi.world.icon.producer.StructureProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.EndCityWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.ImmutableWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.PlayerInformation;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.mojangapi.world.versionfeatures.DefaultVersionFeatures;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;

@Immutable
public class WorldBuilder {
	/**
	 * Create a new WorldBuilder that does not log any seeds and that provides
	 * the singleplayer player information for each requested player.
	 */
	public static WorldBuilder createSilentPlayerless() {
		return new WorldBuilder(
				new ImmutablePlayerInformationProvider(PlayerInformation.theSingleplayerPlayer()),
				SeedHistoryLogger.createDisabled());
	}

	private final PlayerInformationProvider playerInformationProvider;
	private final SeedHistoryLogger seedHistoryLogger;

	public WorldBuilder(PlayerInformationProvider playerInformationProvider, SeedHistoryLogger seedHistoryLogger) {
		this.playerInformationProvider = playerInformationProvider;
		this.seedHistoryLogger = seedHistoryLogger;
	}

	public World from(
			MinecraftInterface minecraftInterface,
			Consumer<World> onDisposeWorld,
			WorldOptions worldOptions) throws MinecraftInterfaceException {
		BiomeDataOracle biomeDataOracle = new BiomeDataOracle(minecraftInterface);
		VersionFeatures versionFeatures = DefaultVersionFeatures.create(minecraftInterface.getRecognisedVersion());
		return create(
				minecraftInterface,
				onDisposeWorld,
				worldOptions.getWorldSeed(),
				worldOptions.getWorldType(),
				worldOptions.getGeneratorOptions(),
				MovablePlayerList.dummy(),
				versionFeatures,
				biomeDataOracle,
				new HeuristicWorldSpawnOracle(
						worldOptions.getWorldSeed().getLong(),
						biomeDataOracle,
						versionFeatures.getValidBiomesForStructure_Spawn()));
	}

	public World fromSaveGame(MinecraftInterface minecraftInterface, Consumer<World> onDisposeWorld, SaveGame saveGame)
			throws IOException,
			MinecraftInterfaceException {
		VersionFeatures versionFeatures = DefaultVersionFeatures.create(minecraftInterface.getRecognisedVersion());
		MovablePlayerList movablePlayerList = new MovablePlayerList(
				playerInformationProvider,
				saveGame,
				true,
				WorldPlayerType.from(saveGame));
		return create(
				minecraftInterface,
				onDisposeWorld,
				WorldSeed.fromSaveGame(saveGame.getSeed()),
				saveGame.getWorldType(),
				saveGame.getGeneratorOptions(),
				movablePlayerList,
				versionFeatures,
				new BiomeDataOracle(minecraftInterface),
				new ImmutableWorldSpawnOracle(saveGame.getWorldSpawn()));
	}

	private World create(
			MinecraftInterface minecraftInterface,
			Consumer<World> onDisposeWorld,
			WorldSeed worldSeed,
			WorldType worldType,
			String generatorOptions,
			MovablePlayerList movablePlayerList,
			VersionFeatures versionFeatures,
			BiomeDataOracle biomeDataOracle,
			WorldSpawnOracle worldSpawnOracle) throws MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		seedHistoryLogger.log(recognisedVersion, worldSeed);
		long seed = worldSeed.getLong();
		boolean buggyStructureCoordinateMath = versionFeatures.getBuggyStructureCoordinateMath();
		minecraftInterface.createWorld(seed, worldType, generatorOptions);
		return new World(
				onDisposeWorld,
				worldSeed,
				worldType,
				generatorOptions,
				movablePlayerList,
				recognisedVersion,
				versionFeatures,
				biomeDataOracle,
				EndIslandOracle.from(seed),
				new SlimeChunkOracle(seed),
				new SpawnProducer(worldSpawnOracle),
				versionFeatures.getStrongholdProducerFactory().apply(
						seed,
						biomeDataOracle,
						versionFeatures.getValidBiomesAtMiddleOfChunk_Stronghold()),
				new PlayerProducer(movablePlayerList),
				new StructureProducer<>(
						Resolution.CHUNK,
						4,
						new VillageLocationChecker(
								seed,
								biomeDataOracle,
								versionFeatures.getValidBiomesForStructure_Village()),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
						Dimension.OVERWORLD,
						false),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.getValidBiomesAtMiddleOfChunk_DesertTemple(),
										versionFeatures.getSeedForStructure_DesertTemple(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.DESERT),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.getValidBiomesAtMiddleOfChunk_Igloo(),
										versionFeatures.getSeedForStructure_Igloo(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.IGLOO),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.getValidBiomesAtMiddleOfChunk_JungleTemple(),
										versionFeatures.getSeedForStructure_JungleTemple(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.JUNGLE),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.getValidBiomesAtMiddleOfChunk_WitchHut(),
										versionFeatures.getSeedForStructure_WitchHut(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WITCH),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										(byte) 16, (byte) 8,
										versionFeatures.getValidBiomesAtMiddleOfChunk_OceanRuins(),
										versionFeatures.getSeedForStructure_OceanRuins(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_RUINS),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										(byte) 15, (byte) 8,
										versionFeatures.getValidBiomesAtMiddleOfChunk_Shipwreck(),
										versionFeatures.getSeedForStructure_Shipwreck(),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.SHIPWRECK),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.getMineshaftAlgorithmFactory().apply(seed),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.getOceanMonumentLocationCheckerFactory().apply(
								seed,
								biomeDataOracle,
								versionFeatures.getValidBiomesAtMiddleOfChunk_OceanMonument(),
								versionFeatures.getValidBiomesForStructure_OceanMonument()),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						new WoodlandMansionLocationChecker(
								seed,
								biomeDataOracle,
								versionFeatures.getValidBiomesForStructure_WoodlandMansion()
						),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.NETHER_CHUNK,
						88,
						new NetherFortressAlgorithm(seed),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.NETHER_FORTRESS),
						Dimension.NETHER,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						new EndCityLocationChecker(seed),
						new EndCityWorldIconTypeProvider(),
						Dimension.END,
						false));
	}
}
