package amidst.mojangapi.world;

import java.util.List;

import amidst.documentation.CalledByAny;
import amidst.documentation.ThreadSafe;
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
	private final WorldSeed seed;
	private final WorldType worldType;
	private final String generatorOptions;
	private final MovablePlayerList movablePlayerList;

	private final BiomeDataOracle biomeDataOracle;
	private final EndIslandOracle endIslandOracle;
	private final SlimeChunkOracle slimeChunkOracle;
	private final CachedWorldIconProducer spawnProducer;
	private final CachedWorldIconProducer strongholdProducer;
	private final CachedWorldIconProducer playerProducer;
	private final WorldIconProducer<Void> villageProducer;
	private final WorldIconProducer<Void> templeProducer;
	private final WorldIconProducer<Void> mineshaftProducer;
	private final WorldIconProducer<Void> netherFortressProducer;
	private final WorldIconProducer<Void> oceanMonumentProducer;
	private final WorldIconProducer<List<EndIsland>> endCityProducer;

	public World(WorldSeed seed, WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList,
			BiomeDataOracle biomeDataOracle, EndIslandOracle endIslandOracle,
			SlimeChunkOracle slimeChunkOracle,
			CachedWorldIconProducer spawnProducer,
			CachedWorldIconProducer strongholdProducer,
			CachedWorldIconProducer playerProducer,
			WorldIconProducer<Void> villageProducer,
			WorldIconProducer<Void> templeProducer,
			WorldIconProducer<Void> mineshaftProducer,
			WorldIconProducer<Void> netherFortressProducer,
			WorldIconProducer<Void> oceanMonumentProducer,
			WorldIconProducer<List<EndIsland>> endCityProducer) {
		this.seed = seed;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.movablePlayerList = movablePlayerList;
		this.biomeDataOracle = biomeDataOracle;
		this.endIslandOracle = endIslandOracle;
		this.slimeChunkOracle = slimeChunkOracle;
		this.spawnProducer = spawnProducer;
		this.strongholdProducer = strongholdProducer;
		this.playerProducer = playerProducer;
		this.villageProducer = villageProducer;
		this.templeProducer = templeProducer;
		this.mineshaftProducer = mineshaftProducer;
		this.netherFortressProducer = netherFortressProducer;
		this.oceanMonumentProducer = oceanMonumentProducer;
		this.endCityProducer = endCityProducer;
	}

	@CalledByAny
	public WorldSeed getWorldSeed() {
		return seed;
	}

	@CalledByAny
	public WorldType getWorldType() {
		return worldType;
	}

	@CalledByAny
	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public MovablePlayerList getMovablePlayerList() {
		return movablePlayerList;
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

	public WorldIconProducer<Void> getNetherFortressProducer() {
		return netherFortressProducer;
	}

	public WorldIconProducer<Void> getOceanMonumentProducer() {
		return oceanMonumentProducer;
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
