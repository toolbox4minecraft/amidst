package amidst.map.finder;

import java.util.List;
import java.util.Random;

import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.world.World;
import amidst.preferences.BooleanPrefModel;

public abstract class StructureFinder {
	protected final List<Biome> validBiomes;
	protected final long magicNumberForSeed1;
	protected final long magicNumberForSeed2;
	protected final long magicNumberForSeed3;
	protected final byte maxDistanceBetweenScatteredFeatures;
	protected final byte minDistanceBetweenScatteredFeatures;
	protected final int distanceBetweenScatteredFeaturesRange;
	protected final int size;
	protected final Random random;

	protected World world;
	protected BooleanPrefModel isVisiblePreference;
	protected Fragment fragment;
	protected int xRelativeToFragmentAsChunkResolution;
	protected int yRelativeToFragmentAsChunkResolution;
	protected int chunkX;
	protected int chunkY;
	protected boolean isSuccessful;
	protected int middleOfChunkX;
	protected int middleOfChunkY;

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

	public void generateMapObjects(World world,
			BooleanPrefModel isVisiblePreference, Fragment fragment) {
		this.world = world;
		this.isVisiblePreference = isVisiblePreference;
		this.fragment = fragment;
		for (xRelativeToFragmentAsChunkResolution = 0; xRelativeToFragmentAsChunkResolution < size; xRelativeToFragmentAsChunkResolution++) {
			for (yRelativeToFragmentAsChunkResolution = 0; yRelativeToFragmentAsChunkResolution < size; yRelativeToFragmentAsChunkResolution++) {
				generateAt();
			}
		}
	}

	private void generateAt() {
		chunkX = xRelativeToFragmentAsChunkResolution
				+ fragment.getChunkXInWorld();
		chunkY = yRelativeToFragmentAsChunkResolution
				+ fragment.getChunkYInWorld();
		MapObject mapObject = checkChunk();
		if (mapObject != null) {
			fragment.addObject(mapObject);
		}
	}

	private MapObject checkChunk() {
		isSuccessful = isSuccessful();
		middleOfChunkX = middleOfChunk(chunkX);
		middleOfChunkY = middleOfChunk(chunkY);
		return getMapObject();
	}

	private boolean isSuccessful() {
		int n = getInitialValue(chunkX);
		int i1 = getInitialValue(chunkY);
		updateSeed(n, i1);
		n = updateValue(n);
		i1 = updateValue(i1);
		return isSuccessful(n, i1);
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
		return n * magicNumberForSeed1 + i1 * magicNumberForSeed2
				+ world.getSeed() + magicNumberForSeed3;
	}

	private boolean isSuccessful(int n, int i1) {
		return chunkX == n && chunkY == i1;
	}

	private int middleOfChunk(int value) {
		return value * 16 + 8;
	}

	protected abstract MapObject getMapObject();

	protected abstract List<Biome> getValidBiomes();

	protected abstract int updateValue(int value);

	protected abstract long getMagicNumberForSeed1();

	protected abstract long getMagicNumberForSeed2();

	protected abstract long getMagicNumberForSeed3();

	protected abstract byte getMaxDistanceBetweenScatteredFeatures();

	protected abstract byte getMinDistanceBetweenScatteredFeatures();

	protected abstract int getSize();
}
