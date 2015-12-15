package amidst.mojangapi.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import amidst.documentation.Immutable;
import amidst.documentation.NotThreadSafe;
import amidst.utilities.ColorUtils;

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
	
	public static final Biome ocean				   = new Biome("Ocean",				     0, ColorUtils.makeColor(0, 0, 112),	     BiomeType.typeC);
	public static final Biome plains			   = new Biome("Plains",				 1, ColorUtils.makeColor(141, 179, 96),	     BiomeType.typeA);
	public static final Biome desert			   = new Biome("Desert",				 2, ColorUtils.makeColor(250, 148, 24),	     BiomeType.typeE);
	public static final Biome extremeHills		   = new Biome("Extreme Hills",		     3, ColorUtils.makeColor(96, 96, 96),	     BiomeType.typeI);
	public static final Biome forest			   = new Biome("Forest",				 4, ColorUtils.makeColor(5, 102, 33),	     BiomeType.typeA);
	public static final Biome taiga			  	   = new Biome("Taiga",				     5, ColorUtils.makeColor(11, 102, 89),	     BiomeType.typeF);
	public static final Biome swampland			   = new Biome("Swampland",			     6, ColorUtils.makeColor(7, 249, 178),	     BiomeType.typeM);
	public static final Biome river				   = new Biome("River",				     7, ColorUtils.makeColor(0, 0, 255),	     BiomeType.typeB);
	public static final Biome hell				   = new Biome("Hell",				     8, ColorUtils.makeColor(255, 0, 0),	     BiomeType.typeA);
	public static final Biome sky				   = new Biome("Sky",					 9, ColorUtils.makeColor(128, 128, 255),     BiomeType.typeA);
	public static final Biome frozenOcean		   = new Biome("Frozen Ocean",		    10, ColorUtils.makeColor(144, 144, 160),     BiomeType.typeC);
	public static final Biome frozenRiver		   = new Biome("Frozen River",		    11, ColorUtils.makeColor(160, 160, 255),     BiomeType.typeB);
	public static final Biome icePlains			   = new Biome("Ice Plains",			12, ColorUtils.makeColor(255, 255, 255),     BiomeType.typeE);
	public static final Biome iceMountains		   = new Biome("Ice Mountains",		    13, ColorUtils.makeColor(160, 160, 160),     BiomeType.typeG);
	public static final Biome mushroomIsland	   = new Biome("Mushroom Island",	    14, ColorUtils.makeColor(255, 0, 255),	     BiomeType.typeL);
	public static final Biome mushroomIslandShore  = new Biome("Mushroom Island Shore", 15, ColorUtils.makeColor(160, 0, 255),	     BiomeType.typeJ);
	public static final Biome beach				   = new Biome("Beach",				    16, ColorUtils.makeColor(250, 222, 85),	     BiomeType.typeJ);
	public static final Biome desertHills		   = new Biome("Desert Hills",		    17, ColorUtils.makeColor(210, 95, 18),	     BiomeType.typeG);
	public static final Biome forestHills		   = new Biome("Forest Hills",		    18, ColorUtils.makeColor(34, 85, 28),	     BiomeType.typeG);
	public static final Biome taigaHills		   = new Biome("Taiga Hills",		    19, ColorUtils.makeColor(22, 57, 51),	     BiomeType.typeG);
	public static final Biome extremeHillsEdge	   = new Biome("Extreme Hills Edge",	20, ColorUtils.makeColor(114, 120, 154),     BiomeType.typeI.getExtreme());
	public static final Biome jungle			   = new Biome("Jungle",				21, ColorUtils.makeColor(83, 123, 9),	     BiomeType.typeA);
	public static final Biome jungleHills		   = new Biome("Jungle Hills",		    22, ColorUtils.makeColor(44, 66, 5),	     BiomeType.typeG);
	public static final Biome jungleEdge		   = new Biome("Jungle Edge",		    23, ColorUtils.makeColor(98, 139, 23),	     BiomeType.typeA);
	public static final Biome deepOcean			   = new Biome("Deep Ocean",			24, ColorUtils.makeColor(0, 0, 48),		     BiomeType.typeD);
	public static final Biome stoneBeach		   = new Biome("Stone Beach",		    25, ColorUtils.makeColor(162, 162, 132),     BiomeType.typeK);
	public static final Biome coldBeach			   = new Biome("Cold Beach",			26, ColorUtils.makeColor(250, 240, 192),     BiomeType.typeJ);
	public static final Biome birchForest		   = new Biome("Birch Forest",		    27, ColorUtils.makeColor(48, 116, 68),	     BiomeType.typeA);
	public static final Biome birchForestHills	   = new Biome("Birch Forest Hills",	28, ColorUtils.makeColor(31, 95, 50),	     BiomeType.typeG);
	public static final Biome roofedForest		   = new Biome("Roofed Forest",		    29, ColorUtils.makeColor(64, 81, 26),	     BiomeType.typeA);
	public static final Biome coldTaiga			   = new Biome("Cold Taiga",			30, ColorUtils.makeColor(49, 85, 74),	     BiomeType.typeF);
	public static final Biome coldTaigaHills	   = new Biome("Cold Taiga Hills",	    31, ColorUtils.makeColor(36, 63, 54),	     BiomeType.typeG);
	public static final Biome megaTaiga			   = new Biome("Mega Taiga",			32, ColorUtils.makeColor(89, 102, 81),	     BiomeType.typeF);
	public static final Biome megaTaigaHills	   = new Biome("Mega Taiga Hills",	    33, ColorUtils.makeColor(69, 79, 62),	     BiomeType.typeG);
	public static final Biome extremeHillsPlus	   = new Biome("Extreme Hills+",		34, ColorUtils.makeColor(80, 112, 80),	     BiomeType.typeI);
	public static final Biome savanna			   = new Biome("Savanna",			    35, ColorUtils.makeColor(189, 178, 95),	     BiomeType.typeE);
	public static final Biome savannaPlateau	   = new Biome("Savanna Plateau",	    36, ColorUtils.makeColor(167, 157, 100),     BiomeType.typeH);
	public static final Biome mesa				   = new Biome("Mesa",				    37, ColorUtils.makeColor(217, 69, 21),	     BiomeType.typeA);
	public static final Biome mesaPlateauF		   = new Biome("Mesa Plateau F",		38, ColorUtils.makeColor(176, 151, 101),     BiomeType.typeH);
	public static final Biome mesaPlateau		   = new Biome("Mesa Plateau",		    39, ColorUtils.makeColor(202, 140, 101),     BiomeType.typeH);
	
	public static final Biome oceanM			   = new Biome("Ocean M",				   128, ColorUtils.makeColor(0, 0, 112));
	public static final Biome sunflowerPlains	   = new Biome("Sunflower Plains",		   129, ColorUtils.makeColor(141, 179, 96));
	public static final Biome desertM			   = new Biome("Desert M",				   130, ColorUtils.makeColor(250, 148, 24));
	public static final Biome extremeHillsM		   = new Biome("Extreme Hills M",		   131, ColorUtils.makeColor(96, 96, 96));
	public static final Biome flowerForest		   = new Biome("Flower Forest",			   132, ColorUtils.makeColor(5, 102, 33));
	public static final Biome taigaM			   = new Biome("Taiga M",				   133, ColorUtils.makeColor(11, 102, 89));
	public static final Biome swamplandM		   = new Biome("Swampland M",			   134, ColorUtils.makeColor(7, 249, 178));
	public static final Biome riverM			   = new Biome("River M",				   135, ColorUtils.makeColor(0, 0, 255));
	public static final Biome hellM				   = new Biome("Hell M",				   136, ColorUtils.makeColor(255, 0, 0));
	public static final Biome skyM				   = new Biome("Sky M",					   137, ColorUtils.makeColor(128, 128, 255));
	public static final Biome frozenOceanM		   = new Biome("Frozen Ocean M",		   138, ColorUtils.makeColor(144, 144, 160));
	public static final Biome frozenRiverM		   = new Biome("Frozen River M",		   139, ColorUtils.makeColor(160, 160, 255));
	public static final Biome icePlainsSpikes	   = new Biome("Ice Plains Spikes",		   140, ColorUtils.makeColor(140, 180, 180));
	public static final Biome iceMountainsM		   = new Biome("Ice Mountains M",		   141, ColorUtils.makeColor(160, 160, 160));
	public static final Biome mushroomIslandM	   = new Biome("Mushroom Island M",		   142, ColorUtils.makeColor(255, 0, 255));
	public static final Biome mushroomIslandShoreM = new Biome("Mushroom Island Shore M",  143, ColorUtils.makeColor(160, 0, 255));
	public static final Biome beachM			   = new Biome("Beach M",				   144, ColorUtils.makeColor(250, 222, 85));
	public static final Biome desertHillsM		   = new Biome("Desert Hills M",		   145, ColorUtils.makeColor(210, 95, 18));
	public static final Biome forestHillsM		   = new Biome("Forest Hills M",		   146, ColorUtils.makeColor(34, 85, 28));
	public static final Biome taigaHillsM		   = new Biome("Taiga Hills M",			   147, ColorUtils.makeColor(22, 57, 51));
	public static final Biome extremeHillsEdgeM	   = new Biome("Extreme Hills Edge M",	   148, ColorUtils.makeColor(114, 120, 154));
	public static final Biome jungleM			   = new Biome("Jungle M",				   149, ColorUtils.makeColor(83, 123, 9));
	public static final Biome jungleHillsM		   = new Biome("Jungle Hills M",		   150, ColorUtils.makeColor(44, 66, 5));
	public static final Biome jungleEdgeM		   = new Biome("Jungle Edge M",			   151, ColorUtils.makeColor(98, 139, 23));
	public static final Biome deepOceanM		   = new Biome("Deep Ocean M",			   152, ColorUtils.makeColor(0, 0, 48));
	public static final Biome stoneBeachM		   = new Biome("Stone Beach M",			   153, ColorUtils.makeColor(162, 162, 132));
	public static final Biome coldBeachM		   = new Biome("Cold Beach M",			   154, ColorUtils.makeColor(250, 240, 192));
	public static final Biome birchForestM		   = new Biome("Birch Forest M",		   155, ColorUtils.makeColor(48, 116, 68));
	public static final Biome birchForestHillsM	   = new Biome("Birch Forest Hills M",	   156, ColorUtils.makeColor(31, 95, 50));
	public static final Biome roofedForestM		   = new Biome("Roofed Forest M",		   157, ColorUtils.makeColor(64, 81, 26));
	public static final Biome coldTaigaM		   = new Biome("Cold Taiga M",			   158, ColorUtils.makeColor(49, 85, 74));
	public static final Biome coldTaigaHillsM	   = new Biome("Cold Taiga Hills M",	   159, ColorUtils.makeColor(36, 63, 54));
	public static final Biome megaSpruceTaiga	   = new Biome("Mega Spruce Taiga",		   160, ColorUtils.makeColor(89, 102, 81));
	public static final Biome megaSpurceTaigaHills = new Biome("Mega Spruce Taiga (Hills)",161, ColorUtils.makeColor(69, 79, 62));
	public static final Biome extremeHillsPlusM	   = new Biome("Extreme Hills+ M",		   162, ColorUtils.makeColor(80, 112, 80));
	public static final Biome savannaM			   = new Biome("Savanna M",				   163, ColorUtils.makeColor(189, 178, 95));
	public static final Biome savannaPlateauM	   = new Biome("Savanna Plateau M",		   164, ColorUtils.makeColor(167, 157, 100));
	public static final Biome mesaBryce			   = new Biome("Mesa (Bryce)",			   165, ColorUtils.makeColor(217, 69, 21));
	public static final Biome mesaPlateauFM		   = new Biome("Mesa Plateau F M",		   166, ColorUtils.makeColor(176, 151, 101));
	public static final Biome mesaPlateauM		   = new Biome("Mesa Plateau M",		   167, ColorUtils.makeColor(202, 140, 101));
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

	public static boolean exists(String name) {
		return biomeMap.containsKey(name);
	}

	public static boolean isSpecialBiomeIndex(int index) {
		return index >= 128;
	}

	private final String name;
	private final int index;
	private final int defaultColor;
	private final BiomeType type;

	public Biome(String name, int index, int defaultColor) {
		this(name, index, defaultColor, biomes[index - 128].type.getRare());
	}

	public Biome(String name, int index, int defaultColor, BiomeType type) {
		this.name = name;
		this.index = index;
		this.defaultColor = lightenColorIfNecessary(index, defaultColor);
		this.type = type;
		biomes[index] = this;
		biomeMap.put(name, this);
	}

	private int lightenColorIfNecessary(int index, int color) {
		if (index >= 128) {
			return ColorUtils.lightenColor(color, 40);
		} else {
			return color;
		}
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public int getDefaultColor() {
		return defaultColor;
	}

	public BiomeType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
