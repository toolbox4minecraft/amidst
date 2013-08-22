package amidst.minecraft;


import java.awt.Color;

import amidst.Util;

public class Biome {
	public static final int length = 23;
	public static final Biome[] biomes = new Biome[256];
	public static final Biome ocean               = new Biome("Ocean",                  0, Util.makeColor( 13,  51, 219));
	public static final Biome plains              = new Biome("Plains",                 1, Util.makeColor(104, 222, 104));
	public static final Biome desert              = new Biome("Desert",                 2, Util.makeColor(226, 242, 131));
	public static final Biome extremeHills        = new Biome("Extreme Hills",          3, Util.makeColor(171, 105,  34));
	public static final Biome forest              = new Biome("Forest",                 4, Util.makeColor( 40, 181,  22));
	public static final Biome taiga               = new Biome("Taiga",                  5, Util.makeColor( 32, 110,  22));
	public static final Biome swampland           = new Biome("Swampland",              6, Util.makeColor(108, 158,  79));
	public static final Biome river               = new Biome("River",                  7, Util.makeColor( 14, 127, 227));
	public static final Biome hell                = new Biome("Hell",                   8, Util.makeColor(143,  25,  10));
	public static final Biome sky                 = new Biome("Sky",                    9, Util.makeColor(209, 233, 235));
	public static final Biome frozenOcean         = new Biome("Frozen Ocean",          10, Util.makeColor( 70, 104, 199));
	public static final Biome frozenRiver         = new Biome("Frozen River",          11, Util.makeColor(171, 216, 255));
	public static final Biome icePlains           = new Biome("Ice Plains",            12, Util.makeColor(156, 214, 190));
	public static final Biome iceMountains        = new Biome("Ice Mountains",         13, Util.makeColor(151, 162, 130));
	public static final Biome mushroomIsland      = new Biome("Mushroom Island",       14, Util.makeColor(219, 196, 164));
	public static final Biome mushroomIslandShore = new Biome("Mushroom Island Shore", 15, Util.makeColor(242, 216, 179));
	public static final Biome beach               = new Biome("Beach",                 16, Util.makeColor(255, 254, 189));
	public static final Biome desertHills         = new Biome("Desert Hills",          17, Util.makeColor(230, 202,  78));
	public static final Biome forestHills         = new Biome("Forest Hills",          18, Util.makeColor( 89, 176,  32));
	public static final Biome taigaHills          = new Biome("Taiga Hills",           19, Util.makeColor( 66, 110,  22));
	public static final Biome extremeHillsEdge    = new Biome("Extreme Hills Edge",    20, Util.makeColor(186, 159,  39));
	public static final Biome jungle              = new Biome("Jungle",                21, Util.makeColor( 26,  87,  34));
	public static final Biome jungleHills         = new Biome("Jungle Hills",          22, Util.makeColor( 73, 105,  33));

	
	public String name;
	public int index;
	public int color;
	public Biome(String name, int index, int color) {
		biomes[index] = this;
		this.name = name;
		this.index = index;
		this.color = color;
	}
}
