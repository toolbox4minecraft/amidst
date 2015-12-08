package amidst.mojangapi.world.icon;

import java.util.List;

import amidst.mojangapi.world.CoordinatesInWorld;

public abstract class WorldIconProducer {
	public abstract void produce(CoordinatesInWorld corner,
			WorldIconConsumer consumer);

	public List<WorldIcon> getAt(CoordinatesInWorld corner) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(corner, collector);
		return collector.get();
	}
}
