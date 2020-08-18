package amidst.mojangapi.world.icon.locationchecker;

import java.util.Random;

import amidst.documentation.Immutable;

@Immutable
public class StructureAlgorithm implements LocationChecker {

	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;

	private final long seed;
	private final long structureSalt;
	private final byte maxDistanceBetweenScatteredFeatures;
	private final int distanceBetweenScatteredFeaturesRange;
	private final boolean useTwoValuesForUpdate;
	private final boolean buggyStructureCoordinateMath;

	public StructureAlgorithm(
			long seed,
			long structureSalt,
			byte maxDistanceBetweenScatteredFeatures,
			byte minDistanceBetweenScatteredFeatures,
			boolean useTwoValuesForUpdate) {
		this(
				seed,
				structureSalt,
				maxDistanceBetweenScatteredFeatures,
				minDistanceBetweenScatteredFeatures,
				useTwoValuesForUpdate,
				false);
	}

	public StructureAlgorithm(
			long seed,
			long structureSalt,
			byte maxDistanceBetweenScatteredFeatures,
			byte minDistanceBetweenScatteredFeatures,
			boolean useTwoValuesForUpdate,
			boolean buggyStructureCoordinateMath) {
		this.seed = seed;
		this.structureSalt = structureSalt;
		this.maxDistanceBetweenScatteredFeatures = maxDistanceBetweenScatteredFeatures;
		this.distanceBetweenScatteredFeaturesRange = maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures;
		this.useTwoValuesForUpdate = useTwoValuesForUpdate;
		this.buggyStructureCoordinateMath = buggyStructureCoordinateMath;
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		int value1 = getInitialValue(x);
		int value2 = getInitialValue(y);
		Random random = new Random(getSeed(value1, value2));
		value1 = updateValue(random, value1);
		value2 = updateValue(random, value2);
		return x == value1 && y == value2 && doExtraCheck(random);
	}

	protected boolean doExtraCheck(Random random) {
		return true;
	}

	private int getInitialValue(int coordinate) {
		return getModified(coordinate) / maxDistanceBetweenScatteredFeatures;
	}

	private int getModified(int coordinate) {
		if (coordinate < 0) {
			if (buggyStructureCoordinateMath) {
				// Bug MC-131462.
				return coordinate - maxDistanceBetweenScatteredFeatures - 1;
			} else {
				return coordinate - maxDistanceBetweenScatteredFeatures + 1;
			}
		} else {
			return coordinate;
		}
	}

	private long getSeed(int value1, int value2) {
		// @formatter:off
		return value1 * MAGIC_NUMBER_FOR_SEED_1
		     + value2 * MAGIC_NUMBER_FOR_SEED_2
		              + seed
		              + structureSalt;
		// @formatter:on
	}

	private int updateValue(Random random, int value) {
		int result = value * maxDistanceBetweenScatteredFeatures;
		if (useTwoValuesForUpdate) {
			result += (random.nextInt(distanceBetweenScatteredFeaturesRange)
					+ random.nextInt(distanceBetweenScatteredFeaturesRange)) / 2;
		} else {
			result += random.nextInt(distanceBetweenScatteredFeaturesRange);
		}
		return result;
	}
}
