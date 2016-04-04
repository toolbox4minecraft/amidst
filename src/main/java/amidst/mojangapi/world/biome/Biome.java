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
			while (nextBiomeIndex < biomes.length
					&& biomes[nextBiomeIndex] == null) {
				nextBiomeIndex++;
			}
		}
	}

	// @formatter:off
	private static final Map<String, Biome> biomeMap = new HashMap<String, Biome>();
	private static final Biome[] biomes = new Biome[256];
	private static final String BETABIOME_PREFIX = "beta ";

	public static final Biome ocean                = new Biome("Ocean",                       0, BiomeColor.from(  0,   0, 112), BiomeType.OCEAN_TERRAIN_DATA);
	public static final Biome plains               = new Biome("Plains",                      1, BiomeColor.from(141, 179,  96), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome desert               = new Biome("Desert",                      2, BiomeColor.from(250, 148,  24), BiomeType.PLAINS_TERRAIN_DATA);
	public static final Biome extremeHills         = new Biome("Extreme Hills",               3, BiomeColor.from( 96,  96,  96), BiomeType.EXTREME_HILLS_TERRAIN_DATA);
	public static final Biome forest               = new Biome("Forest",                      4, BiomeColor.from(  5, 102,  33), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome taiga                = new Biome("Taiga",                       5, BiomeColor.from( 11, 102,  89), BiomeType.TAIGA_TERRAIN_DATA);
	public static final Biome swampland            = new Biome("Swampland",                   6, BiomeColor.from(  7, 249, 178), BiomeType.SWAMPLAND_TERRAIN_DATA);
	public static final Biome river                = new Biome("River",                       7, BiomeColor.from(  0,   0, 255), BiomeType.RIVER_TERRAIN_DATA);
	public static final Biome hell                 = new Biome("Hell",                        8, BiomeColor.from(255,   0,   0), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome theEnd               = new Biome("The End",                     9, BiomeColor.from(128, 128, 255), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome frozenOcean          = new Biome("Frozen Ocean",               10, BiomeColor.from(144, 144, 160), BiomeType.OCEAN_TERRAIN_DATA);
	public static final Biome frozenRiver          = new Biome("Frozen River",               11, BiomeColor.from(160, 160, 255), BiomeType.RIVER_TERRAIN_DATA);
	public static final Biome icePlains            = new Biome("Ice Plains",                 12, BiomeColor.from(255, 255, 255), BiomeType.PLAINS_TERRAIN_DATA);
	public static final Biome iceMountains         = new Biome("Ice Mountains",              13, BiomeColor.from(160, 160, 160), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome mushroomIsland       = new Biome("Mushroom Island",            14, BiomeColor.from(255,   0, 255), BiomeType.MUSHROOM_ISLAND_TERRAIN_DATA);
	public static final Biome mushroomIslandShore  = new Biome("Mushroom Island Shore",      15, BiomeColor.from(160,   0, 255), BiomeType.BEACH_TERRAIN_DATA);
	public static final Biome beach                = new Biome("Beach",                      16, BiomeColor.from(250, 222,  85), BiomeType.BEACH_TERRAIN_DATA);
	public static final Biome desertHills          = new Biome("Desert Hills",               17, BiomeColor.from(210,  95,  18), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome forestHills          = new Biome("Forest Hills",               18, BiomeColor.from( 34,  85,  28), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome taigaHills           = new Biome("Taiga Hills",                19, BiomeColor.from( 22,  57,  51), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome extremeHillsEdge     = new Biome("Extreme Hills Edge",         20, BiomeColor.from(114, 120, 154), BiomeType.EXTREME_HILLS_TERRAIN_DATA.getExtreme());
	public static final Biome jungle               = new Biome("Jungle",                     21, BiomeColor.from( 83, 123,   9), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome jungleHills          = new Biome("Jungle Hills",               22, BiomeColor.from( 44,  66,   5), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome jungleEdge           = new Biome("Jungle Edge",                23, BiomeColor.from( 98, 139,  23), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome deepOcean            = new Biome("Deep Ocean",                 24, BiomeColor.from(  0,   0,  48), BiomeType.DEEP_OCEAN_TERRAIN_DATA);
	public static final Biome stoneBeach           = new Biome("Stone Beach",                25, BiomeColor.from(162, 162, 132), BiomeType.STONE_BEACH_TERRAIN_DATA);
	public static final Biome coldBeach            = new Biome("Cold Beach",                 26, BiomeColor.from(250, 240, 192), BiomeType.BEACH_TERRAIN_DATA);
	public static final Biome birchForest          = new Biome("Birch Forest",               27, BiomeColor.from( 48, 116,  68), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome birchForestHills     = new Biome("Birch Forest Hills",         28, BiomeColor.from( 31,  95,  50), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome roofedForest         = new Biome("Roofed Forest",              29, BiomeColor.from( 64,  81,  26), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome coldTaiga            = new Biome("Cold Taiga",                 30, BiomeColor.from( 49,  85,  74), BiomeType.TAIGA_TERRAIN_DATA);
	public static final Biome coldTaigaHills       = new Biome("Cold Taiga Hills",           31, BiomeColor.from( 36,  63,  54), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome megaTaiga            = new Biome("Mega Taiga",                 32, BiomeColor.from( 89, 102,  81), BiomeType.TAIGA_TERRAIN_DATA);
	public static final Biome megaTaigaHills       = new Biome("Mega Taiga Hills",           33, BiomeColor.from( 69,  79,  62), BiomeType.HILLS_TERRAIN_DATA);
	public static final Biome extremeHillsPlus     = new Biome("Extreme Hills+",             34, BiomeColor.from( 80, 112,  80), BiomeType.EXTREME_HILLS_TERRAIN_DATA);
	public static final Biome savanna              = new Biome("Savanna",                    35, BiomeColor.from(189, 178,  95), BiomeType.PLAINS_TERRAIN_DATA);
	public static final Biome savannaPlateau       = new Biome("Savanna Plateau",            36, BiomeColor.from(167, 157, 100), BiomeType.PLATEAU_TERRAIN_DATA);
	public static final Biome mesa                 = new Biome("Mesa",                       37, BiomeColor.from(217,  69,  21), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome mesaPlateauF         = new Biome("Mesa Plateau F",             38, BiomeColor.from(176, 151, 101), BiomeType.PLATEAU_TERRAIN_DATA);
	public static final Biome mesaPlateau          = new Biome("Mesa Plateau",               39, BiomeColor.from(202, 140, 101), BiomeType.PLATEAU_TERRAIN_DATA);

	public static final Biome oceanM               = new Biome("Ocean M",                   128, BiomeColor.from(  0,   0, 112));
	public static final Biome sunflowerPlains      = new Biome("Sunflower Plains",          129, BiomeColor.from(141, 179,  96));
	public static final Biome desertM              = new Biome("Desert M",                  130, BiomeColor.from(250, 148,  24));
	public static final Biome extremeHillsM        = new Biome("Extreme Hills M",           131, BiomeColor.from( 96,  96,  96));
	public static final Biome flowerForest         = new Biome("Flower Forest",             132, BiomeColor.from(  5, 102,  33));
	public static final Biome taigaM               = new Biome("Taiga M",                   133, BiomeColor.from( 11, 102,  89));
	public static final Biome swamplandM           = new Biome("Swampland M",               134, BiomeColor.from(  7, 249, 178));
	public static final Biome riverM               = new Biome("River M",                   135, BiomeColor.from(  0,   0, 255));
	public static final Biome hellM                = new Biome("Hell M",                    136, BiomeColor.from(255,   0,   0));
	public static final Biome skyM                 = new Biome("Sky M",                     137, BiomeColor.from(128, 128, 255));
	public static final Biome frozenOceanM         = new Biome("Frozen Ocean M",            138, BiomeColor.from(144, 144, 160));
	public static final Biome frozenRiverM         = new Biome("Frozen River M",            139, BiomeColor.from(160, 160, 255));
	public static final Biome icePlainsSpikes      = new Biome("Ice Plains Spikes",         140, BiomeColor.from(140, 180, 180));
	public static final Biome iceMountainsM        = new Biome("Ice Mountains M",           141, BiomeColor.from(160, 160, 160));
	public static final Biome mushroomIslandM      = new Biome("Mushroom Island M",         142, BiomeColor.from(255,   0, 255));
	public static final Biome mushroomIslandShoreM = new Biome("Mushroom Island Shore M",   143, BiomeColor.from(160,   0, 255));
	public static final Biome beachM               = new Biome("Beach M",                   144, BiomeColor.from(250, 222,  85));
	public static final Biome desertHillsM         = new Biome("Desert Hills M",            145, BiomeColor.from(210,  95,  18));
	public static final Biome forestHillsM         = new Biome("Forest Hills M",            146, BiomeColor.from( 34,  85,  28));
	public static final Biome taigaHillsM          = new Biome("Taiga Hills M",             147, BiomeColor.from( 22,  57,  51));
	public static final Biome extremeHillsEdgeM    = new Biome("Extreme Hills Edge M",      148, BiomeColor.from(114, 120, 154));
	public static final Biome jungleM              = new Biome("Jungle M",                  149, BiomeColor.from( 83, 123,   9));
	public static final Biome jungleHillsM         = new Biome("Jungle Hills M",            150, BiomeColor.from( 44,  66,   5));
	public static final Biome jungleEdgeM          = new Biome("Jungle Edge M",             151, BiomeColor.from( 98, 139,  23));
	public static final Biome deepOceanM           = new Biome("Deep Ocean M",              152, BiomeColor.from(  0,   0,  48));
	public static final Biome stoneBeachM          = new Biome("Stone Beach M",             153, BiomeColor.from(162, 162, 132));
	public static final Biome coldBeachM           = new Biome("Cold Beach M",              154, BiomeColor.from(250, 240, 192));
	public static final Biome birchForestM         = new Biome("Birch Forest M",            155, BiomeColor.from( 48, 116,  68));
	public static final Biome birchForestHillsM    = new Biome("Birch Forest Hills M",      156, BiomeColor.from( 31,  95,  50));
	public static final Biome roofedForestM        = new Biome("Roofed Forest M",           157, BiomeColor.from( 64,  81,  26));
	public static final Biome coldTaigaM           = new Biome("Cold Taiga M",              158, BiomeColor.from( 49,  85,  74));
	public static final Biome coldTaigaHillsM      = new Biome("Cold Taiga Hills M",        159, BiomeColor.from( 36,  63,  54));
	public static final Biome megaSpruceTaiga      = new Biome("Mega Spruce Taiga",         160, BiomeColor.from( 89, 102,  81));
	public static final Biome megaSpurceTaigaHills = new Biome("Mega Spruce Taiga (Hills)", 161, BiomeColor.from( 69,  79,  62));
	public static final Biome extremeHillsPlusM    = new Biome("Extreme Hills+ M",          162, BiomeColor.from( 80, 112,  80));
	public static final Biome savannaM             = new Biome("Savanna M",                 163, BiomeColor.from(189, 178,  95));
	public static final Biome savannaPlateauM      = new Biome("Savanna Plateau M",         164, BiomeColor.from(167, 157, 100));
	public static final Biome mesaBryce            = new Biome("Mesa (Bryce)",              165, BiomeColor.from(217,  69,  21));
	public static final Biome mesaPlateauFM        = new Biome("Mesa Plateau F M",          166, BiomeColor.from(176, 151, 101));
	public static final Biome mesaPlateauM         = new Biome("Mesa Plateau M",            167, BiomeColor.from(202, 140, 101));

	// The early-beta biomes didn't have an index number, so we're free 
	// to use any values we like (I'll start at 240), I will be matching
	// them by name strings tho, so think twice before renaming these.
	// http://minecraft.gamepedia.com/Biome/Before_Beta_1.8
	public static final Biome rainforestB          = new Biome("beta Rainforest",           240, BiomeColor.from(  0,  63,   1), BiomeType.DEFAULT_TERRAIN_DATA);	
	public static final Biome seasonalForestB      = new Biome("beta Seasonal Forest",      241, BiomeColor.from( 25,  79,  12), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome swamplandB           = new Biome("beta Swampland",            242, BiomeColor.from( 11, 107,  95), BiomeType.SWAMPLAND_TERRAIN_DATA);
	public static final Biome forestB              = new Biome("beta Forest",               243, BiomeColor.from( 12,  94,  11), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome shrublandB           = new Biome("beta Shrubland",            244, BiomeColor.from( 87, 118,  37), BiomeType.DEFAULT_TERRAIN_DATA);	
	public static final Biome savannaB             = new Biome("beta Savanna",              245, BiomeColor.from(139, 142,  64), BiomeType.PLAINS_TERRAIN_DATA);	
	public static final Biome plainsB              = new Biome("beta Plains",               246, BiomeColor.from(132, 171,  58), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome desertB              = new Biome("beta Desert",               247, BiomeColor.from(216, 210, 156), BiomeType.PLAINS_TERRAIN_DATA);
	public static final Biome taigaB               = new Biome("beta Taiga",                248, BiomeColor.from(134, 190, 143), BiomeType.TAIGA_TERRAIN_DATA);
	public static final Biome tundraB              = new Biome("beta Tundra",               249, BiomeColor.from(255, 255, 255), BiomeType.PLAINS_TERRAIN_DATA);
	// Ocean and frozen ocean are technically landscape features in the early betas, not biomes.
	public static final Biome fake_oceanB          = new Biome("beta Ocean",                250, BiomeColor.from(  0,   0, 112), BiomeType.OCEAN_TERRAIN_DATA);
	public static final Biome fake_frozenOceanB    = new Biome("beta Frozen Ocean",         251, BiomeColor.from(186, 197, 255), BiomeType.OCEAN_TERRAIN_DATA);
	// The following beta biomes aren't used. (Ice desert is never seen in Minecraft due to a bug)
	public static final Biome iceDesertB           = new Biome("beta Ice Desert",           252, BiomeColor.from(144, 144, 160), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome hellB                = new Biome("beta Hell",                 253, BiomeColor.from(255,   0,   0), BiomeType.DEFAULT_TERRAIN_DATA);
	public static final Biome skyB                 = new Biome("beta Sky",                  254, BiomeColor.from(128, 128, 255), BiomeType.DEFAULT_TERRAIN_DATA);
	
	// @formatter:on

	private static final BiomeIterable ITERABLE = new BiomeIterable();

	public static Iterable<Biome> allBiomes() {
		return ITERABLE;
	}

	public static Biome getByIndex(int index) throws UnknownBiomeIndexException {
		if (index < 0 || index >= biomes.length || biomes[index] == null) {
			throw new UnknownBiomeIndexException(
					"unsupported biome index detected: " + index);
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

	public static Biome getBetaBiomeByName(String name) {
		return biomeMap.get(BETABIOME_PREFIX + name);
	}
	
	public static boolean exists(String name) {
		return biomeMap.containsKey(name);
	}

	public static boolean isSpecialBiomeIndex(int index) {
		return index >= 128;
	}

	public static int compareByIndex(String name1, String name2) {
		return getByName(name1).getIndex() - getByName(name2).getIndex();
	}

	/** The name used when serializing, to prevent beta-biome naming collisions */
	private final String uniqueName;
	/** The name used by Minecraft, and displayed to the user */
	private final String userFriendlyName;
	private final int index;
	private final BiomeColor defaultColor;
	private final BiomeType type;
	private final boolean isBeta;

	/** Rare/modified biome constructor */
	public Biome(String name, int index, BiomeColor defaultColor) {
		this(name, index, defaultColor.createLightenedBiomeColor(),
				biomes[index - 128].type.getRare());
	}

	/** normal biome constructor */
	public Biome(String uniqueName, int index, BiomeColor defaultColor, BiomeType type) {
		
		this.uniqueName = uniqueName;
		this.isBeta = uniqueName.startsWith(BETABIOME_PREFIX);
		this.userFriendlyName = isBeta ? uniqueName.substring(BETABIOME_PREFIX.length()) : uniqueName;
		
		this.index = index;
		this.defaultColor = defaultColor;
		this.type = type;
		biomes[index] = this;
		biomeMap.put(uniqueName, this);
	}

	/** 
	 * Returns a name uniquely identifying the biome, for example Biome.desertB 
	 * will return "beta Desert" instead of "Desert". If you want the latter
	 * then use getUserFriendlyName().
	 **/
	public String getUniqueName() {
		return uniqueName;
	}

	/** 
	 * Returns a name appropriate for user interfaces, but not necessarily unique 
	 * since some of the beta biome names overlap with the normal biome names.
	 **/
	public String getUserFriendlyName() {
		return userFriendlyName;
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

	public boolean getIsBeta() {
		return isBeta;
	}
	
	@Override
	public String toString() {
		return "[Biome " + uniqueName + "]";
	}
}
