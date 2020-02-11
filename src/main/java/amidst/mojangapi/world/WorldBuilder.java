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
				versionFeatures.get(FeatureKey.STRONGHOLD_PRODUCER_FACTORY).apply(seed, biomeDataOracle),
				new PlayerProducer(movablePlayerList),
				new MultiProducer<>(
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.VILLAGE_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
							Dimension.OVERWORLD,
							false),
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.PILLAGER_OUTPOST_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.PILLAGER_OUTPOST),
							Dimension.OVERWORLD,
							false)
				),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.DESERT_TEMPLE_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.DESERT),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.IGLOO_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.IGLOO),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.JUNGLE_TEMPLE_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.JUNGLE),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.WITCH_HUT_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WITCH),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.MINESHAFT_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.OCEAN_MONUMENT_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.WOODLAND_MANSION_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.OCEAN_RUINS_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_RUINS),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.SHIPWRECK_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.SHIPWRECK),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								9,
								versionFeatures.get(FeatureKey.BURIED_TREASURE_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BURIED_TREASURE),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.NETHER_CHUNK,
						88,
						versionFeatures.get(FeatureKey.NETHER_FORTRESS_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.NETHER_FORTRESS),
						Dimension.NETHER,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.END_ISLAND_LOCATION_CHECKER_FACTORY).apply(seed, biomeDataOracle),
						new EndCityWorldIconTypeProvider(),
						Dimension.END,
						false));
	}
}
