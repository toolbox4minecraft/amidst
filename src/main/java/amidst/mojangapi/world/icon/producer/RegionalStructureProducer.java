package amidst.mojangapi.world.icon.producer;

import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.util.FastRand;

@ThreadSafe
public class RegionalStructureProducer<T> extends WorldIconProducer<T> {
	private final Resolution resolution;
	private final int offsetInWorld;

	/**
	 * This should ONLY be used for things where we have to check against
	 * it every time. For example, we have to do this with biomes and
	 * buried treasures.
	 */
	private final LocationChecker checker;
	private final WorldIconTypeProvider<T> provider;
	private final Dimension dimension;
	private final boolean displayDimension;

	// These have never changed and probably never will change, so we hard code them instead of giving them a version feature
	private static final long MAGIC_NUMBER_1 = 341873128712L;
	private static final long MAGIC_NUMBER_2 = 132897987541L;

	public final long worldSeed;
	public final long salt;
	public final byte spacing; // spacing in chunks
	public final int separation; // offset in chunks
	public final boolean isTriangular;
	public final boolean buggyStructureCoordinateMath;

	/**
	 * Length of chunks needed to cover the maximum possible regions
	 * intersecting a fragment.
	 */
	private final int intersectingRegionChunks;

	public RegionalStructureProducer(
			Resolution resolution,
			int offsetInWorld,
			LocationChecker checker,
			WorldIconTypeProvider<T> provider,
			Dimension dimension,
			boolean displayDimension,
			long worldSeed,
			long salt,
			byte spacing,
			byte separation,
			boolean isTriangular,
			boolean buggyStructureCoordinateMath) {
		this.resolution = resolution;
		this.offsetInWorld = offsetInWorld;
		this.checker = checker;
		this.provider = provider;
		this.dimension = dimension;
		this.displayDimension = displayDimension;

		this.worldSeed = worldSeed;
		this.salt = salt;
		this.spacing = spacing;
		this.separation = separation;
		this.isTriangular = isTriangular;
		this.buggyStructureCoordinateMath = buggyStructureCoordinateMath;

		this.intersectingRegionChunks = getIntersectingRegionChunks();
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, T additionalData) {
		if(checker != null && !checker.hasValidLocations()) {
			return; // No need to check if the LocationChecker will never accept anything
		}

		for (int xRelativeToFragment = 0; xRelativeToFragment <= intersectingRegionChunks; xRelativeToFragment += spacing) {
			for (int yRelativeToFragment = 0; yRelativeToFragment <= intersectingRegionChunks; yRelativeToFragment += spacing) {
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

		CoordinatesInWorld structLocation = getPossibleLocation(x, y);

		int structX = (int) structLocation.getX();
		int structY = (int) structLocation.getY();

		CoordinatesInWorld resultCoordinates = createCoordinates(structX, structY);

		// This is needed to avoid duplicate world icons.
		// Calling this before isValidLocation gives a huge speedup.
		if(resultCoordinates.isInBoundsOf(corner, Fragment.SIZE)) {

			// if there is no checker provided, skip it
			if (checker == null || checker.isValidLocation(structX, structY)) {

				DefaultWorldIconTypes worldIconType = provider.get(structX, structY, additionalData);
				if (worldIconType != null) {
					consumer.accept(
							new WorldIcon(
									resultCoordinates,
									worldIconType.getLabel(),
									worldIconType.getImage(),
									dimension,
									displayDimension));
				}
			}
		}
	}

	private CoordinatesInWorld createCoordinates(int structX, int structY) {
		long xInWorld = resolution.convertFromThisToWorld(structX);
		long yInWorld = resolution.convertFromThisToWorld(structY);
		return new CoordinatesInWorld(xInWorld + offsetInWorld, yInWorld + offsetInWorld);
	}

	public CoordinatesInWorld getCheckedLocation(int chunkX, int chunkY) {
		CoordinatesInWorld possibleLocation = getPossibleLocation(chunkX, chunkY);

		// if there is no checker provided, skip it
		if (checker == null || checker.isValidLocation((int) possibleLocation.getX(), (int) possibleLocation.getY())) {
			return possibleLocation;
		}
		return null;
	}

	/**
	 * This returns the equivalent of a StructureStart's position.
	 */
	public CoordinatesInWorld getPossibleLocation(int chunkX, int chunkY) {
		int value1 = getRegionCoord(chunkX);
		int value2 = getRegionCoord(chunkY);
		FastRand random = new FastRand(getRegionSeed(value1, value2));
		value1 = getStructCoordInRegion(random, value1);
		value2 = getStructCoordInRegion(random, value2);
		return new CoordinatesInWorld(value1, value2);
	}

	public int getRegionCoord(int coordinate) {
		return getModifiedCoord(coordinate) / spacing;
	}

	private int getModifiedCoord(int coordinate) {
		if (coordinate < 0) {
			if (buggyStructureCoordinateMath) {
				// Bug MC-131462.
				return coordinate - spacing - 1;
			} else {
				return coordinate - spacing + 1;
			}
		} else {
			return coordinate;
		}
	}

	public long getRegionSeed(int value1, int value2) {
		// @formatter:off
		return value1 * MAGIC_NUMBER_1
		     + value2 * MAGIC_NUMBER_2
		              + worldSeed
		              + salt;
		// @formatter:on
	}

	private int getStructCoordInRegion(FastRand random, int value) {
		int result = value * spacing;
		if (isTriangular) {
			result += (random.nextInt(spacing - separation)
					+ random.nextInt(spacing - separation)) / 2;
		} else {
			result += random.nextInt(spacing - separation);
		}
		return result;
	}

	private int getIntersectingRegionChunks() {
		return (resolution.getStepsPerFragment() + spacing - 1) / spacing * spacing;
	}
}
