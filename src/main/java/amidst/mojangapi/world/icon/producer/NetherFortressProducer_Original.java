package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.util.FastRand;

@Immutable
public class NetherFortressProducer_Original extends WorldIconProducer<Void> {
	private static final DefaultWorldIconTypes ICON_TYPE = DefaultWorldIconTypes.NETHER_FORTRESS;
	private static final Dimension DIMENSION = Dimension.NETHER;
	private static final Resolution RESOLUTION = Resolution.NETHER_CHUNK;
	private static final int OFFSET_IN_WORLD = 88;
	private static final int SPACING = 16; // nether chunks (i think) because of the "coord >> 4" bit shift
	/**
	 * Length of chunks needed to cover the maximum possible regions
	 * intersecting a fragment.
	 */
	private static final int INTERSECTING_REGION_CHUNKS = (RESOLUTION.getStepsPerFragment() + SPACING - 1) / SPACING * SPACING;

	private final long seed;

	public NetherFortressProducer_Original(long seed) {
		this.seed = seed;
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, Void additionalData) {
		for (int xRelativeToFragment = 0; xRelativeToFragment <= INTERSECTING_REGION_CHUNKS; xRelativeToFragment += SPACING) {
			for (int yRelativeToFragment = 0; yRelativeToFragment <= INTERSECTING_REGION_CHUNKS; yRelativeToFragment += SPACING) {
				generateAt(corner, consumer, xRelativeToFragment, yRelativeToFragment);
			}
		}
	}

	// TODO: use longs?
	private void generateAt(
			CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer,
			int xRelativeToFragment,
			int yRelativeToFragment) {

		int x = xRelativeToFragment + (int) corner.getXAs(RESOLUTION);
		int y = yRelativeToFragment + (int) corner.getYAs(RESOLUTION);

		CoordinatesInWorld checkedLocation = getCheckedLocation(x, y);

		if(checkedLocation != null) {
			int possibleX = (int) checkedLocation.getX();
			int possibleY = (int) checkedLocation.getY();

			CoordinatesInWorld coordinates = createCoordinates(possibleX, possibleY);
			if(coordinates.isInBoundsOf(corner, Fragment.SIZE)) {
				consumer.accept(
						new WorldIcon(
								coordinates,
								ICON_TYPE.getLabel(),
								ICON_TYPE.getImage(),
								DIMENSION,
								false));
			}
		}
	}

	private CoordinatesInWorld createCoordinates(int structX, int structY) {
		long xInWorld = RESOLUTION.convertFromThisToWorld(structX);
		long yInWorld = RESOLUTION.convertFromThisToWorld(structY);
		return new CoordinatesInWorld(xInWorld + OFFSET_IN_WORLD, yInWorld + OFFSET_IN_WORLD);
	}

	private CoordinatesInWorld getCheckedLocation(int x, int y) {
		int i = x >> 4;
		int j = y >> 4;

		FastRand random = new FastRand(i ^ j << 4 ^ seed);
		random.advance();

		if(random.nextInt(3) == 0) {
			return new CoordinatesInWorld((i << 4) + 4 + random.nextInt(8), (j << 4) + 4 + random.nextInt(8));
		}

		return null;
	}
}
