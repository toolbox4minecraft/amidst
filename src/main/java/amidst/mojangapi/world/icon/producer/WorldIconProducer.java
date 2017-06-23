package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.icon.WorldIcon;

@ThreadSafe
public abstract class WorldIconProducer<T> {
	public abstract void produce(Region.Box region, Consumer<WorldIcon> consumer, T additionalData);

	public List<WorldIcon> getAt(Region.Box region, T additionalData) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(region, collector, additionalData);
		return collector.get();
	}
}
