package amidst.mojangapi.world.icon.locationchecker;

import java.util.Random;

import amidst.documentation.Immutable;

@Immutable
public class StructureAlgorithm implements LocationChecker {
	private final long seed;
	private final long magicNumberForSeed1;
	private final long magicNumberForSeed2;
	private final long magicNumberForSeed3;
	private final byte maxDistanceBetweenScatteredFeatures;
	private final int distanceBetweenScatteredFeaturesRange;
	private final boolean useTwoValuesForUpdate;

	public StructureAlgorithm(
			long seed,
			long magicNumberForSeed1,
			long magicNumberForSeed2,
			long magicNumberForSeed3,
			byte maxDistanceBetweenScatteredFeatures,
			byte minDistanceBetweenScatteredFeatures,
			boolean useTwoValuesForUpdate) {
		this.seed = seed;
		this.magicNumberForSeed1 = magicNumberForSeed1;
		this.magicNumberForSeed2 = magicNumberForSeed2;
		this.magicNumberForSeed3 = magicNumberForSeed3;
		this.maxDistanceBetweenScatteredFeatures = maxDistanceBetweenScatteredFeatures;
		this.distanceBetweenScatteredFeaturesRange = maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures;
		this.useTwoValuesForUpdate = useTwoValuesForUpdate;
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		int value1 = getInitialValue(x);
		int value2 = getInitialValue(y);
		Random random = new Random(getSeed(value1, value2));
		value1 = updateValue(random, value1);
		value2 = updateValue(random, value2);
		return x == value1 && y == value2;
	}

	private int getInitialValue(int coordinate) {
		return getModified(coordinate) / maxDistanceBetweenScatteredFeatures;
	}

	private int getModified(int coordinate) {
		if (coordinate < 0) {
			return coordinate - maxDistanceBetweenScatteredFeatures + 1;
		} else {
			return coordinate;
		}
	}

	private long getSeed(int value1, int value2) {
		// @formatter:off
		return value1 * magicNumberForSeed1
		     + value2 * magicNumberForSeed2
		              + seed
		              + magicNumberForSeed3;
		// @formatter:on
	}

	private int updateValue(Random random, int value) {
		value *= maxDistanceBetweenScatteredFeatures;
		if (useTwoValuesForUpdate) {
			value += (random.nextInt(distanceBetweenScatteredFeaturesRange) + random
					.nextInt(distanceBetweenScatteredFeaturesRange)) / 2;
		} else {
			value += random.nextInt(distanceBetweenScatteredFeaturesRange);
		}
		return value;
	}
}
