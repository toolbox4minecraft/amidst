package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;

@ThreadSafe
public class ChunkStructureProducer<T> extends WorldIconProducer<T> {
	private final Resolution resolution;
	private final int size;
	private final int offsetInWorld;
	private final LocationChecker checker;
	private final WorldIconTypeProvider<T> provider;
	private final Dimension dimension;
	private final boolean displayDimension;

	public ChunkStructureProducer(
			Resolution resolution,
			int offsetInWorld,
			LocationChecker checker,
			WorldIconTypeProvider<T> provider,
			Dimension dimension,
			boolean displayDimension) {
		this.resolution = resolution;
		this.size = resolution.getStepsPerFragment();
		this.offsetInWorld = offsetInWorld;
		this.checker = checker;
		this.provider = provider;
		this.dimension = dimension;
		this.displayDimension = displayDimension;
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, T additionalData) {
		if(checker != null && !checker.hasValidLocations()) {
			return; // No need to check if the LocationChecker will never accept anything
		}

		for (int xRelativeToFragment = 0; xRelativeToFragment < size; xRelativeToFragment++) {
			for (int yRelativeToFragment = 0; yRelativeToFragment < size; yRelativeToFragment++) {
				generateAt(corner, consumer, additionalData, xRelativeToFragment, yRelativeToFragment);
			}
		}
	}

	// TODO: use longs?
	private void generateAt(
			CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer,
			T additionalData,
			int xRelativeToFragment,
			int yRelativeToFragment) {
		int x = xRelativeToFragment + (int) corner.getXAs(resolution);
		int y = yRelativeToFragment + (int) corner.getYAs(resolution);
		
		// if there is no checker provided, skip it
		if (checker == null || checker.isValidLocation(x, y)) {
			DefaultWorldIconTypes worldIconType = provider.get(x, y, additionalData);
			if (worldIconType != null) {
				CoordinatesInWorld coordinates = createCoordinates(corner, xRelativeToFragment, yRelativeToFragment);
				consumer.accept(
						new WorldIcon(
								coordinates,
								worldIconType.getLabel(),
								worldIconType.getImage(),
								dimension,
								displayDimension));
			}
		}
	}

	private CoordinatesInWorld createCoordinates(
			CoordinatesInWorld corner,
			int xRelativeToFragment,
			int yRelativeToFragment) {
		long xInWorld = resolution.convertFromThisToWorld(xRelativeToFragment);
		long yInWorld = resolution.convertFromThisToWorld(yRelativeToFragment);
		return corner.add(xInWorld + offsetInWorld, yInWorld + offsetInWorld);
	}
}
