package amidst.minecraft.world;

import amidst.minecraft.world.finder.CachedWorldObjectProducer;
import amidst.minecraft.world.finder.NetherFortressProducer;
import amidst.minecraft.world.finder.OceanMonumentProducer;
import amidst.minecraft.world.finder.PlayerProducer;
import amidst.minecraft.world.finder.SpawnProducer;
import amidst.minecraft.world.finder.StrongholdProducer;
import amidst.minecraft.world.finder.TempleProducer;
import amidst.minecraft.world.finder.VillageProducer;
import amidst.minecraft.world.finder.WorldObjectConsumer;
import amidst.minecraft.world.finder.WorldObjectProducer;

public abstract class World {
	private final WorldObjectProducer oceanMonumentProducer = new OceanMonumentProducer(
			this);
	private final WorldObjectProducer templeProducer = new TempleProducer(this);
	private final WorldObjectProducer villageProducer = new VillageProducer(
			this);
	private final WorldObjectProducer netherFortressProducer = new NetherFortressProducer(
			this);
	private final CachedWorldObjectProducer playerProducer = new PlayerProducer(
			this);
	private final CachedWorldObjectProducer spawnProducer = new SpawnProducer(
			this);
	private final CachedWorldObjectProducer strongholdProducer = new StrongholdProducer(
			this);

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
			WorldObjectConsumer consumer) {
		oceanMonumentProducer.produce(corner, consumer);
	}

	public void getTemples(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		templeProducer.produce(corner, consumer);
	}

	public void getVillages(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		villageProducer.produce(corner, consumer);
	}

	public void getNetherFortresses(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		netherFortressProducer.produce(corner, consumer);
	}

	public void getPlayers(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		playerProducer.produce(corner, consumer);
	}

	public void getSpawn(CoordinatesInWorld corner, WorldObjectConsumer consumer) {
		spawnProducer.produce(corner, consumer);
	}

	public void getStrongholds(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		strongholdProducer.produce(corner, consumer);
	}

	public void reloadPlayers() {
		playerProducer.resetCache();
	}
}
