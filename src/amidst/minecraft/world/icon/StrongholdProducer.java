package amidst.minecraft.world.icon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.version.VersionInfo;

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

	private final List<Biome> validBiomes;

	private Random random = new Random();

	public StrongholdProducer(World world) {
		super(world);
		this.validBiomes = getValidBiomes();
	}

	@Override
	protected List<WorldIcon> createCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		updateSeed();
		double angle = initAngle();
		for (int i = 0; i < 3; i++) {
			double distance = nextDistance();
			int x = getX(angle, distance) << 4;
			int y = getY(angle, distance) << 4;
			Point strongholdLocation = findStronghold(x, y);
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

	private WorldIcon createWorldIcon(int x, int y) {
		return new WorldIcon(CoordinatesInWorld.from(x, y),
				DefaultWorldIconTypes.STRONGHOLD.getName(),
				DefaultWorldIconTypes.STRONGHOLD.getImage());
	}

	private Point findStronghold(int x, int y) {
		return world.getBiomeDataOracle().findValidLocation(x + 8, y + 8, 112,
				validBiomes, random);
	}

	private int getY(double angle, double distance) {
		return (int) Math.round(Math.sin(angle) * distance);
	}

	private int getX(double angle, double distance) {
		return (int) Math.round(Math.cos(angle) * distance);
	}

	private double nextDistance() {
		return (1.25D + random.nextDouble()) * 32.0D;
	}

	private double initAngle() {
		return random.nextDouble() * 3.141592653589793D * 2.0D;
	}

	private double updateAngle(double angle) {
		return angle + 6.283185307179586D / 3.0D;
	}

	private void updateSeed() {
		random.setSeed(world.getSeed());
	}

	private List<Biome> getValidBiomes() {
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V13w36a)) {
			return getValidBiomesV13w36a();
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w03a)) {
			return VALID_BIOMES_12w03a;
		} else if (MinecraftUtil.getVersion() == VersionInfo.V1_1) {
			return VALID_BIOMES_1_1;
		} else if (MinecraftUtil.getVersion() == VersionInfo.V1_9pre6
				|| MinecraftUtil.getVersion() == VersionInfo.V1_0) {
			return VALID_BIOMES_1_0;
		} else {
			return VALID_BIOMES_DEFAULT;
		}
	}

	private List<Biome> getValidBiomesV13w36a() {
		List<Biome> result = new ArrayList<Biome>();
		for (Biome biome : Biome.iterator()) {
			if (isValidBiomeV13w36a(biome)) {
				result.add(biome);
			}
		}
		return result;
	}

	private boolean isValidBiomeV13w36a(Biome biome) {
		return biome.getType().getValue1() > 0;
	}
}
