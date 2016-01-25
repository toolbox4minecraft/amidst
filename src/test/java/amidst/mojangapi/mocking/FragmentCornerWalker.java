package amidst.mojangapi.mocking;

import java.util.function.Consumer;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;

@Immutable
public class FragmentCornerWalker {
	private final CoordinatesInWorld startCorner;
	private final CoordinatesInWorld endCorner;

	public FragmentCornerWalker(CoordinatesInWorld startCorner,
			CoordinatesInWorld endCorner) {
		this.startCorner = startCorner;
		this.endCorner = endCorner;
	}

	public <T> void walk(WorldIconProducer<T> producer,
			Consumer<WorldIcon> consumer,
			Function<CoordinatesInWorld, T> additionalDataFactory) {
		for (long x = startCorner.getX(); x < endCorner.getX(); x += Fragment.SIZE) {
			for (long y = startCorner.getY(); y < endCorner.getY(); y += Fragment.SIZE) {
				CoordinatesInWorld corner = CoordinatesInWorld.from(x, y);
				producer.produce(corner, consumer,
						additionalDataFactory.apply(corner));
			}
		}
	}
}
