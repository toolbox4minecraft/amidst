package amidst.mojangapi.world;

import java.io.IOException;

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
import amidst.mojangapi.world.icon.producer.DefaultStructureProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.ImmutableWorldSpawnOracle;
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
			WorldOptions worldOptions) throws MinecraftInterfaceException {
		VersionFeatures versionFeatures = initInterfaceAndGetFeatures(worldOptions, minecraftInterface);
		return create(
				minecraftInterface.getRecognisedVersion(),
				MovablePlayerList.dummy(),
				versionFeatures,
				versionFeatures.get(FeatureKey.WORLD_SPAWN_ORACLE));
	}

	public World fromSaveGame(MinecraftInterface minecraftInterface, SaveGame saveGame)
			throws IOException,
			MinecraftInterfaceException {
		VersionFeatures versionFeatures = initInterfaceAndGetFeatures(WorldOptions.fromSaveGame(saveGame), minecraftInterface);
		return create(
				minecraftInterface.getRecognisedVersion(),
				new MovablePlayerList(
					playerInformationProvider,
					saveGame,
					true,
					WorldPlayerType.from(saveGame)),
				versionFeatures,
				new ImmutableWorldSpawnOracle(saveGame.getWorldSpawn()));
	}

	private VersionFeatures initInterfaceAndGetFeatures(WorldOptions worldOptions, MinecraftInterface minecraftInterface)
		throws MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		MinecraftInterface.World minecraftWorld = minecraftInterface.createWorld(
			worldOptions.getWorldSeed().getLong(),
			worldOptions.getWorldType(),
			worldOptions.getGeneratorOptions());
		seedHistoryLogger.log(recognisedVersion, worldOptions.getWorldSeed());
		return DefaultVersionFeatures.builder(worldOptions, minecraftWorld).create(recognisedVersion);
	}

	private World create(
			RecognisedVersion recognisedVersion,
			MovablePlayerList movablePlayerList,
			VersionFeatures versionFeatures,
			WorldSpawnOracle worldSpawnOracle) throws MinecraftInterfaceException {

		return new World(
				versionFeatures.get(FeatureKey.WORLD_OPTIONS),
				movablePlayerList,
				recognisedVersion,
				versionFeatures.get(FeatureKey.BIOME_LIST),
				versionFeatures.get(FeatureKey.ENABLED_LAYERS),
				versionFeatures.get(FeatureKey.BIOME_DATA_ORACLE),
				versionFeatures.get(FeatureKey.END_ISLAND_ORACLE),
				versionFeatures.get(FeatureKey.SLIME_CHUNK_ORACLE),
				new SpawnProducer(worldSpawnOracle),
				versionFeatures.get(FeatureKey.STRONGHOLD_PRODUCER),
				new PlayerProducer(movablePlayerList),
				new MultiProducer<>(
						new DefaultStructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.VILLAGE_PRODUCER),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
							Dimension.OVERWORLD,
							false),
						new DefaultStructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.PILLAGER_OUTPOST_PRODUCER),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.PILLAGER_OUTPOST),
							Dimension.OVERWORLD,
							false)
				),
				new MultiProducer<>(
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.DESERT_TEMPLE_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.DESERT),
								Dimension.OVERWORLD,
								false),
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.IGLOO_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.IGLOO),
								Dimension.OVERWORLD,
								false),
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.JUNGLE_TEMPLE_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.JUNGLE),
								Dimension.OVERWORLD,
								false),
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.WITCH_HUT_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WITCH),
								Dimension.OVERWORLD,
								false)
				),
				new DefaultStructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.MINESHAFT_PRODUCER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false),
				new DefaultStructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.OCEAN_MONUMENT_PRODUCER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						Dimension.OVERWORLD,
						false),
				new DefaultStructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.WOODLAND_MANSION_PRODUCER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false),
				new MultiProducer<>(
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.OCEAN_RUINS_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_RUINS),
								Dimension.OVERWORLD,
								false),
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.SHIPWRECK_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.SHIPWRECK),
								Dimension.OVERWORLD,
								false),
						new DefaultStructureProducer<>(
								Resolution.CHUNK,
								9,
								versionFeatures.get(FeatureKey.BURIED_TREASURE_PRODUCER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BURIED_TREASURE),
								Dimension.OVERWORLD,
								false)
				),
				versionFeatures.get(FeatureKey.NETHER_FORTRESS_PRODUCER),
				versionFeatures.get(FeatureKey.END_CITY_PRODUCER));
	}
}
