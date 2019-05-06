package amidst.mojangapi.world.biome;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import amidst.documentation.Immutable;
import amidst.documentation.NotThreadSafe;

@Immutable
public class Biome {
	@Immutable
	private static class BiomeIterable implements Iterable<Biome> {
		@Override
		public Iterator<Biome> iterator() {
			return new BiomeIterator();
		}
	}

	@NotThreadSafe
	private static class BiomeIterator implements Iterator<Biome> {
		private int nextBiomeIndex = 0;

		private BiomeIterator() {
			findNextValid();
		}

		@Override
		public boolean hasNext() {
			return nextBiomeIndex < biomes.length;
		}

		@Override
		public Biome next() {
			Biome result = biomes[nextBiomeIndex];
			nextBiomeIndex++;
			findNextValid();
			return result;
		}

		private void findNextValid() {
			while (nextBiomeIndex < biomes.length && biomes[nextBiomeIndex] == null) {
				nextBiomeIndex++;
			}
		}
	}

	// @formatter:off
	private static final Map<String, Biome> biomeMap = new HashMap<>();
	private static final Biome[] biomes = new Biome[256];

	public static final Biome ocean                = new Biome("Ocean",                       0, BiomeColor.from(  0,   0, 112), BiomeType.OCEAN);
	public static final Biome plains               = new Biome("Plains",                      1, BiomeColor.from(141, 179,  96), BiomeType.PLAINS);
	public static final Biome desert               = new Biome("Desert",                      2, BiomeColor.from(250, 148,  24), BiomeType.PLAINS_FLAT);
	public static final Biome extremeHills         = new Biome("Extreme Hills",               3, BiomeColor.from( 96,  96,  96), BiomeType.MOUNTAINS);
	public static final Biome forest               = new Biome("Forest",                      4, BiomeColor.from(  5, 102,  33), BiomeType.PLAINS);
	public static final Biome taiga                = new Biome("Taiga",                       5, BiomeColor.from( 11, 102,  89), BiomeType.PLAINS_TAIGA);
	public static final Biome swampland            = new Biome("Swampland",                   6, BiomeColor.from(  7, 249, 178), BiomeType.SWAMPLAND);
	public static final Biome river                = new Biome("River",                       7, BiomeColor.from(  0,   0, 255), BiomeType.RIVER);
	public static final Biome hell                 = new Biome("Hell",                        8, BiomeColor.from(255,   0,   0), BiomeType.PLAINS);
	public static final Biome theEnd               = new Biome("The End",                     9, BiomeColor.from(128, 128, 255), BiomeType.PLAINS);
	public static final Biome frozenOcean          = new Biome("Frozen Ocean",               10, BiomeColor.from(112, 112, 214), BiomeType.OCEAN);
	public static final Biome frozenRiver          = new Biome("Frozen River",               11, BiomeColor.from(160, 160, 255), BiomeType.RIVER);
	public static final Biome icePlains            = new Biome("Ice Plains",                 12, BiomeColor.from(255, 255, 255), BiomeType.PLAINS_FLAT);
	public static final Biome iceMountains         = new Biome("Ice Mountains",              13, BiomeColor.from(160, 160, 160), BiomeType.HILLS);
	public static final Biome mushroomIsland       = new Biome("Mushroom Island",            14, BiomeColor.from(255,   0, 255), BiomeType.ISLAND);
	public static final Biome mushroomIslandShore  = new Biome("Mushroom Island Shore",      15, BiomeColor.from(160,   0, 255), BiomeType.BEACH);
	public static final Biome beach                = new Biome("Beach",                      16, BiomeColor.from(250, 222,  85), BiomeType.BEACH);
	public static final Biome desertHills          = new Biome("Desert Hills",               17, BiomeColor.from(210,  95,  18), BiomeType.HILLS);
	public static final Biome forestHills          = new Biome("Forest Hills",               18, BiomeColor.from( 34,  85,  28), BiomeType.HILLS);
	public static final Biome taigaHills           = new Biome("Taiga Hills",                19, BiomeColor.from( 22,  57,  51), BiomeType.HILLS);
	public static final Biome extremeHillsEdge     = new Biome("Extreme Hills Edge",         20, BiomeColor.from(114, 120, 154), BiomeType.MOUNTAINS.weaken());
	public static final Biome jungle               = new Biome("Jungle",                     21, BiomeColor.from( 83, 123,   9), BiomeType.PLAINS);
	public static final Biome jungleHills          = new Biome("Jungle Hills",               22, BiomeColor.from( 44,  66,   5), BiomeType.HILLS);
	public static final Biome jungleEdge           = new Biome("Jungle Edge",                23, BiomeColor.from( 98, 139,  23), BiomeType.PLAINS);
	public static final Biome deepOcean            = new Biome("Deep Ocean",                 24, BiomeColor.from(  0,   0,  48), BiomeType.DEEP_OCEAN);
	public static final Biome stoneBeach           = new Biome("Stone Beach",                25, BiomeColor.from(162, 162, 132), BiomeType.BEACH_CLIFFS);
	public static final Biome coldBeach            = new Biome("Cold Beach",                 26, BiomeColor.from(250, 240, 192), BiomeType.BEACH);
	public static final Biome birchForest          = new Biome("Birch Forest",               27, BiomeColor.from( 48, 116,  68), BiomeType.PLAINS);
	public static final Biome birchForestHills     = new Biome("Birch Forest Hills",         28, BiomeColor.from( 31,  95,  50), BiomeType.HILLS);
	public static final Biome roofedForest         = new Biome("Roofed Forest",              29, BiomeColor.from( 64,  81,  26), BiomeType.PLAINS);
	public static final Biome coldTaiga            = new Biome("Cold Taiga",                 30, BiomeColor.from( 49,  85,  74), BiomeType.PLAINS_TAIGA);
	public static final Biome coldTaigaHills       = new Biome("Cold Taiga Hills",           31, BiomeColor.from( 36,  63,  54), BiomeType.HILLS);
	public static final Biome megaTaiga            = new Biome("Mega Taiga",                 32, BiomeColor.from( 89, 102,  81), BiomeType.PLAINS_TAIGA);
	public static final Biome megaTaigaHills       = new Biome("Mega Taiga Hills",           33, BiomeColor.from( 69,  79,  62), BiomeType.HILLS);
	public static final Biome extremeHillsPlus     = new Biome("Extreme Hills+",             34, BiomeColor.from( 80, 112,  80), BiomeType.MOUNTAINS);
	public static final Biome savanna              = new Biome("Savanna",                    35, BiomeColor.from(189, 178,  95), BiomeType.PLAINS_FLAT);
	public static final Biome savannaPlateau       = new Biome("Savanna Plateau",            36, BiomeColor.from(167, 157, 100), BiomeType.PLATEAU);
	public static final Biome mesa                 = new Biome("Mesa",                       37, BiomeColor.from(217,  69,  21), BiomeType.PLAINS);
	public static final Biome mesaPlateauF         = new Biome("Mesa Plateau F",             38, BiomeColor.from(176, 151, 101), BiomeType.PLATEAU);
	public static final Biome mesaPlateau          = new Biome("Mesa Plateau",               39, BiomeColor.from(202, 140, 101), BiomeType.PLATEAU);

	//TODO: find better colors for The End biomes
	public static final Biome theEndLow            = new Biome("The End - Floating islands", 40, BiomeColor.from(128, 128, 255), BiomeType.PLAINS);
	public static final Biome theEndMedium         = new Biome("The End - Medium island"   , 41, BiomeColor.from(128, 128, 255), BiomeType.PLAINS);
	public static final Biome theEndHigh           = new Biome("The End - High island"     , 42, BiomeColor.from(128, 128, 255), BiomeType.PLAINS);
	public static final Biome theEndBarren         = new Biome("The End - Barren island"   , 43, BiomeColor.from(128, 128, 255), BiomeType.PLAINS);

	public static final Biome warmOcean            = new Biome("Warm Ocean"                , 44, BiomeColor.from(  0,   0, 172), BiomeType.OCEAN);
	public static final Biome lukewarmOcean        = new Biome("Lukewarm Ocean"            , 45, BiomeColor.from(  0,   0, 144), BiomeType.OCEAN);
	public static final Biome coldOcean            = new Biome("Cold Ocean"                , 46, BiomeColor.from( 32,  32, 112), BiomeType.OCEAN);
	public static final Biome warmDeepOcean        = new Biome("Warm Deep Ocean"           , 47, BiomeColor.from(  0,   0,  80), BiomeType.OCEAN);
	public static final Biome lukewarmDeepOcean    = new Biome("Lukewarm Deep Ocean"       , 48, BiomeColor.from(  0,   0,  64), BiomeType.OCEAN);
	public static final Biome coldDeepOcean        = new Biome("Cold Deep Ocean"           , 49, BiomeColor.from( 32,  32,  56), BiomeType.OCEAN);
	public static final Biome frozenDeepOcean      = new Biome("Frozen Deep Ocean"         , 50, BiomeColor.from( 64,  64, 144), BiomeType.OCEAN);

	public static final Biome theVoid              = new Biome("The Void",                  127, BiomeColor.from(  0,   0,   0), BiomeType.PLAINS);

	public static final Biome sunflowerPlains      = new Biome("Sunflower Plains",          plains);
	public static final Biome desertM              = new Biome("Desert M",                  desert);
	public static final Biome extremeHillsM        = new Biome("Extreme Hills M",           extremeHills);
	public static final Biome flowerForest         = new Biome("Flower Forest",             forest);
	public static final Biome taigaM               = new Biome("Taiga M",                   taiga);
	public static final Biome swamplandM           = new Biome("Swampland M",               swampland);
	public static final Biome icePlainsSpikes      = new Biome("Ice Plains Spikes",         icePlains,          BiomeColor.from(180, 220, 220));
	public static final Biome jungleM              = new Biome("Jungle M",                  jungle);
	public static final Biome jungleEdgeM          = new Biome("Jungle Edge M",             jungleEdge);
	public static final Biome birchForestM         = new Biome("Birch Forest M",            birchForest);
	public static final Biome birchForestHillsM    = new Biome("Birch Forest Hills M",      birchForestHills);
	public static final Biome roofedForestM        = new Biome("Roofed Forest M",           roofedForest);
	public static final Biome coldTaigaM           = new Biome("Cold Taiga M",              coldTaiga);
	public static final Biome megaSpruceTaiga      = new Biome("Mega Spruce Taiga",         megaTaiga);
	public static final Biome megaSpurceTaigaHills = new Biome("Mega Spruce Taiga (Hills)", megaTaigaHills);
	public static final Biome extremeHillsPlusM    = new Biome("Extreme Hills+ M",          extremeHillsPlus);
	public static final Biome savannaM             = new Biome("Savanna M",                 savanna);
	public static final Biome savannaPlateauM      = new Biome("Savanna Plateau M",         savannaPlateau);
	public static final Biome mesaBryce            = new Biome("Mesa (Bryce)",              mesa);
	public static final Biome mesaPlateauFM        = new Biome("Mesa Plateau F M",          mesaPlateauF);
	public static final Biome mesaPlateauM         = new Biome("Mesa Plateau M",            mesaPlateau);

	public static final Biome bambooJungle         = new Biome("Bamboo Jungle",             168, BiomeColor.from(118, 142,  20), BiomeType.PLAINS);
	public static final Biome bambooJungleHills    = new Biome("Bamboo Jungle Hills",       169, BiomeColor.from(59 ,  71,  10), BiomeType.HILLS);
	// @formatter:on

	private static final BiomeIterable ITERABLE = new BiomeIterable();
	private static final int SPECIAL_BIOMES_START = 128;

	public static Iterable<Biome> allBiomes() {
		return ITERABLE;
	}

	public static Biome getByIndex(int index) throws UnknownBiomeIndexException {
		if (index < 0 || index >= biomes.length || biomes[index] == null) {
			throw new UnknownBiomeIndexException("unsupported biome index detected: " + index);
		} else {
			return biomes[index];
		}
	}

	public static int getBiomesLength() {
		return biomes.length;
	}

	public static Biome getByName(String name) {
		return biomeMap.get(name);
	}

	public static boolean exists(String name) {
		return biomeMap.containsKey(name);
	}

	public static boolean isSpecialBiomeIndex(int index) {
		return biomes[index] != null && biomes[index].isSpecialBiome();
	}

	public static int compareByIndex(String name1, String name2) {
		return getByName(name1).getIndex() - getByName(name2).getIndex();
	}

	private final String name;
	private final int index;
	private final BiomeColor defaultColor;
	private final BiomeType type;
	private final boolean isSpecialBiome;

	public Biome(String name, Biome base) {
		this(name, base, base.defaultColor.createLightenedBiomeColor());
	}

	public Biome(String name, Biome base, BiomeColor defaultColor) {
		this(name, base.index + SPECIAL_BIOMES_START, defaultColor, base.type.strengthen(), true);
	}

	public Biome(String name, int index, BiomeColor defaultColor, BiomeType type) {
		this(name, index, defaultColor, type, false);
	}

	public Biome(String name, int index, BiomeColor defaultColor, BiomeType type, boolean isSpecialBiome) {
		this.name = name;
		this.index = index;
		this.defaultColor = defaultColor;
		this.type = type;
		this.isSpecialBiome = isSpecialBiome;
		biomes[index] = this;
		biomeMap.put(name, this);
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public BiomeColor getDefaultColor() {
		return defaultColor;
	}

	public BiomeType getType() {
		return type;
	}

	public boolean isSpecialBiome() {
		return isSpecialBiome;
	}

	public Biome getSpecialVariant() {
		if(isSpecialBiome)
			return this;
		int special = index + SPECIAL_BIOMES_START;
		if(special < biomes.length && biomes[special].isSpecialBiome())
			return biomes[special];
		return null;
	}

	public Biome getNormalVariant() {
		if(!isSpecialBiome)
			return this;
		int normal = index - SPECIAL_BIOMES_START;
		if(normal >= 0 && !biomes[normal].isSpecialBiome())
			return biomes[normal];
		return null;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
