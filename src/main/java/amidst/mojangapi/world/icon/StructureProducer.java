package amidst.mojangapi.world.icon;

import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public abstract class StructureProducer extends WorldIconProducer {
	protected final BiomeDataOracle biomeDataOracle;
	protected final RecognisedVersion recognisedVersion;
	protected final Resolution resolution;
	protected final int size;

	protected final List<Biome> validBiomesForStructure;
	protected final List<Biome> validBiomesAtMiddleOfChunk;
	protected final boolean displayNetherCoordinates;
	protected final int structureSize;

	private CoordinatesInWorld corner;
	private WorldIconConsumer consumer;
	private int xRelativeToFragmentAsChunkResolution;
	private int yRelativeToFragmentAsChunkResolution;
	protected int chunkX;
	protected int chunkY;
	private int middleOfChunkX;
	private int middleOfChunkY;

	public StructureProducer(BiomeDataOracle biomeDataOracle,
			RecognisedVersion recognisedVersion) {
		this.biomeDataOracle = biomeDataOracle;
		this.recognisedVersion = recognisedVersion;
		this.resolution = Resolution.CHUNK;
		this.size = resolution.getStepsPerFragment();
		validBiomesForStructure = getValidBiomesForStructure();
		validBiomesAtMiddleOfChunk = getValidBiomesAtMiddleOfChunk();
		displayNetherCoordinates = displayNetherCoordinates();
		structureSize = getStructureSize();
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldIconConsumer consumer) {
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
			DefaultWorldIconTypes worldIconType = getWorldIconType();
			if (worldIconType != null) {
				consumer.consume(new WorldIcon(createCoordinates(),
						worldIconType.getName(), worldIconType.getImage(),
						displayNetherCoordinates));
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

	private int middleOfChunk(int value) {
		return value * 16 + 8;
	}

	protected boolean isValidBiomeAtMiddleOfChunk() {
		try {
			return validBiomesAtMiddleOfChunk
					.contains(getBiomeAtMiddleOfChunk());
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	protected Biome getBiomeAtMiddleOfChunk()
			throws UnknownBiomeIndexException, MinecraftInterfaceException {
		return biomeDataOracle.getBiomeAt(middleOfChunkX, middleOfChunkY);
	}

	protected boolean isValidBiomeForStructure() {
		return biomeDataOracle.isValidBiome(middleOfChunkX, middleOfChunkY,
				structureSize, validBiomesForStructure);
	}

	protected abstract boolean isValidLocation();

	protected abstract DefaultWorldIconTypes getWorldIconType();

	protected abstract List<Biome> getValidBiomesForStructure();

	protected abstract List<Biome> getValidBiomesAtMiddleOfChunk();

	protected abstract int getStructureSize();

	protected abstract boolean displayNetherCoordinates();
}
