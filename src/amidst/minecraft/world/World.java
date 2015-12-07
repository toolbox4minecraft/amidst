package amidst.minecraft.world;

import java.util.Collections;
import java.util.List;

import amidst.minecraft.IMinecraftInterface;
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

public class World {
	private final BiomeDataOracle biomeDataOracle = new BiomeDataOracle(this);
	private final SlimeChunkOracle slimeChunkOracle = new SlimeChunkOracle(this);
	private final WorldIconProducer oceanMonumentProducer = new OceanMonumentProducer(
			this);
	private final WorldIconProducer templeProducer = new TempleProducer(this);
	private final WorldIconProducer villageProducer = new VillageProducer(this);
	private final WorldIconProducer netherFortressProducer = new NetherFortressProducer(
			this);
	private final CachedWorldIconProducer playerProducer = new PlayerProducer(
			this);
	private final CachedWorldIconProducer spawnProducer = new SpawnProducer(
			this);
	private final CachedWorldIconProducer strongholdProducer = new StrongholdProducer(
			this);

	private final long seed;
	private final String seedText;
	private final WorldType worldType;
	private final String generatorOptions;
	private final boolean isMultiplayerWorld;
	private final List<Player> players;
	private final IMinecraftInterface minecraftInterface;

	World(long seed, String seedText, WorldType worldType,
			IMinecraftInterface minecraftInterface) {
		this.seed = seed;
		this.seedText = seedText;
		this.worldType = worldType;
		this.generatorOptions = "";
		this.isMultiplayerWorld = false;
		this.players = Collections.emptyList();
		this.minecraftInterface = minecraftInterface;
		initMinecraftInterface();
	}

	World(long seed, WorldType worldType, String generatorOptions,
			boolean isMultiplayerWorld, List<Player> players,
			IMinecraftInterface minecraftInterface) {
		this.seed = seed;
		this.seedText = null;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.isMultiplayerWorld = isMultiplayerWorld;
		this.players = players;
		this.minecraftInterface = minecraftInterface;
		initMinecraftInterface();
	}

	private void initMinecraftInterface() {
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

	public List<Player> getMovablePlayers() {
		return players;
	}

	public boolean hasPlayers() {
		return !players.isEmpty();
	}

	public void savePlayerLocations() {
		for (Player player : players) {
			player.saveLocation();
		}
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

	public CachedWorldIconProducer getPlayerProducer() {
		return playerProducer;
	}

	public CachedWorldIconProducer getSpawnProducer() {
		return spawnProducer;
	}

	public CachedWorldIconProducer getStrongholdProducer() {
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

	@Deprecated
	public boolean canSavePlayerLocations() {
		return minecraftInterface.getVersion().isSaveEnabled();
	}

	@Deprecated
	public IMinecraftInterface getMinecraftInterface() {
		return minecraftInterface;
	}
}
