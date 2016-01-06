package amidst.mojangapi.world.icon;

import java.util.Random;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class StructureAlgorithm {
	private final long seed;
	private final long magicNumberForSeed1;
	private final long magicNumberForSeed2;
	private final long magicNumberForSeed3;
	private final byte maxDistanceBetweenScatteredFeatures;
	private final int distanceBetweenScatteredFeaturesRange;
	private final boolean useTwoValuesForUpdate;
	private final Random random;

	public StructureAlgorithm(long seed, long magicNumberForSeed1,
			long magicNumberForSeed2, long magicNumberForSeed3,
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
		this.random = new Random();
	}

	public boolean execute(int x, int y) {
		int value1 = getInitialValue(x);
		int value2 = getInitialValue(y);
		updateSeed(value1, value2);
		value1 = updateValue(value1);
		value2 = updateValue(value2);
		return x == value1 && y == value2;
	}

	private int getInitialValue(int value) {
		return getModified(value) / maxDistanceBetweenScatteredFeatures;
	}

	private int getModified(int value) {
		if (value < 0) {
			return value - maxDistanceBetweenScatteredFeatures + 1;
		} else {
			return value;
		}
	}

	private void updateSeed(int n, int i1) {
		random.setSeed(getSeed(n, i1));
	}

	private long getSeed(int n, int i1) {
		return n * magicNumberForSeed1 + i1 * magicNumberForSeed2 + seed
				+ magicNumberForSeed3;
	}

	private int updateValue(int value) {
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
