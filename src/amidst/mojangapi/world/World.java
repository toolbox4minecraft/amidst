package amidst.mojangapi.world;

import java.util.List;

import amidst.documentation.CalledByAny;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
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
	public static World simple(MinecraftInterface minecraftInterface,
			long seed, String seedText, WorldType worldType) {
		return new World(minecraftInterface, seed, seedText, worldType, "",
				MovablePlayerList.empty());
	}

	public static World file(MinecraftInterface minecraftInterface, long seed,
			WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList) {
		return new World(minecraftInterface, seed, null, worldType,
				generatorOptions, movablePlayerList);
	}

	private final long seed;
	private final String seedText;
	private final WorldType worldType;
	private final String generatorOptions;
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

	private World(MinecraftInterface minecraftInterface, long seed,
			String seedText, WorldType worldType, String generatorOptions,
			MovablePlayerList movablePlayerList) {
		// @formatter:off
		this.seed = seed;
		this.seedText = seedText;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.movablePlayerList = movablePlayerList;
		initMinecraftInterface(minecraftInterface);
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		this.biomeDataOracle =           new BiomeDataOracle(minecraftInterface);
		this.slimeChunkOracle =          new SlimeChunkOracle(seed);
		this.oceanMonumentProducer =     new OceanMonumentProducer(    recognisedVersion, seed, biomeDataOracle);
		this.templeProducer =            new TempleProducer(           recognisedVersion, seed, biomeDataOracle);
		this.villageProducer =           new VillageProducer(          recognisedVersion, seed, biomeDataOracle);
		this.netherFortressProducer =    new NetherFortressProducer(   recognisedVersion, seed, biomeDataOracle);
		this.playerProducer =            new PlayerProducer(           recognisedVersion, movablePlayerList);
		this.spawnProducer =             new SpawnProducer(            recognisedVersion, seed, biomeDataOracle);
		this.strongholdProducer =        new StrongholdProducer(       recognisedVersion, seed, biomeDataOracle);
		// @formatter:on
	}

	private void initMinecraftInterface(MinecraftInterface minecraftInterface) {
		minecraftInterface.createWorld(seed, worldType.getName(),
				generatorOptions);
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
