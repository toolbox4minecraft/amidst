package amidst.mojangapi.world;

import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.oracle.MinetestBiomeDataOracle;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.PlayerProducer;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.player.MovablePlayerList;

@ThreadSafe
public class World {
	private final Consumer<World> onDisposeWorld;

	private final WorldSeed worldSeed;
	private final WorldType worldType;
	private final String generatorOptions;
	private final MovablePlayerList movablePlayerList;
	private final RecognisedVersion recognisedVersion;
	private final IVersionFeatures versionFeatures;

	private final IBiomeDataOracle biomeDataOracle;
	private final EndIslandOracle endIslandOracle;
	private final SlimeChunkOracle slimeChunkOracle;
	private final CachedWorldIconProducer spawnProducer;
	private final CachedWorldIconProducer strongholdProducer;
	private final CachedWorldIconProducer playerProducer;
	private final WorldIconProducer<Void> villageProducer;
	private final WorldIconProducer<Void> templeProducer;
	private final WorldIconProducer<Void> mineshaftProducer;
	private final WorldIconProducer<Void> oceanMonumentProducer;
	private final WorldIconProducer<Void> woodlandMansionProducer;
	private final WorldIconProducer<Void> netherFortressProducer;
	private final WorldIconProducer<List<EndIsland>> endCityProducer;

	/**
	 * Constructor for Minetest worlds
	 */
	public World(
			Consumer<World> onDisposeWorld,
			WorldSeed worldSeed,
			WorldType worldType,
			MovablePlayerList movablePlayerList,
			RecognisedVersion recognisedVersion,
			IVersionFeatures versionFeatures,
			IBiomeDataOracle biomeDataOracle,
			CachedWorldIconProducer spawnProducer,
			WorldIconProducer<Void> dungeonProducer) {
		
		// Constructor for Minetest maps
		this.onDisposeWorld         = onDisposeWorld;
		this.worldSeed              = worldSeed;
		this.worldType              = worldType;
		this.movablePlayerList      = movablePlayerList;
		this.recognisedVersion      = recognisedVersion;
		this.versionFeatures        = versionFeatures;
		this.biomeDataOracle        = biomeDataOracle;
		this.endIslandOracle        = null;
		this.slimeChunkOracle       = null;
		this.spawnProducer          = spawnProducer;
		this.strongholdProducer     = null;
		this.playerProducer         = new PlayerProducer(MovablePlayerList.dummy());
		this.villageProducer        = null;
		this.templeProducer         = dungeonProducer;
		this.mineshaftProducer      = null;
		this.oceanMonumentProducer  = null;
		this.woodlandMansionProducer = null;
		this.netherFortressProducer = null;
		this.endCityProducer        = null;
		
		String mapgenParamsString = "";
		if (biomeDataOracle instanceof MinetestBiomeDataOracle) {
			MapgenParams params = ((MinetestBiomeDataOracle)biomeDataOracle).getMapgenParams();
			if (params != null) mapgenParamsString = params.toString();
		}
		// Repurposing the generatorOptions string from being a prescriptive
		// parameter with Minecraft, into a descriptive parameter for Minetest worlds.
		this.generatorOptions = mapgenParamsString;
	}

	
	/**
	 * Constructor for Minecraft worlds
	 */
	public World(
			Consumer<World> onDisposeWorld,
			WorldSeed worldSeed,
			WorldType worldType,
			String generatorOptions,
			MovablePlayerList movablePlayerList,
			RecognisedVersion recognisedVersion,
			IVersionFeatures versionFeatures,
			IBiomeDataOracle biomeDataOracle,
			EndIslandOracle endIslandOracle,
			SlimeChunkOracle slimeChunkOracle,
			CachedWorldIconProducer spawnProducer,
			CachedWorldIconProducer strongholdProducer,
			CachedWorldIconProducer playerProducer,
			WorldIconProducer<Void> villageProducer,
			WorldIconProducer<Void> templeProducer,
			WorldIconProducer<Void> mineshaftProducer,
			WorldIconProducer<Void> oceanMonumentProducer,
			WorldIconProducer<Void> woodlandMansionProducer,
			WorldIconProducer<Void> netherFortressProducer,
			WorldIconProducer<List<EndIsland>> endCityProducer) {
		
		// Constructor for Minecraft maps		
		this.onDisposeWorld = onDisposeWorld;
		this.worldSeed = worldSeed;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.movablePlayerList = movablePlayerList;
		this.recognisedVersion = recognisedVersion;
		this.versionFeatures = versionFeatures;
		this.biomeDataOracle = biomeDataOracle;
		this.endIslandOracle = endIslandOracle;
		this.slimeChunkOracle = slimeChunkOracle;
		this.spawnProducer = spawnProducer;
		this.strongholdProducer = strongholdProducer;
		this.playerProducer = playerProducer;
		this.villageProducer = villageProducer;
		this.templeProducer = templeProducer;
		this.mineshaftProducer = mineshaftProducer;
		this.woodlandMansionProducer = woodlandMansionProducer;
		this.oceanMonumentProducer = oceanMonumentProducer;
		this.netherFortressProducer = netherFortressProducer;
		this.endCityProducer = endCityProducer;
	}

	public WorldSeed getWorldSeed() {
		return worldSeed;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public MovablePlayerList getMovablePlayerList() {
		return movablePlayerList;
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public IVersionFeatures getVersionFeatures() {
		return versionFeatures;
	}

	public IBiomeDataOracle getBiomeDataOracle() {
		return biomeDataOracle;
	}

	public EndIslandOracle getEndIslandOracle() {
		return endIslandOracle;
	}

	public SlimeChunkOracle getSlimeChunkOracle() {
		return slimeChunkOracle;
	}

	public WorldIconProducer<Void> getSpawnProducer() {
		return spawnProducer;
	}

	public WorldIconProducer<Void> getStrongholdProducer() {
		return strongholdProducer;
	}

	public WorldIconProducer<Void> getPlayerProducer() {
		return playerProducer;
	}

	public WorldIconProducer<Void> getVillageProducer() {
		return villageProducer;
	}

	public WorldIconProducer<Void> getTempleProducer() {
		return templeProducer;
	}

	public WorldIconProducer<Void> getMineshaftProducer() {
		return mineshaftProducer;
	}

	public WorldIconProducer<Void> getOceanMonumentProducer() {
		return oceanMonumentProducer;
	}

	public WorldIconProducer<Void> getNetherFortressProducer() {
		return netherFortressProducer;
	}

	public WorldIconProducer<List<EndIsland>> getEndCityProducer() {
		return endCityProducer;
	}

	public WorldIconProducer<Void> getWoodlandMansionProducer() {
		return woodlandMansionProducer;
	}

	public WorldIcon getSpawnWorldIcon() {
		return spawnProducer.getFirstWorldIcon();
	}

	public List<WorldIcon> getStrongholdWorldIcons() {
		return strongholdProducer.getWorldIcons();
	}

	public List<WorldIcon> getPlayerWorldIcons() {
		return playerProducer.getWorldIcons();
	}

	public void reloadPlayerWorldIcons() {
		playerProducer.resetCache();
	}

	/**
	 * Unlocks the RunningLauncherProfile to allow the creation of another
	 * world. However, this does not actually prevent the usage of this world.
	 * If you keep using it, something will break.
	 */
	public void dispose() {
		onDisposeWorld.accept(this);
	}
}
