package amidst.minecraft.world.object;

import java.util.List;
import java.util.Random;

import amidst.logging.Log;
import amidst.map.MapMarkers;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public abstract class StructureProducer extends WorldObjectProducer {
	protected final World world;
	protected final Resolution resolution;
	protected final int size;

	protected final List<Biome> validBiomesForStructure;
	protected final List<Biome> validBiomesAtMiddleOfChunk;
	protected final long magicNumberForSeed1;
	protected final long magicNumberForSeed2;
	protected final long magicNumberForSeed3;
	protected final byte maxDistanceBetweenScatteredFeatures;
	protected final byte minDistanceBetweenScatteredFeatures;
	protected final int distanceBetweenScatteredFeaturesRange;
	protected final int structureSize;
	protected final Random random;

	private CoordinatesInWorld corner;
	private WorldObjectConsumer consumer;
	private int xRelativeToFragmentAsChunkResolution;
	private int yRelativeToFragmentAsChunkResolution;
	protected int chunkX;
	protected int chunkY;
	private int middleOfChunkX;
	private int middleOfChunkY;

	public StructureProducer(World world, Resolution resolution) {
		this.world = world;
		this.resolution = resolution;
		this.size = resolution.getStepsPerFragment();
		validBiomesForStructure = getValidBiomesForStructure();
		validBiomesAtMiddleOfChunk = getValidBiomesAtMiddleOfChunk();
		magicNumberForSeed1 = getMagicNumberForSeed1();
		magicNumberForSeed2 = getMagicNumberForSeed2();
		magicNumberForSeed3 = getMagicNumberForSeed3();
		maxDistanceBetweenScatteredFeatures = getMaxDistanceBetweenScatteredFeatures();
		minDistanceBetweenScatteredFeatures = getMinDistanceBetweenScatteredFeatures();
		distanceBetweenScatteredFeaturesRange = getDistanceBetweenScatteredFeaturesRange();
		structureSize = getStructureSize();
		random = new Random();
	}

	private int getDistanceBetweenScatteredFeaturesRange() {
		return maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures;
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldObjectConsumer consumer) {
		this.corner = corner;
		this.consumer = consumer;
		for (xRelativeToFragmentAsChunkResolution = 0; xRelativeToFragmentAsChunkResolution < size; xRelativeToFragmentAsChunkResolution++) {
			for (yRelativeToFragmentAsChunkResolution = 0; yRelativeToFragmentAsChunkResolution < size; yRelativeToFragmentAsChunkResolution++) {
				generateAt();
			}
		}
	}

	// TODO: use longs?
	private void generateAt() {
		chunkX = xRelativeToFragmentAsChunkResolution
				+ (int) corner.getXAs(resolution);
		chunkY = yRelativeToFragmentAsChunkResolution
				+ (int) corner.getYAs(resolution);
		middleOfChunkX = middleOfChunk(chunkX);
		middleOfChunkY = middleOfChunk(chunkY);
		if (isValidLocation()) {
			MapMarkers mapMarker = getMapMarker();
			if (mapMarker == null) {
				Log.e("No known structure for this biome type. This might be an error.");
			} else {
				consumer.consume(new WorldObject(createCoordinates(), mapMarker));
			}
		}
	}

	private CoordinatesInWorld createCoordinates() {
		long xInWorld = resolution
				.convertFromThisToWorld(xRelativeToFragmentAsChunkResolution);
		long yInWorld = resolution
				.convertFromThisToWorld(yRelativeToFragmentAsChunkResolution);
		return corner.add(xInWorld, yInWorld);
	}

	protected boolean isSuccessful() {
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

	protected boolean isValidBiomeAtMiddleOfChunk() {
		return validBiomesAtMiddleOfChunk.contains(getBiomeAtMiddleOfChunk());
	}

	protected Biome getBiomeAtMiddleOfChunk() {
		return MinecraftUtil.getBiomeAt(middleOfChunkX, middleOfChunkY);
	}

	protected boolean isValidBiomeForStructure() {
		return MinecraftUtil.isValidBiome(middleOfChunkX, middleOfChunkY,
				structureSize, validBiomesForStructure);
	}

	protected abstract boolean isValidLocation();

	protected abstract MapMarkers getMapMarker();

	protected abstract List<Biome> getValidBiomesForStructure();

	protected abstract List<Biome> getValidBiomesAtMiddleOfChunk();

	protected abstract int updateValue(int value);

	protected abstract long getMagicNumberForSeed1();

	protected abstract long getMagicNumberForSeed2();

	protected abstract long getMagicNumberForSeed3();

	protected abstract byte getMaxDistanceBetweenScatteredFeatures();

	protected abstract byte getMinDistanceBetweenScatteredFeatures();

	protected abstract int getStructureSize();
}
