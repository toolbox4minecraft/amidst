package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public abstract class WorldIconProducer {
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void produce(CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer);

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public List<WorldIcon> getAt(CoordinatesInWorld corner) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(corner, collector);
		return collector.get();
	}
}
