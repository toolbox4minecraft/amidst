package amidst.mojangapi.world.icon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Biome;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class StrongholdProducer extends CachedWorldIconProducer {
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

	public StrongholdProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		super(recognisedVersion);
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = getValidBiomes();
	}

	private List<Biome> getValidBiomes() {
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

	private List<Biome> getValidBiomesV13w36a() {
		List<Biome> result = new ArrayList<Biome>();
		for (Biome biome : Biome.allBiomes()) {
			if (isValidBiomeV13w36a(biome)) {
				result.add(biome);
			}
		}
		return result;
	}

	private boolean isValidBiomeV13w36a(Biome biome) {
		return biome.getType().getValue1() > 0;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		Random random = new Random(seed);
		double angle = createAngle(random);
		for (int i = 0; i < 3; i++) {
			double distance = nextDistance(random);
			int x = getX(angle, distance) << 4;
			int y = getY(angle, distance) << 4;
			Point strongholdLocation = findStronghold(random, x, y);
			if (strongholdLocation != null) {
				result.add(createWorldIcon(strongholdLocation.x,
						strongholdLocation.y));
			} else {
				result.add(createWorldIcon(x, y));
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

	private Point findStronghold(Random random, int x, int y) {
		return biomeDataOracle.findValidLocation(x + 8, y + 8, 112,
				validBiomes, random);
	}

	private WorldIcon createWorldIcon(int x, int y) {
		return new WorldIcon(CoordinatesInWorld.from(x, y),
				DefaultWorldIconTypes.STRONGHOLD.getName(),
				DefaultWorldIconTypes.STRONGHOLD.getImage());
	}

	private double updateAngle(double angle) {
		return angle + 6.283185307179586D / 3.0D;
	}
}
