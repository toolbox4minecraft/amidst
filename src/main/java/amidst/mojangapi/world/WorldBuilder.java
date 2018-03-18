package amidst.mojangapi.world;

import java.io.IOException;
import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.GameEngineType;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.logging.AmidstLogger;
import amidst.minetest.MinetestMapgenInterface;
import amidst.minetest.world.MinetestVersionFeatures;
import amidst.minetest.world.icon.MinetestDungeonLocationChecker;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.mojangapi.file.ImmutablePlayerInformationProvider;
import amidst.mojangapi.file.PlayerInformationProvider;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.TempleLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.producer.PlayerProducer;
import amidst.mojangapi.world.icon.producer.SpawnProducer;
import amidst.mojangapi.world.icon.producer.StructureProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.EndCityWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.TempleWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.ImmutableWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.PlayerInformation;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;

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

	/**
	 * 
	 * @param gameCodeInterface
	 * @param onDisposeWorld
	 * @param worldSeed
	 * @param worldType
	 * @param biomeProfile - Can be null for Minecraft (or for mocks), but with Minetest 
	 * it's best to inject the BiomeProfile the GUI is using.
	 * @return
	 * @throws MinecraftInterfaceException
	 */
	public World fromSeed(
			MinecraftInterface gameCodeInterface,
			Consumer<World> onDisposeWorld,
			WorldSeed worldSeed,
			WorldType worldType,
			BiomeProfileSelection biomeProfileSelection  
			) throws MinecraftInterfaceException {

		IVersionFeatures versionFeatures = gameCodeInterface
				.getGameEngineDetails()
				.getVersionFeatures(
						gameCodeInterface.getRecognisedVersion()
				); 
		
		IBiomeDataOracle biomeDataOracle;
		WorldSpawnOracle spawnOracle;
		
		if (gameCodeInterface instanceof MinetestMapgenInterface) {
			// Minetest
			MinetestMapgenInterface mapgen = (MinetestMapgenInterface)gameCodeInterface;			
			
			biomeDataOracle = ((MinetestVersionFeatures)versionFeatures)
					.getFactory_BiomeDataOracle(worldType)
					.apply(
						worldSeed.getLong(),
						mapgen.params, 
						biomeProfileSelection
					);
			spawnOracle     = new amidst.minetest.world.oracle.HeuristicWorldSpawnOracle((MapgenV7Params)mapgen.params, worldSeed.getLong());
		} else{		
			// Minecraft
			if (versionFeatures instanceof VersionFeatures) {				
				VersionFeatures minecraftVersionFeatures = (VersionFeatures)versionFeatures;
				
				biomeDataOracle = new BiomeDataOracle(gameCodeInterface);
				spawnOracle     = new HeuristicWorldSpawnOracle(
					worldSeed.getLong(),
					(BiomeDataOracle)biomeDataOracle,
					minecraftVersionFeatures.getValidBiomesForStructure_Spawn()
				);
			} else {
				// Must not happen
				throw new MinecraftInterfaceException("gameCodeInterface not Minetest, and not Minecraft");
			}
		}
		return create(
				gameCodeInterface,
				onDisposeWorld,
				worldSeed,
				worldType,
				"",
				MovablePlayerList.dummy(),
				versionFeatures,
				biomeDataOracle,
				spawnOracle);
	}

	public World fromSaveGame(MinecraftInterface gameCodeInterface, Consumer<World> onDisposeWorld, SaveGame saveGame)
			throws IOException,
			MinecraftInterfaceException {
		
		IVersionFeatures versionFeatures = gameCodeInterface
				.getGameEngineDetails()
				.getVersionFeatures(
						gameCodeInterface.getRecognisedVersion()
				); 
		
		MovablePlayerList movablePlayerList = new MovablePlayerList(
				playerInformationProvider,
				saveGame,
				true,
				WorldPlayerType.from(saveGame));
		return create(
				gameCodeInterface,
				onDisposeWorld,
				WorldSeed.fromSaveGame(saveGame.getSeed(), gameCodeInterface.getGameEngineDetails().getType()),
				saveGame.getWorldType(),
				saveGame.getGeneratorOptions(),
				movablePlayerList,
				versionFeatures,
				new BiomeDataOracle(gameCodeInterface),
				new ImmutableWorldSpawnOracle(saveGame.getWorldSpawn()));
	}

	private World create(
			MinecraftInterface minecraftInterface,
			Consumer<World> onDisposeWorld,
			WorldSeed worldSeed,
			WorldType worldType,
			String generatorOptions,
			MovablePlayerList movablePlayerList,
			IVersionFeatures versionFeatures,
			IBiomeDataOracle biomeDataOracle,
			WorldSpawnOracle worldSpawnOracle) throws MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		seedHistoryLogger.log(recognisedVersion, worldSeed);
		long seed = worldSeed.getLong();
		minecraftInterface.createWorld(seed, worldType, generatorOptions);
						
		if (minecraftInterface.getGameEngineDetails().getType() == GameEngineType.MINETEST) {
			return new World(
					onDisposeWorld,
					worldSeed,
					worldType,
					movablePlayerList,
					recognisedVersion,
					versionFeatures,
					biomeDataOracle,
					new SpawnProducer(worldSpawnOracle),
					new StructureProducer<>(
							Resolution.CHUNK,
							8,
							new MinetestDungeonLocationChecker(
									(int)seed,
									biomeDataOracle
							),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.JUNGLE),
							Dimension.OVERWORLD,
							false)
			);
		} else {			
			amidst.mojangapi.world.oracle.BiomeDataOracle minecraftBiome = (amidst.mojangapi.world.oracle.BiomeDataOracle)biomeDataOracle;
			VersionFeatures minecraftVersionFeatures = (VersionFeatures)versionFeatures;			
			
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
					minecraftVersionFeatures.getStrongholdProducerFactory().apply(
							seed,
							minecraftBiome,
							minecraftVersionFeatures.getValidBiomesAtMiddleOfChunk_Stronghold()),
					new PlayerProducer(movablePlayerList),
					new StructureProducer<>(
							Resolution.CHUNK,
							4,
							new VillageLocationChecker(
									seed,
									minecraftBiome,
									minecraftVersionFeatures.getValidBiomesForStructure_Village()),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.VILLAGE),
							Dimension.OVERWORLD,
							false),
					new StructureProducer<>(
							Resolution.CHUNK,
							8,
							new TempleLocationChecker(
									seed,
									minecraftBiome,
									minecraftVersionFeatures.getValidBiomesAtMiddleOfChunk_Temple()),
							new TempleWorldIconTypeProvider(minecraftBiome),
							Dimension.OVERWORLD,
							false),
					new StructureProducer<>(
							Resolution.CHUNK,
							8,
							minecraftVersionFeatures.getMineshaftAlgorithmFactory().apply(seed),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
							Dimension.OVERWORLD,
							false),
					new StructureProducer<>(
							Resolution.CHUNK,
							8,
							minecraftVersionFeatures.getOceanMonumentLocationCheckerFactory().apply(
									seed,
									minecraftBiome,
									minecraftVersionFeatures.getValidBiomesAtMiddleOfChunk_OceanMonument(),
									minecraftVersionFeatures.getValidBiomesForStructure_OceanMonument()),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
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
}
