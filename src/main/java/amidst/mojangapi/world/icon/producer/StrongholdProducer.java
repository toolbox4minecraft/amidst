package amidst.mojangapi.world.icon.producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class StrongholdProducer extends CachedWorldIconProducer {
	public static StrongholdProducer from(long seed,
			BiomeDataOracle biomeDataOracle, RecognisedVersion recognisedVersion) {
		return new StrongholdProducer(seed, biomeDataOracle,
				getValidBiomes(recognisedVersion));
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

	public StrongholdProducer(long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomes) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = validBiomes;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		Random random = new Random(seed);
		double angle = createAngle(random);
		for (int i = 0; i < 3; i++) {
			double distance = nextDistance(random);
			int x = getX(angle, distance);
			int y = getY(angle, distance);
			CoordinatesInWorld strongholdLocation = findStronghold(random, x, y);
			if (strongholdLocation != null) {
				result.add(createWorldIcon(getCornerOfChunk(strongholdLocation)));
			} else {
				result.add(createWorldIcon(CoordinatesInWorld.from(x << 4,
						y << 4)));
			}
			angle = updateAngle(angle);
		}
		return result;
	}

	private double createAngle(Random random) {
		return random.nextDouble() * 3.141592653589793D * 2.0D;
	}

	private double nextDistance(Random random) {
		return (1.25D + random.nextDouble()) * 32.0D;
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

	private CoordinatesInWorld getCornerOfChunk(CoordinatesInWorld coordinates) {
		long xInWorld = (coordinates.getX() >> 4) << 4;
		long yInWorld = (coordinates.getY() >> 4) << 4;
		return CoordinatesInWorld.from(xInWorld, yInWorld);
	}

	private WorldIcon createWorldIcon(CoordinatesInWorld coordinates) {
		return new WorldIcon(coordinates,
				DefaultWorldIconTypes.STRONGHOLD.getName(),
				DefaultWorldIconTypes.STRONGHOLD.getImage());
	}

	private double updateAngle(double angle) {
		return angle + 6.283185307179586D / 3.0D;
	}
}
