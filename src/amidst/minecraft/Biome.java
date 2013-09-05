package amidst.minecraft;


import java.awt.Color;

import amidst.Util;

public class Biome {
	public static final int length = 40;
	public static final Biome[] biomes = new Biome[256];
	public static final Biome ocean               = new Biome("Ocean",                  0, Util.mcColor(112));
	public static final Biome plains              = new Biome("Plains",                 1, Util.mcColor(9286496));
	public static final Biome desert              = new Biome("Desert",                 2, Util.mcColor(16421912));
	public static final Biome extremeHills        = new Biome("Extreme Hills",          3, Util.mcColor(6316128));
	public static final Biome forest              = new Biome("Forest",                 4, Util.mcColor(353825));
	public static final Biome taiga               = new Biome("Taiga",                  5, Util.mcColor(747097));
	public static final Biome swampland           = new Biome("Swampland",              6, Util.mcColor(522674));
	public static final Biome river               = new Biome("River",                  7, Util.mcColor(255));
	public static final Biome hell                = new Biome("Hell",                   8, Util.mcColor(16711680));
	public static final Biome sky                 = new Biome("Sky",                    9, Util.mcColor(8421631));
	public static final Biome frozenOcean         = new Biome("Frozen Ocean",          10, Util.mcColor(9474208));
	public static final Biome frozenRiver         = new Biome("Frozen River",          11, Util.mcColor(10526975));
	public static final Biome icePlains           = new Biome("Ice Plains",            12, Util.mcColor(16777215));
	public static final Biome iceMountains        = new Biome("Ice Mountains",         13, Util.mcColor(10526880));
	public static final Biome mushroomIsland      = new Biome("Mushroom Island",       14, Util.mcColor(16711935));
	public static final Biome mushroomIslandShore = new Biome("Mushroom Island Shore", 15, Util.mcColor(10486015));
	public static final Biome beach               = new Biome("Beach",                 16, Util.mcColor(16440917));
	public static final Biome desertHills         = new Biome("Desert Hills",          17, Util.mcColor(13786898));
	public static final Biome forestHills         = new Biome("Forest Hills",          18, Util.mcColor(2250012));
	public static final Biome taigaHills          = new Biome("Taiga Hills",           19, Util.mcColor(1456435));
	public static final Biome extremeHillsEdge    = new Biome("Extreme Hills Edge",    20, Util.mcColor(7501978));
	public static final Biome jungle              = new Biome("Jungle",                21, Util.mcColor(5470985));
	public static final Biome jungleHills         = new Biome("Jungle Hills",          22, Util.mcColor(2900485));
	public static final Biome jungleEdge          = new Biome("Jungle Edge",           23, Util.mcColor(6458135));
	public static final Biome deepOcean           = new Biome("Deep Ocean",            24, Util.mcColor(48));
	public static final Biome stoneBeach          = new Biome("Stone Beach",           25, Util.mcColor(10658436));
	public static final Biome coldBeach           = new Biome("Cold Beach",            26, Util.mcColor(16445632));
	public static final Biome birchForest         = new Biome("Birch Forest",          27, Util.mcColor(3175492));
	public static final Biome birchForestHills    = new Biome("Birch Forest Hills",    28, Util.mcColor(2055986));
	public static final Biome roofedForest        = new Biome("Roofed Forest",         29, Util.mcColor(4215066));
	public static final Biome coldTaiga           = new Biome("Cold Taiga",            30, Util.mcColor(3233098));
	public static final Biome coldTaigaHills      = new Biome("Cold Taiga Hills",      31, Util.mcColor(2375478));
	public static final Biome megaTaiga           = new Biome("Mega Taiga",            32, Util.mcColor(5858897));
	public static final Biome megaTaigaHills      = new Biome("Mega Taiga Hills",      33, Util.mcColor(4542270));
	public static final Biome extremeHillsPlus    = new Biome("Extreme Hills+",        34, Util.mcColor(5271632));
	public static final Biome savanna             = new Biome("Savanna",               35, Util.mcColor(12431967));
	public static final Biome savannaPlateau      = new Biome("Savanna Plateau",       36, Util.mcColor(10984804));
	public static final Biome mesa                = new Biome("Mesa",                  37, Util.mcColor(14238997));
	public static final Biome mesaPlateauF        = new Biome("Mesa Plateau F",        38, Util.mcColor(11573093));
	public static final Biome mesaPlateau         = new Biome("Mesa Plateau",          39, Util.mcColor(13274213));
	
	
	public String name;
	public int index;
	public int color;
	public Biome(String name, int index, int color) {
		biomes[index] = this;
		biomes[index + 128] = this;
		this.name = name;
		this.index = index;
		this.color = color;
	}
}
