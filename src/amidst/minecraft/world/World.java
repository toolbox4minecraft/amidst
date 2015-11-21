package amidst.minecraft.world;

import amidst.minecraft.world.finder.CachedFinder;
import amidst.minecraft.world.finder.Finder;
import amidst.minecraft.world.finder.FindingConsumer;
import amidst.minecraft.world.finder.NetherFortressFinder;
import amidst.minecraft.world.finder.OceanMonumentFinder;
import amidst.minecraft.world.finder.PlayerFinder;
import amidst.minecraft.world.finder.SpawnFinder;
import amidst.minecraft.world.finder.StrongholdFinder;
import amidst.minecraft.world.finder.TempleFinder;
import amidst.minecraft.world.finder.VillageFinder;

public abstract class World {
	private final Finder oceanMonumentFinder = new OceanMonumentFinder(this);
	private final Finder templeFinder = new TempleFinder(this);
	private final Finder villageFinder = new VillageFinder(this);
	private final Finder netherFortressFinder = new NetherFortressFinder(this);
	private final CachedFinder playerFinder = new PlayerFinder(this);
	private final CachedFinder spawnFinder = new SpawnFinder(this);
	private final CachedFinder strongholdFinder = new StrongholdFinder(this);

	public boolean isFileWorld() {
		return this instanceof FileWorld;
	}

	public FileWorld getAsFileWorld() {
		return (FileWorld) this;
	}

	public abstract long getSeed();

	public abstract String getSeedText();

	public abstract WorldType getWorldType();

	public void getOceanMonuments(CoordinatesInWorld corner,
			FindingConsumer consumer) {
		oceanMonumentFinder.find(corner, consumer);
	}

	public void getTemples(CoordinatesInWorld corner, FindingConsumer consumer) {
		templeFinder.find(corner, consumer);
	}

	public void getVillages(CoordinatesInWorld corner, FindingConsumer consumer) {
		villageFinder.find(corner, consumer);
	}

	public void getNetherFortresses(CoordinatesInWorld corner,
			FindingConsumer consumer) {
		netherFortressFinder.find(corner, consumer);
	}

	public void getPlayers(CoordinatesInWorld corner, FindingConsumer consumer) {
		playerFinder.find(corner, consumer);
	}

	public void getSpawn(CoordinatesInWorld corner, FindingConsumer consumer) {
		spawnFinder.find(corner, consumer);
	}

	public void getStrongholds(CoordinatesInWorld corner,
			FindingConsumer consumer) {
		strongholdFinder.find(corner, consumer);
	}

	public void reloadPlayers() {
		playerFinder.resetCache();
	}
}
