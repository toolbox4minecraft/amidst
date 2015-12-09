package amidst.mojangapi.world.icon;

import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.mojangapi.world.CoordinatesInWorld;

public abstract class WorldIconProducer {
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void produce(CoordinatesInWorld corner,
			WorldIconConsumer consumer);

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public List<WorldIcon> getAt(CoordinatesInWorld corner) {
		WorldIconCollector collector = new WorldIconCollector();
		produce(corner, collector);
		return collector.get();
	}
}
