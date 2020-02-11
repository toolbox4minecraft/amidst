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
			Consumer<World> onDisposeWorld,
			WorldOptions worldOptions) throws MinecraftInterfaceException {
		VersionFeatures versionFeatures = initInterfaceAndGetFeatures(worldOptions, minecraftInterface);
		return create(
				minecraftInterface.getRecognisedVersion(),
				onDisposeWorld,
				MovablePlayerList.dummy(),
				versionFeatures,
				versionFeatures.get(FeatureKey.WORLD_SPAWN_ORACLE));
	}

	public World fromSaveGame(MinecraftInterface minecraftInterface, Consumer<World> onDisposeWorld, SaveGame saveGame)
			throws IOException,
			MinecraftInterfaceException {
		VersionFeatures versionFeatures = initInterfaceAndGetFeatures(WorldOptions.fromSaveGame(saveGame), minecraftInterface);
		return create(
				minecraftInterface.getRecognisedVersion(),
				onDisposeWorld,
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
		minecraftInterface.createWorld(
			worldOptions.getWorldSeed().getLong(),
			worldOptions.getWorldType(),
			worldOptions.getGeneratorOptions());
		seedHistoryLogger.log(recognisedVersion, worldOptions.getWorldSeed());
		return DefaultVersionFeatures.builder(worldOptions, new BiomeDataOracle(minecraftInterface))
				.create(recognisedVersion);
	}

	private World create(
			RecognisedVersion recognisedVersion,
			Consumer<World> onDisposeWorld,
			MovablePlayerList movablePlayerList,
			VersionFeatures versionFeatures,
			WorldSpawnOracle worldSpawnOracle) throws MinecraftInterfaceException {

		return new World(
				onDisposeWorld,
				versionFeatures.get(FeatureKey.WORLD_OPTIONS),
				movablePlayerList,
				recognisedVersion,
				versionFeatures.get(FeatureKey.ENABLED_LAYERS),
				versionFeatures.get(FeatureKey.BIOME_DATA_ORACLE),
				versionFeatures.get(FeatureKey.END_ISLAND_ORACLE),
				versionFeatures.get(FeatureKey.SLIME_CHUNK_ORACLE),
				new SpawnProducer(worldSpawnOracle),
				versionFeatures.get(FeatureKey.STRONGHOLD_PRODUCER),
				new PlayerProducer(movablePlayerList),
				new MultiProducer<>(
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.VILLAGE_LOCATION_CHECKER),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
							Dimension.OVERWORLD,
							false),
						new StructureProducer<>(
							Resolution.CHUNK,
							4,
							versionFeatures.get(FeatureKey.PILLAGER_OUTPOST_LOCATION_CHECKER),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.PILLAGER_OUTPOST),
							Dimension.OVERWORLD,
							false)
				),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.DESERT_TEMPLE_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.DESERT),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.IGLOO_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.IGLOO),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.JUNGLE_TEMPLE_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.JUNGLE),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.WITCH_HUT_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WITCH),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.MINESHAFT_LOCATION_CHECKER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.OCEAN_MONUMENT_LOCATION_CHECKER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
						Dimension.OVERWORLD,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.WOODLAND_MANSION_LOCATION_CHECKER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false),
				new MultiProducer<>(
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.OCEAN_RUINS_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_RUINS),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								8,
								versionFeatures.get(FeatureKey.SHIPWRECK_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.SHIPWRECK),
								Dimension.OVERWORLD,
								false),
						new StructureProducer<>(
								Resolution.CHUNK,
								9,
								versionFeatures.get(FeatureKey.BURIED_TREASURE_LOCATION_CHECKER),
								new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BURIED_TREASURE),
								Dimension.OVERWORLD,
								false)
				),
				new StructureProducer<>(
						Resolution.NETHER_CHUNK,
						88,
						versionFeatures.get(FeatureKey.NETHER_FORTRESS_LOCATION_CHECKER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.NETHER_FORTRESS),
						Dimension.NETHER,
						false),
				new StructureProducer<>(
						Resolution.CHUNK,
						8,
						versionFeatures.get(FeatureKey.END_ISLAND_LOCATION_CHECKER),
						new EndCityWorldIconTypeProvider(),
						Dimension.END,
						false));
	}
}
