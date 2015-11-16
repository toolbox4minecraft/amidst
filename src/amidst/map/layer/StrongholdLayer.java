package amidst.map.layer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.object.MapObjectStronghold;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class StrongholdLayer extends IconLayer {
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

	// TODO: remove me!
	public static StrongholdLayer instance;

	private MapObjectStronghold[] strongholds = new MapObjectStronghold[3];
	private Random random = new Random();

	public StrongholdLayer() {
		instance = this;
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showStrongholds.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y);
			}
		}
	}

	private void generateAt(Fragment fragment, int x, int y) {
		int chunkX = x + fragment.getChunkX();
		int chunkY = y + fragment.getChunkY();
		// TODO: This does not need a per-chunk test!
		if (hasStronghold(chunkX, chunkY)) {
			// FIXME: Possible use of checkChunk causing negative icons
			// to be misaligned!
			MapObjectStronghold mapObject = new MapObjectStronghold(x << 4,
					y << 4);
			mapObject.setParent(this);
			fragment.addObject(mapObject);
		}
	}

	private boolean hasStronghold(int chunkX, int chunkY) {
		for (int i = 0; i < 3; i++) {
			int strongholdChunkX = strongholds[i].x >> 4;
			int strongholdChunkY = strongholds[i].y >> 4;
			if ((strongholdChunkX == chunkX) && (strongholdChunkY == chunkY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reload() {
		findStrongholds();
	}

	private void findStrongholds() {
		List<Biome> validBiomes = getValidBiomes();
		updateSeed();
		double angle = initAngle();
		for (int i = 0; i < 3; i++) {
			double distance = nextDistance();
			int x = getX(angle, distance);
			int y = getY(angle, distance);
			Point strongholdLocation = findStronghold(validBiomes, x, y);
			if (strongholdLocation != null) {
				x = strongholdLocation.x >> 4;
				y = strongholdLocation.y >> 4;
			}
			strongholds[i] = new MapObjectStronghold(x << 4, y << 4);
			angle = updateAngle(angle);
		}
	}

	private Point findStronghold(List<Biome> validBiomes, int x, int y) {
		return MinecraftUtil.findValidLocation((x << 4) + 8, (y << 4) + 8, 112,
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
		random.setSeed(Options.instance.seed);
	}

	// TODO: Replace this system!
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
		for (Biome biome : Biome.biomes) {
			if (isValidBiomeV13w36a(biome)) {
				result.add(biome);
			}
		}
		return result;
	}

	private boolean isValidBiomeV13w36a(Biome biome) {
		return biome != null && biome.type.value1 > 0;
	}

	// TODO: remove me!
	public MapObjectStronghold[] getStrongholds() {
		return strongholds;
	}
}
