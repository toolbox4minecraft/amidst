package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

public class NoopProducer<T> extends WorldIconProducer<T> {

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, T additionalData) {}

}
