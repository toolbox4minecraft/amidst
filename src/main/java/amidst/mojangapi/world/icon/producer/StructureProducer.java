package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;

@ThreadSafe
public class StructureProducer<T> extends WorldIconProducer<T> {
	private final Resolution resolution;
	private final int offsetInWorld;
	private final LocationChecker checker;
	private final WorldIconTypeProvider<T> provider;
	private final Dimension dimension;
	private final boolean displayDimension;

	public StructureProducer(
			Resolution resolution,
			int offsetInWorld,
			LocationChecker checker,
			WorldIconTypeProvider<T> provider,
			Dimension dimension,
			boolean displayDimension) {
		this.resolution = resolution;
		this.offsetInWorld = offsetInWorld;
		this.checker = checker;
		this.provider = provider;
		this.dimension = dimension;
		this.displayDimension = displayDimension;
	}

	@Override
	public void produce(Region.Box region, Consumer<WorldIcon> consumer, T additionalData) {

		region = region.getAs(resolution);
		Coordinates min = region.getCornerNW();
		Coordinates max = region.getCornerSE();
		
		for(int x = min.getX(); x < max.getX(); x++) {
			for(int y = min.getY(); y < max.getY(); y++) {
				generateAt(x, y, consumer, additionalData);
			}
		}
	}
	
	private void generateAt(int x, int y, Consumer<WorldIcon> consumer, T additionalData) {
		if(checker.isValidLocation(x, y)) {
			DefaultWorldIconTypes worldIconType = provider.get(x, y, additionalData);
			if (worldIconType != null) {
				consumer.accept(
						new WorldIcon(
								Coordinates.from(x, y, resolution).add(offsetInWorld, offsetInWorld),
								worldIconType.getLabel(),
								worldIconType.getImage(),
								dimension,
								displayDimension));
			}
		}
	}
}
