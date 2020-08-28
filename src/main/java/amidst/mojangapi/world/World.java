package amidst.mojangapi.world;

import java.util.List;
import java.util.Optional;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.player.MovablePlayerList;

@ThreadSafe
public class World {
	private final WorldOptions worldOptions;
	private final MovablePlayerList movablePlayerList;
	private final RecognisedVersion recognisedVersion;
	private final List<Integer> enabledLayers;
	private final BiomeList biomeList;

	private final BiomeDataOracle overworldBiomeDataOracle;
	private final Optional<BiomeDataOracle> netherBiomeDataOracle;
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
	private final WorldIconProducer<Void> oceanFeaturesProducer;
	private final WorldIconProducer<Void> netherFortressProducer;
	private final WorldIconProducer<List<EndIsland>> endCityProducer;

	public World(
			WorldOptions worldOptions,
			MovablePlayerList movablePlayerList,
			RecognisedVersion recognisedVersion,
			BiomeList biomeList,
			List<Integer> enabledLayers,
			BiomeDataOracle overworldBiomeDataOracle,
			Optional<BiomeDataOracle> netherBiomeDataOracle,
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
			WorldIconProducer<Void> oceanFeaturesProducer,
			WorldIconProducer<Void> netherFortressProducer,
			WorldIconProducer<List<EndIsland>> endCityProducer) {
		this.worldOptions = worldOptions;
		this.movablePlayerList = movablePlayerList;
		this.recognisedVersion = recognisedVersion;
		this.biomeList = biomeList;
		this.enabledLayers = enabledLayers;
		this.overworldBiomeDataOracle = overworldBiomeDataOracle;
		this.netherBiomeDataOracle = netherBiomeDataOracle;
		this.endIslandOracle = endIslandOracle;
		this.slimeChunkOracle = slimeChunkOracle;
		this.spawnProducer = spawnProducer;
		this.strongholdProducer = strongholdProducer;
		this.playerProducer = playerProducer;
		this.villageProducer = villageProducer;
		this.templeProducer = templeProducer;
		this.mineshaftProducer = mineshaftProducer;
		this.oceanMonumentProducer = oceanMonumentProducer;
		this.woodlandMansionProducer = woodlandMansionProducer;
		this.oceanFeaturesProducer = oceanFeaturesProducer;
		this.netherFortressProducer = netherFortressProducer;
		this.endCityProducer = endCityProducer;
	}

	public WorldOptions getWorldOptions() {
		return worldOptions;
	}

	public MovablePlayerList getMovablePlayerList() {
		return movablePlayerList;
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public BiomeList getBiomeList() {
		return biomeList;
	}

	public List<Integer> getEnabledLayers() {
		return enabledLayers;
	}

	public BiomeDataOracle getOverworldBiomeDataOracle() {
		return overworldBiomeDataOracle;
	}

	public Optional<BiomeDataOracle> getNetherBiomeDataOracle() {
		return netherBiomeDataOracle;
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

	public WorldIconProducer<Void> getOceanFeaturesProducer() {
		return oceanFeaturesProducer;
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
