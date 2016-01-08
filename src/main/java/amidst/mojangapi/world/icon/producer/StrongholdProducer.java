package amidst.mojangapi.world.icon.producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class StrongholdProducer extends CachedWorldIconProducer {
	private static final double DISTANCE_IN_CHUNKS = 32.0D;
	private static final int STRUCTURES_ON_FIRST_RING = 3;

	public static StrongholdProducer from(long seed,
			BiomeDataOracle biomeDataOracle, RecognisedVersion recognisedVersion) {
		return new StrongholdProducer(seed, biomeDataOracle,
				getValidBiomes(recognisedVersion),
				getTotalStructureCount(recognisedVersion));
	}

	private static List<Biome> getValidBiomes(
			RecognisedVersion recognisedVersion) {
		if (recognisedVersion.isAtLeast(RecognisedVersion.V13w36a)) {
			return getValidBiomesV13w36a();
		} else if (recognisedVersion.isAtLeast(RecognisedVersion.V12w03a)) {
			return VALID_BIOMES_12w03a;
		} else if (recognisedVersion == RecognisedVersion.V1_1) {
			return VALID_BIOMES_1_1;
		} else if (recognisedVersion == RecognisedVersion.V1_9pre6
				|| recognisedVersion == RecognisedVersion.V1_0) {
			return VALID_BIOMES_1_0;
		} else {
			return VALID_BIOMES_DEFAULT;
		}
	}

	private static List<Biome> getValidBiomesV13w36a() {
		List<Biome> result = new ArrayList<Biome>();
		for (Biome biome : Biome.allBiomes()) {
			if (isValidBiomeV13w36a(biome)) {
				result.add(biome);
			}
		}
		return result;
	}

	private static boolean isValidBiomeV13w36a(Biome biome) {
		return biome.getType().getValue1() > 0;
	}

	private static int getTotalStructureCount(
			RecognisedVersion recognisedVersion) {
		if (recognisedVersion.isAtLeast(RecognisedVersion.V15w43c)) {
			return 128;
		} else {
			return 3;
		}
	}

	// @formatter:off
	private static final List<Biome> VALID_BIOMES_DEFAULT = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland
	);
	
	private static final List<Biome> VALID_BIOMES_1_0 = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains
	);
	
	private static final List<Biome> VALID_BIOMES_1_1 = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains,
			Biome.desertHills,
			Biome.forestHills,
			Biome.extremeHillsEdge
	);
	
	private static final List<Biome> VALID_BIOMES_12w03a = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains,
			Biome.desertHills,
			Biome.forestHills,
			Biome.extremeHillsEdge,
			Biome.jungle,
			Biome.jungleHills
	);
	// @formatter:on

	private final long seed;
	private final BiomeDataOracle biomeDataOracle;
	private final List<Biome> validBiomes;
	private final int totalStructureCount;

	public StrongholdProducer(long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomes, int totalStructureCount) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = Collections.unmodifiableList(validBiomes);
		this.totalStructureCount = totalStructureCount;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		Random random = new Random(seed);
		int ring = 1;
		int structuresPerRing = STRUCTURES_ON_FIRST_RING;
		int currentRingStructureCount = 0;
		double angle = createAngle(random);
		for (int i = 0; i < totalStructureCount; i++) {
			double distance = nextDistance(random, ring);
			int x = getX(angle, distance);
			int y = getY(angle, distance);
			CoordinatesInWorld strongholdLocation = getStrongholdLocation(x, y,
					findStronghold(random, x, y));
			result.add(createWorldIcon(strongholdLocation));
			angle += getAngleDelta(ring, structuresPerRing);
			currentRingStructureCount++;
			if (currentRingStructureCount == structuresPerRing) {
				ring++;
				currentRingStructureCount = 0;
				structuresPerRing += getStructuresPerRingDelta(random,
						structuresPerRing);
			}
		}
		return result;
	}

	private double createAngle(Random random) {
		return random.nextDouble() * 3.141592653589793D * 2.0D;
	}

	private double nextDistance(Random random, int ring) {
		return (1.25D * ring + random.nextDouble())
				* (DISTANCE_IN_CHUNKS * ring);
	}

	private int getX(double angle, double distance) {
		return (int) Math.round(Math.cos(angle) * distance);
	}

	private int getY(double angle, double distance) {
		return (int) Math.round(Math.sin(angle) * distance);
	}

	private CoordinatesInWorld findStronghold(Random random, int chunkX,
			int chunkY) {
		return biomeDataOracle.findValidLocationAtMiddleOfChunk(chunkX, chunkY,
				112, validBiomes, random);
	}

	private CoordinatesInWorld getStrongholdLocation(int x, int y,
			CoordinatesInWorld coordinates) {
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
		return new WorldIcon(coordinates,
				DefaultWorldIconTypes.STRONGHOLD.getName(),
				DefaultWorldIconTypes.STRONGHOLD.getImage(),
				Dimension.OVERWORLD);
	}

	private double getAngleDelta(int ring, int structuresPerRing) {
		return 6.283185307179586D * ring / structuresPerRing;
	}

	private int getStructuresPerRingDelta(Random random, int structuresPerRing) {
		return structuresPerRing + random.nextInt(structuresPerRing);
	}
}
