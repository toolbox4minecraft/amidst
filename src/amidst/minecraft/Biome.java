package amidst.minecraft;

import java.awt.Color;
import java.util.HashMap;

import amidst.Options;
import amidst.Util;

public class Biome {
	public static final HashMap<String,Biome> biomeMap = new HashMap<String,Biome>();
	public static final BiomeType typeA = new BiomeType(0.1F, 0.2F);
	public static final BiomeType typeB = new BiomeType(-0.5F, 0.0F);
	public static final BiomeType typeC = new BiomeType(-1.0F, 0.1F);
	public static final BiomeType typeD = new BiomeType(-1.8F, 0.1F);
	public static final BiomeType typeE = new BiomeType(0.125F, 0.05F);
	public static final BiomeType typeF = new BiomeType(0.2F, 0.2F);
	public static final BiomeType typeG = new BiomeType(0.45F, 0.3F);
	public static final BiomeType typeH = new BiomeType(1.5F, 0.025F);
	public static final BiomeType typeI = new BiomeType(1.0F, 0.5F);
	public static final BiomeType typeJ = new BiomeType(0.0F, 0.025F);
	public static final BiomeType typeK = new BiomeType(0.1F, 0.8F);
	public static final BiomeType typeL = new BiomeType(0.2F, 0.3F);
	public static final BiomeType typeM = new BiomeType(-0.2F, 0.1F);
	
	public static final int length = 40;
	public static final Biome[] biomes = new Biome[256];
	public static final Biome ocean				= new Biome("Ocean",				  0, Util.makeColor(0, 0, 112),	   typeC);
	public static final Biome plains			   = new Biome("Plains",				 1, Util.makeColor(141, 179, 96),	typeA);
	public static final Biome desert			   = new Biome("Desert",				 2, Util.makeColor(250, 148, 24),	typeE);
	public static final Biome extremeHills		 = new Biome("Extreme Hills",		  3, Util.makeColor(96, 96, 96),	  typeI);
	public static final Biome forest			   = new Biome("Forest",				 4, Util.makeColor(5, 102, 33),	  typeA);
	public static final Biome taiga				= new Biome("Taiga",				  5, Util.makeColor(11, 102, 89),	 typeF);
	public static final Biome swampland			= new Biome("Swampland",			  6, Util.makeColor(7, 249, 178),	 typeM);
	public static final Biome river				= new Biome("River",				  7, Util.makeColor(0, 0, 255),	   typeB);
	public static final Biome hell				 = new Biome("Hell",				   8, Util.makeColor(255, 0, 0),	   typeA);
	public static final Biome sky				  = new Biome("Sky",					9, Util.makeColor(128, 128, 255),   typeA);
	public static final Biome frozenOcean		  = new Biome("Frozen Ocean",		  10, Util.makeColor(144, 144, 160),   typeC);
	public static final Biome frozenRiver		  = new Biome("Frozen River",		  11, Util.makeColor(160, 160, 255),   typeB);
	public static final Biome icePlains			= new Biome("Ice Plains",			12, Util.makeColor(255, 255, 255),   typeE);
	public static final Biome iceMountains		 = new Biome("Ice Mountains",		 13, Util.makeColor(160, 160, 160),   typeG);
	public static final Biome mushroomIsland	   = new Biome("Mushroom Island",	   14, Util.makeColor(255, 0, 255),	 typeL);
	public static final Biome mushroomIslandShore  = new Biome("Mushroom Island Shore", 15, Util.makeColor(160, 0, 255),	 typeJ);
	public static final Biome beach				= new Biome("Beach",				 16, Util.makeColor(250, 222, 85),	typeJ);
	public static final Biome desertHills		  = new Biome("Desert Hills",		  17, Util.makeColor(210, 95, 18),	 typeG);
	public static final Biome forestHills		  = new Biome("Forest Hills",		  18, Util.makeColor(34, 85, 28),	  typeG);
	public static final Biome taigaHills		   = new Biome("Taiga Hills",		   19, Util.makeColor(22, 57, 51),	  typeG);
	public static final Biome extremeHillsEdge	 = new Biome("Extreme Hills Edge",	20, Util.makeColor(114, 120, 154),   typeI.getExtreme());
	public static final Biome jungle			   = new Biome("Jungle",				21, Util.makeColor(83, 123, 9),	  typeA);
	public static final Biome jungleHills		  = new Biome("Jungle Hills",		  22, Util.makeColor(44, 66, 5),	   typeG);
	public static final Biome jungleEdge		   = new Biome("Jungle Edge",		   23, Util.makeColor(98, 139, 23),	 typeA);
	public static final Biome deepOcean			= new Biome("Deep Ocean",			24, Util.makeColor(0, 0, 48),		typeD);
	public static final Biome stoneBeach		   = new Biome("Stone Beach",		   25, Util.makeColor(162, 162, 132),   typeK);
	public static final Biome coldBeach			= new Biome("Cold Beach",			26, Util.makeColor(250, 240, 192),   typeJ);
	public static final Biome birchForest		  = new Biome("Birch Forest",		  27, Util.makeColor(48, 116, 68),	 typeA);
	public static final Biome birchForestHills	 = new Biome("Birch Forest Hills",	28, Util.makeColor(31, 95, 50),	  typeG);
	public static final Biome roofedForest		 = new Biome("Roofed Forest",		 29, Util.makeColor(64, 81, 26),	  typeA);
	public static final Biome coldTaiga			= new Biome("Cold Taiga",			30, Util.makeColor(49, 85, 74),	  typeF);
	public static final Biome coldTaigaHills	   = new Biome("Cold Taiga Hills",	  31, Util.makeColor(36, 63, 54),	  typeG);
	public static final Biome megaTaiga			= new Biome("Mega Taiga",			32, Util.makeColor(89, 102, 81),	 typeF);
	public static final Biome megaTaigaHills	   = new Biome("Mega Taiga Hills",	  33, Util.makeColor(69, 79, 62),	  typeG);
	public static final Biome extremeHillsPlus	 = new Biome("Extreme Hills+",		34, Util.makeColor(80, 112, 80),	 typeI);
	public static final Biome savanna			  = new Biome("Savanna",			   35, Util.makeColor(189, 178, 95),	typeE);
	public static final Biome savannaPlateau	   = new Biome("Savanna Plateau",	   36, Util.makeColor(167, 157, 100),   typeH);
	public static final Biome mesa				 = new Biome("Mesa",				  37, Util.makeColor(217, 69, 21),	 typeA);
	public static final Biome mesaPlateauF		 = new Biome("Mesa Plateau F",		38, Util.makeColor(176, 151, 101),   typeH);
	public static final Biome mesaPlateau		  = new Biome("Mesa Plateau",		  39, Util.makeColor(202, 140, 101),   typeH);
	
	
	public static final Biome oceanM			   = new Biome("Ocean M",				  128, Util.makeColor(0, 0, 112));
	public static final Biome sunflowerPlains	  = new Biome("Sunflower Plains",		 129, Util.makeColor(141, 179, 96));
	public static final Biome desertM			  = new Biome("Desert M",				 130, Util.makeColor(250, 148, 24));
	public static final Biome extremeHillsM		= new Biome("Extreme Hills M",		  131, Util.makeColor(96, 96, 96));
	public static final Biome flowerForest		 = new Biome("Flower Forest",			132, Util.makeColor(5, 102, 33));
	public static final Biome taigaM			   = new Biome("Taiga M",				  133, Util.makeColor(11, 102, 89));
	public static final Biome swamplandM		   = new Biome("Swampland M",			  134, Util.makeColor(7, 249, 178));
	public static final Biome riverM			   = new Biome("River M",				  135, Util.makeColor(0, 0, 255));
	public static final Biome hellM				= new Biome("Hell M",				   136, Util.makeColor(255, 0, 0));
	public static final Biome skyM				 = new Biome("Sky M",					137, Util.makeColor(128, 128, 255));
	public static final Biome frozenOceanM		 = new Biome("Frozen Ocean M",		   138, Util.makeColor(144, 144, 160));
	public static final Biome frozenRiverM		 = new Biome("Frozen River M",		   139, Util.makeColor(160, 160, 255));
	public static final Biome icePlainsSpikes	  = new Biome("Ice Plains Spikes",		140, Util.makeColor(140, 180, 180));
	public static final Biome iceMountainsM		= new Biome("Ice Mountains M",		  141, Util.makeColor(160, 160, 160));
	public static final Biome mushroomIslandM	  = new Biome("Mushroom Island M",		142, Util.makeColor(255, 0, 255));
	public static final Biome mushroomIslandShoreM = new Biome("Mushroom Island Shore M",  143, Util.makeColor(160, 0, 255));
	public static final Biome beachM			   = new Biome("Beach M",				  144, Util.makeColor(250, 222, 85));
	public static final Biome desertHillsM		 = new Biome("Desert Hills M",		   145, Util.makeColor(210, 95, 18));
	public static final Biome forestHillsM		 = new Biome("Forest Hills M",		   146, Util.makeColor(34, 85, 28));
	public static final Biome taigaHillsM		  = new Biome("Taiga Hills M",			147, Util.makeColor(22, 57, 51));
	public static final Biome extremeHillsEdgeM	= new Biome("Extreme Hills Edge M",	 148, Util.makeColor(114, 120, 154));
	public static final Biome jungleM			  = new Biome("Jungle M",				 149, Util.makeColor(83, 123, 9));
	public static final Biome jungleHillsM		 = new Biome("Jungle Hills M",		   150, Util.makeColor(44, 66, 5));
	public static final Biome jungleEdgeM		  = new Biome("Jungle Edge M",			151, Util.makeColor(98, 139, 23));
	public static final Biome deepOceanM		   = new Biome("Deep Ocean M",			 152, Util.makeColor(0, 0, 48));
	public static final Biome stoneBeachM		  = new Biome("Stone Beach M",			153, Util.makeColor(162, 162, 132));
	public static final Biome coldBeachM		   = new Biome("Cold Beach M",			 154, Util.makeColor(250, 240, 192));
	public static final Biome birchForestM		 = new Biome("Birch Forest M",		   155, Util.makeColor(48, 116, 68));
	public static final Biome birchForestHillsM	= new Biome("Birch Forest Hills M",	 156, Util.makeColor(31, 95, 50));
	public static final Biome roofedForestM		= new Biome("Roofed Forest M",		  157, Util.makeColor(64, 81, 26));
	public static final Biome coldTaigaM		   = new Biome("Cold Taiga M",			 158, Util.makeColor(49, 85, 74));
	public static final Biome coldTaigaHillsM	  = new Biome("Cold Taiga Hills M",	   159, Util.makeColor(36, 63, 54));
	public static final Biome megaSpruceTaiga	  = new Biome("Mega Spruce Taiga",		160, Util.makeColor(89, 102, 81));
	public static final Biome megaSpurceTaigaHills = new Biome("Mega Spruce Taiga (Hills)",161, Util.makeColor(69, 79, 62));
	public static final Biome extremeHillsPlusM	= new Biome("Extreme Hills+ M",		 162, Util.makeColor(80, 112, 80));
	public static final Biome savannaM			 = new Biome("Savanna M",				163, Util.makeColor(189, 178, 95));
	public static final Biome savannaPlateauM	  = new Biome("Savanna Plateau M",		164, Util.makeColor(167, 157, 100));
	public static final Biome mesaBryce			= new Biome("Mesa (Bryce)",			 165, Util.makeColor(217, 69, 21));
	public static final Biome mesaPlateauFM		= new Biome("Mesa Plateau F M",		 166, Util.makeColor(176, 151, 101));
	public static final Biome mesaPlateauM		 = new Biome("Mesa Plateau M",		   167, Util.makeColor(202, 140, 101));
	
	
	public String name;
	public int index;
	public int color;
	public BiomeType type;
	
	public Biome(String name, int index, int color) {
		this(name, index, color, biomes[index - 128].type.getRare());
	}
	public Biome(String name, int index, int color, BiomeType type) {
		biomes[index] = this;
		this.name = name;
		this.index = index;
		this.color = color;
		this.type = type;
		biomeMap.put(name, this);
		
		if (index >= 128)
			this.color = Util.lightenColor(color, 40);
	}
	
	public String toString() {
		return "[Biome " + name + "]";
	}
	
	public static int indexFromName(String name) {
		Biome biome = biomeMap.get(name);
		if (biome != null)
			return biome.index;
		return -1;
	}
	
	public static final class BiomeType { // TODO: Rename once we figure out what this actually is!
		public float value1, value2;
		public BiomeType(float value1, float value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		public BiomeType getExtreme() {
			return new BiomeType(value1 * 0.8F, value2 * 0.6F);
		}
		public BiomeType getRare(){ 
			return new BiomeType(value1 + 0.1F, value2 + 0.2F);
		}
	}

}
