package amidst.mojangapi.world.icon.producer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

public class MultiProducer<T> extends WorldIconProducer<T> {
	
	private final List<WorldIconProducer<T>> producers;
	
	public MultiProducer(List<WorldIconProducer<T>> producers) {
		this.producers = producers;
	}
	
	@SafeVarargs
	public MultiProducer(WorldIconProducer<T>... producers) {
		this(Arrays.asList(producers));
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, T additionalData) {
		for(WorldIconProducer<T> producer: producers) {
			producer.produce(corner, consumer, additionalData);
		}
	}

}
