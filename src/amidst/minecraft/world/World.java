package amidst.minecraft.world;

import amidst.map.finder.FindingConsumer;
import amidst.map.finder.OceanMonumentFinder;
import amidst.map.finder.TempleFinder;
import amidst.map.finder.VillageFinder;

public abstract class World {
	private OceanMonumentFinder oceanMonumentFinder = new OceanMonumentFinder();
	private TempleFinder templeFinder = new TempleFinder();
	private VillageFinder villageFinder = new VillageFinder();

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
}
