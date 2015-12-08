package amidst.mojangapi.world;

import java.util.List;

import amidst.mojangapi.minecraftinterface.IMinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.icon.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.NetherFortressProducer;
import amidst.mojangapi.world.icon.OceanMonumentProducer;
import amidst.mojangapi.world.icon.PlayerProducer;
import amidst.mojangapi.world.icon.SpawnProducer;
import amidst.mojangapi.world.icon.StrongholdProducer;
import amidst.mojangapi.world.icon.TempleProducer;
import amidst.mojangapi.world.icon.VillageProducer;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

public class World {
	public static World simple(IMinecraftInterface minecraftInterface,
			long seed, String seedText, WorldType worldType) {
		return new World(minecraftInterface, seed, seedText, worldType, "",
				false, MovablePlayerList.empty());
	}

	public static World file(IMinecraftInterface minecraftInterface, long seed,
			WorldType worldType, String generatorOptions,
			boolean isMultiplayerWorld, MovablePlayerList movablePlayerList) {
		return new World(minecraftInterface, seed, null, worldType,
				generatorOptions, isMultiplayerWorld, movablePlayerList);
	}

	private final long seed;
	private final String seedText;
	private final WorldType worldType;
	private final String generatorOptions;
	private final boolean isMultiplayerWorld;
	private final MovablePlayerList movablePlayerList;

	private final BiomeDataOracle biomeDataOracle;
	private final SlimeChunkOracle slimeChunkOracle;
	private final WorldIconProducer oceanMonumentProducer;
	private final WorldIconProducer templeProducer;
	private final WorldIconProducer villageProducer;
	private final WorldIconProducer netherFortressProducer;
	private final CachedWorldIconProducer playerProducer;
	private final CachedWorldIconProducer spawnProducer;
	private final CachedWorldIconProducer strongholdProducer;

	private World(IMinecraftInterface minecraftInterface, long seed,
			String seedText, WorldType worldType, String generatorOptions,
			boolean isMultiplayerWorld, MovablePlayerList movablePlayerList) {
		this.seed = seed;
		this.seedText = seedText;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.isMultiplayerWorld = isMultiplayerWorld;
		this.movablePlayerList = movablePlayerList;
		initMinecraftInterface(minecraftInterface);
		RecognisedVersion recognisedVersion = minecraftInterface
				.getRecognisedVersion();
		this.biomeDataOracle = new BiomeDataOracle(minecraftInterface);
		this.slimeChunkOracle = new SlimeChunkOracle(this);
		this.oceanMonumentProducer = new OceanMonumentProducer(this,
				recognisedVersion);
		this.templeProducer = new TempleProducer(this, recognisedVersion);
		this.villageProducer = new VillageProducer(this, recognisedVersion);
		this.netherFortressProducer = new NetherFortressProducer(this,
				recognisedVersion);
		this.playerProducer = new PlayerProducer(this, recognisedVersion);
		this.spawnProducer = new SpawnProducer(this, recognisedVersion);
		this.strongholdProducer = new StrongholdProducer(this,
				recognisedVersion);
	}

	private void initMinecraftInterface(IMinecraftInterface minecraftInterface) {
		minecraftInterface.createWorld(seed, worldType.getName(),
				generatorOptions);
	}

	public long getSeed() {
		return seed;
	}

	public String getSeedText() {
		return seedText;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public boolean isMultiplayerWorld() {
		return isMultiplayerWorld;
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

	public WorldIconProducer getPlayerProducer() {
		return playerProducer;
	}

	public WorldIconProducer getSpawnProducer() {
		return spawnProducer;
	}

	public WorldIconProducer getStrongholdProducer() {
		return strongholdProducer;
	}

	public List<WorldIcon> getPlayerWorldIcons() {
		return playerProducer.getWorldIcons();
	}

	public WorldIcon getSpawnWorldIcon() {
		return spawnProducer.getFirstWorldIcon();
	}

	public List<WorldIcon> getStrongholdWorldIcons() {
		return strongholdProducer.getWorldIcons();
	}

	public void reloadPlayerWorldIcons() {
		playerProducer.resetCache();
	}
}
