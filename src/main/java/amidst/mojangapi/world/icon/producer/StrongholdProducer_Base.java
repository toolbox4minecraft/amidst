package amidst.mojangapi.world.icon.producer;

import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.util.FastRand;

@ThreadSafe
public abstract class StrongholdProducer_Base extends CachedWorldIconProducer {
	protected static final double DISTANCE_IN_CHUNKS = 32.0D;
	private static final int STRUCTURES_ON_FIRST_RING = 3;

	private final long seed;
	private final BiomeDataOracle biomeDataOracle;
	private final List<Biome> validBiomes;

	public StrongholdProducer_Base(long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = validBiomes;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		List<WorldIcon> result = new LinkedList<>();
		FastRand random = new FastRand(seed);
		int ring = getInitialValue_ring();
		int structuresPerRing = STRUCTURES_ON_FIRST_RING;
		int currentRingStructureCount = 0;
		double angle = getInitialValue_startAngle(random);
		for (int i = 0; i < getTotalStructureCount(); i++) {
			double distance = getNextValue_distance(ring, random);
			int x = getX(angle, distance);
			int y = getY(angle, distance);
			CoordinatesInWorld strongholdLocation = getStrongholdLocation(x, y, findStronghold(random, x, y));
			result.add(createWorldIcon(strongholdLocation));
			angle += getAngleDelta(ring, structuresPerRing);
			currentRingStructureCount++;
			if (currentRingStructureCount == structuresPerRing) {
				// This ring of strongholds is completed, adjust values to
				// begin placing strongholds on the next ring.
				ring = getNextValue_ring(ring);
				currentRingStructureCount = getNextValue_currentRingStructureCount(currentRingStructureCount);
				structuresPerRing = getNextValue_structuresPerRing(
						structuresPerRing,
						ring,
						getTotalStructureCount() - i,
						random);
				angle = getNextValue_startAngle(angle, random);
			}
		}
		return result;
	}

	private int getX(double angle, double distance) {
		return (int) Math.round(Math.cos(angle) * distance);
	}

	private int getY(double angle, double distance) {
		return (int) Math.round(Math.sin(angle) * distance);
	}

	private CoordinatesInWorld findStronghold(FastRand random, int chunkX, int chunkY) {
		return biomeDataOracle.findValidLocationAtMiddleOfChunk(chunkX, chunkY, 112, validBiomes, random);
	}

	private CoordinatesInWorld getStrongholdLocation(int x, int y, CoordinatesInWorld coordinates) {
		if (coordinates != null) {
			return getCornerOfChunk(coordinates);
		} else {
			return CoordinatesInWorld.from(x << 4, y << 4);
		}
	}

	private CoordinatesInWorld getCornerOfChunk(CoordinatesInWorld coordinates) {
		long xInWorld = (coordinates.getX() >> 4) << 4;
		long yInWorld = (coordinates.getY() >> 4) << 4;
		return CoordinatesInWorld.from(xInWorld, yInWorld);
	}

	private WorldIcon createWorldIcon(CoordinatesInWorld coordinates) {
		return new WorldIcon(
				coordinates,
				DefaultWorldIconTypes.STRONGHOLD.getLabel(),
				DefaultWorldIconTypes.STRONGHOLD.getImage(),
				Dimension.OVERWORLD,
				false);
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected int getTotalStructureCount() {
		return 3;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected int getInitialValue_ring() {
		return 1;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected int getNextValue_ring(int currentValue) {
		return currentValue + 1;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected double getInitialValue_startAngle(FastRand random) {
		return random.nextDouble() * 3.141592653589793D * 2.0D;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected double getNextValue_startAngle(double currentValue, FastRand random) {
		return currentValue;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected double getAngleDelta(int currentRing, int structuresPerRing) {
		return 6.283185307179586D * currentRing / structuresPerRing;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected double getNextValue_distance(int currentRing, FastRand random) {
		return (1.25D * currentRing + random.nextDouble()) * (DISTANCE_IN_CHUNKS * currentRing);
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected int getNextValue_currentRingStructureCount(int currentValue) {
		// Versions with more than 3 structures set currentRingStructureCount
		// back to zero each time a ring is complete.
		return 0;
	}

	// This function depends on the Minecraft version, subclasses may override.
	protected int getNextValue_structuresPerRing(
			int currentValue,
			int currentRing,
			int structuresRemaining,
			FastRand random) {
		return currentValue + currentValue + random.nextInt(currentValue);
	}
}
