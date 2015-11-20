package amidst.minecraft.world;

import amidst.minecraft.world.finder.FindingConsumer;
import amidst.minecraft.world.finder.NetherFortressFinder;
import amidst.minecraft.world.finder.OceanMonumentFinder;
import amidst.minecraft.world.finder.TempleFinder;
import amidst.minecraft.world.finder.VillageFinder;

public abstract class World {
	private OceanMonumentFinder oceanMonumentFinder = new OceanMonumentFinder();
	private TempleFinder templeFinder = new TempleFinder();
	private VillageFinder villageFinder = new VillageFinder();
	private NetherFortressFinder netherFortressFinder = new NetherFortressFinder();

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
		oceanMonumentFinder.generateMapObjects(this, corner, consumer);
	}

	public void getTemples(CoordinatesInWorld corner, FindingConsumer consumer) {
		templeFinder.generateMapObjects(this, corner, consumer);
	}

	public void getVillages(CoordinatesInWorld corner, FindingConsumer consumer) {
		villageFinder.generateMapObjects(this, corner, consumer);
	}

	public void getNetherFortresses(CoordinatesInWorld corner,
			FindingConsumer consumer) {
		netherFortressFinder.generateMapObjects(this, corner, consumer);
	}
}
