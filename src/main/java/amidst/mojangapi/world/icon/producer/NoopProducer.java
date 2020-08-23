package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

public class NoopProducer extends WorldIconProducer<Object> {

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, Object additionalData) {}
	
}
