package amidst.minecraft.world.finder;

import amidst.minecraft.world.CoordinatesInWorld;

public interface WorldObjectProducer {
	void produce(CoordinatesInWorld corner, WorldObjectConsumer consumer);
}
