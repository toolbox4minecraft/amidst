package amidst.mojangapi.world;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.SpawnProducer;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;

@ThreadSafe
public class World {
	private final WorldSeed worldSeed;
	private final WorldType worldType;
	private final String generatorOptions;
	private final MovablePlayerList movablePlayerList;
	private final RecognisedVersion recognisedVersion;
	private final VersionFeatures versionFeatures;

	private final BiomeDataOracle biomeDataOracle;
	private final EndIslandOracle endIslandOracle;
	private final SlimeChunkOracle slimeChunkOracle;
	private final WorldSpawnOracle worldSpawnOracle;
	private final CachedWorldIconProducer spawnProducer;
	private final CachedWorldIconProducer strongholdProducer;
	private final CachedWorldIconProducer playerProducer;
	private final WorldIconProducer<Void> villageProducer;
	private final WorldIconProducer<Void> templeProducer;
	private final WorldIconProducer<Void> mineshaftProducer;
	private final WorldIconProducer<Void> oceanMonumentProducer;
	private final WorldIconProducer<Void> netherFortressProducer;
	private final WorldIconProducer<List<EndIsland>> endCityProducer;

	public World(
			WorldSeed worldSeed,
			WorldType worldType,
			String generatorOptions,
			MovablePlayerList movablePlayerList,
			RecognisedVersion recognisedVersion,
			VersionFeatures versionFeatures,
			BiomeDataOracle biomeDataOracle,
			EndIslandOracle endIslandOracle,
			SlimeChunkOracle slimeChunkOracle,
			WorldSpawnOracle worldSpawnOracle,
			CachedWorldIconProducer strongholdProducer,
			CachedWorldIconProducer playerProducer,
			WorldIconProducer<Void> villageProducer,
			WorldIconProducer<Void> templeProducer,
			WorldIconProducer<Void> mineshaftProducer,
			WorldIconProducer<Void> oceanMonumentProducer,
			WorldIconProducer<Void> netherFortressProducer,
			WorldIconProducer<List<EndIsland>> endCityProducer) {
		this.worldSeed = worldSeed;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.movablePlayerList = movablePlayerList;
		this.recognisedVersion = recognisedVersion;
		this.versionFeatures = versionFeatures;
		this.biomeDataOracle = biomeDataOracle;
		this.endIslandOracle = endIslandOracle;
		this.slimeChunkOracle = slimeChunkOracle;
		this.worldSpawnOracle = worldSpawnOracle;
		this.spawnProducer = new SpawnProducer(worldSpawnOracle);
		this.strongholdProducer = strongholdProducer;
		this.playerProducer = playerProducer;
		this.villageProducer = villageProducer;
		this.templeProducer = templeProducer;
		this.mineshaftProducer = mineshaftProducer;
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

	public VersionFeatures getVersionFeatures() {
		return versionFeatures;
	}

	public BiomeDataOracle getBiomeDataOracle() {
		return biomeDataOracle;
	}

	public EndIslandOracle getEndIslandOracle() {
		return endIslandOracle;
	}

	public SlimeChunkOracle getSlimeChunkOracle() {
		return slimeChunkOracle;
	}
	
	public WorldSpawnOracle getSpawnOracle() {
		return worldSpawnOracle;
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
}
