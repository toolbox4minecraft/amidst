package amidst.minecraft.world.object;

import java.util.List;

import amidst.minecraft.world.CoordinatesInWorld;

public abstract class WorldObjectProducer {
	public abstract void produce(CoordinatesInWorld corner,
			WorldObjectConsumer consumer);

	public List<WorldObject> getAt(CoordinatesInWorld corner) {
		WorldObjectCollector collector = new WorldObjectCollector();
		produce(corner, collector);
		return collector.get();
	}
}
