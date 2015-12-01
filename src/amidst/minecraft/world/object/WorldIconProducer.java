package amidst.minecraft.world.object;

import java.util.List;

import amidst.minecraft.world.CoordinatesInWorld;

public abstract class WorldIconProducer {
	public abstract void produce(CoordinatesInWorld corner,
			WorldIconConsumer consumer);

	public List<WorldIcon> getAt(CoordinatesInWorld corner) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(corner, collector);
		return collector.get();
	}
}
