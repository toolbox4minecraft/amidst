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
import amidst.mojangapi.world.icon.locationchecker.BuriedTreasureLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.PillagerOutpostLocationChecker;
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
import amidst.mojangapi.world.versionfeatures.FeatureKey;
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
				worldOptions,
				MovablePlayerList.dummy(),
				versionFeatures,
				biomeDataOracle,
				new HeuristicWorldSpawnOracle(
						worldOptions.getWorldSeed().getLong(),
						biomeDataOracle,
						versionFeatures.get(FeatureKey.VALID_BIOMES_FOR_STRUCTURE_SPAWN)));
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
				WorldOptions.fromSaveGame(saveGame),
				movablePlayerList,
				versionFeatures,
				new BiomeDataOracle(minecraftInterface),
				new ImmutableWorldSpawnOracle(saveGame.getWorldSpawn()));
	}

	private World create(
			MinecraftInterface minecraftInterface,
			Consumer<World> onDisposeWorld,
			WorldOptions worldOptions,
			MovablePlayerList movablePlayerList,
			VersionFeatures versionFeatures,
			BiomeDataOracle biomeDataOracle,
			WorldSpawnOracle worldSpawnOracle) throws MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		seedHistoryLogger.log(recognisedVersion, worldOptions.getWorldSeed());
		long seed = worldOptions.getWorldSeed().getLong();
		boolean buggyStructureCoordinateMath = versionFeatures.get(FeatureKey.BUGGY_STRUCTURE_COORDINATE_MATH);
		LocationChecker villageLocationChecker = new VillageLocationChecker(
			seed,
			biomeDataOracle,
			versionFeatures.get(FeatureKey.VALID_BIOMES_FOR_STRUCTURE_VILLAGE),
			versionFeatures.get(FeatureKey.DO_COMPLEX_VILLAGE_CHECK));
		minecraftInterface.createWorld(seed, worldOptions.getWorldType(), worldOptions.getGeneratorOptions());
		return new World(
				onDisposeWorld,
				worldOptions,
				movablePlayerList,
				recognisedVersion,
				versionFeatures.get(FeatureKey.ENABLED_LAYERS),
				biomeDataOracle,
				EndIslandOracle.from(seed),
				new SlimeChunkOracle(seed),
				new SpawnProducer(worldSpawnOracle),
				versionFeatures.get(FeatureKey.STRONGHOLD_PRODUCER_FACTORY).apply(
						seed,
						biomeDataOracle,
						versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD)),
				new PlayerProducer(movablePlayerList),
				new MultiProducer<>(
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							villageLocationChecker,
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
							Dimension.OVERWORLD,
							false),
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							new PillagerOutpostLocationChecker(
									seed,
									biomeDataOracle,
									villageLocationChecker,
									versionFeatures.get(FeatureKey.OUTPOST_VILLAGE_AVOID_DISTANCE),
									versionFeatures.get(FeatureKey.VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST)),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.PILLAGER_OUTPOST),
							Dimension.OVERWORLD,
							false)
				),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_DESERT_TEMPLE),
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
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_IGLOO),
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
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_JUNGLE_TEMPLE),
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
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_WITCH_HUT),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WITCH),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.MINESHAFT_ALGORITHM_FACTORY).apply(seed),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.OCEAN_MONUMENT_LOCATION_CHECKER_FACTORY).apply(
								seed,
								biomeDataOracle,
								versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT),
								versionFeatures.get(FeatureKey.VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT)),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						new WoodlandMansionLocationChecker(
								seed,
								biomeDataOracle,
								versionFeatures.get(FeatureKey.VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION)
						),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								new ScatteredFeaturesLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.get(FeatureKey.MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS),
										(byte) 8,
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_OCEAN_RUINS),
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
										versionFeatures.get(FeatureKey.MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK),
										versionFeatures.get(FeatureKey.MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK),
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_SHIPWRECK),
										buggyStructureCoordinateMath),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.SHIPWRECK),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								9,
								new BuriedTreasureLocationChecker(
										seed,
										biomeDataOracle,
										versionFeatures.get(FeatureKey.VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE),
										versionFeatures.get(FeatureKey.SEED_FOR_STRUCTURE_BURIED_TREASURE)),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BURIED_TREASURE),
								Dimension.OVERWORLD,
								false)
				),
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
