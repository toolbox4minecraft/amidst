package amidst.minetest.world.mapgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.biome.BiomeColor;

/**
 * Generates a list of all the biomes that MineClone 2 uses 
 */
public class MineCloneBiomes {

	private static final short OVERWORLD_MIN  =   -62;
	private static final short OVERWORLD_MAX  = 31000;
	private static final short OCEAN_MIN      =   -15;
	private static final short DEEP_OCEAN_MIN =   -31;
	private static final short DEEP_OCEAN_MAX = OCEAN_MIN - 1;

	private static final BiomeColor BEACH_BLEND_COLOR      = BiomeColor.from(232, 224, 171); // sand
	private static final BiomeColor OCEAN_BLEND_COLOR      = BiomeColor.from(214, 207, 158); // sand floor
	private static final BiomeColor DEEP_OCEAN_BLEND_COLOR = BiomeColor.from(190, 190, 190); // gravel floor
	
	private static Map<String, BiomeColor> baseBiomes = null;
	
	private enum ColorModifier {
		None,
		Beach,
		Ocean,
		DeepOcean
	}
	
	private static BiomeColor getColor(String baseBiomeName, ColorModifier modifier) {

		BiomeColor baseColor = baseBiomes.get(baseBiomeName);		
		if (baseColor == null) {
			AmidstLogger.error("Base MineClone biome not found: " + baseBiomeName);
			return BiomeColor.error();
		}
		
		switch(modifier) {
			case Beach:     return baseColor.blend(0.83f,  BEACH_BLEND_COLOR);
			case Ocean:     return baseColor.blend(0.83f,  OCEAN_BLEND_COLOR);
			case DeepOcean: return baseColor.blend(0.83f,  DEEP_OCEAN_BLEND_COLOR);						
			default:        return baseColor;
		}
	}
		
	public static MinetestBiome[] getBiomeList() {
		
		if (baseBiomes == null) {
			baseBiomes = new LinkedHashMap<String, BiomeColor>();
			baseBiomes.put("IcePlains",       BiomeColor.from(255, 255, 255));
			baseBiomes.put("IcePlainsSpikes", BiomeColor.from(140, 180, 180));
			baseBiomes.put("ColdTaiga",       BiomeColor.from( 49,  85,  74));
			baseBiomes.put("ExtremeHills",    BiomeColor.from( 96,  96,  96));
			baseBiomes.put("ExtremeHillsM",   BiomeColor.from( 76,  76,  76));
			baseBiomes.put("ExtremeHills+",   BiomeColor.from(114, 120, 154));
			baseBiomes.put("Taiga",           BiomeColor.from( 11, 102,  89));
			baseBiomes.put("MegaTaiga",       BiomeColor.from( 49,  85,  74));
			baseBiomes.put("MegaSpruceTaiga", BiomeColor.from( 89, 102,  81));
			baseBiomes.put("StoneBeach",      BiomeColor.from(162, 162, 132));
			baseBiomes.put("Plains",          BiomeColor.from(141, 179,  96));
			baseBiomes.put("SunflowerPlains", BiomeColor.from(141, 179,  96));
			baseBiomes.put("Forest",          BiomeColor.from(  5, 102,  33));
			baseBiomes.put("FlowerForest",    BiomeColor.from( 32, 175, 180));
			baseBiomes.put("BirchForest",     BiomeColor.from( 48, 116,  68));
			baseBiomes.put("BirchForestM",    BiomeColor.from( 38, 106,  58));
			baseBiomes.put("RoofedForest",    BiomeColor.from( 64,  81,  26));
			baseBiomes.put("Swampland",       BiomeColor.from(  7, 249, 178));
			baseBiomes.put("Jungle",          BiomeColor.from( 83, 123,   9));
			baseBiomes.put("JungleM",         BiomeColor.from( 73, 113,   6));
			baseBiomes.put("JungleEdge",      BiomeColor.from( 98, 139,  23));
			baseBiomes.put("JungleEdgeM",     BiomeColor.from( 88, 129,  18));
			baseBiomes.put("MushroomIsland",  BiomeColor.from(255,   0, 255));
			baseBiomes.put("Desert",          BiomeColor.from(250, 148,  24));
			baseBiomes.put("Savanna",         BiomeColor.from(189, 178,  95));
			baseBiomes.put("SavannaM",        BiomeColor.from(167, 157, 100));
			baseBiomes.put("Mesa",            BiomeColor.from(217,  69,  21));
			baseBiomes.put("MesaPlateauF",    BiomeColor.from(176, 151, 101));
		}		
		
		ArrayList<MinetestBiome> biomes = new ArrayList<MinetestBiome>(
			Arrays.asList(		
				new MinetestBiome("IcePlainsSpikes",        getColor("IcePlainsSpikes",     ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  -5,  24),
				new MinetestBiome("IcePlainsSpikes ocean",  getColor("IcePlainsSpikes",     ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  -5,  24),
				new MinetestBiome("ColdTaiga",              getColor("ColdTaiga",           ColorModifier.None),  (short)         3, (short)  OVERWORLD_MAX,   8,  58),
				new MinetestBiome("ColdTaiga beach",        getColor("ColdTaiga",           ColorModifier.Beach), (short)         1, (short)              2,   8,  58),
				new MinetestBiome("ColdTaiga beach water",  getColor("ColdTaiga",           ColorModifier.Ocean), (short)        -3, (short)              0,   8,  58),
				new MinetestBiome("ColdTaiga ocean",        getColor("ColdTaiga",           ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -4,   8,  58),
				new MinetestBiome("MegaTaiga",              getColor("MegaTaiga",           ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  10,  76),
				new MinetestBiome("MegaTaiga ocean",        getColor("MegaTaiga",           ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  10,  76),
				new MinetestBiome("MegaSpruceTaiga",        getColor("MegaSpruceTaiga",     ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,   8, 100),
				new MinetestBiome("MegaSpruceTaiga ocean",  getColor("MegaSpruceTaiga",     ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,   8, 100),
				new MinetestBiome("ExtremeHills",           getColor("ExtremeHills",        ColorModifier.None),  (short)         4, (short)  OVERWORLD_MAX,  45,  10),
				new MinetestBiome("ExtremeHills beach",     getColor("ExtremeHills",        ColorModifier.Beach), (short)        -3, (short)              3,  45,  10),
				new MinetestBiome("ExtremeHills ocean",     getColor("ExtremeHills",        ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -4,  45,  10),
				new MinetestBiome("ExtremeHillsM",          getColor("ExtremeHillsM",       ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  25,   0),
				new MinetestBiome("ExtremeHillsM ocean",    getColor("ExtremeHillsM",       ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  25,   0),
				new MinetestBiome("ExtremeHills+",          getColor("ExtremeHills+",       ColorModifier.None),  (short)         1, (short)             44,  25,  24),
				new MinetestBiome("ExtremeHills+ snowtop",  BiomeColor.from(230, 230, 255),                       (short)        45, (short)  OVERWORLD_MAX,  25,  24),
				new MinetestBiome("ExtremeHills+ ocean",    getColor("ExtremeHills+",       ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  25,  24),
				new MinetestBiome("StoneBeach",             getColor("StoneBeach",          ColorModifier.None),  (short)        -6, (short)  OVERWORLD_MAX,   8,   0),
				new MinetestBiome("StoneBeach ocean",       getColor("StoneBeach",          ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -7,   8,   0),
				new MinetestBiome("IcePlains",              getColor("IcePlains",           ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,   8,  24),
				new MinetestBiome("IcePlains ocean",        getColor("IcePlains",           ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,   8,  24),
				new MinetestBiome("Plains",                 getColor("Plains",              ColorModifier.None),  (short)         3, (short)  OVERWORLD_MAX,  58,  39),
				new MinetestBiome("Plains beach",           getColor("Plains",              ColorModifier.Beach), (short)         0, (short)              2,  58,  39),
				new MinetestBiome("Plains ocean",           getColor("Plains",              ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -1,  58,  39),
				new MinetestBiome("SunflowerPlains",        getColor("SunflowerPlains",     ColorModifier.None),  (short)         4, (short)  OVERWORLD_MAX,  45,  28),
				new MinetestBiome("SunflowerPlains ocean",  getColor("SunflowerPlains",     ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  45,  28),
				new MinetestBiome("Taiga",                  getColor("Taiga",               ColorModifier.None),  (short)         4, (short)  OVERWORLD_MAX,  22,  58),
				new MinetestBiome("Taiga beach",            getColor("Taiga",               ColorModifier.Beach), (short)         1, (short)              3,  22,  58),
				new MinetestBiome("Taiga ocean",            getColor("Taiga",               ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  22,  58),
				new MinetestBiome("Forest",                 getColor("Forest",              ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  45,  61),
				new MinetestBiome("Forest beach",           getColor("Forest",              ColorModifier.Beach), (short)        -1, (short)              0,  45,  61),
				new MinetestBiome("Forest ocean",           getColor("Forest",              ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -2,  45,  61),
				new MinetestBiome("FlowerForest",           getColor("FlowerForest",        ColorModifier.None),  (short)         3, (short)  OVERWORLD_MAX,  32,  44),
				new MinetestBiome("FlowerForest beach",     getColor("FlowerForest",        ColorModifier.Beach), (short)        -2, (short)              2,  32,  44),
				new MinetestBiome("FlowerForest ocean",     getColor("FlowerForest",        ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -3,  32,  44),
				new MinetestBiome("BirchForest",            getColor("BirchForest",         ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  31,  78),
				new MinetestBiome("BirchForest ocean",      getColor("BirchForest",         ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  31,  78),
				new MinetestBiome("BirchForestM",           getColor("BirchForestM",        ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  27,  77),
				new MinetestBiome("BirchForestM ocean",     getColor("BirchForestM",        ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  27,  77),
				new MinetestBiome("Desert",                 getColor("Desert",              ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  94,  26),
				new MinetestBiome("Desert ocean",           getColor("Desert",              ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  94,  26),
				new MinetestBiome("RoofedForest",           getColor("RoofedForest",        ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  27,  94),
				new MinetestBiome("RoofedForest ocean",     getColor("RoofedForest",        ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  27,  94),
				new MinetestBiome("Mesa",                   getColor("Mesa",                ColorModifier.None),  (short)        11, (short)  OVERWORLD_MAX, 100,   0),
				new MinetestBiome("Mesa sandlevel",         getColor("Mesa",                ColorModifier.Beach), (short)        -3, (short)             10, 100,   0),
				new MinetestBiome("Mesa ocean",             getColor("Mesa",                ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -4, 100,   0),
				new MinetestBiome("MesaPlateauF",           getColor("MesaPlateauF",        ColorModifier.None),  (short)        11, (short)             29,  60,   0),
				new MinetestBiome("MesaPlateauF grasstop",  BiomeColor.from(202, 140, 101),                       (short)        30, (short)  OVERWORLD_MAX,  60,   0),
				new MinetestBiome("MesaPlateauF sandlevel", getColor("MesaPlateauF",        ColorModifier.Beach), (short)        -3, (short)             10,  60,   0),
				new MinetestBiome("MesaPlateauF ocean",     getColor("MesaPlateauF",        ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -4,  60,   0),
				new MinetestBiome("Savanna",                getColor("Savanna",             ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  79,  36),
				new MinetestBiome("Savanna beach",          getColor("Savanna",             ColorModifier.Beach), (short)        -1, (short)              0,  79,  36),
				new MinetestBiome("Savanna ocean",          getColor("Savanna",             ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -2,  79,  36),
				new MinetestBiome("SavannaM",               getColor("SavannaM",            ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX, 100,  48),
				new MinetestBiome("SavannaM ocean",         getColor("SavannaM",            ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0, 100,  48),
				new MinetestBiome("Jungle",                 getColor("Jungle",              ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  81,  88),
				new MinetestBiome("Jungle shore",           getColor("Jungle",              ColorModifier.Beach), (short)        -1, (short)              0,  81,  88),
				new MinetestBiome("Jungle ocean",           getColor("Jungle",              ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -2,  81,  88),
				new MinetestBiome("JungleM",                getColor("JungleM",             ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  81,  92),
				new MinetestBiome("JungleM shore",          getColor("JungleM",             ColorModifier.Beach), (short)        -1, (short)              0,  81,  92),
				new MinetestBiome("JungleM ocean",          getColor("JungleM",             ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -2,  81,  92),
				new MinetestBiome("JungleEdge",             getColor("JungleEdge",          ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  76,  88),
				new MinetestBiome("JungleEdge ocean",       getColor("JungleEdge",          ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  76,  88),
				new MinetestBiome("JungleEdgeM",            getColor("JungleEdgeM",         ColorModifier.None),  (short)         1, (short)  OVERWORLD_MAX,  79,  90),
				new MinetestBiome("JungleEdgeM ocean",      getColor("JungleEdgeM",         ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  79,  90),
				new MinetestBiome("Swampland",              getColor("Swampland",           ColorModifier.None),  (short)         1, (short)             23,  50,  90),
				new MinetestBiome("Swampland shore",        getColor("Swampland",           ColorModifier.Beach), (short)        -4, (short)              0,  50,  90),
				new MinetestBiome("Swampland ocean",        getColor("Swampland",           ColorModifier.Ocean), (short) OCEAN_MIN, (short)             -5,  50,  90),
				new MinetestBiome("MushroomIsland",         getColor("MushroomIsland",      ColorModifier.None),  (short)         4, (short)             20,  50, 106),
				new MinetestBiome("MushroomIsland shore",   getColor("MushroomIsland",      ColorModifier.Beach), (short)         1, (short)              3,  50, 106),
				new MinetestBiome("MushroomIsland ocean",   getColor("MushroomIsland",      ColorModifier.Ocean), (short) OCEAN_MIN, (short)              0,  50, 106)
			)
		);
		
        Map<String, MinetestBiome> biomesByName = biomes.stream().collect(
                Collectors.toMap(biome -> biome.getName(), biome -> biome));
		
        
		for (Map.Entry<String, BiomeColor> entry : baseBiomes.entrySet()) {			
			
			String biomeName = entry.getKey();
			MinetestBiome baseBiome = biomesByName.get(biomeName);
		
			// Add deep ocean biomes (gravel floor)
			MinetestBiome deepOceanBiome = new MinetestBiome(
				biomeName + " deep ocean",
				getColor(biomeName, ColorModifier.DeepOcean),
				DEEP_OCEAN_MIN,
				DEEP_OCEAN_MAX,
				baseBiome.heat_point,
				baseBiome.humidity_point
			);			
			biomes.add(deepOceanBiome);
						
			// Add underground biomes (needed for when ocean depth is lower than normal biome floors)
			MinetestBiome undergroundBiome = new MinetestBiome(
					biomeName + " underground",
					BiomeColor.from(20, 20, 20),
					OVERWORLD_MIN,
					(short)(DEEP_OCEAN_MIN - 1),
					baseBiome.heat_point,
					baseBiome.humidity_point
				);			
				biomes.add(undergroundBiome);
		}
		
		return biomes.toArray(new MinetestBiome[biomes.size()]);
	}	
}
