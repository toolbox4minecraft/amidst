package amidst.mojangapi.world.biome;

import java.util.List;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.versionfeatures.BiomeList;
import amidst.mojangapi.world.versionfeatures.DefaultVersionFeatures;
import amidst.mojangapi.world.versionfeatures.FeatureKey;

@Immutable
public class Biome {
	private static final int SPECIAL_BIOMES_START = 128;
	
	public static final int ocean                = 0;
	public static final int plains               = 1;
	public static final int desert               = 2;
	public static final int extremeHills         = 3;
	public static final int forest               = 4;
	public static final int taiga                = 5;
	public static final int swampland            = 6;
	public static final int river                = 7;
	public static final int hell                 = 8;
	public static final int theEnd               = 9;
	public static final int frozenOcean          = 10;
	public static final int frozenRiver          = 11;
	public static final int icePlains            = 12;
	public static final int iceMountains         = 13;
	public static final int mushroomIsland       = 14;
	public static final int mushroomIslandShore  = 15;
	public static final int beach                = 16;
	public static final int desertHills          = 17;
	public static final int forestHills          = 18;
	public static final int taigaHills           = 19;
	public static final int extremeHillsEdge     = 20;
	public static final int jungle               = 21;
	public static final int jungleHills          = 22;
	public static final int jungleEdge           = 23;
	public static final int deepOcean            = 24;
	public static final int stoneBeach           = 25;
	public static final int coldBeach            = 26;
	public static final int birchForest          = 27;
	public static final int birchForestHills     = 28;
	public static final int roofedForest         = 29;
	public static final int coldTaiga            = 30;
	public static final int coldTaigaHills       = 31;
	public static final int megaTaiga            = 32;
	public static final int megaTaigaHills       = 33;
	public static final int extremeHillsPlus     = 34;
	public static final int savanna              = 35;
	public static final int savannaPlateau       = 36;
	public static final int mesa                 = 37;
	public static final int mesaPlateauF         = 38;
	public static final int mesaPlateau          = 39;

	//TODO: find better colors for The End biomes
	public static final int theEndLow            = 40;
	public static final int theEndMedium         = 41;
	public static final int theEndHigh           = 42;
	public static final int theEndBarren         = 43;

	public static final int warmOcean            = 44;
	public static final int lukewarmOcean        = 45;
	public static final int coldOcean            = 46;
	public static final int warmDeepOcean        = 47;
	public static final int lukewarmDeepOcean    = 48;
	public static final int coldDeepOcean        = 49;
	public static final int frozenDeepOcean      = 50;

	public static final int theVoid              = 127;

	public static final int sunflowerPlains      = plains + SPECIAL_BIOMES_START;
	public static final int desertM              = desert + SPECIAL_BIOMES_START;
	public static final int extremeHillsM        = extremeHills + SPECIAL_BIOMES_START;
	public static final int flowerForest         = forest + SPECIAL_BIOMES_START;
	public static final int taigaM               = taiga + SPECIAL_BIOMES_START;
	public static final int swamplandM           = swampland + SPECIAL_BIOMES_START;
	public static final int icePlainsSpikes      = icePlains + SPECIAL_BIOMES_START;
	public static final int jungleM              = jungle + SPECIAL_BIOMES_START;
	public static final int jungleEdgeM          = jungleEdge + SPECIAL_BIOMES_START;
	public static final int birchForestM         = birchForest + SPECIAL_BIOMES_START;
	public static final int birchForestHillsM    = birchForestHills + SPECIAL_BIOMES_START;
	public static final int roofedForestM        = roofedForest + SPECIAL_BIOMES_START;
	public static final int coldTaigaM           = coldTaiga + SPECIAL_BIOMES_START;
	public static final int megaSpruceTaiga      = megaTaiga + SPECIAL_BIOMES_START;
	public static final int megaSpurceTaigaHills = megaTaigaHills + SPECIAL_BIOMES_START;
	public static final int extremeHillsPlusM    = extremeHillsPlus + SPECIAL_BIOMES_START;
	public static final int savannaM             = savanna + SPECIAL_BIOMES_START;
	public static final int savannaPlateauM      = savannaPlateau + SPECIAL_BIOMES_START;
	public static final int mesaBryce            = mesa + SPECIAL_BIOMES_START;
	public static final int mesaPlateauFM        = mesaPlateauF + SPECIAL_BIOMES_START;
	public static final int mesaPlateauM         = mesaPlateau + SPECIAL_BIOMES_START;

	public static final int bambooJungle         = 168;
	public static final int bambooJungleHills    = 169;
	public static final int soulSandValley       = 170;
	public static final int crimsonForest        = 171;
	public static final int warpedForest         = 172;
	
	private static final BiomeList ALL_BIOMES = DefaultVersionFeatures.builder()
															.create(RecognisedVersion.UNKNOWN)
															.get(FeatureKey.BIOME_LIST);
	
	public static Iterable<Biome> allBiomes() {
		return ALL_BIOMES.iterable();
	}
	
	public static int totalBiomes() {
		return ALL_BIOMES.size();
	}
	
	public static boolean exists(int id) {
		return getByIdOrNull(id) != null;
	}
	
	@Deprecated
	public static Biome getByIdOrNull(int id) {
		return ALL_BIOMES.getByIdOrNull(id);
	}
	
	@Deprecated
	public static Biome getById(int id) throws UnknownBiomeIdException {
		return ALL_BIOMES.getById(id);
	}
	
	public static List<Biome> getBiomeListFromIdList(BiomeList biomeList, List<Integer> idList) {
		return idList.stream().map(i -> biomeList.getByIdOrNull(i)).collect(Collectors.toList());
	}

	private final int id;
	
	private final String name;
	private final BiomeType type;
	private final boolean isSpecialBiome;

	public Biome(String name, int baseId, BiomeType baseType) {
		this(baseId + SPECIAL_BIOMES_START, name, baseType.strengthen(), true);
	}

	public Biome(int id, String name, BiomeType type) {
		this(id, name, type, false);
	}

	public Biome(int id, String name, BiomeType type, boolean isSpecialBiome) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.isSpecialBiome = isSpecialBiome;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public BiomeType getType() {
		return type;
	}

	public boolean isSpecialBiome() {
		return isSpecialBiome;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
