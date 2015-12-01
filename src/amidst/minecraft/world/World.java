package amidst.minecraft.world;

import java.util.List;

import amidst.minecraft.world.icon.CachedWorldIconProducer;
import amidst.minecraft.world.icon.NetherFortressProducer;
import amidst.minecraft.world.icon.OceanMonumentProducer;
import amidst.minecraft.world.icon.PlayerProducer;
import amidst.minecraft.world.icon.SpawnProducer;
import amidst.minecraft.world.icon.StrongholdProducer;
import amidst.minecraft.world.icon.TempleProducer;
import amidst.minecraft.world.icon.VillageProducer;
import amidst.minecraft.world.icon.WorldIcon;
import amidst.minecraft.world.icon.WorldIconProducer;

public abstract class World {
	private final BiomeDataOracle biomeDataOracle = new BiomeDataOracle();
	private final SlimeChunkOracle slimeChunkOracle = new SlimeChunkOracle(this);
	private final WorldIconProducer oceanMonumentProducer = new OceanMonumentProducer(
			this);
	private final WorldIconProducer templeProducer = new TempleProducer(this);
	private final WorldIconProducer villageProducer = new VillageProducer(
			this);
	private final WorldIconProducer netherFortressProducer = new NetherFortressProducer(
			this);
	private final CachedWorldIconProducer playerProducer = new PlayerProducer(
			this);
	private final CachedWorldIconProducer spawnProducer = new SpawnProducer(
			this);
	private final CachedWorldIconProducer strongholdProducer = new StrongholdProducer(
			this);

	public BiomeDataOracle getBiomeDataOracle() {
		return biomeDataOracle;
	}

	public SlimeChunkOracle getSlimeChunkOracle() {
		return slimeChunkOracle;
	}

	public WorldIconProducer getOceanMonumentProducer() {
		return oceanMonumentProducer;
	}

	public WorldIconProducer getTempleProducer() {
		return templeProducer;
	}

	public WorldIconProducer getVillageProducer() {
		return villageProducer;
	}

	public WorldIconProducer getNetherFortressProducer() {
		return netherFortressProducer;
	}

	public CachedWorldIconProducer getPlayerProducer() {
		return playerProducer;
	}

	public CachedWorldIconProducer getSpawnProducer() {
		return spawnProducer;
	}

	public CachedWorldIconProducer getStrongholdProducer() {
		return strongholdProducer;
	}

	public List<WorldIcon> getPlayers() {
		return playerProducer.getWorldIcons();
	}

	public WorldIcon getSpawn() {
		return spawnProducer.getFirstWorldIcon();
	}

	public List<WorldIcon> getStrongholds() {
		return strongholdProducer.getWorldIcons();
	}

	public void reloadPlayers() {
		playerProducer.resetCache();
	}

	@Deprecated
	public boolean hasPlayers() {
		return isFileWorld();
	}

	@Deprecated
	public boolean isFileWorld() {
		return this instanceof FileWorld;
	}

	@Deprecated
	public FileWorld getAsFileWorld() {
		return (FileWorld) this;
	}

	public abstract long getSeed();

	public abstract String getSeedText();

	public abstract WorldType getWorldType();
}
