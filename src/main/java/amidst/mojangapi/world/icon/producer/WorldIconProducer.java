package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

@ThreadSafe
public abstract class WorldIconProducer<T> {
	public abstract void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, T additionalData);

	public List<WorldIcon> getAt(CoordinatesInWorld corner, T additionalData) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(corner, collector, additionalData);
		return collector.get();
	}
}
