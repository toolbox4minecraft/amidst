package amidst.minecraft.world;

import amidst.minecraft.world.finder.FindingConsumer;
import amidst.minecraft.world.finder.NetherFortressFinder;
import amidst.minecraft.world.finder.OceanMonumentFinder;
import amidst.minecraft.world.finder.TempleFinder;
import amidst.minecraft.world.finder.VillageFinder;

public abstract class World {
	private OceanMonumentFinder oceanMonumentFinder = new OceanMonumentFinder(
			this);
	private TempleFinder templeFinder = new TempleFinder(this);
	private VillageFinder villageFinder = new VillageFinder(this);
	private NetherFortressFinder netherFortressFinder = new NetherFortressFinder(
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
}
