package amidst.map.layers;

import java.util.List;
import java.util.Random;

import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObject;
import amidst.minecraft.Biome;

public abstract class StructureFinder<L extends IconLayer> {
	protected final List<Biome> validBiomes;
	protected final long magicNumberForSeed1;
	protected final long magicNumberForSeed2;
	protected final long magicNumberForSeed3;
	protected final byte maxDistanceBetweenScatteredFeatures;
	protected final byte minDistanceBetweenScatteredFeatures;
	protected final int distanceBetweenScatteredFeaturesRange;
	protected final int size;
	protected final Random random;

	private long seed;
	private L parentLayer;

	public StructureFinder() {
		validBiomes = getValidBiomes();
		magicNumberForSeed1 = getMagicNumberForSeed1();
		magicNumberForSeed2 = getMagicNumberForSeed2();
		magicNumberForSeed3 = getMagicNumberForSeed3();
		maxDistanceBetweenScatteredFeatures = getMaxDistanceBetweenScatteredFeatures();
		minDistanceBetweenScatteredFeatures = getMinDistanceBetweenScatteredFeatures();
		distanceBetweenScatteredFeaturesRange = getDistanceBetweenScatteredFeaturesRange();
		size = getSize();
		random = new Random();
	}

	private int getDistanceBetweenScatteredFeaturesRange() {
		return maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures;
	}

	public void generateMapObjects(long seed, L parentLayer, Fragment fragment) {
		this.seed = seed;
		this.parentLayer = parentLayer;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y);
			}
		}
	}

	private void generateAt(Fragment fragment, int x, int y) {
		int chunkX = x + fragment.getChunkX();
		int chunkY = y + fragment.getChunkY();
		MapObject mapObject = checkChunk(x, y, chunkX, chunkY);
		if (mapObject != null) {
			mapObject.setParent(parentLayer);
			fragment.addObject(mapObject);
		}
	}

	private MapObject checkChunk(int x, int y, int chunkX, int chunkY) {
		boolean successful = isSuccessful(chunkX, chunkY);
		int middleOfChunkX = middleOfChunk(chunkX);
		int middleOfChunkY = middleOfChunk(chunkY);
		return getMapObject(successful, middleOfChunkX, middleOfChunkY, x, y);
	}

	private boolean isSuccessful(int chunkX, int chunkY) {
		int n = getInitialValue(chunkX);
		int i1 = getInitialValue(chunkY);
		updateSeed(n, i1);
		n = updateValue(n);
		i1 = updateValue(i1);
		return isSuccessful(chunkX, chunkY, n, i1);
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

	private boolean isSuccessful(int chunkX, int chunkY, int n, int i1) {
		return chunkX == n && chunkY == i1;
	}

	private int middleOfChunk(int value) {
		return value * 16 + 8;
	}

	protected abstract MapObject getMapObject(boolean isSuccessful,
			int middleOfChunkX, int middleOfChunkY, int x, int y);

	protected abstract List<Biome> getValidBiomes();

	protected abstract int updateValue(int value);

	protected abstract long getMagicNumberForSeed1();

	protected abstract long getMagicNumberForSeed2();

	protected abstract long getMagicNumberForSeed3();

	protected abstract byte getMaxDistanceBetweenScatteredFeatures();

	protected abstract byte getMinDistanceBetweenScatteredFeatures();

	protected abstract int getSize();
}
