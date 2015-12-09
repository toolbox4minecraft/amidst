package amidst.mojangapi.world;

import java.util.List;

import amidst.documentation.CalledByAny;
import amidst.mojangapi.world.icon.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

public class World {
	private final long seed;
	private final String seedText;
	private final WorldType worldType;
	private final String generatorOptions;
	private final MovablePlayerList movablePlayerList;

	private final BiomeDataOracle biomeDataOracle;
	private final SlimeChunkOracle slimeChunkOracle;
	private final CachedWorldIconProducer playerProducer;
	private final CachedWorldIconProducer strongholdProducer;
	private final CachedWorldIconProducer spawnProducer;
	private final WorldIconProducer templeProducer;
	private final WorldIconProducer villageProducer;
	private final WorldIconProducer oceanMonumentProducer;
	private final WorldIconProducer netherFortressProducer;

	World(long seed, String seedText, WorldType worldType,
			String generatorOptions, MovablePlayerList movablePlayerList,
			BiomeDataOracle biomeDataOracle, SlimeChunkOracle slimeChunkOracle,
			CachedWorldIconProducer playerProducer,
			CachedWorldIconProducer strongholdProducer,
			CachedWorldIconProducer spawnProducer,
			WorldIconProducer templeProducer,
			WorldIconProducer villageProducer,
			WorldIconProducer oceanMonumentProducer,
			WorldIconProducer netherFortressProducer) {
		this.seed = seed;
		this.seedText = seedText;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.movablePlayerList = movablePlayerList;
		this.biomeDataOracle = biomeDataOracle;
		this.slimeChunkOracle = slimeChunkOracle;
		this.playerProducer = playerProducer;
		this.strongholdProducer = strongholdProducer;
		this.spawnProducer = spawnProducer;
		this.templeProducer = templeProducer;
		this.villageProducer = villageProducer;
		this.oceanMonumentProducer = oceanMonumentProducer;
		this.netherFortressProducer = netherFortressProducer;
	}

	@CalledByAny
	public long getSeed() {
		return seed;
	}

	@CalledByAny
	public String getSeedText() {
		return seedText;
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

	public SlimeChunkOracle getSlimeChunkOracle() {
		return slimeChunkOracle;
	}

	public CachedWorldIconProducer getPlayerProducer() {
		return playerProducer;
	}

	public CachedWorldIconProducer getStrongholdProducer() {
		return strongholdProducer;
	}

	public CachedWorldIconProducer getSpawnProducer() {
		return spawnProducer;
	}

	public WorldIconProducer getTempleProducer() {
		return templeProducer;
	}

	public WorldIconProducer getVillageProducer() {
		return villageProducer;
	}

	public WorldIconProducer getOceanMonumentProducer() {
		return oceanMonumentProducer;
	}

	public WorldIconProducer getNetherFortressProducer() {
		return netherFortressProducer;
	}

	public List<WorldIcon> getPlayerWorldIcons() {
		return playerProducer.getWorldIcons();
	}

	public List<WorldIcon> getStrongholdWorldIcons() {
		return strongholdProducer.getWorldIcons();
	}

	public WorldIcon getSpawnWorldIcon() {
		return spawnProducer.getFirstWorldIcon();
	}

	public void reloadPlayerWorldIcons() {
		playerProducer.resetCache();
	}
}
