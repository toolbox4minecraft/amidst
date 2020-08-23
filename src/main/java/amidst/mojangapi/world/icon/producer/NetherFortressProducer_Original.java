package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import kaptainwutax.seedutils.lcg.rand.JRand;

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
		
		CoordinatesInWorld possibleLocation = getPossibleLocation(x, y);
		
		if(possibleLocation != null) {
			int possibleX = (int) possibleLocation.getX();
			int possibleY = (int) possibleLocation.getY();
			
			CoordinatesInWorld coordinates = createCoordinates(corner, possibleX - (int) corner.getXAs(RESOLUTION), possibleY - (int) corner.getYAs(RESOLUTION));
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

	private CoordinatesInWorld createCoordinates(
			CoordinatesInWorld corner,
			int xRelativeToFragment,
			int yRelativeToFragment) {
		long xInWorld = RESOLUTION.convertFromThisToWorld(xRelativeToFragment);
		long yInWorld = RESOLUTION.convertFromThisToWorld(yRelativeToFragment);
		return corner.add(xInWorld + OFFSET_IN_WORLD, yInWorld + OFFSET_IN_WORLD);
	}
	
	private CoordinatesInWorld getPossibleLocation(int x, int y) {
		int i = x >> 4;
		int j = y >> 4;
		
		JRand random = new JRand(i ^ j << 4 ^ seed);
		random.nextInt();
		
		if(random.nextInt(3) == 0) {
			return new CoordinatesInWorld((i << 4) + 4 + random.nextInt(8), (j << 4) + 4 + random.nextInt(8));
		}
		
		return null;
	}
}
